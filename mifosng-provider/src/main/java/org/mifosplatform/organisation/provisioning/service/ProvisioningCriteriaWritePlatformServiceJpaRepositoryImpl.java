/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.organisation.provisioning.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.mifosplatform.accounting.glaccount.domain.GLAccount;
import org.mifosplatform.accounting.glaccount.domain.GLAccountRepository;
import org.mifosplatform.accounting.provisioning.service.ProvisioningEntriesReadPlatformService;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.organisation.provisioning.constants.ProvisioningCriteriaConstants;
import org.mifosplatform.organisation.provisioning.data.ProvisioningCriteriaDefinitionData;
import org.mifosplatform.organisation.provisioning.domain.ProvisioningCriteria;
import org.mifosplatform.organisation.provisioning.domain.ProvisioningCriteriaRepository;
import org.mifosplatform.organisation.provisioning.exception.ProvisioningCategoryNotFoundException;
import org.mifosplatform.organisation.provisioning.exception.ProvisioningCriteriaCannotBeDeletedException;
import org.mifosplatform.organisation.provisioning.exception.ProvisioningCriteriaNotFoundException;
import org.mifosplatform.organisation.provisioning.serialization.ProvisioningCriteriaDefinitionJsonDeserializer;
import org.mifosplatform.portfolio.loanproduct.domain.LoanProduct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Service
public class ProvisioningCriteriaWritePlatformServiceJpaRepositoryImpl implements ProvisioningCriteriaWritePlatformService {

    private final static Logger logger = LoggerFactory.getLogger(ProvisioningCriteriaWritePlatformServiceJpaRepositoryImpl.class);

    private final ProvisioningCriteriaDefinitionJsonDeserializer fromApiJsonDeserializer;
    private final ProvisioningCriteriaAssembler provisioningCriteriaAssembler;
    private final ProvisioningCriteriaRepository provisioningCriteriaRepository;
    private final FromJsonHelper fromApiJsonHelper;
    private final GLAccountRepository glAccountRepository;
    private final ProvisioningEntriesReadPlatformService provisioningEntriesReadPlatformService ;
    
    @Autowired
    public ProvisioningCriteriaWritePlatformServiceJpaRepositoryImpl(final ProvisioningCriteriaDefinitionJsonDeserializer fromApiJsonDeserializer,
            final ProvisioningCriteriaAssembler provisioningCriteriaAssembler, final ProvisioningCriteriaRepository provisioningCriteriaRepository,
            final FromJsonHelper fromApiJsonHelper,
            final GLAccountRepository glAccountRepository,
            final ProvisioningEntriesReadPlatformService provisioningEntriesReadPlatformService) {
        this.fromApiJsonDeserializer = fromApiJsonDeserializer;
        this.provisioningCriteriaAssembler = provisioningCriteriaAssembler;
        this.provisioningCriteriaRepository = provisioningCriteriaRepository;
        this.fromApiJsonHelper = fromApiJsonHelper ;
        this.glAccountRepository = glAccountRepository ; 
        this.provisioningEntriesReadPlatformService = provisioningEntriesReadPlatformService ;
    }

    @Override
    public CommandProcessingResult createProvisioningCriteria(JsonCommand command) {
        try {
            this.fromApiJsonDeserializer.validateForCreate(command.json());
            ProvisioningCriteria provisioningCriteria = provisioningCriteriaAssembler.fromParsedJson(command.parsedJson());
            this.provisioningCriteriaRepository.save(provisioningCriteria);
            return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(provisioningCriteria.getId()).build();
        } catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }
    }

    @Override
    public CommandProcessingResult deleteProvisioningCriteria(Long criteriaId) {
        ProvisioningCriteria criteria = this.provisioningCriteriaRepository.findOne(criteriaId) ;
        if(criteria == null) {
            throw new ProvisioningCriteriaNotFoundException(criteriaId) ;
        }
        if(this.provisioningEntriesReadPlatformService.retrieveProvisioningEntryDataByCriteriaId(criteriaId) != null) {
            throw new ProvisioningCriteriaCannotBeDeletedException(criteriaId) ;
        }
        this.provisioningCriteriaRepository.delete(criteriaId) ;
        return new CommandProcessingResultBuilder().withEntityId(criteriaId).build();
    }

    @Override
    public CommandProcessingResult updateProvisioningCriteria(final Long criteriaId, JsonCommand command) {
        this.fromApiJsonDeserializer.validateForUpdate(command.json());
        ProvisioningCriteria provisioningCriteria = provisioningCriteriaRepository.findOne(criteriaId) ;
        if(provisioningCriteria == null) {
            throw new ProvisioningCategoryNotFoundException(criteriaId) ;
        }
        List<LoanProduct> products = this.provisioningCriteriaAssembler.parseLoanProducts(command.parsedJson()) ;
        
        final Map<String, Object> changes = provisioningCriteria.update(command, products) ;
        if(!changes.isEmpty()) {
            updateProvisioningCriteriaDefinitions(provisioningCriteria, command) ;
            provisioningCriteriaRepository.save(provisioningCriteria) ;    
        }
        return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(provisioningCriteria.getId()).build();
    }

    private void updateProvisioningCriteriaDefinitions(ProvisioningCriteria provisioningCriteria, JsonCommand command) {
        final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(command.parsedJson().getAsJsonObject());
        JsonArray jsonProvisioningCriteria = this.fromApiJsonHelper.extractJsonArrayNamed(
                ProvisioningCriteriaConstants.JSON_PROVISIONING_DEFINITIONS_PARAM, command.parsedJson());
        for (JsonElement element : jsonProvisioningCriteria) {
            JsonObject jsonObject = element.getAsJsonObject();
            Long id = this.fromApiJsonHelper.extractLongNamed("id", jsonObject) ;
            Long categoryId = this.fromApiJsonHelper.extractLongNamed(ProvisioningCriteriaConstants.JSON_CATEOGRYID_PARAM, jsonObject);
            Long minimumAge = this.fromApiJsonHelper.extractLongNamed(ProvisioningCriteriaConstants.JSON_MINIMUM_AGE_PARAM, jsonObject);
            Long maximumAge = this.fromApiJsonHelper.extractLongNamed(ProvisioningCriteriaConstants.JSON_MAXIMUM_AGE_PARAM, jsonObject);
            BigDecimal provisioningpercentage = this.fromApiJsonHelper.extractBigDecimalNamed(ProvisioningCriteriaConstants.JSON_PROVISIONING_PERCENTAGE_PARAM,
                    jsonObject, locale);
            Long liabilityAccountId = this.fromApiJsonHelper.extractLongNamed(ProvisioningCriteriaConstants.JSON_LIABILITY_ACCOUNT_PARAM, jsonObject);
            Long expenseAccountId = this.fromApiJsonHelper.extractLongNamed(ProvisioningCriteriaConstants.JSON_EXPENSE_ACCOUNT_PARAM, jsonObject);
            GLAccount liabilityAccount = glAccountRepository.findOne(liabilityAccountId);
            GLAccount expenseAccount = glAccountRepository.findOne(expenseAccountId);
            String categoryName = null ;
            String liabilityAccountName = null ;
            String expenseAccountName = null ;
            ProvisioningCriteriaDefinitionData data = new ProvisioningCriteriaDefinitionData(id, categoryId, 
                    categoryName, minimumAge, maximumAge, provisioningpercentage, 
                    liabilityAccount.getId(), liabilityAccount.getGlCode(), liabilityAccountName, expenseAccount.getId(), expenseAccount.getGlCode(), expenseAccountName) ;
            provisioningCriteria.update(data, liabilityAccount, expenseAccount) ;
        }
    }
    
    /*
     * Guaranteed to throw an exception no matter what the data integrity issue
     * is.
     */
    private void handleDataIntegrityIssues(final JsonCommand command, final DataIntegrityViolationException dve) {

        final Throwable realCause = dve.getMostSpecificCause();
        if (realCause.getMessage().contains("criteria_name")) {
            final String name = command.stringValueOfParameterNamed("criteria_name");
            throw new PlatformDataIntegrityException("error.msg.provisioning.duplicate.criterianame", "Provisioning Criteria with name `"
                    + name + "` already exists", "category name", name);
        }
        logger.error(dve.getMessage(), dve);
        throw new PlatformDataIntegrityException("error.msg.provisioning.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource: " + realCause.getMessage());
    }
}

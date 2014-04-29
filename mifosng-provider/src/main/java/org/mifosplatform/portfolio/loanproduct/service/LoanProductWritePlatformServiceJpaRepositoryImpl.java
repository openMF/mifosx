/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.loanproduct.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.joda.time.LocalDate;
import org.mifosplatform.accounting.producttoaccountmapping.service.ProductToGLAccountMappingWritePlatformService;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.charge.domain.Charge;
import org.mifosplatform.portfolio.charge.domain.ChargeRepositoryWrapper;
import org.mifosplatform.portfolio.charge.exception.ProductLoanChargeNotFoundException;
import org.mifosplatform.portfolio.fund.domain.Fund;
import org.mifosplatform.portfolio.fund.domain.FundRepository;
import org.mifosplatform.portfolio.fund.exception.FundNotFoundException;
import org.mifosplatform.portfolio.loanaccount.domain.LoanTransactionProcessingStrategyRepository;
import org.mifosplatform.portfolio.loanaccount.exception.LoanTransactionProcessingStrategyNotFoundException;
import org.mifosplatform.portfolio.loanaccount.loanschedule.domain.AprCalculator;
import org.mifosplatform.portfolio.loanproduct.data.ProductLoanChargeData;
import org.mifosplatform.portfolio.loanproduct.domain.LoanProduct;
import org.mifosplatform.portfolio.loanproduct.domain.LoanProductRepository;
import org.mifosplatform.portfolio.loanproduct.domain.LoanTransactionProcessingStrategy;
import org.mifosplatform.portfolio.loanproduct.domain.ProductLoanCharge;
import org.mifosplatform.portfolio.loanproduct.domain.ProductLoanChargeRepository;
import org.mifosplatform.portfolio.loanproduct.exception.InvalidCurrencyException;
import org.mifosplatform.portfolio.loanproduct.exception.LoanProductDateException;
import org.mifosplatform.portfolio.loanproduct.exception.LoanProductNotFoundException;
import org.mifosplatform.portfolio.loanproduct.serialization.LoanProductDataValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@Service
public class LoanProductWritePlatformServiceJpaRepositoryImpl implements LoanProductWritePlatformService {

    private final static Logger logger = LoggerFactory.getLogger(LoanProductWritePlatformServiceJpaRepositoryImpl.class);
    private final PlatformSecurityContext context;
    private final LoanProductDataValidator fromApiJsonDeserializer;
    private final LoanProductRepository loanProductRepository;
    private final AprCalculator aprCalculator;
    private final FundRepository fundRepository;
    private final LoanTransactionProcessingStrategyRepository loanTransactionProcessingStrategyRepository;
    private final ChargeRepositoryWrapper chargeRepository;
    private final ProductToGLAccountMappingWritePlatformService accountMappingWritePlatformService;
    private final ProductLoanChargeRepository productChargeRepository;
    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public LoanProductWritePlatformServiceJpaRepositoryImpl(final PlatformSecurityContext context,
            final LoanProductDataValidator fromApiJsonDeserializer, final LoanProductRepository loanProductRepository,
            final AprCalculator aprCalculator, final FundRepository fundRepository,
            final LoanTransactionProcessingStrategyRepository loanTransactionProcessingStrategyRepository,
            final ChargeRepositoryWrapper chargeRepository,
            final ProductToGLAccountMappingWritePlatformService accountMappingWritePlatformService,
            final ProductLoanChargeRepository productChargeRepository, final FromJsonHelper fromApiJsonHelper) {
        this.context = context;
        this.fromApiJsonDeserializer = fromApiJsonDeserializer;
        this.loanProductRepository = loanProductRepository;
        this.aprCalculator = aprCalculator;
        this.fundRepository = fundRepository;
        this.loanTransactionProcessingStrategyRepository = loanTransactionProcessingStrategyRepository;
        this.chargeRepository = chargeRepository;
        this.accountMappingWritePlatformService = accountMappingWritePlatformService;
        this.productChargeRepository = productChargeRepository;
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    @Transactional
    @Override
    public CommandProcessingResult createLoanProduct(final JsonCommand command) {

        try {

            this.context.authenticatedUser();

            this.fromApiJsonDeserializer.validateForCreate(command.json());
            validateInputDates(command);

            final Fund fund = findFundByIdIfProvided(command.longValueOfParameterNamed("fundId"));

            final Long transactionProcessingStrategyId = command.longValueOfParameterNamed("transactionProcessingStrategyId");
            final LoanTransactionProcessingStrategy loanTransactionProcessingStrategy = findStrategyByIdIfProvided(transactionProcessingStrategyId);

            final String currencyCode = command.stringValueOfParameterNamed("currencyCode");
            final Set<ProductLoanCharge> charges = assembleListOfProductCharges(command, currencyCode);

            final LoanProduct loanproduct = LoanProduct.assembleFromJson(fund, loanTransactionProcessingStrategy, charges, command,
                    this.aprCalculator);

            this.loanProductRepository.save(loanproduct);

            // save accounting mappings
            this.accountMappingWritePlatformService.createLoanProductToGLAccountMapping(loanproduct.getId(), command);

            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withEntityId(loanproduct.getId()) //
                    .build();

        } catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }

    }

    private LoanTransactionProcessingStrategy findStrategyByIdIfProvided(final Long transactionProcessingStrategyId) {
        LoanTransactionProcessingStrategy strategy = null;
        if (transactionProcessingStrategyId != null) {
            strategy = this.loanTransactionProcessingStrategyRepository.findOne(transactionProcessingStrategyId);
            if (strategy == null) { throw new LoanTransactionProcessingStrategyNotFoundException(transactionProcessingStrategyId); }
        }
        return strategy;
    }

    private Fund findFundByIdIfProvided(final Long fundId) {
        Fund fund = null;
        if (fundId != null) {
            fund = this.fundRepository.findOne(fundId);
            if (fund == null) { throw new FundNotFoundException(fundId); }
        }
        return fund;
    }

    @Transactional
    @Override
    public CommandProcessingResult updateLoanProduct(final Long loanProductId, final JsonCommand command) {

        try {
            this.context.authenticatedUser();

            final LoanProduct product = this.loanProductRepository.findOne(loanProductId);
            if (product == null) { throw new LoanProductNotFoundException(loanProductId); }

            this.fromApiJsonDeserializer.validateForUpdate(command.json(), product);
            validateInputDates(command);

            final Set<ProductLoanCharge> existingCharges = product.getCharges();
            Map<Long, ProductLoanChargeData> chargesMap = new HashMap<Long, ProductLoanChargeData>();
            for (ProductLoanCharge prodctLoanCharge : existingCharges) {
                ProductLoanChargeData chargeData = ProductLoanChargeData.instance(prodctLoanCharge.getId(), prodctLoanCharge.isMandatory(),
                        prodctLoanCharge.getCharge().getId());
                chargesMap.put(prodctLoanCharge.getId(), chargeData);
            }

            /**
             * Stores all charges which are passed in during modify loan product
             **/
            final Set<ProductLoanCharge> possiblyModifedProductLoanCharges = assembleListOfProductCharges(command, product.getCurrency()
                    .getCode());
            /** Boolean determines if any charge has been modified **/
            Boolean isChargesModified = false;

            /**
             * If there are any charges already present, which are now not
             * passed in as a part of the request, deem the charges as modified
             **/

            if (!possiblyModifedProductLoanCharges.containsAll(existingCharges)) {
                isChargesModified = true;
            }
            
            /**
             * If any new charges are added or values of existing charges are
             * modified
             **/
            if (!isChargesModified) {
                for (ProductLoanCharge productLoanCharge : possiblyModifedProductLoanCharges) {
                    if (productLoanCharge.getId() == null) {
                        isChargesModified = true;
                    } else {
                        ProductLoanChargeData chargeData = chargesMap.get(productLoanCharge.getId());
                        if (!productLoanCharge.isMandatory().equals(chargeData.getIsMandatory())) {
                            isChargesModified = true;
                        }
                    }
                }
            }
            
            final Map<String, Object> changes = product.update(command, this.aprCalculator, possiblyModifedProductLoanCharges, isChargesModified);
            
            if (changes.containsKey("fundId")) {
                final Long fundId = (Long) changes.get("fundId");
                final Fund fund = findFundByIdIfProvided(fundId);
                product.update(fund);
            }

            if (changes.containsKey("transactionProcessingStrategyId")) {
                final Long transactionProcessingStrategyId = (Long) changes.get("transactionProcessingStrategyId");
                final LoanTransactionProcessingStrategy loanTransactionProcessingStrategy = findStrategyByIdIfProvided(transactionProcessingStrategyId);
                product.update(loanTransactionProcessingStrategy);
            }

            if (changes.containsKey("charges")) {
                product.update(possiblyModifedProductLoanCharges);
            }

            // accounting related changes
            final boolean accountingTypeChanged = changes.containsKey("accountingRule");
            final Map<String, Object> accountingMappingChanges = this.accountMappingWritePlatformService
                    .updateLoanProductToGLAccountMapping(product.getId(), command, accountingTypeChanged, product.getAccountingType());
            changes.putAll(accountingMappingChanges);

            if (!changes.isEmpty()) {
                this.loanProductRepository.saveAndFlush(product);
            }

            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withEntityId(loanProductId) //
                    .with(changes) //
                    .build();

        } catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command, dve);
            return new CommandProcessingResult(Long.valueOf(-1));
        }

    }

    private Set<ProductLoanCharge> assembleListOfProductCharges(final JsonCommand command, final String currencyCode) {

        final Set<ProductLoanCharge> productCharges = new HashSet<ProductLoanCharge>();

        String loanProductCurrencyCode = command.stringValueOfParameterNamed("currencyCode");
        if (loanProductCurrencyCode == null) {
            loanProductCurrencyCode = currencyCode;
        }

        if (command.parameterExists("charges")) {
            final JsonArray chargesArray = command.arrayOfParameterNamed("charges");
            if (chargesArray != null) {
                for (int i = 0; i < chargesArray.size(); i++) {

                    final JsonObject jsonObject = chargesArray.get(i).getAsJsonObject();
                    final Long id = jsonObject.get("id").getAsLong();
                    final Long productChargeId = this.fromApiJsonHelper.extractLongNamed("productChargeId", jsonObject);
                    final boolean isMandatory = jsonObject.get("isMandatory").getAsBoolean();
                    if (productChargeId == null) {
                        final Charge charge = this.chargeRepository.findOneWithNotFoundDetection(id);
                        final ProductLoanCharge productLoanCharge = ProductLoanCharge.createNewFromJson(charge, isMandatory);
                        if (!loanProductCurrencyCode.equals(charge.getCurrencyCode())) {
                            final String errorMessage = "Charge and Loan Product must have the same currency.";
                            throw new InvalidCurrencyException("charge", "attach.to.loan.product", errorMessage);
                        }
                        productCharges.add(productLoanCharge);
                    } else {
                        final ProductLoanCharge productLoanCharge = this.productChargeRepository.findOne(productChargeId);
                        if (productLoanCharge == null) { throw new ProductLoanChargeNotFoundException(productChargeId); }

                        productLoanCharge.update(isMandatory);

                        productCharges.add(productLoanCharge);
                    }
                }
            }
        }

        return productCharges;
    }

    /*
     * Guaranteed to throw an exception no matter what the data integrity issue
     * is.
     */
    private void handleDataIntegrityIssues(final JsonCommand command, final DataIntegrityViolationException dve) {

        final Throwable realCause = dve.getMostSpecificCause();

        if (realCause.getMessage().contains("external_id")) {

            final String externalId = command.stringValueOfParameterNamed("externalId");
            throw new PlatformDataIntegrityException("error.msg.product.loan.duplicate.externalId", "Loan Product with externalId `"
                    + externalId + "` already exists", "externalId", externalId);
        } else if (realCause.getMessage().contains("unq_name")) {

            final String name = command.stringValueOfParameterNamed("name");
            throw new PlatformDataIntegrityException("error.msg.product.loan.duplicate.name", "Loan product with name `" + name
                    + "` already exists", "name", name);
        } else if (realCause.getMessage().contains("unq_short_name")) {

            final String shortName = command.stringValueOfParameterNamed("shortName");
            throw new PlatformDataIntegrityException("error.msg.product.loan.duplicate.short.name", "Loan product with short name `"
                    + shortName + "` already exists", "shortName", shortName);
        } else if (realCause.getMessage().contains("Duplicate entry")) {
            final Object[] args = null;
            throw new PlatformDataIntegrityException("error.msg.product.loan.duplicate.charge",
                    "Loan product may only have one charge of each type.`", "charges", args);
        }

        logAsErrorUnexpectedDataIntegrityException(dve);
        throw new PlatformDataIntegrityException("error.msg.product.loan.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource.");
    }

    private void validateInputDates(final JsonCommand command) {
        final LocalDate startDate = command.localDateValueOfParameterNamed("startDate");
        final LocalDate closeDate = command.localDateValueOfParameterNamed("closeDate");

        if (startDate != null && closeDate != null) {
            if (closeDate.isBefore(startDate)) { throw new LoanProductDateException(startDate.toString(), closeDate.toString()); }
        }
    }

    private void logAsErrorUnexpectedDataIntegrityException(final DataIntegrityViolationException dve) {
        logger.error(dve.getMessage(), dve);
    }
}
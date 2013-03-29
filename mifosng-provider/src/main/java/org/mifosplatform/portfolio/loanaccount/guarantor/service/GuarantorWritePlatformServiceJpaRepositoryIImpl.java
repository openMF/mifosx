/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.loanaccount.guarantor.service;

import java.util.Map;

import org.mifosplatform.infrastructure.codes.domain.CodeValue;
import org.mifosplatform.infrastructure.codes.domain.CodeValueRepositoryWrapper;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.organisation.staff.domain.StaffRepositoryWrapper;
import org.mifosplatform.portfolio.client.domain.ClientRepositoryWrapper;
import org.mifosplatform.portfolio.loanaccount.domain.Loan;
import org.mifosplatform.portfolio.loanaccount.domain.LoanRepositoryWrapper;
import org.mifosplatform.portfolio.loanaccount.guarantor.GuarantorConstants;
import org.mifosplatform.portfolio.loanaccount.guarantor.GuarantorConstants.GUARANTOR_JSON_INPUT_PARAMS;
import org.mifosplatform.portfolio.loanaccount.guarantor.command.GuarantorCommand;
import org.mifosplatform.portfolio.loanaccount.guarantor.domain.Guarantor;
import org.mifosplatform.portfolio.loanaccount.guarantor.domain.GuarantorRepository;
import org.mifosplatform.portfolio.loanaccount.guarantor.exception.GuarantorNotFoundException;
import org.mifosplatform.portfolio.loanaccount.guarantor.exception.InvalidGuarantorException;
import org.mifosplatform.portfolio.loanaccount.guarantor.serialization.GuarantorCommandFromApiJsonDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GuarantorWritePlatformServiceJpaRepositoryIImpl implements GuarantorWritePlatformService {

    private final static Logger logger = LoggerFactory.getLogger(GuarantorWritePlatformServiceJpaRepositoryIImpl.class);

    private final ClientRepositoryWrapper clientRepositoryWrapper;
    private final StaffRepositoryWrapper staffRepositoryWrapper;
    private final LoanRepositoryWrapper loanRepositoryWrapper;
    private final GuarantorRepository guarantorRepository;
    private final GuarantorCommandFromApiJsonDeserializer fromApiJsonDeserializer;
    private final CodeValueRepositoryWrapper codeValueRepositoryWrapper;

    @Autowired
    public GuarantorWritePlatformServiceJpaRepositoryIImpl(final LoanRepositoryWrapper loanRepositoryWrapper,
            final GuarantorRepository guarantorRepository, final ClientRepositoryWrapper clientRepositoryWrapper,
            final StaffRepositoryWrapper staffRepositoryWrapper, final GuarantorCommandFromApiJsonDeserializer fromApiJsonDeserializer,
            final CodeValueRepositoryWrapper codeValueRepositoryWrapper) {
        this.loanRepositoryWrapper = loanRepositoryWrapper;
        this.clientRepositoryWrapper = clientRepositoryWrapper;
        this.fromApiJsonDeserializer = fromApiJsonDeserializer;
        this.guarantorRepository = guarantorRepository;
        this.staffRepositoryWrapper = staffRepositoryWrapper;
        this.codeValueRepositoryWrapper = codeValueRepositoryWrapper;
    }

    @Override
    @Transactional
    public CommandProcessingResult createGuarantor(final Long loanId, final JsonCommand command) {
        try {
            final GuarantorCommand guarantorCommand = this.fromApiJsonDeserializer.commandFromApiJson(command.json());
            guarantorCommand.validateForCreate();

            Loan loan = this.loanRepositoryWrapper.findOneWithNotFoundDetection(loanId);

            Long clientRelationshipId = guarantorCommand.getClientRelationshipTypeId();
            CodeValue clientRelationshipType = null;

            if (clientRelationshipId != null) {
                clientRelationshipType = this.codeValueRepositoryWrapper.findOneByCodeNameAndIdWithNotFoundDetection(
                        GuarantorConstants.GUARANTOR_RELATIONSHIP_CODE_NAME, clientRelationshipId);
            }

            Guarantor guarantor = null;
            guarantor = Guarantor.fromJson(loan, clientRelationshipType, command);

            validateGuarantorBusinessRules(guarantor);

            this.guarantorRepository.saveAndFlush(guarantor);

            return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withOfficeId(guarantor.getOfficeId())
                    .withEntityId(guarantor.getId()).withLoanId(loanId).build();
        } catch (DataIntegrityViolationException dve) {
            handleGuarantorDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }
    }

    @Override
    @Transactional
    public CommandProcessingResult updateGuarantor(final Long loanId, final Long guarantorId, final JsonCommand command) {
        try {
            final GuarantorCommand guarantorCommand = this.fromApiJsonDeserializer.commandFromApiJson(command.json());
            guarantorCommand.validateForUpdate();

            final Loan loan = this.loanRepositoryWrapper.findOneWithNotFoundDetection(loanId);
            final Guarantor guarantorForUpdate = this.guarantorRepository.findByLoanAndId(loan, guarantorId);
            if (guarantorForUpdate == null) { throw new GuarantorNotFoundException(loanId, guarantorId); }

            final Map<String, Object> changesOnly = guarantorForUpdate.update(command);

            if (changesOnly.containsKey(GUARANTOR_JSON_INPUT_PARAMS.CLIENT_RELATIONSHIP_TYPE_ID)) {
                Long clientRelationshipId = guarantorCommand.getClientRelationshipTypeId();
                CodeValue clientRelationshipType = null;
                if (clientRelationshipId != null) {
                    clientRelationshipType = this.codeValueRepositoryWrapper.findOneByCodeNameAndIdWithNotFoundDetection(
                            GuarantorConstants.GUARANTOR_RELATIONSHIP_CODE_NAME, clientRelationshipId);
                }
                guarantorForUpdate.setClientRelationshipType(clientRelationshipType);
            }

            if (changesOnly.containsKey(GUARANTOR_JSON_INPUT_PARAMS.ENTITY_ID)
                    || changesOnly.containsKey(GUARANTOR_JSON_INPUT_PARAMS.GUARANTOR_TYPE_ID)) {
                validateGuarantorBusinessRules(guarantorForUpdate);
            }

            if (!changesOnly.isEmpty()) {
                this.guarantorRepository.saveAndFlush(guarantorForUpdate);
            }

            return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withOfficeId(guarantorForUpdate.getOfficeId())
                    .withEntityId(guarantorForUpdate.getId()).withOfficeId(guarantorForUpdate.getLoanId()).with(changesOnly).build();
        } catch (DataIntegrityViolationException dve) {
            handleGuarantorDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }
    }

    @Override
    @Transactional
    public CommandProcessingResult removeGuarantor(final Long loanId, final Long guarantorId) {
        final Loan loan = this.loanRepositoryWrapper.findOneWithNotFoundDetection(loanId);
        final Guarantor guarantorForDelete = this.guarantorRepository.findByLoanAndId(loan, guarantorId);
        if (guarantorForDelete == null) { throw new GuarantorNotFoundException(loanId, guarantorId); }

        this.guarantorRepository.delete(guarantorForDelete);

        return new CommandProcessingResultBuilder().withEntityId(guarantorId).withLoanId(guarantorForDelete.getLoanId())
                .withOfficeId(guarantorForDelete.getOfficeId()).build();
    }

    /**
     * @param loanId
     * @param loan
     * @param guarantor
     */
    private void validateGuarantorBusinessRules(Guarantor guarantor) {
        // validate guarantor conditions
        if (guarantor.isExistingCustomer()) {
            // check client exists
            clientRepositoryWrapper.findOneWithNotFoundDetection(guarantor.getEntityId());
            // validate that the client is not set as a self guarantor
            if (guarantor.getClientId().equals(guarantor.getEntityId())) { throw new InvalidGuarantorException(guarantor.getEntityId(),
                    guarantor.getLoanId()); }
        } else if (guarantor.isExistingEmployee()) {
            staffRepositoryWrapper.findOneWithNotFoundDetection(guarantor.getEntityId());
        }
    }

    /**
     * Throws an exception for any data Integrity issue
     * 
     * @param command
     * @param dve
     */
    @SuppressWarnings("unused")
    private void handleGuarantorDataIntegrityIssues(final JsonCommand command, DataIntegrityViolationException dve) {
        Throwable realCause = dve.getMostSpecificCause();
        logger.error(dve.getMessage(), dve);
        throw new PlatformDataIntegrityException("error.msg.guarantor.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource Guarantor: " + realCause.getMessage());
    }

}
/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.paymenttowhom.service;

import java.util.Map;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.portfolio.paymenttowhom.api.PaymentToWhomApiResourceConstants;
import org.mifosplatform.portfolio.paymenttowhom.data.PaymentToWhomDataValidator;
import org.mifosplatform.portfolio.paymenttowhom.domain.PaymentToWhom;
import org.mifosplatform.portfolio.paymenttowhom.domain.PaymentToWhomRepository;
import org.mifosplatform.portfolio.paymenttowhom.domain.PaymentToWhomRepositoryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class PaymentToWhomWriteServiceImpl implements PaymentToWhomWriteService {

    private final PaymentToWhomRepository repository;
    private final PaymentToWhomRepositoryWrapper repositoryWrapper;
    private final PaymentToWhomDataValidator fromApiJsonDeserializer;

    @Autowired
    public PaymentToWhomWriteServiceImpl(PaymentToWhomRepository repository, PaymentToWhomRepositoryWrapper repositoryWrapper,
            PaymentToWhomDataValidator fromApiJsonDeserializer) {
        this.repository = repository;
        this.repositoryWrapper = repositoryWrapper;
        this.fromApiJsonDeserializer = fromApiJsonDeserializer;

    }

    @Override
    public CommandProcessingResult createPaymentToWhom(JsonCommand command) {
        this.fromApiJsonDeserializer.validateForCreate(command.json());
        String name = command.stringValueOfParameterNamed(PaymentToWhomApiResourceConstants.NAME);
        String description = command.stringValueOfParameterNamed(PaymentToWhomApiResourceConstants.DESCRIPTION);
        Boolean isCashPayment = command.booleanObjectValueOfParameterNamed(PaymentToWhomApiResourceConstants.ISCASHPAYMENT);
        Long position = command.longValueOfParameterNamed(PaymentToWhomApiResourceConstants.POSITION);

        PaymentToWhom newPaymentToWhom = PaymentToWhom.create(name, description, isCashPayment, position);
        this.repository.save(newPaymentToWhom);
        return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(newPaymentToWhom.getId()).build();
    }

    @Override
    public CommandProcessingResult updatePaymentToWhom(Long paymentToWhomId, JsonCommand command) {

        this.fromApiJsonDeserializer.validateForUpdate(command.json());
        final PaymentToWhom paymentToWhom = this.repositoryWrapper.findOneWithNotFoundDetection(paymentToWhomId);
        final Map<String, Object> changes = paymentToWhom.update(command);

        if (!changes.isEmpty()) {
            this.repository.save(paymentToWhom);
        }

        return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(command.entityId()).build();
    }

    @Override
    public CommandProcessingResult deletePaymentToWhom(Long paymentToWhomId) {
        final PaymentToWhom paymentToWhom = this.repositoryWrapper.findOneWithNotFoundDetection(paymentToWhomId);
        try {
            this.repository.delete(paymentToWhom);
            this.repository.flush();
        } catch (final DataIntegrityViolationException e) {
            handleDataIntegrityIssues(e);
        }
        return new CommandProcessingResultBuilder().withEntityId(paymentToWhom.getId()).build();
    }

    private void handleDataIntegrityIssues(final DataIntegrityViolationException dve) {

        final Throwable realCause = dve.getMostSpecificCause();
        if (realCause.getMessage().contains("acc_product_mapping")) {
            throw new PlatformDataIntegrityException("error.msg.payment.type.association.exist",
                    "cannot.delete.payment.type.with.association");
        } else if (realCause.getMessage().contains("payment_to_whom_id")) { throw new PlatformDataIntegrityException(
                "error.msg.payment.to.whom.association.exist", "cannot.delete.payment.to.whom.with.association"); }

        throw new PlatformDataIntegrityException("error.msg.paymenttypes.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource.");
    }
}

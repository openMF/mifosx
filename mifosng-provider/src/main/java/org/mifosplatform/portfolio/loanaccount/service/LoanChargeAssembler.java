/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.loanaccount.service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.portfolio.charge.domain.Charge;
import org.mifosplatform.portfolio.charge.domain.ChargeCalculationType;
import org.mifosplatform.portfolio.charge.domain.ChargePaymentMode;
import org.mifosplatform.portfolio.charge.domain.ChargeRepositoryWrapper;
import org.mifosplatform.portfolio.charge.domain.ChargeTimeType;
import org.mifosplatform.portfolio.charge.exception.LoanChargeNotFoundException;
import org.mifosplatform.portfolio.loanaccount.domain.LoanCharge;
import org.mifosplatform.portfolio.loanaccount.domain.LoanChargeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Service
public class LoanChargeAssembler {

    private final FromJsonHelper fromApiJsonHelper;
    private final ChargeRepositoryWrapper chargeRepository;
    private final LoanChargeRepository loanChargeRepository;

    @Autowired
    public LoanChargeAssembler(final FromJsonHelper fromApiJsonHelper, final ChargeRepositoryWrapper chargeRepository,
            final LoanChargeRepository loanChargeRepository) {
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.chargeRepository = chargeRepository;
        this.loanChargeRepository = loanChargeRepository;
    }

    public Set<LoanCharge> fromParsedJson(final JsonElement element) {

        final Set<LoanCharge> loanCharges = new HashSet<LoanCharge>();

        final BigDecimal principal = fromApiJsonHelper.extractBigDecimalWithLocaleNamed("principal", element);

        if (element.isJsonObject()) {
            final JsonObject topLevelJsonElement = element.getAsJsonObject();
            final String dateFormat = fromApiJsonHelper.extractDateFormatParameter(topLevelJsonElement);
            final Locale locale = fromApiJsonHelper.extractLocaleParameter(topLevelJsonElement);
            if (topLevelJsonElement.has("charges") && topLevelJsonElement.get("charges").isJsonArray()) {
                final JsonArray array = topLevelJsonElement.get("charges").getAsJsonArray();
                for (int i = 0; i < array.size(); i++) {

                    final JsonObject loanChargeElement = array.get(i).getAsJsonObject();

                    final Long id = fromApiJsonHelper.extractLongNamed("id", loanChargeElement);
                    final Long chargeId = fromApiJsonHelper.extractLongNamed("chargeId", loanChargeElement);
                    final BigDecimal amount = fromApiJsonHelper.extractBigDecimalNamed("amount", loanChargeElement, locale);
                    final Integer chargeTimeType = fromApiJsonHelper.extractIntegerNamed("chargeTimeType", loanChargeElement, locale);
                    final Integer chargeCalculationType = fromApiJsonHelper.extractIntegerNamed("chargeCalculationType", loanChargeElement,
                            locale);
                    final LocalDate dueDate = fromApiJsonHelper.extractLocalDateNamed("dueDate", loanChargeElement,
                            dateFormat, locale);
                    final Integer chargePaymentMode = fromApiJsonHelper.extractIntegerNamed("chargePaymentMode", loanChargeElement, locale);
                    if (id == null) {
                        final Charge chargeDefinition = this.chargeRepository.findOneWithNotFoundDetection(chargeId);
                        ChargeTimeType chargeTime = null;
                        if (chargeTimeType != null) {
                            chargeTime = ChargeTimeType.fromInt(chargeTimeType);
                        }
                        ChargeCalculationType chargeCalculation = null;
                        if (chargeCalculationType != null) {
                            chargeCalculation = ChargeCalculationType.fromInt(chargeCalculationType);
                        }
                        ChargePaymentMode chargePaymentModeEnum = null;
                        if(chargePaymentMode != null){
                            chargePaymentModeEnum = ChargePaymentMode.fromInt(chargePaymentMode);
                        }
                        final LoanCharge loanCharge = LoanCharge.createNewWithoutLoan(chargeDefinition, principal, amount, chargeTime,
                                chargeCalculation, dueDate, chargePaymentModeEnum);
                        loanCharges.add(loanCharge);
                    } else {
                        final Long loanChargeId = id;
                        final LoanCharge loanCharge = this.loanChargeRepository.findOne(loanChargeId);
                        if (loanCharge == null) { throw new LoanChargeNotFoundException(loanChargeId); }

                        loanCharge.update(amount, dueDate, principal);

                        loanCharges.add(loanCharge);
                    }
                }
            }
        }

        return loanCharges;
    }
}
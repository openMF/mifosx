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
import org.mifosplatform.portfolio.charge.exception.LoanChargeCannotBeAddedException;
import org.mifosplatform.portfolio.charge.exception.LoanChargeNotFoundException;
import org.mifosplatform.portfolio.loanaccount.api.LoanApiConstants;
import org.mifosplatform.portfolio.loanaccount.domain.LoanCharge;
import org.mifosplatform.portfolio.loanaccount.domain.LoanChargeRepository;
import org.mifosplatform.portfolio.loanaccount.domain.LoanDisbursementDetails;
import org.mifosplatform.portfolio.loanaccount.domain.LoanTrancheDisbursementCharge;
import org.mifosplatform.portfolio.loanproduct.domain.LoanProduct;
import org.mifosplatform.portfolio.loanproduct.domain.LoanProductRepository;
import org.mifosplatform.portfolio.loanproduct.exception.LoanProductNotFoundException;
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
    private final LoanProductRepository loanProductRepository;

    @Autowired
    public LoanChargeAssembler(final FromJsonHelper fromApiJsonHelper, final ChargeRepositoryWrapper chargeRepository,
            final LoanChargeRepository loanChargeRepository, final LoanProductRepository loanProductRepository) {
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.chargeRepository = chargeRepository;
        this.loanChargeRepository = loanChargeRepository;
        this.loanProductRepository = loanProductRepository;
    }

    public Set<LoanCharge> fromParsedJson(final JsonElement element, Set<LoanDisbursementDetails> disbursementDetails) {

        final Set<LoanCharge> loanCharges = new HashSet<>();

        final BigDecimal principal = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed("principal", element);
        final Integer numberOfRepayments = this.fromApiJsonHelper.extractIntegerWithLocaleNamed("numberOfRepayments", element);
        final Long productId = this.fromApiJsonHelper.extractLongNamed("productId", element);
        final LoanProduct loanProduct = this.loanProductRepository.findOne(productId);
        if (loanProduct == null) { throw new LoanProductNotFoundException(productId); }
        final boolean isMultiDisbursal = loanProduct.isMultiDisburseLoan();
        LocalDate expectedDisbursementDate = null;

        if (element.isJsonObject()) {
            final JsonObject topLevelJsonElement = element.getAsJsonObject();
            final String dateFormat = this.fromApiJsonHelper.extractDateFormatParameter(topLevelJsonElement);
            final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(topLevelJsonElement);
            if (topLevelJsonElement.has("charges") && topLevelJsonElement.get("charges").isJsonArray()) {
                final JsonArray array = topLevelJsonElement.get("charges").getAsJsonArray();
                for (int i = 0; i < array.size(); i++) {

                    final JsonObject loanChargeElement = array.get(i).getAsJsonObject();

                    final Long id = this.fromApiJsonHelper.extractLongNamed("id", loanChargeElement);
                    final Long chargeId = this.fromApiJsonHelper.extractLongNamed("chargeId", loanChargeElement);
                    final BigDecimal amount = this.fromApiJsonHelper.extractBigDecimalNamed("amount", loanChargeElement, locale);
                    final Integer chargeTimeType = this.fromApiJsonHelper.extractIntegerNamed("chargeTimeType", loanChargeElement, locale);
                    final Integer chargeCalculationType = this.fromApiJsonHelper.extractIntegerNamed("chargeCalculationType",
                            loanChargeElement, locale);
                    final LocalDate dueDate = this.fromApiJsonHelper
                            .extractLocalDateNamed("dueDate", loanChargeElement, dateFormat, locale);
                    final Integer chargePaymentMode = this.fromApiJsonHelper.extractIntegerNamed("chargePaymentMode", loanChargeElement,
                            locale);
                    if (id == null) {
                        final Charge chargeDefinition = this.chargeRepository.findOneWithNotFoundDetection(chargeId);

                        if (chargeDefinition.isOverdueInstallment()) {

                            final String defaultUserMessage = "Installment charge cannot be added to the loan.";
                            throw new LoanChargeCannotBeAddedException("loanCharge", "overdue.charge", defaultUserMessage, null,
                                    chargeDefinition.getName());
                        }

                        ChargeTimeType chargeTime = null;
                        if (chargeTimeType != null) {
                            chargeTime = ChargeTimeType.fromInt(chargeTimeType);
                        }
                        ChargeCalculationType chargeCalculation = null;
                        if (chargeCalculationType != null) {
                            chargeCalculation = ChargeCalculationType.fromInt(chargeCalculationType);
                        }
                        ChargePaymentMode chargePaymentModeEnum = null;
                        if (chargePaymentMode != null) {
                            chargePaymentModeEnum = ChargePaymentMode.fromInt(chargePaymentMode);
                        }
                       if(!isMultiDisbursal){
                            final LoanCharge loanCharge = LoanCharge.createNewWithoutLoan(chargeDefinition, principal, amount, chargeTime,
                                    chargeCalculation, dueDate, chargePaymentModeEnum, numberOfRepayments);
                            loanCharges.add(loanCharge);
                        }
                        if(isMultiDisbursal && topLevelJsonElement.has("disbursementData") && topLevelJsonElement.get("disbursementData").isJsonArray()){
                            final JsonArray disbursementArray = topLevelJsonElement.get("disbursementData").getAsJsonArray();
                            JsonObject disbursementDataElement = disbursementArray.get(0).getAsJsonObject();
                            expectedDisbursementDate = this.fromApiJsonHelper.extractLocalDateNamed(LoanApiConstants.disbursementDateParameterName,
                                    disbursementDataElement, dateFormat, locale);
                             
                        }
                        
                        if((chargeDefinition.isPercentageOfDisbursementAmount() || chargeDefinition.isPercentageOfApprovedAmount()) && disbursementDetails != null){
                            LoanTrancheDisbursementCharge loanTrancheDisbursementCharge = null;
                            for(LoanDisbursementDetails disbursementDetail : disbursementDetails){
                                if(chargeDefinition.getChargeTime() == ChargeTimeType.DISBURSEMENT.getValue()) {
                                	if(chargeDefinition.isPercentageOfApprovedAmount() && expectedDisbursementDate.equals(disbursementDetail.expectedDisbursementDateAsLocalDate())){
                                		final LoanCharge loanCharge = LoanCharge.createNewWithoutLoan(chargeDefinition, principal, amount, chargeTime,
                                                chargeCalculation, dueDate, chargePaymentModeEnum, numberOfRepayments);
                                        loanCharges.add(loanCharge);
                                        loanTrancheDisbursementCharge = new LoanTrancheDisbursementCharge(loanCharge,disbursementDetail);
                                        loanCharge.updateLoanTrancheDisbursementCharge(loanTrancheDisbursementCharge);
                                	}else{
                                		if(expectedDisbursementDate.equals(disbursementDetail.expectedDisbursementDateAsLocalDate())){
                                            final LoanCharge loanCharge = LoanCharge.createNewWithoutLoan(chargeDefinition, disbursementDetail.principal(), amount, chargeTime,
                                                    chargeCalculation, disbursementDetail.expectedDisbursementDateAsLocalDate(), chargePaymentModeEnum, numberOfRepayments);
                                            loanCharges.add(loanCharge);
                                            loanTrancheDisbursementCharge = new LoanTrancheDisbursementCharge(loanCharge,disbursementDetail);
                                            loanCharge.updateLoanTrancheDisbursementCharge(loanTrancheDisbursementCharge);
                                        }
                                	}
                                    
                                }else if(chargeDefinition.getChargeTime() == ChargeTimeType.TRANCHE_DISBURSEMENT.getValue()) {
                                        final LoanCharge loanCharge = LoanCharge.createNewWithoutLoan(chargeDefinition, disbursementDetail.principal(), amount, chargeTime,
                                                chargeCalculation, disbursementDetail.expectedDisbursementDateAsLocalDate(), chargePaymentModeEnum, numberOfRepayments);
                                        loanCharges.add(loanCharge);
                                        loanTrancheDisbursementCharge = new LoanTrancheDisbursementCharge(loanCharge,disbursementDetail);
                                        loanCharge.updateLoanTrancheDisbursementCharge(loanTrancheDisbursementCharge);
                                }   
                            }
                         }else{
                        	 LoanTrancheDisbursementCharge loanTrancheDisbursementCharge = null;
                        	 if(disbursementDetails != null){
                        		 for(LoanDisbursementDetails disbursementDetail : disbursementDetails){
                            		 if(!(expectedDisbursementDate.isBefore(disbursementDetail.expectedDisbursementDateAsLocalDate()))){
                            			 if(chargeDefinition.getChargeCalculation() == ChargeCalculationType.FLAT.getValue()){
                                      		if(chargeDefinition.getChargeTime() == ChargeTimeType.DISBURSEMENT.getValue() ||
                                      				chargeDefinition.getChargeTime() == ChargeTimeType.SPECIFIED_DUE_DATE.getValue()){
                                      			final LoanCharge loanCharge = LoanCharge.createNewWithoutLoan(chargeDefinition, principal, amount, chargeTime,
                                                          chargeCalculation, dueDate, chargePaymentModeEnum, numberOfRepayments);
                                                  loanCharges.add(loanCharge);
                                                  loanTrancheDisbursementCharge = new LoanTrancheDisbursementCharge(loanCharge,disbursementDetail);
                                                  loanCharge.updateLoanTrancheDisbursementCharge(loanTrancheDisbursementCharge);
                                      		}
                                      	}
                            		 }
                            	 }
                        	 }
                        }
                    } else {
                        final Long loanChargeId = id;
                        final LoanCharge loanCharge = this.loanChargeRepository.findOne(loanChargeId);
                        if (loanCharge == null) { throw new LoanChargeNotFoundException(loanChargeId); }

                        loanCharge.update(amount, dueDate, numberOfRepayments);

                        loanCharges.add(loanCharge);
                    }
                }
            }
        }

        return loanCharges;
    }
}
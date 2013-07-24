/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.savings.domain;

import static org.mifosplatform.portfolio.savings.SavingsApiConstants.accountNoParamName;
import static org.mifosplatform.portfolio.savings.SavingsApiConstants.annualFeeAmountParamName;
import static org.mifosplatform.portfolio.savings.SavingsApiConstants.annualFeeOnMonthDayParamName;
import static org.mifosplatform.portfolio.savings.SavingsApiConstants.clientIdParamName;
import static org.mifosplatform.portfolio.savings.SavingsApiConstants.externalIdParamName;
import static org.mifosplatform.portfolio.savings.SavingsApiConstants.fieldOfficerIdParamName;
import static org.mifosplatform.portfolio.savings.SavingsApiConstants.groupIdParamName;
import static org.mifosplatform.portfolio.savings.SavingsApiConstants.interestCalculationDaysInYearTypeParamName;
import static org.mifosplatform.portfolio.savings.SavingsApiConstants.interestCalculationTypeParamName;
import static org.mifosplatform.portfolio.savings.SavingsApiConstants.interestCompoundingPeriodTypeParamName;
import static org.mifosplatform.portfolio.savings.SavingsApiConstants.interestPostingPeriodTypeParamName;
import static org.mifosplatform.portfolio.savings.SavingsApiConstants.lockinPeriodFrequencyParamName;
import static org.mifosplatform.portfolio.savings.SavingsApiConstants.lockinPeriodFrequencyTypeParamName;
import static org.mifosplatform.portfolio.savings.SavingsApiConstants.minRequiredOpeningBalanceParamName;
import static org.mifosplatform.portfolio.savings.SavingsApiConstants.nominalAnnualInterestRateParamName;
import static org.mifosplatform.portfolio.savings.SavingsApiConstants.productIdParamName;
import static org.mifosplatform.portfolio.savings.SavingsApiConstants.submittedOnDateParamName;
import static org.mifosplatform.portfolio.savings.SavingsApiConstants.withdrawalFeeAmountParamName;
import static org.mifosplatform.portfolio.savings.SavingsApiConstants.withdrawalFeeTypeParamName;

import java.math.BigDecimal;

import org.joda.time.LocalDate;
import org.joda.time.MonthDay;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.organisation.staff.domain.Staff;
import org.mifosplatform.organisation.staff.domain.StaffRepositoryWrapper;
import org.mifosplatform.portfolio.client.domain.Client;
import org.mifosplatform.portfolio.client.domain.ClientRepositoryWrapper;
import org.mifosplatform.portfolio.group.domain.Group;
import org.mifosplatform.portfolio.group.domain.GroupRepositoryWrapper;
import org.mifosplatform.portfolio.savings.SavingsCompoundingInterestPeriodType;
import org.mifosplatform.portfolio.savings.SavingsInterestCalculationDaysInYearType;
import org.mifosplatform.portfolio.savings.SavingsInterestCalculationType;
import org.mifosplatform.portfolio.savings.SavingsPeriodFrequencyType;
import org.mifosplatform.portfolio.savings.SavingsPostingInterestPeriodType;
import org.mifosplatform.portfolio.savings.SavingsWithdrawalFeesType;
import org.mifosplatform.portfolio.savings.exception.SavingsProductNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonElement;

@Service
public class SavingsAccountAssembler {

    private final SavingsAccountTransactionSummaryWrapper savingsAccountTransactionSummaryWrapper;
    private final SavingsHelper savingsHelper = new SavingsHelper();
    private final ClientRepositoryWrapper clientRepository;
    private final GroupRepositoryWrapper groupRepository;
    private final StaffRepositoryWrapper staffRepository;
    private final SavingsProductRepository savingProductRepository;
    private final SavingsAccountRepositoryWrapper savingsAccountRepository;
    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public SavingsAccountAssembler(final SavingsAccountTransactionSummaryWrapper savingsAccountTransactionSummaryWrapper,
            final ClientRepositoryWrapper clientRepository, final GroupRepositoryWrapper groupRepository,
            final StaffRepositoryWrapper staffRepository, final SavingsProductRepository savingProductRepository,
            final SavingsAccountRepositoryWrapper savingsAccountRepository,
            final FromJsonHelper fromApiJsonHelper) {
        this.savingsAccountTransactionSummaryWrapper = savingsAccountTransactionSummaryWrapper;
        this.clientRepository = clientRepository;
        this.groupRepository = groupRepository;
        this.staffRepository = staffRepository;
        this.savingProductRepository = savingProductRepository;
        this.savingsAccountRepository = savingsAccountRepository;
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    /**
     * Assembles a new {@link SavingsAccount} from JSON details passed in
     * request inheriting details where relevant from chosen
     * {@link SavingsProduct}.
     */
    public SavingsAccount assembleFrom(final JsonCommand command) {

        final JsonElement element = command.parsedJson();

        final String accountNo = fromApiJsonHelper.extractStringNamed(accountNoParamName, element);
        final String externalId = fromApiJsonHelper.extractStringNamed(externalIdParamName, element);
        final Long productId = fromApiJsonHelper.extractLongNamed(productIdParamName, element);

        final SavingsProduct product = this.savingProductRepository.findOne(productId);
        if (product == null) { throw new SavingsProductNotFoundException(productId); }

        Client client = null;
        Group group = null;
        Staff fieldOfficer = null;

        final Long clientId = fromApiJsonHelper.extractLongNamed(clientIdParamName, element);
        if (clientId != null) {
            client = this.clientRepository.findOneWithNotFoundDetection(clientId);
        }

        final Long groupId = fromApiJsonHelper.extractLongNamed(groupIdParamName, element);
        if (groupId != null) {
            group = this.groupRepository.findOneWithNotFoundDetection(groupId);
        }

        final Long fieldOfficerId = fromApiJsonHelper.extractLongNamed(fieldOfficerIdParamName, element);
        if (fieldOfficerId != null) {
            fieldOfficer = this.staffRepository.findOneWithNotFoundDetection(fieldOfficerId);
        }

        final LocalDate submittedOnDate = fromApiJsonHelper.extractLocalDateNamed(submittedOnDateParamName, element);

        BigDecimal interestRate = null;
        if (command.parameterExists(nominalAnnualInterestRateParamName)) {
            interestRate = command.bigDecimalValueOfParameterNamed(nominalAnnualInterestRateParamName);
        } else {
            interestRate = product.nominalAnnualInterestRate();
        }

        SavingsCompoundingInterestPeriodType interestCompoundingPeriodType = null;
        final Integer interestPeriodTypeValue = command.integerValueOfParameterNamed(interestCompoundingPeriodTypeParamName);
        if (interestPeriodTypeValue != null) {
            interestCompoundingPeriodType = SavingsCompoundingInterestPeriodType.fromInt(interestPeriodTypeValue);
        } else {
            interestCompoundingPeriodType = product.interestCompoundingPeriodType();
        }

        SavingsPostingInterestPeriodType interestPostingPeriodType = null;
        final Integer interestPostingPeriodTypeValue = command.integerValueOfParameterNamed(interestPostingPeriodTypeParamName);
        if (interestPostingPeriodTypeValue != null) {
            interestPostingPeriodType = SavingsPostingInterestPeriodType.fromInt(interestPostingPeriodTypeValue);
        } else {
            interestPostingPeriodType = product.interestPostingPeriodType();
        }

        SavingsInterestCalculationType interestCalculationType = null;
        final Integer interestCalculationTypeValue = command.integerValueOfParameterNamed(interestCalculationTypeParamName);
        if (interestCalculationTypeValue != null) {
            interestCalculationType = SavingsInterestCalculationType.fromInt(interestCalculationTypeValue);
        } else {
            interestCalculationType = product.interestCalculationType();
        }

        SavingsInterestCalculationDaysInYearType interestCalculationDaysInYearType = null;
        final Integer interestCalculationDaysInYearTypeValue = command
                .integerValueOfParameterNamed(interestCalculationDaysInYearTypeParamName);
        if (interestCalculationDaysInYearTypeValue != null) {
            interestCalculationDaysInYearType = SavingsInterestCalculationDaysInYearType.fromInt(interestCalculationDaysInYearTypeValue);
        } else {
            interestCalculationDaysInYearType = product.interestCalculationDaysInYearType();
        }

        BigDecimal minRequiredOpeningBalance = null;
        if (command.parameterExists(minRequiredOpeningBalanceParamName)) {
            minRequiredOpeningBalance = command.bigDecimalValueOfParameterNamed(minRequiredOpeningBalanceParamName);
        } else {
            minRequiredOpeningBalance = product.minRequiredOpeningBalance();
        }

        Integer lockinPeriodFrequency = null;
        if (command.parameterExists(lockinPeriodFrequencyParamName)) {
            lockinPeriodFrequency = command.integerValueOfParameterNamed(lockinPeriodFrequencyParamName);
        } else {
            lockinPeriodFrequency = product.lockinPeriodFrequency();
        }

        SavingsPeriodFrequencyType lockinPeriodFrequencyType = null;
        Integer lockinPeriodFrequencyTypeValue = null;
        if (command.parameterExists(lockinPeriodFrequencyTypeParamName)) {
            lockinPeriodFrequencyTypeValue = command.integerValueOfParameterNamed(lockinPeriodFrequencyTypeParamName);
            if (lockinPeriodFrequencyTypeValue != null) {
                lockinPeriodFrequencyType = SavingsPeriodFrequencyType.fromInt(lockinPeriodFrequencyTypeValue);
            }
        } else {
            lockinPeriodFrequencyType = product.lockinPeriodFrequencyType();
        }

        BigDecimal withdrawalFeeAmount = null;
        if (command.parameterExists(withdrawalFeeAmountParamName)) {
            withdrawalFeeAmount = command.bigDecimalValueOfParameterNamed(withdrawalFeeAmountParamName);
        } else {
            withdrawalFeeAmount = product.withdrawalFeeAmount();
        }

        SavingsWithdrawalFeesType withdrawalFeeType = null;
        if (command.parameterExists(withdrawalFeeAmountParamName)) {
            final Integer withdrawalFeeTypeValue = command.integerValueOfParameterNamed(withdrawalFeeTypeParamName);
            if (withdrawalFeeTypeValue != null) {
                withdrawalFeeType = SavingsWithdrawalFeesType.fromInt(withdrawalFeeTypeValue);
            }
        } else {
            withdrawalFeeType = product.withdrawalFeeType();
        }

        BigDecimal annualFeeAmount = null;
        if (command.parameterExists(annualFeeAmountParamName)) {
            annualFeeAmount = command.bigDecimalValueOfParameterNamed(annualFeeAmountParamName);
        } else {
            annualFeeAmount = product.annualFeeAmount();
        }

        MonthDay monthDayOfAnnualFee = null;
        if (command.parameterExists(annualFeeOnMonthDayParamName)) {
            monthDayOfAnnualFee = command.extractMonthDayNamed(annualFeeOnMonthDayParamName);
        } else {
            monthDayOfAnnualFee = product.monthDayOfAnnualFee();
        }

        final SavingsAccount account = SavingsAccount.createNewApplicationForSubmittal(client, group, product, fieldOfficer, accountNo, externalId,
                submittedOnDate, interestRate, interestCompoundingPeriodType, interestPostingPeriodType, interestCalculationType,
                interestCalculationDaysInYearType, minRequiredOpeningBalance, lockinPeriodFrequency, lockinPeriodFrequencyType,
                withdrawalFeeAmount, withdrawalFeeType, annualFeeAmount, monthDayOfAnnualFee);
        account.setHelpers(this.savingsAccountTransactionSummaryWrapper, this.savingsHelper);

        account.validateNewApplicationState(DateUtils.getLocalDateOfTenant());

        return account;
    }

    public SavingsAccount assembleFrom(final Long savingsId) {
        SavingsAccount account = this.savingsAccountRepository.findOneWithNotFoundDetection(savingsId);
        account.setHelpers(this.savingsAccountTransactionSummaryWrapper, savingsHelper);

        return account;
    }
}
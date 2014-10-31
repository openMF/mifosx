/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.loanaccount.serialization;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.data.ApiParameterError;
import org.mifosplatform.infrastructure.core.data.DataValidatorBuilder;
import org.mifosplatform.infrastructure.core.exception.InvalidJsonException;
import org.mifosplatform.infrastructure.core.exception.PlatformApiDataValidationException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.portfolio.calendar.domain.Calendar;
import org.mifosplatform.portfolio.calendar.domain.CalendarInstance;
import org.mifosplatform.portfolio.calendar.exception.NotValidRecurringDateException;
import org.mifosplatform.portfolio.calendar.service.CalendarUtils;
import org.mifosplatform.portfolio.loanaccount.api.LoanApiConstants;
import org.mifosplatform.portfolio.loanaccount.domain.LoanDisbursementDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

@Component
public final class LoanEventApiJsonValidator {

    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public LoanEventApiJsonValidator(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
                "Validation errors exist.", dataValidationErrors); }
    }

    public void validateDisbursement(final String json, boolean isAccountTransfer) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        Set<String> disbursementParameters = null;

        if (isAccountTransfer) {
            disbursementParameters = new HashSet<>(Arrays.asList("actualDisbursementDate", "externalId", "note", "locale",
                    "dateFormat", LoanApiConstants.principalDisbursedParameterName, LoanApiConstants.emiAmountParameterName));
        } else {
            disbursementParameters = new HashSet<>(Arrays.asList("actualDisbursementDate", "externalId", "note", "locale",
                    "dateFormat", "paymentTypeId", "accountNumber", "checkNumber", "routingCode", "receiptNumber", "bankNumber",
                    LoanApiConstants.principalDisbursedParameterName, LoanApiConstants.emiAmountParameterName));
        }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, disbursementParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("loan.disbursement");

        final JsonElement element = this.fromApiJsonHelper.parse(json);
        final LocalDate actualDisbursementDate = this.fromApiJsonHelper.extractLocalDateNamed("actualDisbursementDate", element);
        baseDataValidator.reset().parameter("actualDisbursementDate").value(actualDisbursementDate).notNull();

        final String note = this.fromApiJsonHelper.extractStringNamed("note", element);
        baseDataValidator.reset().parameter("note").value(note).notExceedingLengthOf(1000);

        final BigDecimal principal = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(
                LoanApiConstants.principalDisbursedParameterName, element);
        baseDataValidator.reset().parameter(LoanApiConstants.principalDisbursedParameterName).value(principal).ignoreIfNull()
                .positiveAmount();

        final BigDecimal emiAmount = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(LoanApiConstants.emiAmountParameterName,
                element);
        baseDataValidator.reset().parameter(LoanApiConstants.emiAmountParameterName).value(emiAmount).ignoreIfNull().positiveAmount()
                .notGreaterThanMax(principal);

        validatePaymentDetails(baseDataValidator, element);

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    public void validateDisbursementDateWithMeetingDate(final LocalDate actualDisbursementDate, final CalendarInstance calendarInstance) {
        if (null != calendarInstance) {
            final Calendar calendar = calendarInstance.getCalendar();
            if (!calendar.isValidRecurringDate(actualDisbursementDate)) {
                // Disbursement date should fall on a meeting date
                final String errorMessage = "Expected disbursement date '" + actualDisbursementDate.toString()
                        + "' does not fall on a meeting date.";
                throw new NotValidRecurringDateException("loan.actual.disbursement.date", errorMessage, actualDisbursementDate.toString(),
                        calendar.getTitle());
            }
        }
    }

    public void validateTransaction(final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Set<String> transactionParameters = new HashSet<>(Arrays.asList("transactionDate", "transactionAmount", "externalId",
                "note", "locale", "dateFormat", "paymentTypeId", "accountNumber", "checkNumber", "routingCode", "receiptNumber",
                "bankNumber"));

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, transactionParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("loan.transaction");

        final JsonElement element = this.fromApiJsonHelper.parse(json);
        final LocalDate transactionDate = this.fromApiJsonHelper.extractLocalDateNamed("transactionDate", element);
        baseDataValidator.reset().parameter("transactionDate").value(transactionDate).notNull();

        final BigDecimal transactionAmount = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed("transactionAmount", element);
        baseDataValidator.reset().parameter("transactionAmount").value(transactionAmount).notNull().zeroOrPositiveAmount();

        final String note = this.fromApiJsonHelper.extractStringNamed("note", element);
        baseDataValidator.reset().parameter("note").value(note).notExceedingLengthOf(1000);

        validatePaymentDetails(baseDataValidator, element);
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    public void validateNewRepaymentTransaction(final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Set<String> transactionParameters = new HashSet<>(Arrays.asList("transactionDate", "transactionAmount", "externalId",
                "note", "locale", "dateFormat", "paymentTypeId", "accountNumber", "checkNumber", "routingCode", "receiptNumber",
                "bankNumber"));

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, transactionParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("loan.transaction");

        final JsonElement element = this.fromApiJsonHelper.parse(json);
        final LocalDate transactionDate = this.fromApiJsonHelper.extractLocalDateNamed("transactionDate", element);
        baseDataValidator.reset().parameter("transactionDate").value(transactionDate).notNull();

        final BigDecimal transactionAmount = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed("transactionAmount", element);
        baseDataValidator.reset().parameter("transactionAmount").value(transactionAmount).notNull().positiveAmount();

        final String note = this.fromApiJsonHelper.extractStringNamed("note", element);
        baseDataValidator.reset().parameter("note").value(note).notExceedingLengthOf(1000);

        validatePaymentDetails(baseDataValidator, element);
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    public void validateRepaymentDateWithMeetingDate(final LocalDate repaymentDate, final CalendarInstance calendarInstance) {
        if (null != calendarInstance) {
            final Calendar calendar = calendarInstance.getCalendar();
            if (calendar != null && repaymentDate != null) {
                // Disbursement date should fall on a meeting date
                if (!CalendarUtils.isValidRedurringDate(calendar.getRecurrence(), calendar.getStartDateLocalDate(), repaymentDate)) {
                    final String errorMessage = "Transaction date '" + repaymentDate.toString() + "' does not fall on a meeting date.";
                    throw new NotValidRecurringDateException("loan.transaction.date", errorMessage, repaymentDate.toString(),
                            calendar.getTitle());
                }

            }
        }
    }

    private void validatePaymentDetails(final DataValidatorBuilder baseDataValidator, final JsonElement element) {
        // Validate all string payment detail fields for max length
        final Integer paymentTypeId = this.fromApiJsonHelper.extractIntegerWithLocaleNamed("paymentTypeId", element);
        baseDataValidator.reset().parameter("paymentTypeId").value(paymentTypeId).ignoreIfNull().integerGreaterThanZero();
        final Set<String> paymentDetailParameters = new HashSet<>(Arrays.asList("accountNumber", "checkNumber", "routingCode",
                "receiptNumber", "bankNumber"));
        for (final String paymentDetailParameterName : paymentDetailParameters) {
            final String paymentDetailParameterValue = this.fromApiJsonHelper.extractStringNamed(paymentDetailParameterName, element);
            baseDataValidator.reset().parameter(paymentDetailParameterName).value(paymentDetailParameterValue).ignoreIfNull()
                    .notExceedingLengthOf(50);
        }
    }

    public void validateTransactionWithNoAmount(final String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Set<String> disbursementParameters = new HashSet<>(Arrays.asList("transactionDate", "note", "locale", "dateFormat"));

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, disbursementParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("loan.transaction");

        final JsonElement element = this.fromApiJsonHelper.parse(json);
        final LocalDate transactionDate = this.fromApiJsonHelper.extractLocalDateNamed("transactionDate", element);
        baseDataValidator.reset().parameter("transactionDate").value(transactionDate).notNull();

        final String note = this.fromApiJsonHelper.extractStringNamed("note", element);
        baseDataValidator.reset().parameter("note").value(note).notExceedingLengthOf(1000);

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    public void validateAddLoanCharge(final String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Set<String> disbursementParameters = new HashSet<>(Arrays.asList("chargeId", "amount", "dueDate", "locale",
                "dateFormat"));

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, disbursementParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("loanCharge");

        final JsonElement element = this.fromApiJsonHelper.parse(json);
        final Long chargeId = this.fromApiJsonHelper.extractLongNamed("chargeId", element);
        baseDataValidator.reset().parameter("chargeId").value(chargeId).notNull().integerGreaterThanZero();

        final BigDecimal amount = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed("amount", element);
        baseDataValidator.reset().parameter("amount").value(amount).notNull().positiveAmount();

        if (this.fromApiJsonHelper.parameterExists("dueDate", element)) {
            this.fromApiJsonHelper.extractLocalDateNamed("dueDate", element);
        }

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    public void validateUpdateOfLoanCharge(final String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Set<String> disbursementParameters = new HashSet<>(Arrays.asList("amount", "dueDate", "locale", "dateFormat"));

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, disbursementParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("loanCharge");

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final BigDecimal amount = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed("amount", element);
        baseDataValidator.reset().parameter("amount").value(amount).notNull().positiveAmount();

        if (this.fromApiJsonHelper.parameterExists("dueDate", element)) {
            this.fromApiJsonHelper.extractLocalDateNamed("dueDate", element);
        }

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    public void validateUpdateOfLoanOfficer(final String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Set<String> disbursementParameters = new HashSet<>(Arrays.asList("assignmentDate", "fromLoanOfficerId",
                "toLoanOfficerId", "locale", "dateFormat"));

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, disbursementParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("loanOfficer");

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final Long toLoanOfficerId = this.fromApiJsonHelper.extractLongNamed("toLoanOfficerId", element);
        baseDataValidator.reset().parameter("toLoanOfficerId").value(toLoanOfficerId).notNull().integerGreaterThanZero();

        final String assignmentDateStr = this.fromApiJsonHelper.extractStringNamed("assignmentDate", element);
        baseDataValidator.reset().parameter("assignmentDate").value(assignmentDateStr).notBlank();

        if (!StringUtils.isBlank(assignmentDateStr)) {
            final LocalDate assignmentDate = this.fromApiJsonHelper.extractLocalDateNamed("assignmentDate", element);
            baseDataValidator.reset().parameter("assignmentDate").value(assignmentDate).notNull();
        }

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    public void validateForBulkLoanReassignment(final String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Set<String> supportedParameters = new HashSet<>(Arrays.asList("assignmentDate", "fromLoanOfficerId", "toLoanOfficerId",
                "loans", "locale", "dateFormat"));

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("loanOfficer");

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final LocalDate assignmentDate = this.fromApiJsonHelper.extractLocalDateNamed("assignmentDate", element);
        baseDataValidator.reset().parameter("assignmentDate").value(assignmentDate).notNull();
        final Long fromLoanOfficerId = this.fromApiJsonHelper.extractLongNamed("fromLoanOfficerId", element);
        baseDataValidator.reset().parameter("fromLoanOfficerId").value(fromLoanOfficerId).notNull().longGreaterThanZero();
        final Long toLoanOfficerId = this.fromApiJsonHelper.extractLongNamed("toLoanOfficerId", element);
        baseDataValidator.reset().parameter("toLoanOfficerId").value(toLoanOfficerId).notNull().longGreaterThanZero();
        final String[] loans = this.fromApiJsonHelper.extractArrayNamed("loans", element);
        baseDataValidator.reset().parameter("loans").value(loans).arrayNotEmpty();

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    public void validateChargePaymentTransaction(final String json, final boolean isChargeIdIncluded) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }
        Set<String> transactionParameters = null;
        if (isChargeIdIncluded) {
            transactionParameters = new HashSet<>(Arrays.asList("transactionDate", "locale", "dateFormat", "chargeId", "dueDate",
                    "installmentNumber"));
        } else {
            transactionParameters = new HashSet<>(Arrays.asList("transactionDate", "locale", "dateFormat", "dueDate",
                    "installmentNumber"));
        }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, transactionParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource("loan.charge.payment.transaction");

        final JsonElement element = this.fromApiJsonHelper.parse(json);
        final LocalDate transactionDate = this.fromApiJsonHelper.extractLocalDateNamed("transactionDate", element);
        if (isChargeIdIncluded) {
            final Long chargeId = this.fromApiJsonHelper.extractLongNamed("chargeId", element);
            baseDataValidator.reset().parameter("chargeId").value(chargeId).notNull().integerGreaterThanZero();
        }
        baseDataValidator.reset().parameter("transactionDate").value(transactionDate).notNull();
        final Integer installmentNumber = this.fromApiJsonHelper.extractIntegerWithLocaleNamed("installmentNumber", element);
        baseDataValidator.reset().parameter("installmentNumber").value(installmentNumber).ignoreIfNull().integerGreaterThanZero();

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    public void validateInstallmentChargeTransaction(final String json) {

        if (StringUtils.isBlank(json)) { return; }
        Set<String> transactionParameters = new HashSet<>(Arrays.asList("dueDate", "locale", "dateFormat", "installmentNumber"));

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, transactionParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource("loan.charge.waive.transaction");

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final Integer installmentNumber = this.fromApiJsonHelper.extractIntegerWithLocaleNamed("installmentNumber", element);
        baseDataValidator.reset().parameter("installmentNumber").value(installmentNumber).ignoreIfNull().integerGreaterThanZero();
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    public void validateUpdateDisbursementDate(final String json, LoanDisbursementDetails loanDisbursementDetails) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Set<String> disbursementParameters = new HashSet<>(Arrays.asList(LoanApiConstants.disbursementDateParameterName,
                "locale", "dateFormat"));

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, disbursementParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("loan.update.disbursement");

        final JsonElement element = this.fromApiJsonHelper.parse(json);
        final LocalDate actualDisbursementDate = this.fromApiJsonHelper.extractLocalDateNamed(
                LoanApiConstants.disbursementDateParameterName, element);
        baseDataValidator.reset().parameter(LoanApiConstants.disbursementDateParameterName).value(actualDisbursementDate).notNull();

        if (loanDisbursementDetails.actualDisbursementDate() != null) {
            baseDataValidator.reset().parameter(LoanApiConstants.disbursementDateParameterName)
                    .failWithCode(LoanApiConstants.ALREADY_DISBURSED);
        }

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }
    
    public void validateNewRefundTransaction(final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Set<String> transactionParameters = new HashSet<String>(Arrays.asList("transactionDate", "transactionAmount", "externalId",
                "note", "locale", "dateFormat", "paymentTypeId", "accountNumber", "checkNumber", "routingCode", "receiptNumber",
                "bankNumber"));

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, transactionParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("loan.transaction");

        final JsonElement element = this.fromApiJsonHelper.parse(json);
        final LocalDate transactionDate = this.fromApiJsonHelper.extractLocalDateNamed("transactionDate", element);
        baseDataValidator.reset().parameter("transactionDate").value(transactionDate).notNull();

        final BigDecimal transactionAmount = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed("transactionAmount", element);
        baseDataValidator.reset().parameter("transactionAmount").value(transactionAmount).notNull().positiveAmount();

        final String note = this.fromApiJsonHelper.extractStringNamed("note", element);
        baseDataValidator.reset().parameter("note").value(note).notExceedingLengthOf(1000);

        validatePaymentDetails(baseDataValidator, element);
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

}
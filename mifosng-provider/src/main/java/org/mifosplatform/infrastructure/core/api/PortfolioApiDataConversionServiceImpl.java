package org.mifosplatform.infrastructure.core.api;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.mifosplatform.infrastructure.core.data.ApiParameterError;
import org.mifosplatform.infrastructure.core.exception.InvalidJsonException;
import org.mifosplatform.infrastructure.core.exception.PlatformApiDataValidationException;
import org.mifosplatform.infrastructure.core.exception.UnsupportedParameterException;
import org.mifosplatform.infrastructure.core.serialization.JsonParserHelper;
import org.mifosplatform.organisation.staff.command.BulkTransferLoanOfficerCommand;
import org.mifosplatform.portfolio.client.command.ClientCommand;
import org.mifosplatform.portfolio.client.command.NoteCommand;
import org.mifosplatform.portfolio.client.data.ClientData;
import org.mifosplatform.portfolio.client.serialization.ClientCommandFromApiJsonDeserializer;
import org.mifosplatform.portfolio.group.command.GroupCommand;
import org.mifosplatform.portfolio.loanaccount.command.AdjustLoanTransactionCommand;
import org.mifosplatform.portfolio.loanaccount.command.GroupLoanApplicationCommand;
import org.mifosplatform.portfolio.loanaccount.command.GroupLoanChargeCommand;
import org.mifosplatform.portfolio.loanaccount.command.LoanApplicationCommand;
import org.mifosplatform.portfolio.loanaccount.command.LoanChargeCommand;
import org.mifosplatform.portfolio.loanaccount.command.LoanStateTransitionCommand;
import org.mifosplatform.portfolio.loanaccount.command.LoanTransactionCommand;
import org.mifosplatform.portfolio.loanaccount.command.MemberLoanChargeCommand;
import org.mifosplatform.portfolio.loanaccount.command.MemberLoanCommand;
import org.mifosplatform.portfolio.loanaccount.gaurantor.command.GuarantorCommand;
import org.mifosplatform.portfolio.savingsaccount.command.SavingAccountCommand;
import org.mifosplatform.portfolio.savingsaccountproduct.command.SavingProductCommand;
import org.mifosplatform.portfolio.savingsdepositaccount.command.DepositAccountCommand;
import org.mifosplatform.portfolio.savingsdepositaccount.command.DepositAccountWithdrawInterestCommand;
import org.mifosplatform.portfolio.savingsdepositaccount.command.DepositAccountWithdrawalCommand;
import org.mifosplatform.portfolio.savingsdepositaccount.command.DepositStateTransitionApprovalCommand;
import org.mifosplatform.portfolio.savingsdepositaccount.command.DepositStateTransitionCommand;
import org.mifosplatform.portfolio.savingsdepositproduct.command.DepositProductCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.number.NumberFormatter;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

@Service
public class PortfolioApiDataConversionServiceImpl implements PortfolioApiDataConversionService {

    /**
     * Google-gson class for converting to and from json.
     */
    private final Gson gsonConverter;

    private final ClientCommandFromApiJsonDeserializer clientCommandFromApiJsonDeserializer;

    @Autowired
    public PortfolioApiDataConversionServiceImpl(final ClientCommandFromApiJsonDeserializer clientCommandFromApiJsonDeserializer) {
        this.clientCommandFromApiJsonDeserializer = clientCommandFromApiJsonDeserializer;
        this.gsonConverter = new Gson();
    }

    @Override
    public BulkTransferLoanOfficerCommand convertJsonToLoanReassignmentCommand(Long resourceIdentifier, String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        Type typeOfMap = new TypeToken<Map<String, String>>() {}.getType();
        Map<String, String> requestMap = gsonConverter.fromJson(json, typeOfMap);

        Set<String> supportedParams = new HashSet<String>(Arrays.asList("fromLoanOfficerId", "toLoanOfficerId", "assignmentDate", "locale",
                "dateFormat"));

        checkForUnsupportedParameters(requestMap, supportedParams);

        Set<String> modifiedParameters = new HashSet<String>();

        Long fromLoanOfficerId = extractLongParameter("fromLoanOfficerId", requestMap, modifiedParameters);
        Long toLoanOfficerId = extractLongParameter("toLoanOfficerId", requestMap, modifiedParameters);
        LocalDate assignmentDate = extractLocalDateParameter("assignmentDate", requestMap, modifiedParameters);

        return new BulkTransferLoanOfficerCommand(resourceIdentifier, fromLoanOfficerId, toLoanOfficerId, assignmentDate);
    }

    @Override
    public BulkTransferLoanOfficerCommand convertJsonToBulkLoanReassignmentCommand(final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        Map<String, String> requestMap = gsonConverter.fromJson(json, typeOfMap);

        Set<String> supportedParams = new HashSet<String>(Arrays.asList("fromLoanOfficerId", "toLoanOfficerId", "assignmentDate", "locale",
                "dateFormat", "loans"));

        checkForUnsupportedParameters(requestMap, supportedParams);

        Set<String> modifiedParameters = new HashSet<String>();

        Long fromLoanOfficerId = extractLongParameter("fromLoanOfficerId", requestMap, modifiedParameters);
        Long toLoanOfficerId = extractLongParameter("toLoanOfficerId", requestMap, modifiedParameters);
        LocalDate assignmentDate = extractLocalDateParameter("assignmentDate", requestMap, modifiedParameters);

        // check array
        JsonParser parser = new JsonParser();

        String[] loans = null;
        JsonElement element = parser.parse(json);
        if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            if (object.has("loans")) {
                modifiedParameters.add("loans");
                JsonArray array = object.get("loans").getAsJsonArray();
                loans = new String[array.size()];
                for (int i = 0; i < array.size(); i++) {
                    loans[i] = array.get(i).getAsString();
                }
            }
        }
        //

        return new BulkTransferLoanOfficerCommand(fromLoanOfficerId, toLoanOfficerId, assignmentDate, loans);
    }

    @Override
    public ClientData convertInternalJsonFormatToClientDataChange(final Long resourceIdentifier, final String json) {

        final ClientCommand command = this.clientCommandFromApiJsonDeserializer.commandFromApiJson(resourceIdentifier, json);

        return ClientData.dataChangeInstance(resourceIdentifier, command.getOfficeId(), command.getExternalId(), command.getFirstname(),
                command.getLastname(), command.getClientOrBusinessName(), command.getJoiningDate());
    }

    @Override
    public GroupCommand convertJsonToGroupCommand(final Long resourceIdentifier, final String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        final Map<String, String> requestMap = gsonConverter.fromJson(json, typeOfMap);

        final Set<String> supportedParams = new HashSet<String>(Arrays.asList("name", "officeId", "externalId", "clientMembers"));

        checkForUnsupportedParameters(requestMap, supportedParams);

        final Set<String> modifiedParameters = new HashSet<String>();

        final Long officeId = extractLongParameter("officeId", requestMap, modifiedParameters);
        final String externalId = extractStringParameter("externalId", requestMap, modifiedParameters);
        final String name = extractStringParameter("name", requestMap, modifiedParameters);

        // check array
        final JsonParser parser = new JsonParser();

        String[] clientMembers = null;
        JsonElement element = parser.parse(json);
        if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            if (object.has("clientMembers")) {
                modifiedParameters.add("clientMembers");
                JsonArray array = object.get("clientMembers").getAsJsonArray();
                clientMembers = new String[array.size()];
                for (int i = 0; i < array.size(); i++) {
                    clientMembers[i] = array.get(i).getAsString();
                }
            }
        }
        //

        return new GroupCommand(modifiedParameters, resourceIdentifier, externalId, name, officeId, clientMembers);
    }

    @Override
    public LoanApplicationCommand convertApiRequestJsonToLoanApplicationCommand(final Long resourceIdentifier, final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        final Map<String, Object> requestMap = gsonConverter.fromJson(json, typeOfMap);

        final Set<String> supportedParams = new HashSet<String>(Arrays.asList("clientId", "groupId", "productId", "externalId", "fundId",
                "transactionProcessingStrategyId", "principal", "inArrearsTolerance", "interestRatePerPeriod", "repaymentEvery",
                "numberOfRepayments", "loanTermFrequency", "loanTermFrequencyType", "charges", "repaymentFrequencyType",
                "interestRateFrequencyType", "amortizationType", "interestType", "interestCalculationPeriodType",
                "expectedDisbursementDate", "repaymentsStartingFromDate", "interestChargedFromDate", "submittedOnDate", "submittedOnNote",
                "locale", "dateFormat", "loanOfficerId", "id"));

        checkForUnsupportedParameters(requestMap, supportedParams);

        final Set<String> parametersPassedInCommand = new HashSet<String>();

        final JsonParser parser = new JsonParser();
        final JsonElement element = parser.parse(json);
        final JsonParserHelper helper = new JsonParserHelper();

        final Long clientId = helper.extractLongNamed("clientId", element, parametersPassedInCommand);
        final Long groupId = helper.extractLongNamed("groupId", element, parametersPassedInCommand);
        final Long productId = helper.extractLongNamed("productId", element, parametersPassedInCommand);
        final Long fundId = helper.extractLongNamed("fundId", element, parametersPassedInCommand);
        final Long loanOfficerId = helper.extractLongNamed("loanOfficerId", element, parametersPassedInCommand);
        final Long transactionProcessingStrategyId = helper.extractLongNamed("transactionProcessingStrategyId", element,
                parametersPassedInCommand);
        final String externalId = helper.extractStringNamed("externalId", element, parametersPassedInCommand);
        final BigDecimal principal = helper.extractBigDecimalWithLocaleNamed("principal", element, parametersPassedInCommand);
        final BigDecimal inArrearsToleranceValue = helper.extractBigDecimalWithLocaleNamed("inArrearsTolerance", element,
                parametersPassedInCommand);
        final BigDecimal interestRatePerPeriod = helper.extractBigDecimalWithLocaleNamed("interestRatePerPeriod", element,
                parametersPassedInCommand);

        final Integer repaymentEvery = helper.extractIntegerWithLocaleNamed("repaymentEvery", element, parametersPassedInCommand);
        final Integer numberOfRepayments = helper.extractIntegerWithLocaleNamed("numberOfRepayments", element, parametersPassedInCommand);
        final Integer repaymentFrequencyType = helper.extractIntegerWithLocaleNamed("repaymentFrequencyType", element,
                parametersPassedInCommand);
        final Integer loanTermFrequency = helper.extractIntegerWithLocaleNamed("loanTermFrequency", element, parametersPassedInCommand);
        final Integer loanTermFrequencyType = helper.extractIntegerWithLocaleNamed("loanTermFrequencyType", element,
                parametersPassedInCommand);
        final Integer interestRateFrequencyType = helper.extractIntegerWithLocaleNamed("interestRateFrequencyType", element,
                parametersPassedInCommand);
        final Integer amortizationType = helper.extractIntegerWithLocaleNamed("amortizationType", element, parametersPassedInCommand);
        final Integer interestType = helper.extractIntegerWithLocaleNamed("interestType", element, parametersPassedInCommand);
        final Integer interestCalculationPeriodType = helper.extractIntegerWithLocaleNamed("interestCalculationPeriodType", element,
                parametersPassedInCommand);

        final LocalDate expectedDisbursementDate = helper.extractLocalDateNamed("expectedDisbursementDate", element,
                parametersPassedInCommand);
        final LocalDate repaymentsStartingFromDate = helper.extractLocalDateNamed("repaymentsStartingFromDate", element,
                parametersPassedInCommand);
        final LocalDate interestChargedFromDate = helper.extractLocalDateNamed("interestChargedFromDate", element,
                parametersPassedInCommand);
        final LocalDate submittedOnDate = helper.extractLocalDateNamed("submittedOnDate", element, parametersPassedInCommand);

        final String submittedOnNote = helper.extractStringNamed("submittedOnNote", element, parametersPassedInCommand);

        LoanChargeCommand[] charges = null;
        if (element.isJsonObject()) {
            final JsonObject topLevelJsonElement = element.getAsJsonObject();
            final String dateFormat = helper.extractDateFormatParameter(topLevelJsonElement);
            final Locale locale = helper.extractLocaleParameter(topLevelJsonElement);
            if (topLevelJsonElement.has("charges") && topLevelJsonElement.get("charges").isJsonArray()) {

                parametersPassedInCommand.add("charges");
                final JsonArray array = topLevelJsonElement.get("charges").getAsJsonArray();
                charges = new LoanChargeCommand[array.size()];
                for (int i = 0; i < array.size(); i++) {

                    final JsonObject loanChargeElement = array.get(i).getAsJsonObject();
                    final Set<String> parametersPassedInForChargesCommand = new HashSet<String>();

                    final Long id = helper.extractLongNamed("id", loanChargeElement, parametersPassedInForChargesCommand);
                    final Long chargeId = helper.extractLongNamed("chargeId", loanChargeElement, parametersPassedInForChargesCommand);
                    final BigDecimal amount = helper.extractBigDecimalNamed("amount", loanChargeElement, locale,
                            parametersPassedInForChargesCommand);
                    final Integer chargeTimeType = helper.extractIntegerNamed("chargeTimeType", loanChargeElement, locale,
                            parametersPassedInForChargesCommand);
                    final Integer chargeCalculationType = helper.extractIntegerNamed("chargeCalculationType", loanChargeElement, locale,
                            parametersPassedInForChargesCommand);
                    final LocalDate specifiedDueDate = helper.extractLocalDateNamed("specifiedDueDate", loanChargeElement, dateFormat,
                            locale, parametersPassedInForChargesCommand);

                    charges[i] = new LoanChargeCommand(parametersPassedInForChargesCommand, id, null, chargeId, amount, chargeTimeType,
                            chargeCalculationType, specifiedDueDate);
                }
            }
        }

        return new LoanApplicationCommand(parametersPassedInCommand, resourceIdentifier, clientId, groupId, productId, externalId, fundId,
                transactionProcessingStrategyId, submittedOnDate, submittedOnNote, expectedDisbursementDate, repaymentsStartingFromDate,
                interestChargedFromDate, principal, interestRatePerPeriod, interestRateFrequencyType, interestType,
                interestCalculationPeriodType, repaymentEvery, repaymentFrequencyType, numberOfRepayments, amortizationType,
                loanTermFrequency, loanTermFrequencyType, inArrearsToleranceValue, charges, loanOfficerId);
    }

    @Override
    public GroupLoanApplicationCommand convertJsonToGroupLoanApplicationCommand(Long resourceIdentifier, String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        final Map<String, Object> requestMap = gsonConverter.fromJson(json, typeOfMap);

        final Set<String> supportedParams = new HashSet<String>(Arrays.asList("groupId", "productId", "externalId", "fundId",
                "transactionProcessingStrategyId", "principal", "inArrearsTolerance", "interestRatePerPeriod", "repaymentEvery",
                "numberOfRepayments", "loanTermFrequency", "loanTermFrequencyType", "charges", "repaymentFrequencyType",
                "interestRateFrequencyType", "amortizationType", "interestType", "interestCalculationPeriodType",
                "expectedDisbursementDate", "repaymentsStartingFromDate", "interestChargedFromDate", "submittedOnDate", "submittedOnNote",
                "locale", "dateFormat", "loanOfficerId", "id", "memberLoans"));

        checkForUnsupportedParameters(requestMap, supportedParams);

        final Set<String> parametersPassedInCommand = new HashSet<String>();

        final JsonParser parser = new JsonParser();
        final JsonElement element = parser.parse(json);
        final JsonParserHelper helper = new JsonParserHelper();

        final Long groupId = helper.extractLongNamed("groupId", element, parametersPassedInCommand);
        final Long productId = helper.extractLongNamed("productId", element, parametersPassedInCommand);
        final Long fundId = helper.extractLongNamed("fundId", element, parametersPassedInCommand);
        final Long loanOfficerId = helper.extractLongNamed("loanOfficerId", element, parametersPassedInCommand);
        final Long transactionProcessingStrategyId = helper.extractLongNamed("transactionProcessingStrategyId", element,
                parametersPassedInCommand);
        final String externalId = helper.extractStringNamed("externalId", element, parametersPassedInCommand);
        final BigDecimal principal = helper.extractBigDecimalWithLocaleNamed("principal", element, parametersPassedInCommand);
        final BigDecimal inArrearsToleranceValue = helper.extractBigDecimalWithLocaleNamed("inArrearsTolerance", element,
                parametersPassedInCommand);
        final BigDecimal interestRatePerPeriod = helper.extractBigDecimalWithLocaleNamed("interestRatePerPeriod", element,
                parametersPassedInCommand);

        final Integer repaymentEvery = helper.extractIntegerWithLocaleNamed("repaymentEvery", element, parametersPassedInCommand);
        final Integer numberOfRepayments = helper.extractIntegerWithLocaleNamed("numberOfRepayments", element, parametersPassedInCommand);
        final Integer repaymentFrequencyType = helper.extractIntegerWithLocaleNamed("repaymentFrequencyType", element,
                parametersPassedInCommand);
        final Integer loanTermFrequency = helper.extractIntegerWithLocaleNamed("loanTermFrequency", element, parametersPassedInCommand);
        final Integer loanTermFrequencyType = helper.extractIntegerWithLocaleNamed("loanTermFrequencyType", element,
                parametersPassedInCommand);
        final Integer interestRateFrequencyType = helper.extractIntegerWithLocaleNamed("interestRateFrequencyType", element,
                parametersPassedInCommand);
        final Integer amortizationType = helper.extractIntegerWithLocaleNamed("amortizationType", element, parametersPassedInCommand);
        final Integer interestType = helper.extractIntegerWithLocaleNamed("interestType", element, parametersPassedInCommand);
        final Integer interestCalculationPeriodType = helper.extractIntegerWithLocaleNamed("interestCalculationPeriodType", element,
                parametersPassedInCommand);

        final LocalDate expectedDisbursementDate = helper.extractLocalDateNamed("expectedDisbursementDate", element,
                parametersPassedInCommand);
        final LocalDate repaymentsStartingFromDate = helper.extractLocalDateNamed("repaymentsStartingFromDate", element,
                parametersPassedInCommand);
        final LocalDate interestChargedFromDate = helper.extractLocalDateNamed("interestChargedFromDate", element,
                parametersPassedInCommand);
        final LocalDate submittedOnDate = helper.extractLocalDateNamed("submittedOnDate", element, parametersPassedInCommand);

        final String submittedOnNote = helper.extractStringNamed("submittedOnNote", element, parametersPassedInCommand);

        MemberLoanCommand[] memberLoans = null;
        GroupLoanChargeCommand[] charges = null;
        if (element.isJsonObject()) {
            final JsonObject topLevelJsonElement = element.getAsJsonObject();
            final Locale locale = helper.extractLocaleParameter(topLevelJsonElement);
            final String dateFormat = helper.extractDateFormatParameter(topLevelJsonElement);
            if (topLevelJsonElement.has("memberLoans") && topLevelJsonElement.get("memberLoans").isJsonArray()) {

                parametersPassedInCommand.add("memberLoans");
                final JsonArray array = topLevelJsonElement.get("memberLoans").getAsJsonArray();
                memberLoans = new MemberLoanCommand[array.size()];
                for (int i = 0; i < array.size(); i++) {

                    final JsonObject memberLoanElement = array.get(i).getAsJsonObject();
                    final Set<String> parametersPassedInForMemberLoansCommand = new HashSet<String>();

                    final Long loanId = helper.extractLongNamed("loanId", memberLoanElement, parametersPassedInForMemberLoansCommand);
                    final Long clientId = helper.extractLongNamed("clientId", memberLoanElement, parametersPassedInForMemberLoansCommand);
                    final String memberLoanExternalId = helper.extractStringNamed("externalId", memberLoanElement,
                            parametersPassedInForMemberLoansCommand);
                    final BigDecimal memberLoanPrincipal = helper.extractBigDecimalNamed("principal", memberLoanElement, locale,
                            parametersPassedInForMemberLoansCommand);

                    memberLoans[i] = new MemberLoanCommand(parametersPassedInForMemberLoansCommand, loanId, clientId, memberLoanExternalId,
                            memberLoanPrincipal);
                }
            }

            if (topLevelJsonElement.has("charges") && topLevelJsonElement.get("charges").isJsonArray()) {

                parametersPassedInCommand.add("charges");
                final JsonArray chargesArray = topLevelJsonElement.get("charges").getAsJsonArray();
                charges = new GroupLoanChargeCommand[chargesArray.size()];
                for (int i = 0; i < chargesArray.size(); i++) {

                    final JsonObject loanChargeElement = chargesArray.get(i).getAsJsonObject();
                    final Set<String> parametersPassedInForChargesCommand = new HashSet<String>();

                    final Long id = helper.extractLongNamed("id", loanChargeElement, parametersPassedInForChargesCommand);
                    final Long chargeId = helper.extractLongNamed("chargeId", loanChargeElement, parametersPassedInForChargesCommand);
                    final BigDecimal amount = helper.extractBigDecimalNamed("amount", loanChargeElement, locale,
                            parametersPassedInForChargesCommand);
                    final Integer chargeTimeType = helper.extractIntegerNamed("chargeTimeType", loanChargeElement, locale,
                            parametersPassedInForChargesCommand);
                    final Integer chargeCalculationType = helper.extractIntegerNamed("chargeCalculationType", loanChargeElement, locale,
                            parametersPassedInForChargesCommand);
                    final LocalDate specifiedDueDate = helper.extractLocalDateNamed("specifiedDueDate", loanChargeElement, dateFormat,
                            locale, parametersPassedInForChargesCommand);

                    charges[i] = new GroupLoanChargeCommand(parametersPassedInForChargesCommand, id, null, chargeId, amount, chargeTimeType,
                            chargeCalculationType, specifiedDueDate);

                    if (loanChargeElement.has("memberCharges") && loanChargeElement.get("memberCharges").isJsonArray()) {

                        parametersPassedInCommand.add("memberCharges");
                        final JsonArray memberChargesArray = loanChargeElement.get("memberCharges").getAsJsonArray();
                        final Set<String> parametersPassedInForMemberChargesCommand = new HashSet<String>();
                        for (int j = 0; j < memberChargesArray.size(); j++) {
                            final JsonObject memberChargeElement = memberChargesArray.get(j).getAsJsonObject();

                            final Long memberChargeClientId = helper.extractLongNamed("clientId", memberChargeElement,
                                    parametersPassedInForMemberChargesCommand);
                            final BigDecimal memberChargeAmount = helper.extractBigDecimalNamed("amount", memberChargeElement, locale,
                                    parametersPassedInForMemberChargesCommand);

                            charges[i].addMemberCharge(parametersPassedInForMemberChargesCommand, memberChargeClientId, memberChargeAmount);
                        }
                    }
                }
            }
        }

        return new GroupLoanApplicationCommand(parametersPassedInCommand, resourceIdentifier, groupId, productId, externalId, fundId,
                loanOfficerId, transactionProcessingStrategyId, principal, inArrearsToleranceValue, loanTermFrequency,
                loanTermFrequencyType, numberOfRepayments, repaymentEvery, interestRatePerPeriod, repaymentFrequencyType,
                interestRateFrequencyType, amortizationType, interestType, interestCalculationPeriodType, expectedDisbursementDate,
                repaymentsStartingFromDate, interestChargedFromDate, submittedOnDate, submittedOnNote, memberLoans, charges);
    }

    @Override
    public LoanChargeCommand convertJsonToLoanChargeCommand(final Long loanChargeId, final Long loanId, final String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        Type typeOfMap = new TypeToken<Map<String, String>>() {}.getType();
        Map<String, String> requestMap = gsonConverter.fromJson(json, typeOfMap);

        final Set<String> supportedParams = new HashSet<String>(Arrays.asList("chargeId", "amount", "chargeTimeType",
                "chargeCalculationType", "specifiedDueDate", "locale", "dateFormat"));

        checkForUnsupportedParameters(requestMap, supportedParams);

        Set<String> modifiedParameters = new HashSet<String>();

        Long chargeId = extractLongParameter("chargeId", requestMap, modifiedParameters);
        BigDecimal amount = extractBigDecimalParameter("amount", requestMap, modifiedParameters);
        Integer chargeTimeType = extractIntegerParameter("chargeTimeType", requestMap, modifiedParameters);
        Integer chargeCalculationType = extractIntegerParameter("chargeCalculationType", requestMap, modifiedParameters);
        final LocalDate submittedOnDate = extractLocalDateParameter("specifiedDueDate", requestMap, modifiedParameters);

        return new LoanChargeCommand(modifiedParameters, loanChargeId, loanId, chargeId, amount, chargeTimeType, chargeCalculationType,
                submittedOnDate);
    }

    @Override
    public LoanStateTransitionCommand convertJsonToLoanStateTransitionCommand(final Long resourceIdentifier, final String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        Map<String, Object> requestMap = gsonConverter.fromJson(json, typeOfMap);

        Set<String> supportedParams = new HashSet<String>(Arrays.asList("eventDate", "note", "locale", "dateFormat"));

        checkForUnsupportedParameters(requestMap, supportedParams);

        Set<String> modifiedParameters = new HashSet<String>();

        LocalDate eventDate = extractLocalDateParameter("eventDate", requestMap, modifiedParameters);
        String note = extractStringParameter("note", requestMap, modifiedParameters);

        return new LoanStateTransitionCommand(resourceIdentifier, eventDate, note);
    }

    @Override
    public LoanTransactionCommand convertJsonToLoanTransactionCommand(final Long resourceIdentifier, final String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        Map<String, Object> requestMap = gsonConverter.fromJson(json, typeOfMap);

        Set<String> supportedParams = new HashSet<String>(Arrays.asList("transactionDate", "transactionAmount", "note", "dateFormat",
                "locale"));

        checkForUnsupportedParameters(requestMap, supportedParams);

        Set<String> modifiedParameters = new HashSet<String>();

        LocalDate transactionDate = extractLocalDateParameter("transactionDate", requestMap, modifiedParameters);
        BigDecimal transactionAmount = extractBigDecimalParameter("transactionAmount", requestMap, modifiedParameters);
        String note = extractStringParameter("note", requestMap, modifiedParameters);

        return new LoanTransactionCommand(resourceIdentifier, transactionDate, transactionAmount, note);
    }

    @Override
    public AdjustLoanTransactionCommand convertJsonToAdjustLoanTransactionCommand(final Long loanId, final Long transactionId,
            final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        Map<String, Object> requestMap = gsonConverter.fromJson(json, typeOfMap);

        Set<String> supportedParams = new HashSet<String>(Arrays.asList("transactionDate", "transactionAmount", "note", "dateFormat",
                "locale"));

        checkForUnsupportedParameters(requestMap, supportedParams);

        Set<String> modifiedParameters = new HashSet<String>();

        LocalDate transactionDate = extractLocalDateParameter("transactionDate", requestMap, modifiedParameters);
        BigDecimal transactionAmount = extractBigDecimalParameter("transactionAmount", requestMap, modifiedParameters);
        String note = extractStringParameter("note", requestMap, modifiedParameters);

        return new AdjustLoanTransactionCommand(loanId, transactionId, transactionDate, note, transactionAmount);
    }

    @Override
    public NoteCommand convertJsonToNoteCommand(final Long noteId, final Long clientId, final String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        Map<String, Object> requestMap = gsonConverter.fromJson(json, typeOfMap);

        Set<String> supportedParams = new HashSet<String>(Arrays.asList("note"));

        checkForUnsupportedParameters(requestMap, supportedParams);

        Set<String> modifiedParameters = new HashSet<String>();

        String note = extractStringParameter("note", requestMap, modifiedParameters);

        return new NoteCommand(noteId, clientId, note);
    }

    private void checkForUnsupportedParameters(Map<String, ?> requestMap, Set<String> supportedParams) {
        List<String> unsupportedParameterList = new ArrayList<String>();
        for (String providedParameter : requestMap.keySet()) {
            if (!supportedParams.contains(providedParameter)) {
                unsupportedParameterList.add(providedParameter);
            }
        }

        if (!unsupportedParameterList.isEmpty()) { throw new UnsupportedParameterException(unsupportedParameterList); }
    }

    private String extractStringParameter(final String paramName, final Map<String, ?> requestMap, final Set<String> modifiedParameters) {
        String paramValue = null;
        if (requestMap.containsKey(paramName)) {
            paramValue = (String) requestMap.get(paramName);
            modifiedParameters.add(paramName);
        }

        if (paramValue != null) {
            paramValue = paramValue.trim();
        }

        return paramValue;
    }

    private Integer extractIntegerParameter(final String paramName, final Map<String, ?> requestMap, final Set<String> modifiedParameters) {
        Integer paramValue = null;
        if (requestMap.containsKey(paramName)) {
            String valueAsString = (String) requestMap.get(paramName);
            paramValue = convertToInteger(valueAsString, paramName, extractLocaleValue(requestMap));
            modifiedParameters.add(paramName);
        }
        return paramValue;
    }

    private BigDecimal extractBigDecimalParameter(final String paramName, final Map<String, ?> requestMap,
            final Set<String> modifiedParameters) {
        BigDecimal paramValue = null;
        if (requestMap.containsKey(paramName)) {
            String valueAsString = (String) requestMap.get(paramName);
            paramValue = convertFrom(valueAsString, paramName, extractLocaleValue(requestMap));
            modifiedParameters.add(paramName);
        }
        return paramValue;
    }

    private boolean extractBooleanParameter(final String paramName, final Map<String, ?> requestMap, final Set<String> modifiedParameters) {
        boolean paramValue = false;
        String paramValueAsString = null;
        if (requestMap.containsKey(paramName)) {
            paramValueAsString = (String) requestMap.get(paramName);

            if (paramValueAsString != null) {
                paramValueAsString = paramValueAsString.trim();
            }

            paramValue = Boolean.valueOf(paramValueAsString);
            modifiedParameters.add(paramName);
        }
        return paramValue;
    }

    private Long extractLongParameter(final String paramName, final Map<String, ?> requestMap, final Set<String> modifiedParameters) {
        Long paramValue = null;
        if (requestMap.containsKey(paramName)) {
            String valueAsString = (String) requestMap.get(paramName);
            if (StringUtils.isNotBlank(valueAsString)) {
                paramValue = Long.valueOf(Double.valueOf(valueAsString).longValue());
            }
            modifiedParameters.add(paramName);
        }
        return paramValue;
    }

    private LocalDate extractLocalDateParameter(final String paramName, final Map<String, ?> requestMap,
            final Set<String> modifiedParameters) {
        LocalDate paramValue = null;
        if (requestMap.containsKey(paramName)) {
            String valueAsString = (String) requestMap.get(paramName);
            if (StringUtils.isNotBlank(valueAsString)) {
                final String dateFormat = (String) requestMap.get("dateFormat");
                final Locale locale = new Locale((String) requestMap.get("locale"));
                paramValue = convertFrom(valueAsString, paramName, dateFormat, locale);
            }
            modifiedParameters.add(paramName);
        }
        return paramValue;
    }

    private Locale extractLocaleValue(Map<String, ?> requestMap) {
        Locale clientApplicationLocale = null;
        String locale = null;
        if (requestMap.containsKey("locale")) {
            locale = (String) requestMap.get("locale");
            clientApplicationLocale = localeFromString(locale);
        }
        return clientApplicationLocale;
    }

    private LocalDate convertFrom(final String dateAsString, final String parameterName, final String dateFormat,
            final Locale clientApplicationLocale) {

        if (StringUtils.isBlank(dateFormat) || clientApplicationLocale == null) {

            List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
            if (StringUtils.isBlank(dateFormat)) {
                String defaultMessage = new StringBuilder("The parameter '" + parameterName
                        + "' requires a 'dateFormat' parameter to be passed with it.").toString();
                ApiParameterError error = ApiParameterError.parameterError("validation.msg.missing.dateFormat.parameter", defaultMessage,
                        parameterName);
                dataValidationErrors.add(error);
            }
            if (clientApplicationLocale == null) {
                String defaultMessage = new StringBuilder("The parameter '" + parameterName
                        + "' requires a 'locale' parameter to be passed with it.").toString();
                ApiParameterError error = ApiParameterError.parameterError("validation.msg.missing.locale.parameter", defaultMessage,
                        parameterName);
                dataValidationErrors.add(error);
            }
            throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist", "Validation errors exist.",
                    dataValidationErrors);
        }

        LocalDate eventLocalDate = null;
        if (StringUtils.isNotBlank(dateAsString)) {
            try {
                // Locale locale = LocaleContextHolder.getLocale();
                eventLocalDate = DateTimeFormat.forPattern(dateFormat).withLocale(clientApplicationLocale)
                        .parseLocalDate(dateAsString.toLowerCase(clientApplicationLocale));
            } catch (IllegalArgumentException e) {
                List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
                ApiParameterError error = ApiParameterError.parameterError("validation.msg.invalid.date.format", "The parameter "
                        + parameterName + " is invalid based on the dateFormat: '" + dateFormat + "' and locale: '"
                        + clientApplicationLocale + "' provided:", parameterName, dateAsString, dateFormat);
                dataValidationErrors.add(error);

                throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist", "Validation errors exist.",
                        dataValidationErrors);
            }
        }

        return eventLocalDate;
    }

    private Integer convertToInteger(final String numericalValueFormatted, final String parameterName, final Locale clientApplicationLocale) {

        if (clientApplicationLocale == null) {

            List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
            String defaultMessage = new StringBuilder("The parameter '" + parameterName
                    + "' requires a 'locale' parameter to be passed with it.").toString();
            ApiParameterError error = ApiParameterError.parameterError("validation.msg.missing.locale.parameter", defaultMessage,
                    parameterName);
            dataValidationErrors.add(error);

            throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist", "Validation errors exist.",
                    dataValidationErrors);
        }

        try {
            Integer number = null;

            if (StringUtils.isNotBlank(numericalValueFormatted)) {

                String source = numericalValueFormatted.trim();

                NumberFormat format = NumberFormat.getInstance(clientApplicationLocale);
                DecimalFormat df = (DecimalFormat) format;
                DecimalFormatSymbols symbols = df.getDecimalFormatSymbols();
                df.setParseBigDecimal(true);

                // http://bugs.sun.com/view_bug.do?bug_id=4510618
                char groupingSeparator = symbols.getGroupingSeparator();
                if (groupingSeparator == '\u00a0') {
                    source = source.replaceAll(" ", Character.toString('\u00a0'));
                }

                Number parsedNumber = df.parse(source);

                double parsedNumberDouble = parsedNumber.doubleValue();
                int parsedNumberInteger = parsedNumber.intValue();

                if (source.contains(Character.toString(symbols.getDecimalSeparator()))) { throw new ParseException(source, 0); }

                if (!Double.valueOf(parsedNumberDouble).equals(Double.valueOf(Integer.valueOf(parsedNumberInteger)))) { throw new ParseException(
                        source, 0); }

                number = parsedNumber.intValue();
            }

            return number;
        } catch (ParseException e) {

            List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
            ApiParameterError error = ApiParameterError.parameterError("validation.msg.invalid.integer.format", "The parameter "
                    + parameterName + " has value: " + numericalValueFormatted + " which is invalid integer value for provided locale of ["
                    + clientApplicationLocale.toString() + "].", parameterName, numericalValueFormatted, clientApplicationLocale);
            dataValidationErrors.add(error);

            throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist", "Validation errors exist.",
                    dataValidationErrors);
        }
    }

    private BigDecimal convertFrom(final String numericalValueFormatted, final String parameterName, final Locale clientApplicationLocale) {

        if (clientApplicationLocale == null) {

            List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
            String defaultMessage = new StringBuilder("The parameter '" + parameterName
                    + "' requires a 'locale' parameter to be passed with it.").toString();
            ApiParameterError error = ApiParameterError.parameterError("validation.msg.missing.locale.parameter", defaultMessage,
                    parameterName);
            dataValidationErrors.add(error);

            throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist", "Validation errors exist.",
                    dataValidationErrors);
        }

        try {
            BigDecimal number = null;

            if (StringUtils.isNotBlank(numericalValueFormatted)) {

                String source = numericalValueFormatted.trim();

                NumberFormat format = NumberFormat.getNumberInstance(clientApplicationLocale);
                DecimalFormat df = (DecimalFormat) format;
                DecimalFormatSymbols symbols = df.getDecimalFormatSymbols();
                // http://bugs.sun.com/view_bug.do?bug_id=4510618
                char groupingSeparator = symbols.getGroupingSeparator();
                if (groupingSeparator == '\u00a0') {
                    source = source.replaceAll(" ", Character.toString('\u00a0'));
                }

                NumberFormatter numberFormatter = new NumberFormatter();
                Number parsedNumber = numberFormatter.parse(source, clientApplicationLocale);
                number = BigDecimal.valueOf(Double.valueOf(parsedNumber.doubleValue()));
            }

            return number;
        } catch (ParseException e) {

            List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
            ApiParameterError error = ApiParameterError.parameterError("validation.msg.invalid.decimal.format", "The parameter "
                    + parameterName + " has value: " + numericalValueFormatted + " which is invalid decimal value for provided locale of ["
                    + clientApplicationLocale.toString() + "].", parameterName, numericalValueFormatted, clientApplicationLocale);
            dataValidationErrors.add(error);

            throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist", "Validation errors exist.",
                    dataValidationErrors);
        }
    }

    /*
     * private LocalDate convertFrom(final String dateAsString, final String
     * parameterName, final String dateFormat) {
     * 
     * if (StringUtils.isBlank(dateFormat)) {
     * 
     * List<ApiParameterError> dataValidationErrors = new
     * ArrayList<ApiParameterError>(); String defaultMessage = new
     * StringBuilder("The parameter '" + parameterName +
     * "' requires a 'dateFormat' parameter to be passed with it.").toString();
     * ApiParameterError error = ApiParameterError.parameterError(
     * "validation.msg.missing.dateFormat.parameter", defaultMessage,
     * parameterName); dataValidationErrors.add(error);
     * 
     * throw new PlatformApiDataValidationException(
     * "validation.msg.validation.errors.exist", "Validation errors exist.",
     * dataValidationErrors); }
     * 
     * LocalDate eventLocalDate = null; if
     * (StringUtils.isNotBlank(dateAsString)) { try { Locale locale =
     * LocaleContextHolder.getLocale(); eventLocalDate =
     * DateTimeFormat.forPattern(dateFormat) .withLocale(locale)
     * .parseLocalDate(dateAsString.toLowerCase(locale)); } catch
     * (IllegalArgumentException e) { List<ApiParameterError>
     * dataValidationErrors = new ArrayList<ApiParameterError>();
     * ApiParameterError error = ApiParameterError .parameterError(
     * "validation.msg.invalid.date.format", "The parameter " + parameterName +
     * " is invalid based on the dateFormat provided:" + dateFormat,
     * parameterName, dateAsString, dateFormat);
     * dataValidationErrors.add(error);
     * 
     * throw new PlatformApiDataValidationException(
     * "validation.msg.validation.errors.exist", "Validation errors exist.",
     * dataValidationErrors); } }
     * 
     * return eventLocalDate; }
     * 
     * private Integer convertToInteger(final String numericalValueFormatted,
     * final String parameterName, final Locale clientApplicationLocale) {
     * 
     * if (clientApplicationLocale == null) {
     * 
     * List<ApiParameterError> dataValidationErrors = new
     * ArrayList<ApiParameterError>(); String defaultMessage = new
     * StringBuilder("The parameter '" + parameterName +
     * "' requires a 'locale' parameter to be passed with it.").toString();
     * ApiParameterError error =
     * ApiParameterError.parameterError("validation.msg.missing.locale.parameter"
     * , defaultMessage, parameterName); dataValidationErrors.add(error);
     * 
     * throw new PlatformApiDataValidationException(
     * "validation.msg.validation.errors.exist", "Validation errors exist.",
     * dataValidationErrors); }
     * 
     * try { Integer number = null;
     * 
     * if (StringUtils.isNotBlank(numericalValueFormatted)) {
     * 
     * String source = numericalValueFormatted.trim();
     * 
     * NumberFormat format = NumberFormat.getInstance(clientApplicationLocale);
     * DecimalFormat df = (DecimalFormat) format; DecimalFormatSymbols symbols =
     * df.getDecimalFormatSymbols(); df.setParseBigDecimal(true);
     * 
     * // http://bugs.sun.com/view_bug.do?bug_id=4510618 char groupingSeparator
     * = symbols.getGroupingSeparator(); if (groupingSeparator == '\u00a0') {
     * source = source.replaceAll(" ", Character.toString('\u00a0')); }
     * 
     * Number parsedNumber = df.parse(source);
     * 
     * double parsedNumberDouble = parsedNumber.doubleValue(); int
     * parsedNumberInteger = parsedNumber.intValue();
     * 
     * if (source.contains(Character.toString(symbols.getDecimalSeparator()))) {
     * throw new ParseException(source, 0); }
     * 
     * if
     * (!Double.valueOf(parsedNumberDouble).equals(Double.valueOf(Integer.valueOf
     * (parsedNumberInteger)))) { throw new ParseException(source, 0); }
     * 
     * number = parsedNumber.intValue(); }
     * 
     * return number; } catch (ParseException e) {
     * 
     * List<ApiParameterError> dataValidationErrors = new
     * ArrayList<ApiParameterError>(); ApiParameterError error =
     * ApiParameterError.parameterError(
     * "validation.msg.invalid.integer.format", "The parameter " + parameterName
     * + " has value: " + numericalValueFormatted +
     * " which is invalid integer value for provided locale of [" +
     * clientApplicationLocale.toString() + "].", parameterName,
     * numericalValueFormatted, clientApplicationLocale);
     * dataValidationErrors.add(error);
     * 
     * throw new PlatformApiDataValidationException(
     * "validation.msg.validation.errors.exist", "Validation errors exist.",
     * dataValidationErrors); } }
     * 
     * private BigDecimal convertFrom(final String numericalValueFormatted,
     * final String parameterName, final Locale clientApplicationLocale) {
     * 
     * if (clientApplicationLocale == null) {
     * 
     * List<ApiParameterError> dataValidationErrors = new
     * ArrayList<ApiParameterError>(); String defaultMessage = new
     * StringBuilder("The parameter '" + parameterName +
     * "' requires a 'locale' parameter to be passed with it.").toString();
     * ApiParameterError error =
     * ApiParameterError.parameterError("validation.msg.missing.locale.parameter"
     * , defaultMessage, parameterName); dataValidationErrors.add(error);
     * 
     * throw new PlatformApiDataValidationException(
     * "validation.msg.validation.errors.exist", "Validation errors exist.",
     * dataValidationErrors); }
     * 
     * try { BigDecimal number = null;
     * 
     * if (StringUtils.isNotBlank(numericalValueFormatted)) {
     * 
     * String source = numericalValueFormatted.trim();
     * 
     * NumberFormat format =
     * NumberFormat.getNumberInstance(clientApplicationLocale); DecimalFormat df
     * = (DecimalFormat) format; DecimalFormatSymbols symbols =
     * df.getDecimalFormatSymbols(); //
     * http://bugs.sun.com/view_bug.do?bug_id=4510618 char groupingSeparator =
     * symbols.getGroupingSeparator(); if (groupingSeparator == '\u00a0') {
     * source = source.replaceAll(" ", Character.toString('\u00a0')); }
     * 
     * NumberFormatter numberFormatter = new NumberFormatter(); Number
     * parsedNumber = numberFormatter.parse(source, clientApplicationLocale);
     * number = BigDecimal.valueOf(Double.valueOf(parsedNumber.doubleValue()));
     * }
     * 
     * return number; } catch (ParseException e) {
     * 
     * List<ApiParameterError> dataValidationErrors = new
     * ArrayList<ApiParameterError>(); ApiParameterError error =
     * ApiParameterError.parameterError(
     * "validation.msg.invalid.decimal.format", "The parameter " + parameterName
     * + " has value: " + numericalValueFormatted +
     * " which is invalid decimal value for provided locale of [" +
     * clientApplicationLocale.toString() + "].", parameterName,
     * numericalValueFormatted, clientApplicationLocale);
     * dataValidationErrors.add(error);
     * 
     * throw new PlatformApiDataValidationException(
     * "validation.msg.validation.errors.exist", "Validation errors exist.",
     * dataValidationErrors); } }
     */
    private Locale localeFromString(final String localeAsString) {

        if (StringUtils.isBlank(localeAsString)) {
            List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
            ApiParameterError error = ApiParameterError.parameterError("validation.msg.invalid.locale.format",
                    "The parameter locale is invalid. It cannot be blank.", "locale");
            dataValidationErrors.add(error);

            throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist", "Validation errors exist.",
                    dataValidationErrors);
        }

        String languageCode = "";
        String courntryCode = "";
        String variantCode = "";

        String[] localeParts = localeAsString.split("_");

        if (localeParts != null && localeParts.length == 1) {
            languageCode = localeParts[0];
        }

        if (localeParts != null && localeParts.length == 2) {
            languageCode = localeParts[0];
            courntryCode = localeParts[1];
        }

        if (localeParts != null && localeParts.length == 3) {
            languageCode = localeParts[0];
            courntryCode = localeParts[1];
            variantCode = localeParts[2];
        }

        return localeFrom(languageCode, courntryCode, variantCode);
    }

    private Locale localeFrom(final String languageCode, final String courntryCode, final String variantCode) {

        List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();

        List<String> allowedLanguages = Arrays.asList(Locale.getISOLanguages());
        if (!allowedLanguages.contains(languageCode.toLowerCase())) {
            ApiParameterError error = ApiParameterError.parameterError("validation.msg.invalid.locale.format",
                    "The parameter locale has an invalid language value " + languageCode + " .", "locale", languageCode);
            dataValidationErrors.add(error);
        }

        if (StringUtils.isNotBlank(courntryCode.toUpperCase())) {
            List<String> allowedCountries = Arrays.asList(Locale.getISOCountries());
            if (!allowedCountries.contains(courntryCode)) {
                ApiParameterError error = ApiParameterError.parameterError("validation.msg.invalid.locale.format",
                        "The parameter locale has an invalid country value " + courntryCode + " .", "locale", courntryCode);
                dataValidationErrors.add(error);
            }
        }

        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
                "Validation errors exist.", dataValidationErrors); }

        return new Locale(languageCode.toLowerCase(), courntryCode.toUpperCase(), variantCode);
    }

    @Override
    public SavingProductCommand convertJsonToSavingProductCommand(final Long resourceIdentifier, final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }
        Type typeOfMap = new TypeToken<Map<String, String>>() {}.getType();
        Map<String, String> requestMap = gsonConverter.fromJson(json, typeOfMap);

        Set<String> supportedParams = new HashSet<String>(Arrays.asList("locale", "name", "description", "currencyCode",
                "digitsAfterDecimal", "interestRate", "minInterestRate", "maxInterestRate", "savingsDepositAmount", "savingProductType",
                "tenureType", "tenure", "frequency", "interestType", "interestCalculationMethod", "minimumBalanceForWithdrawal",
                "isPartialDepositAllowed", "isLockinPeriodAllowed", "lockinPeriod", "lockinPeriodType"));

        checkForUnsupportedParameters(requestMap, supportedParams);
        Set<String> modifiedParameters = new HashSet<String>();
        String name = extractStringParameter("name", requestMap, modifiedParameters);
        String description = extractStringParameter("description", requestMap, modifiedParameters);
        String currencyCode = extractStringParameter("currencyCode", requestMap, modifiedParameters);
        Integer digitsAfterDecimalValue = extractIntegerParameter("digitsAfterDecimal", requestMap, modifiedParameters);
        BigDecimal interestRate = extractBigDecimalParameter("interestRate", requestMap, modifiedParameters);
        BigDecimal minInterestRate = extractBigDecimalParameter("minInterestRate", requestMap, modifiedParameters);
        BigDecimal maxInterestRate = extractBigDecimalParameter("maxInterestRate", requestMap, modifiedParameters);
        BigDecimal savingsDepositAmount = extractBigDecimalParameter("savingsDepositAmount", requestMap, modifiedParameters);
        Integer savingProductType = extractIntegerParameter("savingProductType", requestMap, modifiedParameters);
        Integer tenureType = extractIntegerParameter("tenureType", requestMap, modifiedParameters);
        Integer tenure = extractIntegerParameter("tenure", requestMap, modifiedParameters);
        Integer frequency = extractIntegerParameter("frequency", requestMap, modifiedParameters);
        Integer interestType = extractIntegerParameter("interestType", requestMap, modifiedParameters);
        Integer interestCalculationMethod = extractIntegerParameter("interestCalculationMethod", requestMap, modifiedParameters);
        BigDecimal minimumBalanceForWithdrawal = extractBigDecimalParameter("minimumBalanceForWithdrawal", requestMap, modifiedParameters);
        boolean isPartialDepositAllowed = extractBooleanParameter("isPartialDepositAllowed", requestMap, modifiedParameters);
        boolean isLockinPeriodAllowed = extractBooleanParameter("isLockinPeriodAllowed", requestMap, modifiedParameters);
        Integer lockinPeriod = extractIntegerParameter("lockinPeriod", requestMap, modifiedParameters);
        Integer lockinPeriodType = extractIntegerParameter("lockinPeriodType", requestMap, modifiedParameters);

        return new SavingProductCommand(modifiedParameters, resourceIdentifier, name, description, currencyCode, digitsAfterDecimalValue,
                interestRate, minInterestRate, maxInterestRate, savingsDepositAmount, savingProductType, tenureType, tenure, frequency,
                interestType, interestCalculationMethod, minimumBalanceForWithdrawal, isPartialDepositAllowed, isLockinPeriodAllowed,
                lockinPeriod, lockinPeriodType);
    }

    @Override
    public DepositAccountCommand convertJsonToDepositAccountCommand(final Long resourceIdentifier, final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        Type typeOfMap = new TypeToken<Map<String, String>>() {}.getType();
        Map<String, String> requestMap = gsonConverter.fromJson(json, typeOfMap);

        // preClosureInterestRate
        Set<String> supportedParams = new HashSet<String>(Arrays.asList("clientId", "productId", "externalId", "deposit",
                "maturityInterestRate", "preClosureInterestRate", "tenureInMonths", "interestCompoundedEvery",
                "interestCompoundedEveryPeriodType", "commencementDate", "renewalAllowed", "preClosureAllowed",
                "interestCompoundingAllowed", "locale", "dateFormat", "isInterestWithdrawable", "isLockinPeriodAllowed", "lockinPeriod",
                "lockinPeriodType"));
        checkForUnsupportedParameters(requestMap, supportedParams);
        Set<String> modifiedParameters = new HashSet<String>();

        Long clientId = extractLongParameter("clientId", requestMap, modifiedParameters);
        Long productId = extractLongParameter("productId", requestMap, modifiedParameters);
        String externalId = extractStringParameter("externalId", requestMap, modifiedParameters);
        BigDecimal deposit = extractBigDecimalParameter("deposit", requestMap, modifiedParameters);
        BigDecimal interestRate = extractBigDecimalParameter("maturityInterestRate", requestMap, modifiedParameters);
        BigDecimal preClosureInterestRate = extractBigDecimalParameter("preClosureInterestRate", requestMap, modifiedParameters);
        Integer tenureInMonths = extractIntegerParameter("tenureInMonths", requestMap, modifiedParameters);

        boolean isLockinPeriodAllowed = extractBooleanParameter("isLockinPeriodAllowed", requestMap, modifiedParameters);
        Integer lockinPeriod = extractIntegerParameter("lockinPeriod", requestMap, modifiedParameters);
        Integer lockinPeriodType = extractIntegerParameter("lockinPeriodType", requestMap, modifiedParameters);

        Integer interestCompoundedEvery = extractIntegerParameter("interestCompoundedEvery", requestMap, modifiedParameters);
        Integer interestCompoundedEveryPeriodType = extractIntegerParameter("interestCompoundedEveryPeriodType", requestMap,
                modifiedParameters);
        LocalDate commencementDate = extractLocalDateParameter("commencementDate", requestMap, modifiedParameters);

        boolean renewalAllowed = extractBooleanParameter("renewalAllowed", requestMap, modifiedParameters);
        boolean preClosureAllowed = extractBooleanParameter("preClosureAllowed", requestMap, modifiedParameters);
        boolean isInterestWithdrawable = extractBooleanParameter("isInterestWithdrawable", requestMap, modifiedParameters);
        boolean interestCompoundingAllowed = extractBooleanParameter("interestCompoundingAllowed", requestMap, modifiedParameters);

        return new DepositAccountCommand(modifiedParameters, resourceIdentifier, clientId, productId, externalId, deposit, interestRate,
                preClosureInterestRate, tenureInMonths, interestCompoundedEvery, interestCompoundedEveryPeriodType, commencementDate,
                renewalAllowed, preClosureAllowed, isInterestWithdrawable, interestCompoundingAllowed, isLockinPeriodAllowed, lockinPeriod,
                lockinPeriodType);
    }

    @Override
    public DepositProductCommand convertJsonToDepositProductCommand(final Long resourceIdentifier, final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        Type typeOfMap = new TypeToken<Map<String, String>>() {}.getType();
        Map<String, String> requestMap = gsonConverter.fromJson(json, typeOfMap);

        Set<String> supportedParams = new HashSet<String>(Arrays.asList("locale", "name", "externalId", "description", "currencyCode",
                "digitsAfterDecimal", "minimumBalance", "maximumBalance", "tenureInMonths", "maturityDefaultInterestRate",
                "maturityMinInterestRate", "maturityMaxInterestRate", "interestCompoundedEvery", "interestCompoundedEveryPeriodType",
                "renewalAllowed", "preClosureAllowed", "preClosureInterestRate", "interestCompoundingAllowed", "isLockinPeriodAllowed",
                "lockinPeriod", "lockinPeriodType"));
        checkForUnsupportedParameters(requestMap, supportedParams);
        Set<String> modifiedParameters = new HashSet<String>();
        String name = extractStringParameter("name", requestMap, modifiedParameters);
        String externalId = extractStringParameter("externalId", requestMap, modifiedParameters);

        String description = extractStringParameter("description", requestMap, modifiedParameters);
        String currencyCode = extractStringParameter("currencyCode", requestMap, modifiedParameters);
        Integer digitsAfterDecimalValue = extractIntegerParameter("digitsAfterDecimal", requestMap, modifiedParameters);
        BigDecimal minimumBalance = extractBigDecimalParameter("minimumBalance", requestMap, modifiedParameters);
        BigDecimal maximumBalance = extractBigDecimalParameter("maximumBalance", requestMap, modifiedParameters);

        Integer tenureMonths = extractIntegerParameter("tenureInMonths", requestMap, modifiedParameters);
        BigDecimal maturityDefaultInterestRate = extractBigDecimalParameter("maturityDefaultInterestRate", requestMap, modifiedParameters);
        BigDecimal maturityMinInterestRate = extractBigDecimalParameter("maturityMinInterestRate", requestMap, modifiedParameters);
        BigDecimal maturityMaxInterestRate = extractBigDecimalParameter("maturityMaxInterestRate", requestMap, modifiedParameters);

        Integer interestCompoundedEvery = extractIntegerParameter("interestCompoundedEvery", requestMap, modifiedParameters);
        Integer interestCompoundedEveryPeriodType = extractIntegerParameter("interestCompoundedEveryPeriodType", requestMap,
                modifiedParameters);

        boolean interestCompoundingAllowed = extractBooleanParameter("interestCompoundingAllowed", requestMap, modifiedParameters);

        boolean canRenew = extractBooleanParameter("renewalAllowed", requestMap, modifiedParameters);
        boolean canPreClose = extractBooleanParameter("preClosureAllowed", requestMap, modifiedParameters);
        BigDecimal preClosureInterestRate = extractBigDecimalParameter("preClosureInterestRate", requestMap, modifiedParameters);

        boolean isLockinPeriodAllowed = extractBooleanParameter("isLockinPeriodAllowed", requestMap, modifiedParameters);
        Integer lockinPeriod = extractIntegerParameter("lockinPeriod", requestMap, modifiedParameters);
        Integer lockinPeriodType = extractIntegerParameter("lockinPeriodType", requestMap, modifiedParameters);

        return new DepositProductCommand(modifiedParameters, resourceIdentifier, externalId, name, description, currencyCode,
                digitsAfterDecimalValue, minimumBalance, maximumBalance, tenureMonths, maturityDefaultInterestRate,
                maturityMinInterestRate, maturityMaxInterestRate, interestCompoundedEvery, interestCompoundedEveryPeriodType, canRenew,
                canPreClose, preClosureInterestRate, interestCompoundingAllowed, isLockinPeriodAllowed, lockinPeriod, lockinPeriodType);
    }

    @Override
    public DepositStateTransitionCommand convertJsonToDepositStateTransitionCommand(Long resourceIdentifier, String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        Map<String, Object> requestMap = gsonConverter.fromJson(json, typeOfMap);

        Set<String> supportedParams = new HashSet<String>(Arrays.asList("eventDate", "locale", "dateFormat", "note"));

        checkForUnsupportedParameters(requestMap, supportedParams);

        Set<String> modifiedParameters = new HashSet<String>();

        LocalDate eventDate = extractLocalDateParameter("eventDate", requestMap, modifiedParameters);
        String note = extractStringParameter("note", requestMap, modifiedParameters);

        return new DepositStateTransitionCommand(resourceIdentifier, eventDate, note);
    }

    @Override
    public DepositStateTransitionApprovalCommand convertJsonToDepositStateTransitionApprovalCommand(final Long resourceIdentifier,
            final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        Type typeOfMap = new TypeToken<Map<String, String>>() {}.getType();
        Map<String, String> requestMap = gsonConverter.fromJson(json, typeOfMap);

        Set<String> supportedParams = new HashSet<String>(Arrays.asList("locale", "commencementDate", "locale", "dateFormat",
                "tenureInMonths", "deposit", "interestCompoundedEveryPeriodType", "productId", "interestCompoundedEvery", "note",
                "maturityInterestRate"));

        checkForUnsupportedParameters(requestMap, supportedParams);

        Set<String> modifiedParameters = new HashSet<String>();

        LocalDate commencementDate = extractLocalDateParameter("commencementDate", requestMap, modifiedParameters);
        BigDecimal deposit = extractBigDecimalParameter("deposit", requestMap, modifiedParameters);
        BigDecimal maturityInterestRate = extractBigDecimalParameter("maturityInterestRate", requestMap, modifiedParameters);
        Integer tenureInMonths = extractIntegerParameter("tenureInMonths", requestMap, modifiedParameters);
        Integer interestCompoundedEveryPeriodType = extractIntegerParameter("interestCompoundedEveryPeriodType", requestMap,
                modifiedParameters);
        Integer interestCompoundedEvery = extractIntegerParameter("interestCompoundedEvery", requestMap, modifiedParameters);
        Long productId = extractLongParameter("productId", requestMap, modifiedParameters);
        String note = extractStringParameter("note", requestMap, modifiedParameters);

        return new DepositStateTransitionApprovalCommand(resourceIdentifier, productId, commencementDate, tenureInMonths, deposit,
                interestCompoundedEveryPeriodType, interestCompoundedEvery, note, maturityInterestRate);
    }

    @Override
    public DepositAccountWithdrawalCommand convertJsonToDepositWithdrawalCommand(final Long resourceIdentifier, final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        Type typeOfMap = new TypeToken<Map<String, String>>() {}.getType();
        Map<String, String> requestMap = gsonConverter.fromJson(json, typeOfMap);

        Set<String> supportedParams = new HashSet<String>(Arrays.asList("note", "locale", "maturesOnDate", "dateFormat"));

        checkForUnsupportedParameters(requestMap, supportedParams);

        Set<String> modifiedParameters = new HashSet<String>();

        String note = extractStringParameter("note", requestMap, modifiedParameters);
        LocalDate maturesOnDate = extractLocalDateParameter("maturesOnDate", requestMap, modifiedParameters);

        return new DepositAccountWithdrawalCommand(resourceIdentifier, note, maturesOnDate);
    }

    @Override
    public DepositAccountWithdrawInterestCommand convertJsonToDepositAccountWithdrawInterestCommand(final Long resourceIdentifier,
            final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        Type typeOfMap = new TypeToken<Map<String, String>>() {}.getType();
        Map<String, String> requestMap = gsonConverter.fromJson(json, typeOfMap);

        Set<String> supportedParams = new HashSet<String>(Arrays.asList("amount", "note", "locale"));

        checkForUnsupportedParameters(requestMap, supportedParams);

        Set<String> modifiedParameters = new HashSet<String>();

        BigDecimal amount = extractBigDecimalParameter("amount", requestMap, modifiedParameters);
        String note = extractStringParameter("note", requestMap, modifiedParameters);

        return new DepositAccountWithdrawInterestCommand(resourceIdentifier, amount, note);
    }

    @Override
    public SavingAccountCommand convertJsonToSavingAccountCommand(Long resourceIdentifier, String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        Type typeOfMap = new TypeToken<Map<String, String>>() {}.getType();
        Map<String, String> requestMap = gsonConverter.fromJson(json, typeOfMap);

        // currencyCode, currencyDigits
        Set<String> supportedParams = new HashSet<String>(Arrays.asList("clientId", "productId", "externalId", "currencyCode",
                "digitsAfterDecimal", "savingsDepositAmountPerPeriod", "recurringInterestRate", "savingInterestRate", "tenure",
                "commencementDate", "locale", "dateFormat", "isLockinPeriodAllowed", "lockinPeriod", "lockinPeriodType",
                "savingProductType", "tenureType", "frequency", "interestType", "interestCalculationMethod", "minimumBalanceForWithdrawal",
                "isPartialDepositAllowed", "payEvery"));
        checkForUnsupportedParameters(requestMap, supportedParams);
        Set<String> modifiedParameters = new HashSet<String>();

        Long clientId = extractLongParameter("clientId", requestMap, modifiedParameters);
        Long productId = extractLongParameter("productId", requestMap, modifiedParameters);
        String externalId = extractStringParameter("externalId", requestMap, modifiedParameters);
        String currencyCode = extractStringParameter("currencyCode", requestMap, modifiedParameters);
        Integer digitsAfterDecimalValue = extractIntegerParameter("digitsAfterDecimal", requestMap, modifiedParameters);
        BigDecimal savingsDepositAmount = extractBigDecimalParameter("savingsDepositAmountPerPeriod", requestMap, modifiedParameters);
        BigDecimal recurringInterestRate = extractBigDecimalParameter("recurringInterestRate", requestMap, modifiedParameters);
        BigDecimal savingInterestRate = extractBigDecimalParameter("savingInterestRate", requestMap, modifiedParameters);
        Integer tenure = extractIntegerParameter("tenure", requestMap, modifiedParameters);

        boolean isLockinPeriodAllowed = extractBooleanParameter("isLockinPeriodAllowed", requestMap, modifiedParameters);
        Integer lockinPeriod = extractIntegerParameter("lockinPeriod", requestMap, modifiedParameters);
        Integer lockinPeriodType = extractIntegerParameter("lockinPeriodType", requestMap, modifiedParameters);

        LocalDate commencementDate = extractLocalDateParameter("commencementDate", requestMap, modifiedParameters);
        Integer savingProductType = extractIntegerParameter("savingProductType", requestMap, modifiedParameters);
        Integer tenureType = extractIntegerParameter("tenureType", requestMap, modifiedParameters);
        Integer frequency = extractIntegerParameter("frequency", requestMap, modifiedParameters);
        Integer interestType = extractIntegerParameter("interestType", requestMap, modifiedParameters);
        Integer interestCalculationMethod = extractIntegerParameter("interestCalculationMethod", requestMap, modifiedParameters);
        BigDecimal minimumBalanceForWithdrawal = extractBigDecimalParameter("minimumBalanceForWithdrawal", requestMap, modifiedParameters);
        boolean isPartialDepositAllowed = extractBooleanParameter("isPartialDepositAllowed", requestMap, modifiedParameters);
        Integer payEvery = extractIntegerParameter("payEvery", requestMap, modifiedParameters);

        return new SavingAccountCommand(modifiedParameters, resourceIdentifier, clientId, productId, externalId, currencyCode,
                digitsAfterDecimalValue, savingsDepositAmount, recurringInterestRate, savingInterestRate, tenure, commencementDate,
                savingProductType, tenureType, frequency, interestType, minimumBalanceForWithdrawal, interestCalculationMethod,
                isLockinPeriodAllowed, isPartialDepositAllowed, lockinPeriod, lockinPeriodType, payEvery);
    }

    @Override
    public GuarantorCommand convertJsonToGuarantorCommand(Long resourceIdentifier, Long loanId, String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        Type typeOfMap = new TypeToken<Map<String, String>>() {}.getType();
        Map<String, String> requestMap = gsonConverter.fromJson(json, typeOfMap);

        Set<String> supportedParams = new HashSet<String>(Arrays.asList("externalGuarantor", "existingClientId", "firstname", "lastname",
                "addressLine1", "addressLine2", "city", "state", "zip", "country", "mobileNumber", "housePhoneNumber", "comment", "dob",
                "locale", "dateFormat"));

        checkForUnsupportedParameters(requestMap, supportedParams);

        Set<String> modifiedParameters = new HashSet<String>();

        boolean externalGuarantor = extractBooleanParameter("externalGuarantor", requestMap, modifiedParameters);

        Long existingClientId = extractLongParameter("existingClientId", requestMap, modifiedParameters);
        String firstname = extractStringParameter("firstname", requestMap, modifiedParameters);
        String lastname = extractStringParameter("lastname", requestMap, modifiedParameters);
        String addressLine1 = extractStringParameter("addressLine1", requestMap, modifiedParameters);
        String addressLine2 = extractStringParameter("addressLine2", requestMap, modifiedParameters);
        String city = extractStringParameter("city", requestMap, modifiedParameters);
        String state = extractStringParameter("state", requestMap, modifiedParameters);
        String zip = extractStringParameter("zip", requestMap, modifiedParameters);
        String country = extractStringParameter("country", requestMap, modifiedParameters);
        String mobileNumber = extractStringParameter("mobileNumber", requestMap, modifiedParameters);
        String housePhoneNumber = extractStringParameter("housePhoneNumber", requestMap, modifiedParameters);
        String comment = extractStringParameter("comment", requestMap, modifiedParameters);
        String dob = extractStringParameter("dob", requestMap, modifiedParameters);

        // workaround for passing locale info to data table api
        final String dateFormat = requestMap.get("dateFormat");
        final String locale = requestMap.get("locale");

        GuarantorCommand command = new GuarantorCommand(modifiedParameters, existingClientId, firstname, lastname, externalGuarantor,
                addressLine1, addressLine2, city, state, zip, country, mobileNumber, housePhoneNumber, comment, dob);
        command.setDateFormat(dateFormat);
        command.setLocale(locale);
        return command;
    }
}
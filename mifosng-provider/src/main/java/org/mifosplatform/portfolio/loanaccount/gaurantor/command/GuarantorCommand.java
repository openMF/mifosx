package org.mifosplatform.portfolio.loanaccount.gaurantor.command;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.data.ApiParameterError;
import org.mifosplatform.infrastructure.core.data.DataValidatorBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformApiDataValidationException;
import org.mifosplatform.portfolio.loanaccount.gaurantor.GuarantorConstants.GUARANTOR_JSON_INPUT_PARAMS;
import org.mifosplatform.portfolio.loanaccount.gaurantor.domain.GuarantorType;

/**
 * Immutable command for creating or updating details of a Guarantor.
 */
public class GuarantorCommand {

    /*** Fields for current customers serving as guarantors **/
    private final Long loanId;
    private final Integer guarantorTypeId;
    private final Long entityId;

    /*** Fields for external persons serving as guarantors ***/
    private final String firstname;
    private final String lastname;
    private final String addressLine1;
    private final String addressLine2;
    private final String city;
    private final String state;
    private final String zip;
    private final String country;
    private final String mobileNumber;
    private final String housePhoneNumber;
    private final String comment;
    private final LocalDate dob;

    public GuarantorCommand(final Long loanId, final Integer guarantorTypeId, final Long entityId, final String firstname,
            final String lastname, final String addressLine1, final String addressLine2, final String city, final String state,
            final String zip, final String country, final String mobileNumber, final String housePhoneNumber, final String comment,
            final LocalDate dob) {
        this.loanId = loanId;
        /*** Fields for current entities serving as guarantors **/
        this.guarantorTypeId = guarantorTypeId;
        this.entityId = entityId;

        /*** Fields for external persons serving as guarantors ***/
        this.firstname = firstname;
        this.lastname = lastname;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.country = country;
        this.mobileNumber = mobileNumber;
        this.housePhoneNumber = housePhoneNumber;
        this.comment = comment;
        this.dob = dob;
    }

    public boolean isExternalGuarantor() {
        return GuarantorType.EXTERNAL.getValue().equals(guarantorTypeId);
    }

    public Date getDobAsDate() {
        return this.dob.toDateMidnight().toDate();
    }

    public void validateForCreate() {

        List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();

        DataValidatorBuilder baseDataValidator = getDataValidator(dataValidationErrors);

        baseDataValidator.reset().parameter(GUARANTOR_JSON_INPUT_PARAMS.LOAN_ID.getValue()).value(this.loanId).notNull()
                .integerGreaterThanZero();
        baseDataValidator.reset().parameter(GUARANTOR_JSON_INPUT_PARAMS.GUARANTOR_TYPE_ID.getValue()).value(this.guarantorTypeId).notNull()
                .inMinMaxRange(GuarantorType.getMinValue(), GuarantorType.getMaxValue());

        // validate for existing Client or Staff serving as gurantor
        if (!this.isExternalGuarantor()) {
            baseDataValidator.reset().parameter(GUARANTOR_JSON_INPUT_PARAMS.ENTITY_ID.getValue()).value(this.entityId).notNull()
                    .integerGreaterThanZero();
        } else {
            // validate for an external guarantor
            baseDataValidator.reset().parameter(GUARANTOR_JSON_INPUT_PARAMS.FIRSTNAME.getValue()).value(this.firstname).notBlank()
                    .notExceedingLengthOf(50);
            baseDataValidator.reset().parameter(GUARANTOR_JSON_INPUT_PARAMS.LASTNAME.getValue()).value(this.lastname).notBlank()
                    .notExceedingLengthOf(50);
            validateNonMandatoryFieldsForMaxLength(baseDataValidator);
        }

        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
                "Validation errors exist.", dataValidationErrors); }
    }

    public void validateForUpdate() {
        List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();

        DataValidatorBuilder baseDataValidator = getDataValidator(dataValidationErrors);

        baseDataValidator.reset().parameter(GUARANTOR_JSON_INPUT_PARAMS.GUARANTOR_TYPE_ID.getValue()).value(this.guarantorTypeId)
                .ignoreIfNull().inMinMaxRange(GuarantorType.getMinValue(), GuarantorType.getMaxValue());

        // validate for existing Client or Staff serving as gurantor
        if (!this.isExternalGuarantor()) {
            baseDataValidator.reset().parameter(GUARANTOR_JSON_INPUT_PARAMS.ENTITY_ID.getValue()).value(this.entityId).ignoreIfNull()
                    .integerGreaterThanZero();
        } else {
            // TODO: Vishwas this validation is buggy (it is compulsory to
            // update
            // firstname and last name when a guarantor type is changed), to be
            // corrected while
            // refactoring for maker checker
            baseDataValidator.reset().parameter(GUARANTOR_JSON_INPUT_PARAMS.FIRSTNAME.getValue()).value(this.firstname).ignoreIfNull()
                    .notExceedingLengthOf(50);
            baseDataValidator.reset().parameter(GUARANTOR_JSON_INPUT_PARAMS.LASTNAME.getValue()).value(this.lastname).ignoreIfNull()
                    .notExceedingLengthOf(50);

            validateNonMandatoryFieldsForMaxLength(baseDataValidator);
        }
        baseDataValidator.reset().anyOfNotNull(this.entityId, this.addressLine1, this.addressLine2, this.city, this.comment, this.country,
                this.firstname, this.housePhoneNumber, this.lastname, this.mobileNumber, this.state, this.zip);

        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
                "Validation errors exist.", dataValidationErrors); }
    }

    /**
     * @param baseDataValidator
     */
    private void validateNonMandatoryFieldsForMaxLength(DataValidatorBuilder baseDataValidator) {
        // validate non mandatory fields for length
        baseDataValidator.reset().parameter(GUARANTOR_JSON_INPUT_PARAMS.ADDRESS_LINE_1.getValue()).value(this.addressLine1).ignoreIfNull()
                .notExceedingLengthOf(50);
        baseDataValidator.reset().parameter(GUARANTOR_JSON_INPUT_PARAMS.ADDRESS_LINE_2.getValue()).value(this.addressLine2).ignoreIfNull()
                .notExceedingLengthOf(50);
        baseDataValidator.reset().parameter(GUARANTOR_JSON_INPUT_PARAMS.CITY.getValue()).value(this.city).ignoreIfNull()
                .notExceedingLengthOf(50);
        baseDataValidator.reset().parameter(GUARANTOR_JSON_INPUT_PARAMS.STATE.getValue()).value(this.state).ignoreIfNull()
                .notExceedingLengthOf(50);
        baseDataValidator.reset().parameter(GUARANTOR_JSON_INPUT_PARAMS.ZIP.getValue()).value(this.zip).ignoreIfNull()
                .notExceedingLengthOf(50);
        baseDataValidator.reset().parameter(GUARANTOR_JSON_INPUT_PARAMS.COUNTRY.getValue()).value(this.country).ignoreIfNull()
                .notExceedingLengthOf(50);
        baseDataValidator.reset().parameter(GUARANTOR_JSON_INPUT_PARAMS.MOBILE_NUMBER.getValue()).value(this.mobileNumber).ignoreIfNull()
                .notExceedingLengthOf(20);
        baseDataValidator.reset().parameter(GUARANTOR_JSON_INPUT_PARAMS.PHONE_NUMBER.getValue()).value(this.housePhoneNumber)
                .ignoreIfNull().notExceedingLengthOf(20);
        baseDataValidator.reset().parameter(GUARANTOR_JSON_INPUT_PARAMS.COMMENT.getValue()).value(this.comment).ignoreIfNull()
                .notExceedingLengthOf(500);
    }

    /**
     * @param dataValidationErrors
     * @return
     */
    private DataValidatorBuilder getDataValidator(List<ApiParameterError> dataValidationErrors) {
        DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("Guarantor");
        return baseDataValidator;
    }
}
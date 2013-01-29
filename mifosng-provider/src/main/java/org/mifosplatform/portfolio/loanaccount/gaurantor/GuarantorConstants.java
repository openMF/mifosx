package org.mifosplatform.portfolio.loanaccount.gaurantor;

import java.util.HashSet;
import java.util.Set;

public class GuarantorConstants {

    /***
     * Enum of all parameters passed in while creating/updating a loan product
     ***/
    public static enum GUARANTOR_JSON_INPUT_PARAMS {
        LOAN_ID("loanId"), GUARANTOR_TYPE_ID("guarantorTypeId"), ENTITY_ID("entityId"), FIRSTNAME("firstname"), LASTNAME("lastname"), ADDRESS_LINE_1(
                "addressLine1"), ADDRESS_LINE_2("addressLine2"), CITY("city"), STATE("state"), ZIP("zip"), COUNTRY("country"), MOBILE_NUMBER(
                "mobileNumber"), PHONE_NUMBER("housePhoneNumber"), COMMENT("comment"), DATE_OF_BIRTH("dob"), ;

        private final String value;

        private GUARANTOR_JSON_INPUT_PARAMS(final String value) {
            this.value = value;
        }

        private static final Set<String> values = new HashSet<String>();
        static {
            for (GUARANTOR_JSON_INPUT_PARAMS type : GUARANTOR_JSON_INPUT_PARAMS.values()) {
                values.add(type.value);
            }
        }

        public static Set<String> getAllValues() {
            return values;
        }

        @Override
        public String toString() {
            return name().toString().replaceAll("_", " ");
        }

        public String getValue() {
            return this.value;
        }
    }
}

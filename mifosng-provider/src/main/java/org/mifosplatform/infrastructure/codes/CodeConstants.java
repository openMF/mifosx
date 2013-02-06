package org.mifosplatform.infrastructure.codes;

import java.util.HashSet;
import java.util.Set;

public class CodeConstants {

    /***
     * Enum of all parameters passed in while creating/updating a code and code value
     ***/
    public static enum CODEVALUE_JSON_INPUT_PARAMS {
        CODEVALUE_ID("id"), NAME("name"), POSITION("position");

        private final String value;

        private CODEVALUE_JSON_INPUT_PARAMS(final String value) {
            this.value = value;
        }

        private static final Set<String> values = new HashSet<String>();
        static {
            for (CODEVALUE_JSON_INPUT_PARAMS type : CODEVALUE_JSON_INPUT_PARAMS.values()) {
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

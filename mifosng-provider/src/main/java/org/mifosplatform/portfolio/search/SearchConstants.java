package org.mifosplatform.portfolio.search;

import java.util.HashSet;
import java.util.Set;

public class SearchConstants {

    public static enum SEARCH_RESPONSE_PARAMETERS {
        ENTITY_ID("entityId"), ENTITY_ACCOUNT_NO("entityAccountNo"), ENTITY_EXTERNAL_ID("entityExternalId"), ENTITY_NAME("entityName"), ENTITY_TYPE(
                "entityType"), PARENT_ID("parentId"), PARENT_NAME("parentName");

        private final String value;

        private SEARCH_RESPONSE_PARAMETERS(final String value) {
            this.value = value;
        }

        private static final Set<String> values = new HashSet<String>();
        static {
            for (SEARCH_RESPONSE_PARAMETERS param : SEARCH_RESPONSE_PARAMETERS.values()) {
                values.add(param.value);
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
    
    public static enum SEARCH_SUPPORTED_PARAMETERS {
        QUERY("query"), RESOURCE("resource");

        private final String value;

        private SEARCH_SUPPORTED_PARAMETERS(final String value) {
            this.value = value;
        }

        private static final Set<String> values = new HashSet<String>();
        static {
            for (SEARCH_SUPPORTED_PARAMETERS param : SEARCH_SUPPORTED_PARAMETERS.values()) {
                values.add(param.value);
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

    public static enum SEARCH_SUPPORTED_RESOURCES {
        CLIENT("client"), GROUP("group"), LOAN("loan");

        private final String value;

        private SEARCH_SUPPORTED_RESOURCES(final String value) {
            this.value = value;
        }

        private static final Set<String> values = new HashSet<String>();
        static {
            for (SEARCH_SUPPORTED_RESOURCES param : SEARCH_SUPPORTED_RESOURCES.values()) {
                values.add(param.value);
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

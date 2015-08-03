/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.collateral.api;

import java.util.HashSet;
import java.util.Set;

public class CollateralApiConstants {

    public static final String COLLATERAL_CODE_NAME = "LoanCollateral";
    public static final String TW_MAKE_TYPE = "MakeType";
    public static final String GOLD_FINENESS_TYPE = "GoldFineness";
    public static final String JEWELLERY_KIND_TYPE = "Jewellery";

    /***
     * Enum of all parameters passed in while creating/updating a collateral
     ***/
    public static enum COLLATERAL_JSON_INPUT_PARAMS {
        LOAN_ID("loanId"), COLLATERAL_ID("collateralId"), COLLATERAL_TYPE_ID("collateralTypeId"),GOLD_FINENESS_TYPE_ID("goldfineTypeId"),JEWELLERY_KIND_TYPE_ID("jewelleryTypeId"),TW_MAKE_TYPE ("maketwoTypeId"),GROSS_WEIGHT("gross"),IMPURITY_WEIGHT("impurity"),NET_WEIGHT("net"),STONE_WT("stone"), VALUE("value"),ACTUALCOST("actualcost"), DESCRIPTION("description");

        private final String value;

        private COLLATERAL_JSON_INPUT_PARAMS(final String value) {
            this.value = value;
        }

        private static final Set<String> values = new HashSet<>();
        static {
            for (final COLLATERAL_JSON_INPUT_PARAMS type : COLLATERAL_JSON_INPUT_PARAMS.values()) {
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

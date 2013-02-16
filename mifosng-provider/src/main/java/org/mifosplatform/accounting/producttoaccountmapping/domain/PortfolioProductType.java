/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.accounting.producttoaccountmapping.domain;

import java.util.HashMap;
import java.util.Map;

public enum PortfolioProductType {
    LOAN(1, "productType.loan"), SAVING(2, "productType.saving");

    private final Integer value;
    private final String code;

    private PortfolioProductType(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    @Override
    public String toString() {
        return name().toString().replaceAll("_", " ");
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return code;
    }

    private static final Map<Integer, PortfolioProductType> intToEnumMap = new HashMap<Integer, PortfolioProductType>();
    static {
        for (PortfolioProductType type : PortfolioProductType.values()) {
            intToEnumMap.put(type.value, type);
        }
    }

    public static PortfolioProductType fromInt(int i) {
        PortfolioProductType type = intToEnumMap.get(Integer.valueOf(i));
        return type;
    }

}

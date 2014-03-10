/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.interestratechart;

import java.util.ArrayList;
import java.util.List;


/**
 * An enumeration of supported calendar periods used in savings.
 */
public enum InterestRateChartPeriodType {
    INVALID(0, "interestChartPeriodType.invalid"),
    DAYS(1, "interestChartPeriodType.days"), //
    WEEKS(2, "interestChartPeriodType.weeks"), //
    MONTHS(3, "interestChartPeriodType.months"), //
    YEARS(4, "interestChartPeriodType.years"); //
    

    private final Integer value;
    private final String code;

    private InterestRateChartPeriodType(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    public static InterestRateChartPeriodType fromInt(final Integer type) {
        InterestRateChartPeriodType periodType = InterestRateChartPeriodType.INVALID;
        if (type != null) {
            switch (type) {
                case 1:
                    periodType = InterestRateChartPeriodType.DAYS;
                break;
                case 2:
                    periodType = InterestRateChartPeriodType.WEEKS;
                break;
                case 3:
                    periodType = InterestRateChartPeriodType.MONTHS;
                break;
                case 4:
                    periodType = InterestRateChartPeriodType.YEARS;
                break;
            }
        }
        return periodType;
    }
    
    public static Object[] integerValues() {
        final List<Integer> values = new ArrayList<Integer>();
        for (final InterestRateChartPeriodType enumType : values()) {
            if (enumType.getValue() > 0) {
                values.add(enumType.getValue());
            }
        }

        return values.toArray();
    }
    
    public boolean isInvalid(){
        return this.value.equals(InterestRateChartPeriodType.INVALID.value);
    }
}

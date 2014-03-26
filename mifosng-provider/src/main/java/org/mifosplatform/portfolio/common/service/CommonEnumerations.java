package org.mifosplatform.portfolio.common.service;

import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.portfolio.common.domain.PeriodFrequencyType;

public class CommonEnumerations {

    public static EnumOptionData termFrequencyType(final int id, final String codePrefix) {
        return termFrequencyType(PeriodFrequencyType.fromInt(id), codePrefix);
    }

    public static EnumOptionData termFrequencyType(final PeriodFrequencyType type, final String codePrefix) {
        EnumOptionData optionData = null;
        switch (type) {
            case DAYS:
                optionData = new EnumOptionData(PeriodFrequencyType.DAYS.getValue().longValue(), codePrefix
                        + PeriodFrequencyType.DAYS.getCode(), "Days");
            break;
            case WEEKS:
                optionData = new EnumOptionData(PeriodFrequencyType.WEEKS.getValue().longValue(), codePrefix
                        + PeriodFrequencyType.WEEKS.getCode(), "Weeks");
            break;
            case MONTHS:
                optionData = new EnumOptionData(PeriodFrequencyType.MONTHS.getValue().longValue(), codePrefix
                        + PeriodFrequencyType.MONTHS.getCode(), "Months");
            break;
            default:
                optionData = new EnumOptionData(PeriodFrequencyType.INVALID.getValue().longValue(), PeriodFrequencyType.INVALID.getCode(),
                        "Invalid");
            break;
        }
        return optionData;
    }

}

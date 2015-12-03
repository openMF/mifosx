/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.creditcheck.data;

import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.portfolio.creditcheck.domain.CreditCheckRelatedEntity;
import org.mifosplatform.portfolio.creditcheck.domain.CreditCheckSeverityLevel;

public class CreditCheckEnumerations {
    
    public static EnumOptionData severityLevel(final Integer severityLevel) {
        return severityLevel(CreditCheckSeverityLevel.fromInt(severityLevel));
    }

    public static EnumOptionData severityLevel(final CreditCheckSeverityLevel severityLevel) {
        EnumOptionData enumData = null;
        
        if (severityLevel != null) {
            switch (severityLevel) {
                case ERROR:
                    enumData = new EnumOptionData(severityLevel.getValue().longValue(), severityLevel.getCode(), 
                            "Error");
                    break;
                    
                case WARNING:
                    enumData = new EnumOptionData(severityLevel.getValue().longValue(), severityLevel.getCode(), 
                            "Warning");
                    break;
                    
                case NOTICE:
                    enumData = new EnumOptionData(severityLevel.getValue().longValue(), severityLevel.getCode(), 
                            "Notice");
                    break;
                    
                default:
                    break;
            }
        }
        
        return enumData;
    }
    
    public static EnumOptionData CreditCheckRelatedEntity(final Integer creditCheckRelatedEntity) {
        return CreditCheckRelatedEntity(CreditCheckRelatedEntity.fromInt(creditCheckRelatedEntity));
    }
    
    public static EnumOptionData CreditCheckRelatedEntity(final CreditCheckRelatedEntity creditCheckRelatedEntity) {
        EnumOptionData enumData = null;
        
        if (creditCheckRelatedEntity != null) {
            switch (creditCheckRelatedEntity) {
                case LOAN:
                    enumData = new EnumOptionData(creditCheckRelatedEntity.getValue().longValue(), creditCheckRelatedEntity.getCode(), 
                            "Loan");
                    break;
                    
                case SAVINGS:
                    enumData = new EnumOptionData(creditCheckRelatedEntity.getValue().longValue(), creditCheckRelatedEntity.getCode(), 
                            "Savings Account");
                    break;
                    
                default:
                    break;
            }
        }
        
        return enumData;
    }
}

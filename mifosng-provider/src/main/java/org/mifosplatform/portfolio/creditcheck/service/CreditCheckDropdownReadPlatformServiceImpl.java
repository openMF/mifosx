/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.creditcheck.service;

import java.util.ArrayList;
import java.util.List;

import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.portfolio.creditcheck.data.CreditCheckEnumerations;
import org.mifosplatform.portfolio.creditcheck.domain.CreditCheckRelatedEntity;
import org.mifosplatform.portfolio.creditcheck.domain.CreditCheckSeverityLevel;
import org.springframework.stereotype.Service;

@Service
public class CreditCheckDropdownReadPlatformServiceImpl implements CreditCheckDropdownReadPlatformService {

    @Override
    public List<EnumOptionData> retrieveRelatedEntityOptions() {
        final List<EnumOptionData> relatedEntityOptions = new ArrayList<>();
        
        for (final CreditCheckRelatedEntity creditCheckRelatedEntity : CreditCheckRelatedEntity.values()) {
            
            if (CreditCheckRelatedEntity.INVALID.equals(creditCheckRelatedEntity)) {
                continue;
            }
            
            relatedEntityOptions.add(CreditCheckEnumerations.CreditCheckRelatedEntity(creditCheckRelatedEntity));
        }
        
        return relatedEntityOptions;
    }

    @Override
    public List<EnumOptionData> retrieveSeverityLevelOptions() {
        final List<EnumOptionData> severityLevelOptions = new ArrayList<>();
        
        for (final CreditCheckSeverityLevel creditCheckSeverityLevel : CreditCheckSeverityLevel.values()) {
            
            if (CreditCheckSeverityLevel.INVALID.equals(creditCheckSeverityLevel)) {
                continue;
            }
            
            severityLevelOptions.add(CreditCheckEnumerations.severityLevel(creditCheckSeverityLevel));
        }
        
        return severityLevelOptions;
    }
}

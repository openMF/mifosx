/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.savings.service;

import java.util.Collection;

import org.mifosplatform.infrastructure.core.data.EnumOptionData;

public interface DepositsDropdownReadPlatformService {

    // fixed deposit product dropdowns

    Collection<EnumOptionData> retrieveInterestFreePeriodFrequencyTypeOptions();

    Collection<EnumOptionData> retrievePreClosurePenalInterestOnTypeOptions();

    // RECURRING deposit product dropdowns
    Collection<EnumOptionData> retrieveRecurringDepositTypeOptions();

    Collection<EnumOptionData> retrieveRecurringDepositFrequencyTypeOptions();

    Collection<EnumOptionData> retrieveDepositTermTypeOptions();

    Collection<EnumOptionData> retrieveInMultiplesOfDepositTermTypeOptions();

}
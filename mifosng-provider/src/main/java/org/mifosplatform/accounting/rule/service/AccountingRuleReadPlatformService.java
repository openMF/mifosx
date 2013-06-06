/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.accounting.rule.service;

import java.util.List;

import org.mifosplatform.accounting.rule.data.AccountingRuleData;

public interface AccountingRuleReadPlatformService {

    List<AccountingRuleData> retrieveAllAccountingRules(Long OfficeId, boolean isAssociationParametersExists);

    AccountingRuleData retrieveAccountingRuleById(Long accountingRuleId);

}

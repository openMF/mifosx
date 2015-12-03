/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.sms.service;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.sms.data.SmsData;

public interface SmsReadPlatformService {

    Collection<SmsData> retrieveAll();

    SmsData retrieveOne(Long resourceId);
    
    Collection<SmsData> retrieveAllPending(Integer limit);
    
    Collection<SmsData> retrieveAllSent(Integer limit);
    
    Collection<SmsData> retrieveAllDelivered(Integer limit);
    
    Collection<SmsData> retrieveAllFailed(Integer limit);

    Page<SmsData> retrieveSmsByStatus(Integer limit,Integer status,Date dateFrom,Date dateTo);
    
    List<Long> retrieveExternalIdsOfAllSent(Integer limit);
}

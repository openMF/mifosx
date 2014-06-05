/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.sms.data;

import org.mifosplatform.infrastructure.core.data.EnumOptionData;

/**
 * Immutable data object representing a SMS message.
 */
public class SmsData {

    @SuppressWarnings("unused")
    private final Long id;
    @SuppressWarnings("unused")
    private final Long groupId;
    @SuppressWarnings("unused")
    private final Long clientId;
    @SuppressWarnings("unused")
    private final Long staffId;
    @SuppressWarnings("unused")
    private final EnumOptionData status;
    @SuppressWarnings("unused")
    private final String mobileNo;
    @SuppressWarnings("unused")
    private final String message;
    @SuppressWarnings("unused")
    private final Long gatewayId;

    public static SmsData instance(final Long id, final Long groupId, final Long clientId, final Long staffId, final EnumOptionData status,
            final String mobileNo, final String message, final Long gatewayId) {
        return new SmsData(id, groupId, clientId, staffId, status, mobileNo, message, gatewayId);
    }

    private SmsData(final Long id, final Long groupId, final Long clientId, final Long staffId, final EnumOptionData status,
            final String mobileNo, final String message, final Long gatewayId) {
        this.id = id;
        this.groupId = groupId;
        this.clientId = clientId;
        this.staffId = staffId;
        this.status = status;
        this.mobileNo = mobileNo;
        this.message = message;
        this.gatewayId = gatewayId;
    }
}
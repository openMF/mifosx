/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.sms.domain;

public enum SmsCampaignStatus {

    INVALID(0, "smsCampaignStatus.invalid"), //
    PENDING(100, "smsCampaignStatus.pending"), //
    ACTIVE(300, "smsCampaignStatus.active"), //
    CLOSED(600, "smsCampaignStatus.closed");

    private final Integer value;
    private final String code;

    private SmsCampaignStatus(Integer value, String code) {
        this.value = value;
        this.code = code;
    }

    public static SmsCampaignStatus fromInt(final Integer statusValue) {

        SmsCampaignStatus enumeration = SmsCampaignStatus.INVALID;
        switch (statusValue) {
            case 100:
                enumeration = SmsCampaignStatus.PENDING;
                break;
            case 300:
                enumeration = SmsCampaignStatus.ACTIVE;
                break;
            case 600:
                enumeration = SmsCampaignStatus.CLOSED;
                break;
        }
        return enumeration;
    }

    public Integer getValue() {
        return value;
    }

    public String getCode() {
        return code;
    }
    public boolean isActive(){
        return this.value.equals(SmsCampaignStatus.ACTIVE.getValue());
    }

    public boolean isPending(){
        return this.value.equals(SmsCampaignStatus.PENDING.getValue());
    }

    public boolean isClosed(){
         return this.value.equals(SmsCampaignStatus.CLOSED.getValue());
    }
}

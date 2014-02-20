/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.loanaccount.domain;

import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.portfolio.loanaccount.domain.LoanStatus;

public class LoanEnumerations {

    public static EnumOptionData status(final Integer statusId) {
        return status(LoanStatus.fromInt(statusId));
    }

    public static EnumOptionData status(final LoanStatus status) {
        EnumOptionData optionData = new EnumOptionData(LoanStatus.INVALID.getValue().longValue(), 
                LoanStatus.INVALID.getCode(), "Invalid");
        
        switch (status) {
            case INVALID:
                optionData = new EnumOptionData(LoanStatus.INVALID.getValue().longValue(), LoanStatus.INVALID.getCode(), "Invalid");
                break;
            case SUBMITTED_AND_PENDING_APPROVAL:
                optionData = new EnumOptionData(LoanStatus.SUBMITTED_AND_PENDING_APPROVAL.getValue().longValue(), 
                        LoanStatus.SUBMITTED_AND_PENDING_APPROVAL.getCode(), "Submitted and pending approval");
                break;
            case APPROVED:
                optionData = new EnumOptionData(LoanStatus.APPROVED.getValue().longValue(), LoanStatus.APPROVED.getCode(), "Approved");
                break;
            case ACTIVE:
                optionData = new EnumOptionData(LoanStatus.ACTIVE.getValue().longValue(), LoanStatus.ACTIVE.getCode(), "Active");
                break;
            case TRANSFER_IN_PROGRESS:
                optionData = new EnumOptionData(LoanStatus.TRANSFER_IN_PROGRESS.getValue().longValue(),
                        LoanStatus.TRANSFER_IN_PROGRESS.getCode(), "Transfer in progress");
                break;
            case TRANSFER_ON_HOLD:
                optionData = new EnumOptionData(LoanStatus.TRANSFER_ON_HOLD.getValue().longValue(),
                        LoanStatus.TRANSFER_ON_HOLD.getCode(), "Transfer on hold");
                break;
            case WITHDRAWN_BY_CLIENT:
                optionData = new EnumOptionData(LoanStatus.WITHDRAWN_BY_CLIENT.getValue().longValue(),
                        LoanStatus.WITHDRAWN_BY_CLIENT.getCode(), "Withdrawn by client");
                break;
            case REJECTED:
                optionData = new EnumOptionData(LoanStatus.REJECTED.getValue().longValue(), LoanStatus.REJECTED.getCode(), "Rejected");
                break;
            case CLOSED_OBLIGATIONS_MET:
                optionData = new EnumOptionData(LoanStatus.CLOSED_OBLIGATIONS_MET.getValue().longValue(), 
                        LoanStatus.CLOSED_OBLIGATIONS_MET.getCode(), "Closed obligation met");
                break;
            case CLOSED_WRITTEN_OFF:
                optionData = new EnumOptionData(LoanStatus.CLOSED_WRITTEN_OFF.getValue().longValue(), 
                        LoanStatus.CLOSED_WRITTEN_OFF.getCode(), "Closed written off");
                break;
            case CLOSED_RESCHEDULE_OUTSTANDING_AMOUNT:
                optionData = new EnumOptionData(LoanStatus.CLOSED_RESCHEDULE_OUTSTANDING_AMOUNT.getValue().longValue(), 
                        LoanStatus.CLOSED_RESCHEDULE_OUTSTANDING_AMOUNT.getCode(), "Closed reschedule outstanding amount");
                break;
            case OVERPAID:
                optionData = new EnumOptionData(LoanStatus.OVERPAID.getValue().longValue(), LoanStatus.OVERPAID.getCode(), "Overpaid");
                break;
            default:
                break;
        }

        return optionData;
    }
}
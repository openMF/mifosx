/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.loanaccount.domain;

/**
 * Enum representation of loan status states.
 */
public enum LoanStatus {

    INVALID(0, "loanStatusType.invalid"), //
    SUBMITTED_AND_PENDING_APPROVAL(100, "loanStatusType.submitted.and.pending.approval"), //
    APPROVED(200, "loanStatusType.approved"), //
    ACTIVE(300, "loanStatusType.active"), //
    WITHDRAWN_BY_CLIENT(400, "loanStatusType.withdrawn.by.client"), //
    REJECTED(500, "loanStatusType.rejected"), //
    CLOSED_OBLIGATIONS_MET(600, "loanStatusType.closed.obligations.met"), //
    CLOSED_WRITTEN_OFF(601, "loanStatusType.closed.written.off"), //
    CLOSED_RESCHEDULE_OUTSTANDING_AMOUNT(602, "loanStatusType.closed.reschedule.outstanding.amount"), //
    OVERPAID(700, "loanStatusType.overpaid");

    private final Integer value;
    private final String code;

    public static LoanStatus fromInt(final Integer statusValue) {

        LoanStatus enumeration = LoanStatus.INVALID;
        switch (statusValue) {
            case 100:
                enumeration = LoanStatus.SUBMITTED_AND_PENDING_APPROVAL;
            break;
            case 200:
                enumeration = LoanStatus.APPROVED;
            break;
            case 300:
                enumeration = LoanStatus.ACTIVE;
            break;
            case 400:
                enumeration = LoanStatus.WITHDRAWN_BY_CLIENT;
            break;
            case 500:
                enumeration = LoanStatus.REJECTED;
            break;
            case 600:
                enumeration = LoanStatus.CLOSED_OBLIGATIONS_MET;
            break;
            case 601:
                enumeration = LoanStatus.CLOSED_WRITTEN_OFF;
            break;
            case 602:
                enumeration = LoanStatus.CLOSED_RESCHEDULE_OUTSTANDING_AMOUNT;
            break;
            case 700:
                enumeration = LoanStatus.OVERPAID;
            break;
        }
        return enumeration;
    }

    private LoanStatus(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public boolean hasStateOf(final LoanStatus state) {
        return this.value.equals(state.getValue());
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return code;
    }

    public boolean isSubmittedAndPendingApproval() {
        return this.value.equals(LoanStatus.SUBMITTED_AND_PENDING_APPROVAL.getValue());
    }

    public boolean isApproved() {
        return this.value.equals(LoanStatus.APPROVED.getValue());
    }

    public boolean isActive() {
        return this.value.equals(LoanStatus.ACTIVE.getValue());
    }

    public boolean isClosed() {
        return isClosedObligationsMet() || isClosedWrittenOff() || isClosedWithOutsandingAmountMarkedForReschedule();
    }

    public boolean isClosedObligationsMet() {
        return this.value.equals(LoanStatus.CLOSED_OBLIGATIONS_MET.getValue());
    }

    public boolean isClosedWrittenOff() {
        return this.value.equals(LoanStatus.CLOSED_WRITTEN_OFF.getValue());
    }

    public boolean isClosedWithOutsandingAmountMarkedForReschedule() {
        return this.value.equals(LoanStatus.CLOSED_RESCHEDULE_OUTSTANDING_AMOUNT.getValue());
    }

    public boolean isWithdrawnByClient() {
        return this.value.equals(LoanStatus.WITHDRAWN_BY_CLIENT.getValue());
    }

    public boolean isRejected() {
        return this.value.equals(LoanStatus.REJECTED.getValue());
    }

    public boolean isActiveOrAwaitingApprovalOrDisbursal() {
        return this.isApproved() || this.isSubmittedAndPendingApproval() || this.isActive();
    }
}
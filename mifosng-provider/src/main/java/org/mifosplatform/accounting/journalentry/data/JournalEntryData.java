/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.accounting.journalentry.data;

import java.math.BigDecimal;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;

/**
 * Immutable object representing a General Ledger Account
 * 
 * Note: no getter/setters required as google will produce json from fields of
 * object.
 */
public class JournalEntryData {

    private final Long id;
    private final Long officeId;
    @SuppressWarnings("unused")
    private final String officeName;
    @SuppressWarnings("unused")
    private final String glAccountName;
    private final Long glAccountId;
    @SuppressWarnings("unused")
    private final String glAccountCode;
    private final EnumOptionData glAccountType;
    @SuppressWarnings("unused")
    private final LocalDate transactionDate;
    private final EnumOptionData entryType;
    private final BigDecimal amount;
    @SuppressWarnings("unused")
    private final String transactionId;
    @SuppressWarnings("unused")
    private final Boolean manualEntry;
    @SuppressWarnings("unused")
    private final EnumOptionData entityType;
    @SuppressWarnings("unused")
    private final Long entityId;
    @SuppressWarnings("unused")
    private final Long createdByUserId;
    @SuppressWarnings("unused")
    private final LocalDate createdDate;
    @SuppressWarnings("unused")
    private final String createdByUserName;
    @SuppressWarnings("unused")
    private final String comments;
    @SuppressWarnings("unused")
    private final Boolean reversed;
    @SuppressWarnings("unused")
    private final String referenceNumber;
    @SuppressWarnings("unused")
    private final BigDecimal officeRunningBalance;
    @SuppressWarnings("unused")
    private final BigDecimal organizationRunningBalance;
    @SuppressWarnings("unused")
    private final Boolean runningBalanceComputed;

    public JournalEntryData(final Long id, final Long officeId, final String officeName, final String glAccountName,
            final Long glAccountId, final String glAccountCode, final EnumOptionData glAccountClassification,
            final LocalDate transactionDate, final EnumOptionData entryType, final BigDecimal amount, final String transactionId,
            final Boolean manualEntry, final EnumOptionData entityType, final Long entityId, final Long createdByUserId,
            final LocalDate createdDate, final String createdByUserName, final String comments, final Boolean reversed,
            final String referenceNumber, final BigDecimal officeRunningBalance, final BigDecimal organizationRunningBalance,
            final Boolean runningBalanceComputed) {
        this.id = id;
        this.officeId = officeId;
        this.officeName = officeName;
        this.glAccountName = glAccountName;
        this.glAccountId = glAccountId;
        this.glAccountCode = glAccountCode;
        this.glAccountType = glAccountClassification;
        this.transactionDate = transactionDate;
        this.entryType = entryType;
        this.amount = amount;
        this.transactionId = transactionId;
        this.manualEntry = manualEntry;
        this.entityType = entityType;
        this.entityId = entityId;
        this.createdByUserId = createdByUserId;
        this.createdDate = createdDate;
        this.createdByUserName = createdByUserName;
        this.comments = comments;
        this.reversed = reversed;
        this.referenceNumber = referenceNumber;
        this.officeRunningBalance = officeRunningBalance;
        this.organizationRunningBalance = organizationRunningBalance;
        this.runningBalanceComputed = runningBalanceComputed;
    }

    public Long getId() {
        return this.id;
    }

    public Long getGlAccountId() {
        return this.glAccountId;
    }

    public EnumOptionData getGlAccountType() {
        return this.glAccountType;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public EnumOptionData getEntryType() {
        return this.entryType;
    }

    public Long getOfficeId() {
        return this.officeId;
    }
}
/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.accounting.rule.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.mifosplatform.accounting.glaccount.domain.GLAccount;
import org.mifosplatform.accounting.journalentry.domain.JournalEntryType;
import org.mifosplatform.accounting.rule.api.AccountingRuleJsonInputParams;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.organisation.office.domain.Office;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "acc_accounting_rule", uniqueConstraints = { @UniqueConstraint(columnNames = { "name" }, name = "accounting_rule_name_unique") })
public class AccountingRule extends AbstractPersistable<Long> {

    @Column(name = "name", nullable = false, length = 500)
    private String name;

    @ManyToOne
    @JoinColumn(name = "office_id", nullable = true)
    private Office office;

    @ManyToOne
    @JoinColumn(name = "debit_account_id", nullable = true)
    private GLAccount accountToDebit;

    @ManyToOne
    @JoinColumn(name = "credit_account_id", nullable = true)
    private GLAccount accountToCredit;

    @Column(name = "description", nullable = true, length = 500)
    private String description;

    @Column(name = "system_defined", nullable = false)
    private Boolean systemDefined;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "accountingRule", orphanRemoval = true)
    private final List<AccountingTagRule> accountingTagRules = new ArrayList<AccountingTagRule>();

    @Column(name = "allow_multiple_credits", nullable = false)
    private boolean allowMultipleCreditEntries;

    @Column(name = "allow_multiple_debits", nullable = false)
    private boolean allowMultipleDebitEntries;

    protected AccountingRule() {}

    private AccountingRule(final Office office, final GLAccount accountToDebit, final GLAccount accountToCredit, final String name,
            final String description, final boolean systemDefined, final boolean allowMultipleCreditEntries,
            final boolean allowMultipleDebitEntries) {
        this.accountToDebit = accountToDebit;
        this.accountToCredit = accountToCredit;
        this.name = name;
        this.office = office;
        this.description = StringUtils.defaultIfEmpty(description, null);
        if (this.description != null) {
            this.description = this.description.trim();
        }
        this.systemDefined = systemDefined;
        this.allowMultipleCreditEntries = allowMultipleCreditEntries;
        this.allowMultipleDebitEntries = allowMultipleDebitEntries;
    }

    public static AccountingRule fromJson(final Office office, final GLAccount accountToDebit, final GLAccount accountToCredit,
            final JsonCommand command, final boolean allowMultipleCreditEntries, final boolean allowMultipleDebitEntries) {
        final String name = command.stringValueOfParameterNamed(AccountingRuleJsonInputParams.NAME.getValue());
        final String description = command.stringValueOfParameterNamed(AccountingRuleJsonInputParams.DESCRIPTION.getValue());
        final boolean systemDefined = false;
        return new AccountingRule(office, accountToDebit, accountToCredit, name, description, systemDefined, allowMultipleCreditEntries,
                allowMultipleDebitEntries);
    }

    public Map<String, Object> update(final JsonCommand command) {
        final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(10);
        handlePropertyUpdate(command, actualChanges, AccountingRuleJsonInputParams.OFFICE_ID.getValue(), this.office == null ? 0L
                : this.office.getId());
        handlePropertyUpdate(command, actualChanges, AccountingRuleJsonInputParams.ACCOUNT_TO_DEBIT.getValue(),
                this.accountToDebit == null ? 0L : this.accountToDebit.getId());
        handlePropertyUpdate(command, actualChanges, AccountingRuleJsonInputParams.ACCOUNT_TO_CREDIT.getValue(),
                this.accountToCredit == null ? 0L : this.accountToCredit.getId());
        handlePropertyUpdate(command, actualChanges, AccountingRuleJsonInputParams.NAME.getValue(), this.name);
        handlePropertyUpdate(command, actualChanges, AccountingRuleJsonInputParams.DESCRIPTION.getValue(), this.description);
        handlePropertyUpdate(command, actualChanges, AccountingRuleJsonInputParams.SYSTEM_DEFINED.getValue(), this.systemDefined);
        handlePropertyUpdate(command, actualChanges, AccountingRuleJsonInputParams.ALLOW_MULTIPLE_CREDIT_ENTRIES.getValue(),
                this.allowMultipleCreditEntries);
        handlePropertyUpdate(command, actualChanges, AccountingRuleJsonInputParams.ALLOW_MULTIPLE_DEBIT_ENTRIES.getValue(),
                this.allowMultipleDebitEntries);
        return actualChanges;
    }

    private void handlePropertyUpdate(final JsonCommand command, final Map<String, Object> actualChanges, final String paramName,
            final String propertyToBeUpdated) {
        if (command.isChangeInStringParameterNamed(paramName, propertyToBeUpdated)) {
            final String newValue = command.stringValueOfParameterNamed(paramName);
            actualChanges.put(paramName, newValue);
            // now update actual property
            if (paramName.equals(AccountingRuleJsonInputParams.DESCRIPTION.getValue())) {
                this.description = newValue;
            } else if (paramName.equals(AccountingRuleJsonInputParams.NAME.getValue())) {
                this.name = newValue;
            }
        }
    }

    private void handlePropertyUpdate(final JsonCommand command, final Map<String, Object> actualChanges, final String paramName,
            final boolean propertyToBeUpdated) {
        if (command.isChangeInBooleanParameterNamed(paramName, propertyToBeUpdated)) {
            final Boolean newValue = command.booleanObjectValueOfParameterNamed(paramName);
            actualChanges.put(paramName, newValue);
            // now update actual property
            if (paramName.equals(AccountingRuleJsonInputParams.SYSTEM_DEFINED.getValue())) {
                this.systemDefined = newValue;
            } else if (paramName.equals(AccountingRuleJsonInputParams.ALLOW_MULTIPLE_CREDIT_ENTRIES.getValue())) {
                if (this.accountToCredit == null) {
                    this.allowMultipleCreditEntries = newValue;
                }
            } else if (paramName.equals(AccountingRuleJsonInputParams.ALLOW_MULTIPLE_DEBIT_ENTRIES.getValue())) {
                if (this.accountToDebit == null) {
                    this.allowMultipleDebitEntries = newValue;
                }
            }
        }
    }

    private void handlePropertyUpdate(final JsonCommand command, final Map<String, Object> actualChanges, final String paramName,
            final Long propertyToBeUpdated) {
        if (command.isChangeInLongParameterNamed(paramName, propertyToBeUpdated)) {
            final Long newValue = command.longValueOfParameterNamed(paramName);
            actualChanges.put(paramName, newValue);
            // now update actual property
            if (paramName.equals(AccountingRuleJsonInputParams.ACCOUNT_TO_CREDIT.getValue())) {
                // do nothing as this is a nested property
            } else if (paramName.equals(AccountingRuleJsonInputParams.ACCOUNT_TO_DEBIT.getValue())) {
                // do nothing as this is a nested property
            } else if (paramName.equals(AccountingRuleJsonInputParams.OFFICE_ID.getValue())) {
                // do nothing as this is a nested property
            }
        }
    }

    public void setOffice(final Office office) {
        this.office = office;
    }

    public Office getOffice() {
        return this.office;
    }

    public GLAccount getAccountToDebit() {
        return this.accountToDebit;
    }

    public GLAccount getAccountToCredit() {
        return this.accountToCredit;
    }

    public void setAccountToDebit(final GLAccount accountToDebit) {
        this.accountToDebit = accountToDebit;
    }

    public void setAccountToCredit(final GLAccount accountToCredit) {
        this.accountToCredit = accountToCredit;
    }

    public String getDescription() {
        return this.description;
    }

    public List<AccountingTagRule> getAccountingTagRules() {
        return this.accountingTagRules;
    }

    public void updateAccountingRuleForTags(final List<AccountingTagRule> debitAccountingTagRules) {
        for (final AccountingTagRule accountingTagRule : debitAccountingTagRules) {
            accountingTagRule.updateAccountingTagRule(this);
            this.accountingTagRules.add(accountingTagRule);
        }
    }

    public void updateDebitAccount(final GLAccount accountToDebit) {
        this.accountToDebit = accountToDebit;
        this.allowMultipleDebitEntries = false;
    }

    public void updateCreditAccount(final GLAccount accountToCredit) {
        this.accountToCredit = accountToCredit;
        this.allowMultipleCreditEntries = false;
    }

    public void updateAllowMultipleCreditEntries(final boolean allowMultipleCreditEntries) {
        this.allowMultipleCreditEntries = allowMultipleCreditEntries;
    }

    public void updateAllowMultipleDebitEntries(final boolean allowMultipleDebitEntries) {
        this.allowMultipleDebitEntries = allowMultipleDebitEntries;
    }

    public void updateTags(final JournalEntryType type) {
        final Set<AccountingTagRule> existedCreditTags = new HashSet<AccountingTagRule>();
        final Set<AccountingTagRule> existedDebitTags = new HashSet<AccountingTagRule>();
        for (final AccountingTagRule accountingTagRule : this.accountingTagRules) {
            if (accountingTagRule.isCreditAccount()) {
                existedCreditTags.add(accountingTagRule);
            } else if (accountingTagRule.isDebitAccount()) {
                existedDebitTags.add(accountingTagRule);
            }
        }

        if (type.isCreditType()) {
            this.accountingTagRules.retainAll(existedCreditTags);
        } else if (type.isDebitType()) {
            this.accountingTagRules.retainAll(existedDebitTags);
        }

    }

    public Set<AccountingTagRule> getAccountingTagRulesByType(final JournalEntryType type) {
        final Set<AccountingTagRule> existedTags = new HashSet<AccountingTagRule>();
        for (final AccountingTagRule accountingTagRule : this.accountingTagRules) {
            if (accountingTagRule.getAccountType().equals(type.getValue())) {
                existedTags.add(accountingTagRule);
            }
        }
        return existedTags;
    }

    public void removeOldTags(final List<AccountingTagRule> accountsToRemove) {
        this.accountingTagRules.removeAll(accountsToRemove);
    }

}
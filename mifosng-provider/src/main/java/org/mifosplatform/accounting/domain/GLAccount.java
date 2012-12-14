package org.mifosplatform.accounting.domain;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.mifosplatform.accounting.api.commands.GLAccountCommand;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "acc_gl_account", uniqueConstraints = { @UniqueConstraint(columnNames = { "gl_code" }, name = "acc_gl_code") })
public class GLAccount extends AbstractPersistable<Long> {

    @SuppressWarnings("unused")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private GLAccount parent;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private List<GLAccount> children = new LinkedList<GLAccount>();

    @Column(name = "name", nullable = false, length = 45)
    private String name;

    @Column(name = "gl_code", nullable = false, length = 100)
    private String glCode;

    @Column(name = "disabled", nullable = false)
    private boolean disabled = false;

    @Column(name = "manual_journal_entries_allowed", nullable = false)
    private boolean manualEntriesAllowed = true;

    @SuppressWarnings("unused")
    @Column(name = "classification", nullable = false, length = 45)
    private String classification;

    @Column(name = "header_account", nullable = false)
    private boolean headerAccount;

    @SuppressWarnings("unused")
    @Column(name = "description", nullable = true, length = 500)
    private String description;

    public static GLAccount createNew(final GLAccount parent, final String name, final String glCode, final boolean disabled,
            final boolean manualEntriesAllowed, final String classification, final boolean headerAccount, final String description) {
        return new GLAccount(parent, name, glCode, disabled, manualEntriesAllowed, classification, headerAccount, description);
    }

    protected GLAccount() {
        //
    }

    private GLAccount(final GLAccount parent, final String name, final String glCode, final boolean disabled,
            final boolean manualEntriesAllowed, final String classification, final boolean headerAccount, final String description) {
        this.parent = parent;
        this.name = StringUtils.defaultIfEmpty(name, null);
        this.glCode = StringUtils.defaultIfEmpty(glCode, null);
        this.disabled = BooleanUtils.toBooleanDefaultIfNull(disabled, false);
        this.manualEntriesAllowed = BooleanUtils.toBooleanDefaultIfNull(manualEntriesAllowed, true);
        this.headerAccount = BooleanUtils.toBooleanDefaultIfNull(headerAccount, false);
        this.classification = classification;
        this.description = StringUtils.defaultIfEmpty(description, null);
    }

    public void update(final GLAccountCommand command, final GLAccount parentGLAccount) {
        if (command.isClassificationChanged()) {
            this.classification = command.getClassification();
        }
        if (command.isDescriptionChanged()) {
            this.description = command.getDescription();
        }
        if (command.isDisabledFlagChanged()) {
            this.disabled = command.getDisabled();
        }
        if (command.isGLCodeChanged()) {
            this.glCode = command.getGlCode();
        }
        if (command.isHeaderAccountFlagChanged()) {
            this.headerAccount = command.getHeaderAccount();
        }
        if (command.isManualEntriesAllowedFlagChanged()) {
            this.manualEntriesAllowed = command.getManualEntriesAllowed();
        }
        if (command.isNameChanged()) {
            this.name = command.getName();
        }
        if (command.isParentIdChanged()) {
            this.parent = parentGLAccount;
        }
    }

    public boolean isHeaderAccount() {
        return headerAccount;
    }

    public List<GLAccount> getChildren() {
        return this.children;
    }

    public boolean isDisabled() {
        return this.disabled;
    }

    public boolean isManualEntriesAllowed() {
        return this.manualEntriesAllowed;
    }

    public String getGlCode() {
        return this.glCode;
    }

    public void setGlCode(String glCode) {
        this.glCode = glCode;
    }

    public String getName() {
        return this.name;
    }

}
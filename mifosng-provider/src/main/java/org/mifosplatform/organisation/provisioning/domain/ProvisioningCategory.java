/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.organisation.provisioning.domain;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "m_provision_category", uniqueConstraints = { @UniqueConstraint(columnNames = { "category_name" }, name = "category_name") })
public class ProvisioningCategory extends AbstractPersistable<Long> {

    @Column(name = "category_name", nullable = false, unique = true)
    private String categoryName;

    @Column(name = "description", nullable = true)
    private String categoryDescription;

    protected ProvisioningCategory() {

    }

    private ProvisioningCategory(String categoryName, String categoryDescription) {
        this.categoryName = categoryName;
        this.categoryDescription = categoryDescription;
    }

    public static ProvisioningCategory fromJson(JsonCommand jsonCommand) {
        final String categoryName = jsonCommand.stringValueOfParameterNamed("categoryname");
        final String description = jsonCommand.stringValueOfParameterNamed("description");
        return new ProvisioningCategory(categoryName, description);
    }

    public Map<String, Object> update(JsonCommand command) {
        final Map<String, Object> actualChanges = new LinkedHashMap<>(2);
        final String nameParamName = "categoryname";
        if (command.isChangeInStringParameterNamed(nameParamName, this.categoryName)) {
            final String newValue = command.stringValueOfParameterNamed(nameParamName);
            actualChanges.put(nameParamName, newValue);
            this.categoryName = newValue;
        }

        final String descriptionParamName = "categorydescription";
        if (command.isChangeInStringParameterNamed(descriptionParamName, this.categoryDescription)) {
            final String newValue = command.stringValueOfParameterNamed(descriptionParamName);
            actualChanges.put(descriptionParamName, newValue);
            this.categoryDescription = newValue;
        }
        return actualChanges;
    }

    public String getCategoryName() {
        return this.categoryName;
    }

    public String getCategoryDescription() {
        return this.categoryDescription;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) { return false; }
        if (obj == this) { return true; }
        if (obj.getClass() != getClass()) { return false; }
        ProvisioningCategory pc = (ProvisioningCategory) obj;
        return pc.getCategoryName().equals(this.categoryName);
    }

    @Override
    public int hashCode() {
        return categoryName.hashCode() ^ getId().hashCode();
    }
}

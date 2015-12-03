/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.template.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "m_templatemappers")
public class TemplateMapper extends AbstractPersistable<Long> {

    @Column(name = "mapperorder")
    private int mapperOrder;

    @Column(name = "mapperkey")
    private String mapperKey;

    @Column(name = "mappervalue")
    private String mapperValue;

    protected TemplateMapper() {}

    public TemplateMapper(final int mapperOrder, final String mapperKey, final String mapperValue) {
        this.mapperOrder = mapperOrder;
        this.mapperKey = mapperKey;
        this.mapperValue = mapperValue;
    }

    public int getMapperOrder() {
        return mapperOrder;
    }

    public String getMapperKey() {
        return mapperKey;
    }

    public String getMapperValue() {
        return mapperValue;
    }
}

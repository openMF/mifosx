/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.codes.data;

import java.io.Serializable;

/**
 * Immutable data object represent code-value data in system.
 */
public class CodeValueData implements Serializable {

    private final Long id;

    private final String name;

    @SuppressWarnings("unused")
    private final Integer position;

    @SuppressWarnings("unused")
    private final String description;
    
    @SuppressWarnings("unused")
    private final Long parentId;

    public static CodeValueData instance(final Long id, final String name, final Integer position) {
        String description = null;
        Long parentId = null;
        return new CodeValueData(id, name, position, description, parentId);
    }

    public static CodeValueData instance(final Long id, final String name, final String description) {
        Integer position = null;
        final Long parentId = null;
        return new CodeValueData(id, name, position, description, parentId);
    }

    public static CodeValueData instance(final Long id, final String name) {
        String description = null;
        Integer position = null;
        Long parentId = null;
        return new CodeValueData(id, name, position, description, parentId);
    }

    public static CodeValueData instance(final Long id, final String name, final Integer position, final String description, final Long parentId) {
        return new CodeValueData(id, name, position, description, parentId);
    }

    private CodeValueData(final Long id, final String name, final Integer position, final String description, final Long parentId) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.description = description;
        this.parentId = parentId;
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }
}
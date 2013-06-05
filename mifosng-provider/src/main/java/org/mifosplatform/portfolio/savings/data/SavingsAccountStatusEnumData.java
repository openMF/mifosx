/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.savings.data;

/**
 * Immutable data object represent savings account status enumerations.
 */
public class SavingsAccountStatusEnumData {

    @SuppressWarnings("unused")
    private final Long id;
    @SuppressWarnings("unused")
    private final String code;
    @SuppressWarnings("unused")
    private final String value;
    @SuppressWarnings("unused")
    private final boolean unactivated;
    @SuppressWarnings("unused")
    private final boolean active;
    @SuppressWarnings("unused")
    private final boolean closed;

    public SavingsAccountStatusEnumData(final Long id, final String code, final String value, final boolean unactivated,
            final boolean active, final boolean closed) {
        this.id = id;
        this.code = code;
        this.value = value;
        this.unactivated = unactivated;
        this.active = active;
        this.closed = closed;
    }
    
    public Long id() {
        return this.id;
    }
}
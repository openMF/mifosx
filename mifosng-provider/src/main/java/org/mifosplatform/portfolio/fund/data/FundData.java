/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.fund.data;

import java.io.Serializable;

/**
 * Immutable data object to represent fund data.
 */
public class FundData implements Serializable {

    @SuppressWarnings("unused")
    private final Long id;
    @SuppressWarnings("unused")
    private final String name;
    @SuppressWarnings("unused")
    private final String externalId;
    @SuppressWarnings("unused")
    private final Long fundTypeId;

    public static FundData instance(final Long id, final String name, final String externalId, final Long fundTypeId) {
        return new FundData(id, name, externalId, fundTypeId);
    }

    private FundData(final Long id, final String name, final String externalId, final Long fundTypeId) {
        this.id = id;
        this.name = name;
        this.externalId = externalId;
        this.fundTypeId = fundTypeId;
    }
}
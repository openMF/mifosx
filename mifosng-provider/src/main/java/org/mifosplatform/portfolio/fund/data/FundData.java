/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.fund.data;

import java.io.Serializable;
import java.util.Collection;

import org.mifosplatform.infrastructure.codes.data.CodeValueData;

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
    @SuppressWarnings("unused")
	private final Collection<CodeValueData> fundTypeOptions;

    public static FundData instance(final Long id, final String name, final String externalId, final Long fundTypeId,
    		final Collection<CodeValueData> fundTypeOptions) {
        return new FundData(id, name, externalId, fundTypeId, fundTypeOptions);
    }

    private FundData(final Long id, final String name, final String externalId, final Long fundTypeId,
    		final Collection<CodeValueData> fundTypeOptions) {
        this.id = id;
        this.name = name;
        this.externalId = externalId;
        this.fundTypeId = fundTypeId;
        this.fundTypeOptions = fundTypeOptions;
    }
}
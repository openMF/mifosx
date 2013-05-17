/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.core.data;

import java.lang.SuppressWarnings;
import java.util.Date;

public class TenantData {

	@SuppressWarnings("unused")
	private Long id;
	
	@SuppressWarnings("unused")
	private String identifier;

	@SuppressWarnings("unused")
	private String name;

	@SuppressWarnings("unused")
	private String schemaName;

	@SuppressWarnings("unused")
	private String timezoneId;

	@SuppressWarnings("unused")
	private Integer countryId;

	@SuppressWarnings("unused")
	private Date joinedDate;

	@SuppressWarnings("unused")
	private Date createdDate;

	@SuppressWarnings("unused")
	private Date lastModified;

	@SuppressWarnings("unused")
	private String schemaServer;

	@SuppressWarnings("unused")
	private String schemaServerPort;

	@SuppressWarnings("unused")
	private String schemaUsername;

	@SuppressWarnings("unused")
	private String schemaPassword;

	@SuppressWarnings("unused")
	private Short autoUpdate;

	public static TenantData instance(final Long id, final String identifier, final String name,
			final String schemaName, final String timezoneId,
			final Integer countryId, final Date joinedDate,
			final Date createdDate, final Date lastModified,
			final String schemaServer, final String schemaServerPort,
			final String schemaUsername, final String schemaPassword,
			final Short autoUpdate) {
		
		return new TenantData(id, identifier, name, schemaName, timezoneId,
				countryId, joinedDate, createdDate, lastModified, schemaServer,
				schemaServerPort, schemaUsername, schemaPassword, autoUpdate);
	}
	
	private TenantData(final Long id, final String identifier, final String name,
			final String schemaName, final String timezoneId,
			final Integer countryId, final Date joinedDate,
			final Date createdDate, final Date lastModified,
			final String schemaServer, final String schemaServerPort,
			final String schemaUsername, final String schemaPassword,
			final Short autoUpdate) {

		this.id = id;
		this.identifier = identifier;
		this.name = name;
		this.schemaName = schemaName;
		this.timezoneId = timezoneId;
		this.countryId = countryId;
		this.joinedDate = joinedDate;
		this.createdDate = createdDate;
		this.lastModified = lastModified;
		this.schemaServer = schemaServer;
		this.schemaServerPort = schemaServerPort;
		this.schemaUsername = schemaUsername;
		this.schemaPassword = schemaPassword;
		this.autoUpdate = autoUpdate;
	}

}
/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.core.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(schema="`mifosplatform-tenants`", name = "tenants")
public class Tenant extends AbstractPersistable<Long> {

	@Column(name="identifier", nullable=false, length=100, unique=true)
	private final String identifier;
	
	@Column(name="name", nullable=false, length=100)
    private final String name;
	
	@Column(name="schema_name", nullable=false, length=100, unique=true)
    private final String schemaName;
	
	@Column(name="timezone_id", nullable=false, length=100)
    private final String timezoneId;
	
	@Column(name="country_id", nullable=true)
    private final Integer countryId;
	
	@Column(name="joined_date", nullable=true)
    private final Date joinedDate;
	
	@Column(name="created_date", nullable=true)
    private final Date createdDate;
	
	@Column(name="lastmodified_date", nullable=true)
    private final Date lastModified;
	
	@Column(name="schema_server", nullable=false, length=100)
    private final String schemaServer;
	
	@Column(name="schema_server_port", nullable=false, length=10)
    private final String schemaServerPort;
	
	@Column(name="schema_username", nullable=false, length=100)
	private final String schemaUsername;
	
	@Column(name="schema_password", nullable=false, length=100)
    private final String schemaPassword;
	
	@Column(name="auto_update", nullable=true)
	private final Short autoUpdate;
	
	public String databaseURL() {
        
		String url = new StringBuilder("jdbc:mysql://").append(schemaServer).append(':').append(schemaServerPort).append('/')
                .append(schemaName).toString();
        return url;
    }
	
	public String getSchemaName() {
		
		return schemaName;
	}
	
	public String getSchemaUsername() {
		
		return schemaUsername;
	}
	
	public String getSchemaPassword() {
		
		return schemaPassword;
	}
	
	public static Tenant fromJson(final JsonCommand command) {
		
		final String identifier = command.stringValueOfParameterNamed("identifier");
		final String name = command.stringValueOfParameterNamed("name");
		final String schemaName = command.stringValueOfParameterNamed("schemaName");
		final String timezoneId = command.stringValueOfParameterNamed("timezoneId");
		final Integer countryId = command.integerValueOfParameterNamed("countryId");
		final Date joinedDate = command.DateValueOfParameterNamed("joinedDate");
		final Date createdDate = command.DateValueOfParameterNamed("createdDate");
		final Date lastModified = command.DateValueOfParameterNamed("lastModified");
		final String schemaServer = command.stringValueOfParameterNamed("schemaServer");
		final String schemaServerPort = command.stringValueOfParameterNamed("schemaServerPort");
		final String schemaUsername = command.stringValueOfParameterNamed("schemaUsername");
		final String schemaPassword = command.stringValueOfParameterNamed("schemaPassword");
		final Short autoUpdate = Short.valueOf(command.stringValueOfParameterNamed("autoUpdate"));
		
		return new Tenant(identifier, name, schemaName, timezoneId, countryId,
				joinedDate, createdDate, lastModified, schemaServer,
				schemaServerPort, schemaUsername, schemaPassword, autoUpdate);
	}
	
	public Tenant(final String identifier, final String name,
			final String schemaName, final String timezoneId,
			final Integer countryId, final Date joinedDate,
			final Date createdDate, final Date lastModified,
			final String schemaServer, final String schemaServerPort,
			final String schemaUsername, final String schemaPassword,
			final Short autoUpdate) {
        
		this.identifier = StringUtils.defaultIfEmpty(identifier, null);
        this.name = StringUtils.defaultIfEmpty(name, null);
        this.schemaName = StringUtils.defaultIfEmpty(schemaName, null);
        this.timezoneId = StringUtils.defaultIfEmpty(timezoneId, null);
        this.countryId = countryId;
        this.joinedDate = joinedDate;
        this.createdDate = createdDate;
        this.lastModified = lastModified;
        this.schemaServer = StringUtils.defaultIfEmpty(schemaServer, null);
        this.schemaServerPort = StringUtils.defaultIfEmpty(schemaServerPort, null);
        this.schemaUsername = StringUtils.defaultIfEmpty(schemaUsername, null);
        this.schemaPassword = StringUtils.defaultIfEmpty(schemaPassword, null);
        this.autoUpdate = autoUpdate;
    }
	
}
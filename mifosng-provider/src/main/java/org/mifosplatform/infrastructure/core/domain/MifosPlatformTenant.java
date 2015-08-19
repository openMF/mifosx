/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.core.domain;

public class MifosPlatformTenant {

    private final MifosPlatformTenantConnection oltpConnection;
    private final MifosPlatformTenantConnection reportsConnection;
    private final Long id;
    private final String tenantIdentifier;
    private final String name;
    private final String timezoneId;
    private final Long oltpId;
    private final Long reportId;

    public MifosPlatformTenant(final Long id, final Long oltpId, final Long reportId, final String tenantIdentifier, final String name,
            final String timezoneId, final MifosPlatformTenantConnection oltpConnection,
            final MifosPlatformTenantConnection reportsConnection) {
        this.id = id;
        this.oltpId = oltpId;
        this.reportId = reportId;
        this.tenantIdentifier = tenantIdentifier;
        this.name = name;
        this.timezoneId = timezoneId;
        this.oltpConnection = oltpConnection;
        this.reportsConnection = reportsConnection;
    }

    public Long getId() {
        return this.id;
    }

    public String getTenantIdentifier() {
        return this.tenantIdentifier;
    }

    public String getName() {
        return this.name;
    }

    public String getTimezoneId() {
        return this.timezoneId;
    }

    public MifosPlatformTenantConnection getOltpConnection() {
        return oltpConnection;
    }

    public MifosPlatformTenantConnection getReportsConnection() {
        return reportsConnection;
    }

    public Long getOltpId() {
        return oltpId;
    }

    public Long getReportId() {
        return reportId;
    }

}
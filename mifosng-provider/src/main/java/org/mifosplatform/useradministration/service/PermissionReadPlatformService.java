/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.useradministration.service;

import java.util.Collection;

import org.mifosplatform.useradministration.data.PermissionData;

public interface PermissionReadPlatformService {

    Collection<PermissionData> retrieveAllPermissions();

    Collection<PermissionData> retrieveAllMakerCheckerablePermissions();

    Collection<PermissionData> retrieveAllRolePermissions(Long roleId);
}
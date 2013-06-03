/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.configuration.domain;

public interface ConfigurationDomainService {

    boolean isMakerCheckerEnabledForTask(String taskPermissionCode);
    boolean isAmazonS3Enabled();
}
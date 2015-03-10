/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.entityaccess.domain;

import org.springframework.data.jpa.repository.JpaRepository;


public interface MifosEntityRelationRepository extends JpaRepository<MifosEntityRelation, Long>{

}

/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.group.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.mifosplatform.portfolio.group.data.CenterData;

public class GroupingTypesApiConstants {

    public static final String CENTER_RESOURCE_NAME = "center";
    public static final String GROUP_RESOURCE_NAME = "group";
    public static final String COMMUNAL_BANK_RESOURCE_NAME = "communalbank";
    
    //group roles
    public static final String GROUP_ROLE_RESOURCE_NAME = "grouprole";
    public static final String GROUP_ROLE_NAME = "GROUPROLE";
    
    public static final String roleParamName = "role";
    public static final String groupIdParamName = "groupId";
    public static final String clientIdParamName = "clientId";
    public static final String groupRolesParamName = "groupRoles";

    // general
    public static final String localeParamName = "locale";
    public static final String dateFormatParamName = "dateFormat";

    // center parameters
    public static final String idParamName = "id";
    public static final String nameParamName = "name";
    public static final String externalIdParamName = "externalId";
    public static final String officeIdParamName = "officeId";
    public static final String staffIdParamName = "staffId";
    public static final String activeParamName = "active";
    public static final String activationDateParamName = "activationDate";
    public static final String groupMembersParamName = "groupMembers";

    // group parameters
    public static final String centerIdParamName = "centerId";
    public static final String clientMembersParamName = "clientMembers";

    // response parameters
    public static final String statusParamName = "status";
    public static final String hierarchyParamName = "hierarchy";
    public static final String officeNameParamName = "officeName";
    public static final String staffNameParamName = "staffName";
    public static final String officeOptionsParamName = "officeOptions";
    public static final String staffOptionsParamName = "staffOptions";
    public static final String clientOptionsParamName = "clientOptions";

    public static final Set<String> CENTER_REQUEST_DATA_PARAMETERS = new HashSet<String>(Arrays.asList(localeParamName,
            dateFormatParamName, idParamName, nameParamName, externalIdParamName, officeIdParamName, staffIdParamName, activeParamName,
            activationDateParamName, groupMembersParamName));

    public static final Set<String> GROUP_REQUEST_DATA_PARAMETERS = new HashSet<String>(Arrays.asList(localeParamName, dateFormatParamName,
            idParamName, nameParamName, externalIdParamName, centerIdParamName, officeIdParamName, staffIdParamName, activeParamName,
            activationDateParamName, clientMembersParamName));
    
    public static final Set<String> GROUP_ROLES_REQUEST_DATA_PARAMETERS = new HashSet<String>(Arrays.asList(roleParamName, clientIdParamName));

    /**
     * These parameters will match the class level parameters of
     * {@link CenterData}. Where possible, we try to get response parameters to
     * match those of request parameters.
     */
    public static final Set<String> CENTER_RESPONSE_DATA_PARAMETERS = new HashSet<String>(Arrays.asList(idParamName, nameParamName,
            externalIdParamName, officeIdParamName, officeNameParamName, staffIdParamName, staffNameParamName, hierarchyParamName,
            officeOptionsParamName, staffOptionsParamName));

    public static final Set<String> CENTER_GROUP_RESPONSE_DATA_PARAMETERS = new HashSet<String>(Arrays.asList(idParamName, nameParamName,
            externalIdParamName, officeIdParamName, officeNameParamName, staffIdParamName, staffNameParamName, hierarchyParamName,
            officeOptionsParamName, staffOptionsParamName, clientOptionsParamName));

    public static final Set<String> GROUP_RESPONSE_DATA_PARAMETERS = new HashSet<String>(Arrays.asList(idParamName, nameParamName,
            externalIdParamName, officeIdParamName, officeNameParamName, "parentId", "parentName", staffIdParamName, staffNameParamName,
            hierarchyParamName, officeOptionsParamName, staffOptionsParamName, clientOptionsParamName));

    public static final Set<String> ACTIVATION_REQUEST_DATA_PARAMETERS = new HashSet<String>(Arrays.asList(localeParamName,
            dateFormatParamName, activationDateParamName));
    
    
    public static final Set<String> COLLECTIONSHEET_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("dueDate", "loanProducts", "groups"));

}
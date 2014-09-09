/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.integrationtests.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class GroupHelper {

    private static final String CREATE_GROUP_URL = "/mifosng-provider/api/v1/groups?" + Utils.TENANT_IDENTIFIER;

    public static Integer createGroup(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
            @SuppressWarnings("unused") final boolean active) {
        System.out.println("---------------------------------CREATING A GROUP---------------------------------------------");
        return createGroup(requestSpec, responseSpec, "04 March 2011");
    }

    public static Integer createGroup(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
            final String activationDate) {
        System.out.println("---------------------------------CREATING A GROUP---------------------------------------------");
        return Utils.performServerPost(requestSpec, responseSpec, CREATE_GROUP_URL, getTestGroupAsJSON(true, activationDate), "groupId");
    }

    public static Integer createGroup(final RequestSpecification requestSpec, final ResponseSpecification responseSpec) {
        System.out.println("---------------------------------CREATING A GROUP---------------------------------------------");
        return Utils.performServerPost(requestSpec, responseSpec, CREATE_GROUP_URL, getTestGroupAsJSON(false, ""), "groupId");
    }

    public static Integer associateClient(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
            final String groupId, final String clientMember) {
        final String GROUP_ASSOCIATE_URL = "/mifosng-provider/api/v1/groups/" + groupId
                + "?command=associateClients&" + Utils.TENANT_IDENTIFIER;
        System.out.println("---------------------------------Associate Client To A GROUP---------------------------------------------");
        return Utils.performServerPost(requestSpec, responseSpec, GROUP_ASSOCIATE_URL, associateClientAsJSON(clientMember), "groupId");
    }

    public static Integer disAssociateClient(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
            final String groupId, final String clientMember) {
        final String GROUP_ASSOCIATE_URL = "/mifosng-provider/api/v1/groups/" + groupId
                + "?command=disassociateClients&" + Utils.TENANT_IDENTIFIER;
        System.out.println("---------------------------------Disassociate Client To A GROUP---------------------------------------------");
        return Utils.performServerPost(requestSpec, responseSpec, GROUP_ASSOCIATE_URL, associateClientAsJSON(clientMember), "groupId");
    }

    public static Integer activateGroup(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
            final String groupId) {
        final String GROUP_ASSOCIATE_URL = "/mifosng-provider/api/v1/groups/" + groupId + "?command=activate&" + Utils.TENANT_IDENTIFIER;
        System.out.println("---------------------------------Activate A GROUP---------------------------------------------");
        return Utils.performServerPost(requestSpec, responseSpec, GROUP_ASSOCIATE_URL, activateGroupAsJSON(""), "groupId");
    }

    public static Integer updateGroup(final RequestSpecification requestSpec, final ResponseSpecification responseSpec, final String name,
            final String groupId) {
        final String GROUP_ASSOCIATE_URL = "/mifosng-provider/api/v1/groups/" + groupId + "?" + Utils.TENANT_IDENTIFIER;
        System.out.println("---------------------------------UPDATE GROUP---------------------------------------------");
        return Utils.performServerPut(requestSpec, responseSpec, GROUP_ASSOCIATE_URL, updateGroupAsJSON(name), "groupId");
    }

    public static Integer deleteGroup(final RequestSpecification requestSpec, final ResponseSpecification responseSpec, final String groupId) {
        final String GROUP_ASSOCIATE_URL = "/mifosng-provider/api/v1/groups/" + groupId + "?" + Utils.TENANT_IDENTIFIER;
        System.out.println("---------------------------------DELETE GROUP---------------------------------------------");
        return Utils.performServerDelete(requestSpec, responseSpec, GROUP_ASSOCIATE_URL, "groupId");
    }

    public static String getTestGroupAsJSON(final boolean active, final String activationDate) {
        final HashMap<String, String> map = new HashMap<>();
        map.put("officeId", "1");
        map.put("name", randomNameGenerator("Group_Name_", 5));
        map.put("externalId", randomIDGenerator("ID_", 7));
        map.put("dateFormat", "dd MMMM yyyy");
        map.put("locale", "en");
        if (active) {
            map.put("active", "true");
            map.put("activationDate", activationDate);
        } else {
            map.put("active", "false");
            map.put("submittedOnDate", "04 March 2011");
            System.out.println("defaulting to inactive group: 04 March 2011");
        }

        System.out.println("map : " + map);
        return new Gson().toJson(map);
    }

    public static String associateClientAsJSON(final String clientMember) {
        final HashMap<String, List<String>> map = new HashMap<String, List<String>>();
        final List<String> list = new ArrayList<>();
        list.add(clientMember);
        map.put("clientMembers", list);
        System.out.println("map : " + map);
        return new Gson().toJson(map);
    }

    public static String activateGroupAsJSON(final String activationDate) {
        final HashMap<String, String> map = new HashMap<>();
        map.put("dateFormat", "dd MMMM yyyy");
        map.put("locale", "en");
        if (StringUtils.isNotEmpty(activationDate)) {
            map.put("activationDate", activationDate);
        } else {
            map.put("activationDate", "04 March 2011");
            System.out.println("defaulting to fixed date: 04 March 2011");
        }
        System.out.println("map : " + map);
        return new Gson().toJson(map);
    }

    public static String updateGroupAsJSON(final String name) {
        final HashMap<String, String> map = new HashMap<>();
        map.put("name", name);
        System.out.println("map : " + map);
        return new Gson().toJson(map);
    }

    public static void verifyGroupCreatedOnServer(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
            final Integer generatedGroupID) {
        System.out.println("------------------------------CHECK GROUP DETAILS------------------------------------\n");
        final String GROUP_URL = "/mifosng-provider/api/v1/groups/" + generatedGroupID + "?" + Utils.TENANT_IDENTIFIER;
        final Integer responseGroupID = Utils.performServerGet(requestSpec, responseSpec, GROUP_URL, "id");
        assertEquals("ERROR IN CREATING THE GROUP", generatedGroupID, responseGroupID);
    }

    public static void verifyGroupDetails(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
            final Integer generatedGroupID, final String field, final String expectedValue) {
        System.out.println("------------------------------CHECK GROUP DETAILS------------------------------------\n");
        final String GROUP_URL = "/mifosng-provider/api/v1/groups/" + generatedGroupID + "?" + Utils.TENANT_IDENTIFIER;
        final String responseValue = Utils.performServerGet(requestSpec, responseSpec, GROUP_URL, field);
        assertEquals("ERROR IN CREATING THE GROUP", expectedValue, responseValue);
    }

    public static void verifyGroupActivatedOnServer(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
            final Integer generatedGroupID, final boolean generatedGroupStatus) {
        System.out.println("------------------------------CHECK GROUP STATUS------------------------------------\n");
        final String GROUP_URL = "/mifosng-provider/api/v1/groups/" + generatedGroupID + "?" + Utils.TENANT_IDENTIFIER;
        final Boolean responseGroupStatus = Utils.performServerGet(requestSpec, responseSpec, GROUP_URL, "active");
        assertEquals("ERROR IN ACTIVATING THE GROUP", generatedGroupStatus, responseGroupStatus);
    }

    public static void verifyGroupMembers(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
            final Integer generatedGroupID, final Integer groupMember) {
        List<String> list = new ArrayList<>();
        System.out.println("------------------------------CHECK GROUP MEMBERS------------------------------------\n");
        final String GROUP_URL = "/mifosng-provider/api/v1/groups/" + generatedGroupID
                + "?associations=clientMembers&" + Utils.TENANT_IDENTIFIER;
        list = Utils.performServerGet(requestSpec, responseSpec, GROUP_URL, "clientMembers");
        assertTrue("ERROR IN GROUP MEMBER", list.toString().contains("id=" + groupMember.toString()));
    }

    public static void verifyEmptyGroupMembers(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
            final Integer generatedGroupID) {
        List<String> list = new ArrayList<>();
        System.out.println("------------------------------CHECK EMPTY GROUP MEMBER LIST------------------------------------\n");
        final String GROUP_URL = "/mifosng-provider/api/v1/groups/" + generatedGroupID
                + "?associations=clientMembers&" + Utils.TENANT_IDENTIFIER;
        list = Utils.performServerGet(requestSpec, responseSpec, GROUP_URL, "clientMembers");
        assertEquals("GROUP MEMBER LIST NOT EMPTY", list, null);
    }

    public static void verifyGroupDeleted(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
            final Integer generatedGroupID) {
        List<String> list = new ArrayList<>();
        System.out.println("------------------------------CHECK GROUP DELETED------------------------------------\n");
        final String GROUP_URL = "/mifosng-provider/api/v1/groups/?" + Utils.TENANT_IDENTIFIER;
        list = Utils.performServerGet(requestSpec, responseSpec, GROUP_URL, "pageItems");

        assertFalse("GROUP NOT DELETED", list.toString().contains("id=" + generatedGroupID.toString()));
    }

    public static String randomNameGenerator(final String prefix, final int lenOfRandomSuffix) {
        return Utils.randomStringGenerator(prefix, lenOfRandomSuffix);
    }

    private static String randomIDGenerator(final String prefix, final int lenOfRandomSuffix) {
        return Utils.randomStringGenerator(prefix, lenOfRandomSuffix, "ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }
}
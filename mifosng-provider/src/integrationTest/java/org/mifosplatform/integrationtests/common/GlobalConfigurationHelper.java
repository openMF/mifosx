package org.mifosplatform.integrationtests.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.google.gson.Gson;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

@SuppressWarnings({ "unused", "rawtypes" })
public class GlobalConfigurationHelper {

    private final RequestSpecification requestSpec;
    private final ResponseSpecification responseSpec;

    public GlobalConfigurationHelper(final RequestSpecification requestSpec, final ResponseSpecification responseSpec) {
        this.requestSpec = requestSpec;
        this.responseSpec = responseSpec;
    }

    public static ArrayList getAllGlobalConfigurations(final RequestSpecification requestSpec, final ResponseSpecification responseSpec) {
        final String GET_ALL_GLOBAL_CONFIG_URL = "/mifosng-provider/api/v1/configurations?tenantIdentifier=default";
        System.out.println("------------------------ RETRIEVING ALL GLOBAL CONFIGURATIONS -------------------------");
        final HashMap response = Utils.performServerGet(requestSpec, responseSpec, GET_ALL_GLOBAL_CONFIG_URL, "");
        return (ArrayList) response.get("globalConfiguration");
    }

    public static HashMap getGlobalConfigurationById(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
            final String configId) {
        final String GET_GLOBAL_CONFIG_BY_ID_URL = "/mifosng-provider/api/v1/configurations/" + configId + "?tenantIdentifier=default";
        System.out.println("------------------------ RETRIEVING GLOBAL CONFIGURATION BY ID -------------------------");
        return Utils.performServerGet(requestSpec, responseSpec, GET_GLOBAL_CONFIG_BY_ID_URL, "");
    }

    public static Integer updateValueForGlobalConfiguration(final RequestSpecification requestSpec,
            final ResponseSpecification responseSpec, final String configId, final String value) {
        final String GLOBAL_CONFIG_UPDATE_URL = "/mifosng-provider/api/v1/configurations/" + configId + "?tenantIdentifier=default";
        System.out.println("---------------------------------UPDATE VALUE FOR GLOBAL CONFIG---------------------------------------------");
        return Utils.performServerPut(requestSpec, responseSpec, GLOBAL_CONFIG_UPDATE_URL, updateGlobalConfigUpdateValueAsJSON(value),
                "resourceId");
    }

    public static Integer updateEnabledFlagForGlobalConfiguration(final RequestSpecification requestSpec,
            final ResponseSpecification responseSpec, final String configId, final Boolean enabled) {
        final String GLOBAL_CONFIG_UPDATE_URL = "/mifosng-provider/api/v1/configurations/" + configId + "?tenantIdentifier=default";
        System.out
                .println("---------------------------------UPDATE GLOBAL CONFIG FOR ENABLED FLAG---------------------------------------------");
        return Utils.performServerPut(requestSpec, responseSpec, GLOBAL_CONFIG_UPDATE_URL,
                updateGlobalConfigUpdateEnabledFlagAsJSON(enabled), "resourceId");
    }

    public static ArrayList getGlobalConfigurationIsCacheEnabled(final RequestSpecification requestSpec,
            final ResponseSpecification responseSpec) {
        final String GET_IS_CACHE_GLOBAL_CONFIG_URL = "/mifosng-provider/api/v1/caches?tenantIdentifier=default";
        System.out.println("------------------------ RETRIEVING IS CACHE ENABLED GLOBAL CONFIGURATION -------------------------");
        final ArrayList<HashMap> response = Utils.performServerGet(requestSpec, responseSpec, GET_IS_CACHE_GLOBAL_CONFIG_URL, "");
        return response;
    }

    public static Integer updateIsCacheEnabledForGlobalConfiguration(final RequestSpecification requestSpec,
            final ResponseSpecification responseSpec, final String cacheType) {
        final String IS_CACHE_GLOBAL_CONFIG_UPDATE_URL = "/mifosng-provider/api/v1/caches?tenantIdentifier=default";
        System.out.println("------------------UPDATE GLOBAL CONFIG FOR IS CACHE ENABLED----------------------");
        return Utils.performServerPut(requestSpec, responseSpec, IS_CACHE_GLOBAL_CONFIG_UPDATE_URL,
                updateIsCacheEnabledGlobalConfigUpdateAsJSON(cacheType), "");
    }

    public static String updateGlobalConfigUpdateValueAsJSON(final String value) {
        final HashMap<String, String> map = new HashMap<String, String>();
        map.put("value", value);
        System.out.println("map : " + map);
        return new Gson().toJson(map);
    }

    public static String updateGlobalConfigUpdateEnabledFlagAsJSON(final Boolean enabled) {
        final HashMap<String, Boolean> map = new HashMap<String, Boolean>();
        map.put("enabled", enabled);
        System.out.println("map : " + map);
        return new Gson().toJson(map);
    }

    public static String updateIsCacheEnabledGlobalConfigUpdateAsJSON(final String cacheType) {
        final HashMap<String, String> map = new HashMap<String, String>();
        map.put("cacheType", cacheType);
        System.out.println("map : " + map);
        return new Gson().toJson(map);
    }

    public static int randomValueGenerator(int minValue, int maxValue) {
        Random randValue = new Random();
        int randomNum = randValue.nextInt((maxValue - minValue) + 1) + minValue;
        return randomNum;
    }
}
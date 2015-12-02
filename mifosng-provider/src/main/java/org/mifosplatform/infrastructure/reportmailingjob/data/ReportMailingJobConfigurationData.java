/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.reportmailingjob.data;

/** 
 * Immutable data object representing report mailing job configuration data. 
 **/
public class ReportMailingJobConfigurationData {
    private final int id;
    private final String name;
    private final String value;
    
    /** 
     * ReportMailingJobConfigurationData private constructor 
     **/
    private ReportMailingJobConfigurationData(final int id, final String name, final String value) {
        this.id = id;
        this.name = name;
        this.value = value;
    }
    
    /** 
     * creates an instance of the ReportMailingJobConfigurationData class
     * 
     * @return ReportMailingJobConfigurationData object
     **/
    public static ReportMailingJobConfigurationData instance(final int id, final String name, final String value) {
        return new ReportMailingJobConfigurationData(id, name, value);
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return "ReportMailingJobConfigurationData [id=" + id + ", name=" + name + ", value=" + value + "]";
    }
}

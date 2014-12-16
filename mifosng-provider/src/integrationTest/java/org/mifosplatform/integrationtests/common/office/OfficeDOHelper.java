/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.integrationtests.common.office;

import com.google.gson.Gson;
import java.util.*;


public class OfficeDOHelper {

    public static class Builder {

        private String name;
        private String externalId;
        private Long id;
        private String nameDecorated;
        private Date openingDate = new Date();
        private String hierarchy;
        private String locale;
        private String dateFormat;
        String text;



        private Builder(final String name) {
            this.name = name;
        }

        public Builder externalId(final String externalId) {
            this.externalId = externalId;
            return this;
        }


        public Builder nameDecorated(final String nameDecorated) {
            this.nameDecorated = nameDecorated;
            return this;
        }

        public Builder openingDate(final Date openingDate) {
            this.openingDate = openingDate;
            return this;
        }

        public Builder hierarchy(final String hierarchy) {
            this.hierarchy = hierarchy;
            return this;
        }

        public Builder locale(final String locale){this.locale = locale;
            return this;};

        public Builder dateFormat(final String dateFormat){this.dateFormat = dateFormat;
            return this;}

        public Builder text(final String text){this.text = text;
            return this;}

        public OfficeDOHelper build() {
            return new OfficeDOHelper(this.name, this.externalId, this.nameDecorated, this.hierarchy, this.openingDate);
        }

    }


    private String name;
    private String externalId;
    private Long id;
    private String nameDecorated;
    private String hierarchy;
    Date openingDate = new Date();
    private String dateFormat;
    private String locale;
    private String text;


    OfficeDOHelper() {
        super();
    }

    private OfficeDOHelper(final String name, final String externalId, final String nameDecorated, final String hierarchy, final Date openingDate) {
        super();
        this.name = name;
        this.externalId = externalId;
        this.nameDecorated = nameDecorated;
        this.hierarchy = hierarchy;
        this.openingDate = openingDate;
    }




    public String toJSON() {
        return new Gson().toJson(this);
    }

    public static OfficeDOHelper fromJSON(final String jsonData) {
        return new Gson().fromJson(jsonData, OfficeDOHelper.class);
    }

    public String getName() {
        return this.name;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(){this.id = id;}

    public String getNameDecorated() {
        return this.nameDecorated;
    }

    public String getHierarchy() {
        return this.hierarchy;
    }

    public String getExternalId() {
        return this.externalId;
    }

    public Date getOpeningDate() {
        return this.openingDate;
    }

    public String getDateFormat(){return this.dateFormat;}

    public String getLocale(){return this.locale;}

    public void setOpeningDate(Date created) {
        this.openingDate = openingDate;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }


    public static Builder create(final String name) {
        return new Builder(name);
    }
}






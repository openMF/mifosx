/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.calendar;

import java.util.HashSet;
import java.util.Set;

public class CalendarConstants {

    public static enum CALENDAR_SUPPORTED_PARAMETERS {
        CALENDAR_ID("id"), ENTITY_TYPE("entityType"), ENTITY_ID("entityId"), TITLE("title"), DESCRIPTION("description"), LOCATION(
                "location"), START_DATE("startDate"), END_DATE("endDate"), CREATED_DATE("createdDate"), DURATION("duration"), TYPE_ID(
                "typeId"), REPEATING("repeating"), REMIND_BY_ID("remindById"), FIRST_REMINDER("firstReminder"), SECOND_REMINDER(
                "secondReminder"), LOCALE("locale"), DATE_FORMAT("dateFormat"), REPEATS("repeats"), REPEATS_EVERY("repeatsEvery"),
                REPEATS_ON_DAY("repeatsOnDay");

        private final String value;

        private CALENDAR_SUPPORTED_PARAMETERS(final String value) {
            this.value = value;
        }

        private static final Set<String> values = new HashSet<String>();
        static {
            for (final CALENDAR_SUPPORTED_PARAMETERS param : CALENDAR_SUPPORTED_PARAMETERS.values()) {
                values.add(param.value);
            }
        }

        public static Set<String> getAllValues() {
            return values;
        }

        @Override
        public String toString() {
            return name().toString().replaceAll("_", " ");
        }

        public String getValue() {
            return this.value;
        }
    }

}

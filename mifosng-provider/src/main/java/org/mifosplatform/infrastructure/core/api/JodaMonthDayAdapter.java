/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.core.api;

import java.lang.reflect.Type;

import org.joda.time.MonthDay;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Serializer for joda time {@link MonthDay} that returns date in array format
 * to match previous jackson functionality.
 */
public class JodaMonthDayAdapter implements JsonSerializer<MonthDay> {

    @SuppressWarnings("unused")
    @Override
    public JsonElement serialize(final MonthDay src, final Type typeOfSrc, final JsonSerializationContext context) {

        JsonArray array = null;
        if (src != null) {
            array = new JsonArray();
            array.add(new JsonPrimitive(src.getMonthOfYear()));
            array.add(new JsonPrimitive(src.getDayOfMonth()));
        }

        return array;
    }
}
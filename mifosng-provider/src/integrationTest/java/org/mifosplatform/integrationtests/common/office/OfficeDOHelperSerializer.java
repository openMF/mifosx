/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.integrationtests.common.office;

import com.fasterxml.jackson.core.JsonParseException;
import com.google.gson.*;
import org.mifosplatform.organisation.office.domain.Office;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class OfficeDOHelperSerializer implements JsonDeserializer<OfficeDOHelper>, JsonSerializer<OfficeDOHelper> {

    public OfficeDOHelperSerializer() {
        super();
    }

    @Override
    public OfficeDOHelper deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {

        if (typeOfT.equals(OfficeDOHelper.class)) {


            final JsonObject jsonObject = json.getAsJsonObject();
            final OfficeDOHelper oh = new OfficeDOHelper();

            final JsonArray jsonOpeningDateArray = jsonObject.getAsJsonArray("created");
            final int year = jsonOpeningDateArray.get(0).getAsInt();
            final int month = jsonOpeningDateArray.get(1).getAsInt();
            final int day = jsonOpeningDateArray.get(2).getAsInt();

            final Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, (month - 1));
            calendar.set(Calendar.DAY_OF_MONTH, day);


            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            oh.setOpeningDate(calendar.getTime());

            return oh;
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public JsonElement serialize(OfficeDOHelper src, Type typeOfSrc, JsonSerializationContext context) {
        if (typeOfSrc.equals(OfficeDOHelper.class)) {
            final JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", src.getId());
            jsonObject.addProperty("text", src.getText());

            final SimpleDateFormat sdf = new SimpleDateFormat(src.getDateFormat());

            jsonObject.addProperty("openingDate", sdf.format(src.getOpeningDate()));

            return jsonObject;
        } else {
            throw new IllegalArgumentException();
        }

    }
}





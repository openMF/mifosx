package org.mifosplatform.infrastructure.core.api;

import java.lang.reflect.Type;

import org.joda.time.LocalDateTime;

import com.google.gson.JsonArray;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;


public class JodaLocalDateTimeAdapter implements JsonSerializer<LocalDateTime>{
    
    @SuppressWarnings("unused")
    @Override

    public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
        JsonArray array = null;
        if (src != null) {
            array = new JsonArray();
            array.add(new JsonPrimitive(src.getYearOfEra()));
            array.add(new JsonPrimitive(src.getMonthOfYear()));
            array.add(new JsonPrimitive(src.getDayOfMonth()));
            array.add(new JsonPrimitive(src.getHourOfDay()));
            array.add(new JsonPrimitive(src.getMinuteOfHour()));
        }

        return array;
    }
}

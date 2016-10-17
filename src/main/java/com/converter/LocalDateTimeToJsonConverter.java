package com.converter;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeToJsonConverter implements JsonSerializer<LocalDateTime> {

	@Override
	public JsonElement serialize(LocalDateTime src, Type arg1, JsonSerializationContext arg2) {
		return new JsonPrimitive(src.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")));
	}

}

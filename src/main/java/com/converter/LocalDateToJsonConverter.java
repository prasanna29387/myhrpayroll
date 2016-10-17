package com.converter;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.LocalDate;

public class LocalDateToJsonConverter implements JsonSerializer<LocalDate> {

	@Override
	public JsonElement serialize(LocalDate src, Type arg1, JsonSerializationContext arg2) {
		return new JsonPrimitive(src.toString());
	}

}

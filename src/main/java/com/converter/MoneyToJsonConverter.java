package com.converter;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.money.Money;

import java.lang.reflect.Type;

public class MoneyToJsonConverter implements JsonSerializer<Money> {

	@Override
	public JsonElement serialize(Money src, Type arg1, JsonSerializationContext arg2) {
		return new JsonPrimitive(src.toString());
	}

}

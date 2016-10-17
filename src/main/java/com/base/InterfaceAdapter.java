package com.base;

import com.google.gson.*;
import com.money.MoneyFactory;

import java.lang.reflect.Type;

public final class InterfaceAdapter<T> implements JsonSerializer<T>, JsonDeserializer<T> {
	@Override
	public JsonElement serialize(T object, Type interfaceType, JsonSerializationContext context) {
		final JsonObject wrapper = new JsonObject();
		wrapper.addProperty("type", object.getClass().getName());
		wrapper.add("data", context.serialize(object));
		return wrapper;
	}

	@Override
	public T deserialize(JsonElement elem, Type interfaceType, JsonDeserializationContext context) {
		final JsonObject wrapper = (JsonObject) elem;
		final JsonElement typeName = get(wrapper, "type");
		final JsonElement data = get(wrapper, "data");
		final Type actualType = typeForName(typeName);
		return actualType.getTypeName().startsWith("com.bnym.money.Money")
				? deserializeMoney(actualType, (JsonObject) data) : context.deserialize(data, actualType);
	}

	@SuppressWarnings("unchecked")
	private T deserializeMoney(final Type actualType, final JsonObject data) {
		if ("com.bnym.money.MoneyLong".equals(actualType.getTypeName())) {
			return (T) MoneyFactory.fromUnits(data.get("mUnits").getAsLong(), data.get("mPrecision").getAsInt());
		}
        if ("com.bnym.money.MoneyBigDecimal".equals(actualType.getTypeName())) {
            return (T) MoneyFactory.fromBigDecimal(data.get("mValue").getAsBigDecimal());
        }
        return (T) MoneyFactory.fromDouble(data.get("mValue").getAsDouble());
	}

	private Type typeForName(final JsonElement typeElem) {
		try {
			return Class.forName(typeElem.getAsString());
		} catch (ClassNotFoundException e) {
			throw new JsonParseException(e);
		}
	}

	private JsonElement get(final JsonObject wrapper, String memberName) {
		final JsonElement elem = wrapper.get(memberName);
		if (elem == null)
			throw new JsonParseException(
					"no '" + memberName + "' member found in what was expected to be an interface wrapper");
		return elem;
	}
}
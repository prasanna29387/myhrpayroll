package com.master.client.bean;

import com.base.InterfaceAdapter;
import com.converter.LocalDateTimeToJsonConverter;
import com.converter.LocalDateToJsonConverter;
import com.converter.MoneyToJsonConverter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.money.Money;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ClientMethods {


    public String toJson(Client client) {
        Gson gson = new GsonBuilder().registerTypeAdapter(Money.class, new InterfaceAdapter<Money>()).create();
        return gson.toJson(client);
    }

    public String toJsonForUI(Client client) {
        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeToJsonConverter())
                .registerTypeAdapter(LocalDate.class, new LocalDateToJsonConverter())
                .registerTypeAdapter(Money.class, new MoneyToJsonConverter()).excludeFieldsWithoutExposeAnnotation()
                .create();
        return gson.toJson(client);
    }

    public static Client fromJson(String jsonString) {
        if (jsonString == null) {
            return null;
        }

        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeToJsonConverter())
                .registerTypeAdapter(LocalDate.class, new LocalDateToJsonConverter())
                .registerTypeAdapter(Money.class, new InterfaceAdapter<Money>()).create();
        return gson.fromJson(jsonString, Client.class);
    }

}

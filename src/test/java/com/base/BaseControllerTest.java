package com.base;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.money.Money;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Created by Athul Ravindran  on 10/16/2016.
 */
public class BaseControllerTest {

    protected MockMvc mockMvc;

    protected GsonHttpMessageConverter createGsonHttpMessageConverter() {
        Gson gson = new GsonBuilder().registerTypeAdapter(Money.class, new InterfaceAdapter<Money>()).create();
        GsonHttpMessageConverter gsonConverter = new GsonHttpMessageConverter();
        gsonConverter.setGson(gson);
        gsonConverter.setPrefixJson(true);
        return gsonConverter;
    }

}

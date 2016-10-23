package com.master.client.bean;

import com.google.gson.annotations.Expose;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Athul on 10/16/16.
 */

@Data
@Builder
public class Client {
    @Expose
    private String clientName;
    @Expose
    private long clientId;
    @Expose
    private String clientContactName;
    @Expose
    private String clientContactEmail;
    @Expose
    private String clientContactPhone;
    @Expose
    private String panNumber;
    @Expose
    private List<String> errors = new ArrayList<>();
    @Expose
    private List<String> warnings = new ArrayList<>();

    public static Client fromJson(String jsonString) {
        return ClientMethods.fromJson(jsonString);
    }

    public String toJson() {
        return new ClientMethods().toJson(this);
    }

    public String toJsonForUI() {
        return new ClientMethods().toJsonForUI(this);
    }


}

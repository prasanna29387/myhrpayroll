package com.master.client.bean;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;

/**
 * Created by Athul on 10/16/16.
 */

@Data
@Builder
public class Client {

    private String clientName;
    private long clientId;
    private String clientContactName;
    private String clientContactEmail;
    private String clientContactPhone;
    private String taxIdentifactionNumber;
    private String panNumber;
    private ArrayList<String> errors;
    private ArrayList<String> warnings;

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

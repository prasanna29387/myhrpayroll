package com.master.client.bean;

import lombok.Data;

/**
 * Created by Athul on 10/16/16.
 */

@Data
public class Client {

    private String clientName;
    private long clientId;
    private String clientContactName;
    private String clientContactEmail;
    private String clientContactPhone;
    private String taxIdentifactionNumber;
    private String panNumber;

}

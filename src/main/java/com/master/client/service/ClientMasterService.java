package com.master.client.service;


import com.master.client.bean.Client;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Slf4j
@Service
public class ClientMasterService {


    public ArrayList<String> addClient(String clientJson) {
        Client client = Client.fromJson(clientJson);
        return null;

    }
}


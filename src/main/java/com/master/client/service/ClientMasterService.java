package com.master.client.service;


import com.master.client.bean.Client;
import com.master.client.dao.ClientMasterDao;
import com.util.RuleRunner;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ClientMasterService {

    @Autowired
    @Setter
    private RuleRunner ruleRunner;

    @Autowired
    private ClientMasterDao clientMasterDao;


    public Client addClient(String clientJson) {
        Client client = Client.fromJson(clientJson);
        validateClientData(client);
        return client.getErrors()!=null && client.getErrors().size()>0 ? client : addClientToDB(client);

    }

    protected Client addClientToDB(Client client) {
        return clientMasterDao.addClient(client);
    }

    protected Client validateClientData(Client client)
    {
        ruleRunner.runRules(client);
        return client;
    }
}


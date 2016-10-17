package com.master.client.controller;

import com.master.client.service.ClientMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
public class ClientMasterController {

    @Autowired
    ClientMasterService clientMasterService;

    protected static final String ADD = "/addClient" ;

    @RequestMapping(value = ADD, method = RequestMethod.POST, consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ArrayList<String>> addNewClient(@RequestBody String clientJson) {
        return new ResponseEntity<>(clientMasterService.addClient(clientJson), HttpStatus.OK );
    }
}

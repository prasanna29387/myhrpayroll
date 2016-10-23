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

@RestController
public class ClientMasterController {

    @Autowired
    ClientMasterService clientMasterService;

    protected static final String ADD = "/addClient" ;
    protected static final String UPDATE = "/updateClient" ;
    protected static final String DELETE = "/deleteClient" ;

    @RequestMapping(value = ADD, method = RequestMethod.POST, consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addNewClient(@RequestBody String clientJson) {
        return new ResponseEntity<>(clientMasterService.addClient(clientJson).toJsonForUI(), HttpStatus.OK );
    }

    @RequestMapping(value = UPDATE, method = RequestMethod.POST, consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateClient(@RequestBody String clientJson) {
        return new ResponseEntity<>(clientMasterService.updateClient(clientJson).toJsonForUI(), HttpStatus.OK );
    }

    @RequestMapping(value = DELETE, method = RequestMethod.POST, consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> deleteClient(@RequestBody String clientJson) {
        return new ResponseEntity<>(clientMasterService.deleteClient(clientJson).toJsonForUI(), HttpStatus.OK );
    }
}

package com.master.client.bean;

import org.junit.Test;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * Created by Athul on 10/19/16.
 */
public class ClientMethodsTest {

    private ClientMethods testObj = new ClientMethods();


    @Test
    public void testObjIsNotNull() throws Exception
    {
        assertNotNull(testObj);
    }

    @Test
    public void testToJsonForClient() throws Exception
    {

        Client client = Client.builder().clientId(1).clientName("A").errors(new ArrayList<>()).build();
        assertThat(testObj.toJson(client),is(equalTo("{\"clientName\":\"A\",\"clientId\":1,\"errors\":[]}")));

    }

}
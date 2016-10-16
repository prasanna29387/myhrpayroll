package com.master.client.controller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Athul on 10/16/16.
 */
public class ClientMasterControllerTest {


    ClientMasterController testObj = new ClientMasterController();


    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testAddMethod() throws Exception{

        testObj.addNewClient();

    }
}
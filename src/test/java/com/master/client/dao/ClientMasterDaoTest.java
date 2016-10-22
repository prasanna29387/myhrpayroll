package com.master.client.dao;

import com.master.client.bean.Client;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import static org.junit.Assert.*;

/**
 * Created by Athul on 10/22/16.
 */
public class ClientMasterDaoTest {

    private ClientMasterDao testObj = new ClientMasterDao();

    private Client fakeClient;

    @Before
    public void setUp() throws Exception {

        fakeClient = Client.builder().clientContactEmail("yaamini@gmail.com").clientContactName("Yamu").clientName("Somu and Co").clientId(6)
                .clientContactPhone("551-267-1754").panNumber("AMJPA2957P").taxIdentifactionNumber("TIN1234561234561").errors(new ArrayList<>()).warnings(new ArrayList<>()).build();

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testObjNotNull() throws Exception{
        assertNotNull(testObj);
    }

    @Test
    public void testAddClient() throws Exception
    {
        assertNotNull(testObj.addClient(fakeClient));
    }


}
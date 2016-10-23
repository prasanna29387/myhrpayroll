package com.master.client.dao;

import com.master.client.bean.Client;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.doReturn;

/**
 * Created by Athul on 10/22/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class ClientMasterDaoTest {


    @Mock
    private JdbcTemplate fakeJdbcTemplate;

    @InjectMocks
    @Spy
    private ClientMasterDao testObj = new ClientMasterDao();

    private Client fakeClient;

    @Before
    public void setUp() throws Exception {

        fakeClient = Client.builder().clientContactEmail("yaamini@gmail.com").clientContactName("Yamu").clientName("Somu and Co").clientId(6)
                .clientContactPhone("551-267-1754").panNumber("AMJPA2957P").errors(new ArrayList<>()).warnings(new ArrayList<>()).build();

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testObjNotNull() throws Exception{
        assertNotNull(testObj);
    }

    @Test
    public void testAddClientHappyPath() throws Exception
    {
        doReturn(1).when(testObj).addStatement(anyObject());
        testObj.addClient(fakeClient);
        assertTrue(fakeClient.getErrors().size()==0);
    }


    @Test
    public void testUpdateClientHappyPath() throws Exception
    {
        doReturn(1).when(testObj).updateStatement(anyObject());
        testObj.updateClient(fakeClient);
        assertTrue(fakeClient.getErrors().size()==0);
    }

    @Test
    public void testDeleteClientHappyPath() throws Exception
    {
        doReturn(1).when(testObj).deleteStatement(anyObject());
        testObj.deleteClient(fakeClient);
        assertTrue(fakeClient.getErrors().size()==0);
    }

    @Test
    public void testAddClientWithException() throws Exception
    {
        doReturn(-1).when(testObj).addStatement(anyObject());
        testObj.addClient(fakeClient);
        assertTrue(fakeClient.getErrors().size()==1);
    }


    @Test
    public void testUpdateWithException() throws Exception
    {
        fakeClient = Client.builder().clientContactEmail("yaamini@gmail.com").clientContactName("Yamu").clientName("Somu and Co").clientId(6)
                .clientContactPhone("551-267-1754").panNumber("AMJPA2957P").errors(new ArrayList<>()).warnings(new ArrayList<>()).build();
        doReturn(-1).when(testObj).updateStatement(anyObject());
        testObj.updateClient(fakeClient);
        assertTrue(fakeClient.getErrors().size()==1);
    }

    @Test
    public void testDeleteWithException() throws Exception
    {
        fakeClient = Client.builder().clientContactEmail("yaamini@gmail.com").clientContactName("Yamu").clientName("Somu and Co").clientId(6)
                .clientContactPhone("551-267-1754").panNumber("AMJPA2957P").errors(new ArrayList<>()).warnings(new ArrayList<>()).build();
        doReturn(-1).when(testObj).deleteStatement(anyObject());
        testObj.deleteClient(fakeClient);
        assertTrue(fakeClient.getErrors().size()==1);
    }
}
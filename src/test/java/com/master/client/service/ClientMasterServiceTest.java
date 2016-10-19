package com.master.client.service;

import com.master.client.bean.Client;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemErrRule;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class ClientMasterServiceTest {

    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().muteForSuccessfulTests().enableLog();
    @Rule
    public final SystemErrRule systemErrRule = new SystemErrRule().muteForSuccessfulTests().enableLog();


    private ClientMasterService fakeClientMasterService = new ClientMasterService();

    private Client client;

    @Before
    public void setUp() throws Exception {
        client =  Client.builder().clientName("Stephen").clientId(12345).clientContactName("StephenRaj").clientContactPhone("98410")
                .clientContactEmail("stephen@gmail.com").panNumber("AMJ1234").taxIdentifactionNumber("1234").build();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testEmptyClient() throws Exception
    {
        Client client = Client.builder().build();
        fakeClientMasterService.addClient(client.toJson());
        assertTrue(client.getErrors().size()>0);
    }
}
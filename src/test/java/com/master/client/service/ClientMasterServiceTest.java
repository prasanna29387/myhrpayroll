package com.master.client.service;

import com.master.client.bean.Client;
import com.util.RuleRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemErrRule;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;

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

       RuleRunner ruleRunner = new RuleRunner();

        client =  Client.builder().clientName("Stephen").clientId(12345).clientContactName("StephenRaj").clientContactPhone("98410")
                .clientContactEmail("stephen@gmail.com").panNumber("AMJ1234").taxIdentifactionNumber("1234").errors(new ArrayList<>())
                .warnings(new ArrayList<>()).build();
        fakeClientMasterService.setRuleRunner(ruleRunner);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testEmptyClient() throws Exception
    {
        Client client = Client.builder().errors(new ArrayList<>()).warnings(new ArrayList<>()).build();
        Client result = fakeClientMasterService.addClient(client.toJson());
        assertTrue(result.getErrors().size()>0);
    }
}
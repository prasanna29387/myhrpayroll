package com.master.client.service;

import com.master.client.bean.Client;
import com.master.client.dao.ClientMasterDao;
import com.util.RuleRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemErrRule;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ClientMasterServiceTest {

    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().muteForSuccessfulTests().enableLog();
    @Rule
    public final SystemErrRule systemErrRule = new SystemErrRule().muteForSuccessfulTests().enableLog();


    @Mock
    private ClientMasterDao fakeClientMasterDao = new ClientMasterDao();

    @Spy
    @InjectMocks
    private ClientMasterService fakeClientMasterService = new ClientMasterService();


    @Before
    public void setUp() throws Exception {
        fakeClientMasterService.setRuleRunner(new RuleRunner());
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testEmptyClient() throws Exception {
        Client client = Client.builder().errors(new ArrayList<>()).warnings(new ArrayList<>()).build();
        Client result = fakeClientMasterService.addClient(client.toJson());
        assertTrue(result.getErrors().size() > 0);
    }

    @Test
    public void testClientHappyPath() throws Exception {
        Client goodClient = Client.builder().clientContactEmail("yaamini@gmail.com").clientContactName("Yamu").clientName("Somu and Co").clientId(6)
                .clientContactPhone("551-267-1754").panNumber("AMJPA2957P").taxIdentifactionNumber("TIN1234561234561").errors(new ArrayList<>()).warnings(new ArrayList<>()).build();

        fakeClientMasterService.addClient(goodClient.toJson());
        verify(fakeClientMasterDao).addClient(anyObject());
    }


    @Test
    public void testClientFullOfErrors() throws Exception {
        Client badClient = Client.builder().clientContactEmail("yamini.com").clientContactName("YAM!@#").clientId(0)
                .clientContactPhone("123-123").panNumber("AMJ123").taxIdentifactionNumber("123123!!!").errors(new ArrayList<>()).warnings(new ArrayList<>()).build();

        Client result = fakeClientMasterService.addClient(badClient.toJson());
        assertTrue(result.getErrors().toString().contains("Client Contact Email is not in valid format."));
        assertTrue(result.getErrors().toString().contains("Client Name is a required field."));
        assertTrue(result.getErrors().toString().contains("Client Id cannot be 0."));
        assertTrue(result.getErrors().toString().contains("Client Contact Name must not contain anything other special characters."));
        assertTrue(result.getErrors().toString().contains("TAX Identification Number cannot contain any special characters and must be 16 characters."));
        assertTrue(result.getErrors().toString().contains("Client Contact Email is not in valid format."));
        assertTrue(result.getErrors().toString().contains("Client Contact Phone is not in valid format."));
        verify(fakeClientMasterDao,never()).addClient(anyObject());
    }
}
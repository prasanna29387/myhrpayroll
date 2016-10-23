package com.master.client.controller;

import com.base.BaseControllerTest;
import com.master.client.bean.Client;
import com.master.client.service.ClientMasterService;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemErrRule;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class ClientMasterControllerTest extends BaseControllerTest {

    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().muteForSuccessfulTests().enableLog();

    @Rule
    public final SystemErrRule systemErrRule = new SystemErrRule().muteForSuccessfulTests().enableLog();

    @Mock
    ClientMasterService fakeClientMasterService = new ClientMasterService();

    @InjectMocks
    ClientMasterController testObj = new ClientMasterController();


    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(testObj).build();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testAddClient() throws Exception{
        Client client = Client.builder().build();
        when(fakeClientMasterService.addClient(anyString())).thenReturn(client);

        Client content = Client.builder().clientName("Stephen").clientId(12345).clientContactName("StephenRaj").clientContactPhone("98410")
                .clientContactEmail("stephen@gmail.com").panNumber("AMJ1234").build();

        mockMvc.perform(post(ClientMasterController.ADD).contentType(MediaType.ALL_VALUE)
                .content(content.toJson())).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().string(containsString(client.toJsonForUI())));

    }

    @Test
    public void testUpdateClient() throws Exception{
        Client client = Client.builder().build();
        when(fakeClientMasterService.updateClient(anyString())).thenReturn(client);

        Client content = Client.builder().clientName("Stephen").clientId(12345).clientContactName("StephenRaj").clientContactPhone("98410")
                .clientContactEmail("stephen@gmail.com").panNumber("AMJ1234").build();

        mockMvc.perform(post(ClientMasterController.UPDATE).contentType(MediaType.ALL_VALUE)
                .content(content.toJson())).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().string(containsString(client.toJsonForUI())));

    }

    @Test
    public void testDeleteClient() throws Exception{
        Client client = Client.builder().build();
        when(fakeClientMasterService.deleteClient(anyString())).thenReturn(client);

        Client content = Client.builder().clientName("Stephen").clientId(12345).clientContactName("StephenRaj").clientContactPhone("98410")
                .clientContactEmail("stephen@gmail.com").panNumber("AMJ1234").build();

        mockMvc.perform(post(ClientMasterController.DELETE).contentType(MediaType.ALL_VALUE)
                .content(content.toJson())).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().string(containsString(client.toJsonForUI())));

    }
}
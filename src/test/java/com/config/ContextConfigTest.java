package com.config;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;

/**
 * Created by Athul on 10/22/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class ContextConfigTest {

    @Mock
    private DataSource fakeDataSource = new DriverManagerDataSource();


    @InjectMocks
    @Spy
    private ContextConfig testObj = new ContextConfig();



    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testGetDataSource() throws Exception {
    }

    @Test
    public void testGetJdbcTemplate() throws Exception {
        doReturn(fakeDataSource).when(testObj).getDataSource();
        assertThat(testObj.getJdbcTemplate(),is(instanceOf(JdbcTemplate.class)));
    }
}
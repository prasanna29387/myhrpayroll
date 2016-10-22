package com.config;

import com.util.RuleRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * Created by Athul on 10/22/16.
 */
public class ContextConfigTest {

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
        assertThat(testObj.getJdbcTemplate(),is(instanceOf(JdbcTemplate.class)));
    }
}
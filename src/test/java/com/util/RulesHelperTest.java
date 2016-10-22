package com.util;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by xeccwrj on 10/22/2016.
 */
public class RulesHelperTest {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testIsValidEmail() throws Exception {

        assertTrue(RulesHelper.isValidEmail("yaamini6@gmail.com"));
        assertTrue(RulesHelper.isValidEmail("yaamini@gmail.com"));
        assertTrue(RulesHelper.isValidEmail("yaamini_6@gmail.com"));
        assertTrue(RulesHelper.isValidEmail("yaamini_6@Yahoo.com"));
        assertTrue(RulesHelper.isValidEmail("yaamini_6@yahho.co.in"));
        assertTrue(RulesHelper.isValidEmail("yaamini.6@yahho.co.in"));
        assertTrue(RulesHelper.isValidEmail("yaamini.shankar@yahho.co.in"));
        assertTrue(RulesHelper.isValidEmail("yaamini.shankar6@yahho.co.in"));

    }
}
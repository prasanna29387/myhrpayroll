package com.util;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * Created by Athul Ravindran  on 10/22/2016.
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


    @Test
    public void testAddition() throws Exception
    {

        assertThat(RulesHelper.addNumbers(3,4,0),is(equalTo(7)));
        assertThat(RulesHelper.addNumbers(100,55,0),is(equalTo(155)));
        assertThat(RulesHelper.addNumbers(100,55,10),is(equalTo(165)));
        assertThat(RulesHelper.addNumbers(100,55,10,10,80,18,16,17),is(equalTo(306)));
    }
}
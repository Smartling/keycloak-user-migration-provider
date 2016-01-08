package com.acme.legacy.app.manager;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by scott on 12/23/15.
 */
public class SimplePasswordEncoderTest
{

    private PasswordEncoder passwordEncoder = new SimplePasswordEncoder();

    @Test
    public void testEncode() throws Exception
    {
        System.err.println(passwordEncoder.encode("Martini4", "dhfq178"));
    }
}
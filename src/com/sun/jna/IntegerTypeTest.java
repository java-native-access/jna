package com.sun.jna;

import junit.framework.TestCase;

/**
 * Created with IntelliJ IDEA.
 * User: lwahonen
 * Date: 2018-01-13
 * Time: 2:06 PM
 */
public class IntegerTypeTest extends TestCase {

    public void testEquals() {
        NativeLong l=new NativeLong(5);
        assertTrue(l.equals(5));
        assertTrue(l.equals(new NativeLong(5)));
        assertFalse(l.equals(7));
        assertFalse(l.equals(new NativeLong(7)));
    }

    public void testCompare() {
        NativeLong l=new NativeLong(5);
        assertEquals(0, IntegerType.compare(l, 5));
        assertTrue(( IntegerType.compare(l, 7) < 0));
        assertTrue(( IntegerType.compare(l, 4) > 0));
    }
}
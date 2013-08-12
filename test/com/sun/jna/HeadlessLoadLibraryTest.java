package com.sun.jna;

import junit.framework.TestCase;

public class HeadlessLoadLibraryTest extends TestCase {
    
    public void testLoadWhenHeadless() {
        System.setProperty("java.awt.headless", "true");
        assertTrue("Pointer size must not be zero", Pointer.SIZE > 0);
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(HeadlessLoadLibraryTest.class);
    }
}

package com.sun.jna;

import junit.framework.TestCase;

public class IntegerTypeTest extends TestCase {

    public static class Sized extends IntegerType {
        public Sized() { this(4, 0); }
        public Sized(int size, long value) { super(size, value); }
    }

    public void testWriteNull() {
        class NTStruct extends Structure {
            public Sized field;
        }
        NTStruct s = new NTStruct();
        assertNotNull("Field not initialized", s.field);
    }
    public void testReadNull() {
        class NTStruct extends Structure {
            public Sized field;
        }
        NTStruct s = new NTStruct();
        s.read();
        assertNotNull("Integer type field should be initialized on read", s.field);
    }

    public void testCheckArgumentSize() {
        for (int i=1;i <= 8;i*=2) {
            long value = -1L << (i*8-1);
            new Sized(i, value);
            new Sized(i, -1);
            new Sized(i, 0);
            new Sized(i, 1);

            value = 1L << (i*8-1);
            new Sized(i, value);
            value = -1L & ~(-1L << (i*8));
            new Sized(i, value);

            if (i < 8) {
                try {
                    value = 1L << (i*8);
                    new Sized(i, value);
                    fail("Value exceeding size (" + i + ") should fail");
                }
                catch(IllegalArgumentException e) {
                }
            }
            if (i < 8) {
                try {
                    value = -1L << (i*8);
                    new Sized(i, value);
                    fail("Negative value (" + value + ") exceeding size (" + i + ") should fail");
                }
                catch(IllegalArgumentException e) {
                }
            }
        }
    }
	
    public void testInitialValue() {
        long VALUE = 20;
        NativeLong nl = new NativeLong(VALUE);
        assertEquals("Wrong initial value", VALUE, nl.longValue());
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(IntegerTypeTest.class);
    }
}

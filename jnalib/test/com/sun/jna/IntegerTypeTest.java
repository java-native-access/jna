package com.sun.jna;

import junit.framework.TestCase;

public class IntegerTypeTest extends TestCase {
	
	private class Sized extends IntegerType {
		public Sized(int size, long value) { super(size, value); }
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
	
	public static void main(String[] args) {
		junit.textui.TestRunner.run(IntegerTypeTest.class);
	}
}

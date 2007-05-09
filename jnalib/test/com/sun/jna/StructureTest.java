package com.sun.jna;

import junit.framework.TestCase;
import com.sun.jna.win32.User32;

/**
 *
 * @author Todd Fast, todd.fast@sun.com
 */
public class StructureTest extends TestCase {
    
    public StructureTest(String testName) {
        super(testName);
    }            

	public static junit.framework.Test suite() {

		junit.framework.TestSuite suite = new junit.framework.TestSuite(StructureTest.class);
		
		return suite;
	}

	public static void main(java.lang.String[] argList) {

		junit.textui.TestRunner.run(suite());
	}

//	/**
//	 * Test of size method, of class com.sun.jna.Structure.
//	 */
//	public void testSize() {
//
//		System.out.println("testSize");
//		
//		// TODO add your test code below by replacing the default call to fail.
//		fail("The test case is empty.");
//	}
//
//	/**
//	 * Test of free method, of class com.sun.jna.Structure.
//	 */
//	public void testFree() {
//
//		System.out.println("testFree");
//		
//		// TODO add your test code below by replacing the default call to fail.
//		fail("The test case is empty.");
//	}
//
//	/**
//	 * Test of getFieldOffset method, of class com.sun.jna.Structure.
//	 */
//	public void testGetFieldOffset() {
//
//		System.out.println("testGetFieldOffset");
//		
//		// TODO add your test code below by replacing the default call to fail.
//		fail("The test case is empty.");
//	}
//
//	/**
//	 * Test of getMemory method, of class com.sun.jna.Structure.
//	 */
//	public void testGetMemory() {
//
//		System.out.println("testGetMemory");
//		
//		// TODO add your test code below by replacing the default call to fail.
//		fail("The test case is empty.");
//	}
//
//	/**
//	 * Test of fields method, of class com.sun.jna.Structure.
//	 */
//	public void testFields() {
//
//		System.out.println("testFields");
//		
//		// TODO add your test code below by replacing the default call to fail.
//		fail("The test case is empty.");
//	}
    
    // TODO add test methods here. The name must begin with 'test'. For example:
    // public void testHello() {}

	public void testConstructor()
		throws Exception
	{
		// This way is difficult; instead, use allocate method to avoid
		// additional generic declaration
//		Structure<User32.FlashWinInfo> flashWinInfo=
//			new Structure<User32.FlashWinInfo>(User32.FlashWinInfo.class);

		Structure<User32.FlashWinInfo> flashWinInfo=
			Structure.allocate(User32.FlashWinInfo.class);
		
		flashWinInfo.getMemory().clear();

		assertEquals("Size should equal 0",0,flashWinInfo.struct().size);

		flashWinInfo.read();
		assertEquals("Size should equal 0",0,flashWinInfo.struct().size);

		flashWinInfo.struct().size=10;
		flashWinInfo.write();
		assertEquals("Size should equal 10",10,flashWinInfo.struct().size);

		flashWinInfo.read();
		assertEquals("Size should equal 10",10,flashWinInfo.struct().size);

		flashWinInfo.getMemory().setInt(flashWinInfo.getFieldOffset("size"),13);
		flashWinInfo.read();
		assertEquals("Size should equal 13",13,flashWinInfo.struct().size);
	}
}

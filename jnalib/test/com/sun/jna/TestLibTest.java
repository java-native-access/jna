package com.sun.jna;

import com.sun.jna.TestLib.MathOp;
import junit.framework.TestCase;

/**
 *
 * @author Todd Fast, todd.fast@sun.com
 */
public class TestLibTest extends TestCase {
    
    public TestLibTest(String testName) {
        super(testName);
    }            

	public static junit.framework.Test suite() {

		junit.framework.TestSuite suite = new junit.framework.TestSuite(TestLibTest.class);
		
		return suite;
	}

	public static void main(java.lang.String[] argList) {

		junit.textui.TestRunner.run(suite());
	}

    
    // TODO add test methods here. The name must begin with 'test'. For example:
    // public void testHello() {}

	public void testLibrary()
		throws Exception
	{
		TestLib testLib=Native.loadLibrary(TestLib.class);
		
		testLib.doNothing();
		testLib.doNothingLoop();
		testLib.callClock();
		testLib.loopClock();
		
		assertTrue("returnTrue() should return true",
			testLib.returnTrue());

		assertFalse("returnFalse() should return false",
			testLib.returnFalse());
	
		assertEquals("returnInt() should return 42",42,
			testLib.returnInt());
		
		assertEquals("returnDouble() should return 42d",42d,
			testLib.returnDouble());
	
		assertEquals("returnString() should return \"An adventurer is you!\"",
			"An adventurer is you!",
			testLib.returnString());
		
		assertTrue("not(false) should return true",
			testLib.not(false));
	
		assertFalse("not(true) should return false",
			testLib.not(true));
	
		assertEquals("add(-1,1) should return 0",0,
			testLib.add(-1,1));
		assertEquals("add(1,1) should return 2",2,
			testLib.add(1,1));
		assertEquals("add(42,42) should return 84",84,
			testLib.add(42,42));

		testLib.print("This string was printed from native code!");
		
//		Structure<MathOp> mathOp=Structure.allocate(MathOp.class);
//		mathOp.struct().value1=15;
//		mathOp.struct().value2=16;
//		mathOp.struct().message="This is a message from Java code";

		MathOp mathOp=new MathOp();
		mathOp.value1=15;
		mathOp.value2=16;
		mathOp.message="This is a message from Java code";
		
		testLib.addAndPrint(mathOp);
	}
}

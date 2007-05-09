package com.sun.jna;

import com.sun.jna.annotation.NativeFunction;
import junit.framework.TestCase;

/**
 *
 * @author Todd Fast, todd.fast@sun.com
 */
public class SanityTest extends Object {
    
	public static void main(java.lang.String[] argList)
		throws Exception
	{
//		TestLib testLib=Native.loadLibrary("testlib.dll",TestLib.class);

		Function returnTrue = new Function("testlib", "returnTrue");
		System.out.println(returnTrue.invokeBoolean());
		
//		testLib.doNothing();
//		testLib.doNothingLoop();
//		testLib.returnTrue();
	}
}

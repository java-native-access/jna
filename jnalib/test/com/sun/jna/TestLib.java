package com.sun.jna;

import com.sun.jna.annotation.*;

/**
 *
 * @author  Todd Fast, todd.fast@sun.com
 */
@NativeLibrary(name="testlib", convention=CallingConvention.STDCALL)
public interface TestLib
{
	@NativeFunction
	public void doNothing();
	
	@NativeFunction
	public void doNothingLoop();
	
	@NativeFunction
	public void callClock();
	
	@NativeFunction
	public void loopClock();
	
	@NativeFunction
	public boolean returnTrue();
	
	@NativeFunction
	public boolean returnFalse();
	
	@NativeFunction
	public int returnInt();
	
	@NativeFunction
	public double returnDouble();
	
	@NativeFunction
	public String returnString();
	
	@NativeFunction
	public boolean not(boolean value);
	
	@NativeFunction
	public int add(int value1, int value2);
	
	@NativeFunction
	public void print(String value);
	
	@NativeFunction
//	public void addAndPrint(Structure<MathOp> mathop);
	public void addAndPrint(MathOp mathop);

	@NativeStructure
	public static class MathOp extends Structure
	{
		@NativeField
		public int value1;
		
		@NativeField
		public int value2;
		
		@NativeField
		public String message;
	}
}

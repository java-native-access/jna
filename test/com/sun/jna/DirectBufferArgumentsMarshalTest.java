/* Copyright (c) 2009 Timothy Wall, All Rights Reserved
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.  
 */
package com.sun.jna;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;

import com.sun.jna.ArgumentsMarshalTest.TestLibrary.CheckFieldAlignment.ByReference;

/** Exercise a range of native methods.
 *
 * @author twall@users.sf.net
 */
public class DirectBufferArgumentsMarshalTest extends DirectArgumentsMarshalTest {

    public static class DirectTestLibrary implements TestLibrary {
        /** Dummy.  Automatically fail when passed an object. */
        public String returnStringArgument(Object arg) {throw new IllegalArgumentException(arg.getClass().getName()); }
        public native boolean returnBooleanArgument(boolean arg);
        public native byte returnInt8Argument(byte arg);
        public native char returnWideCharArgument(char arg);
        public native short returnInt16Argument(short arg);
        public native int returnInt32Argument(int i);
        public native long returnInt64Argument(long l);
        public native NativeLong returnLongArgument(NativeLong l);
        public native float returnFloatArgument(float f);
        public native double returnDoubleArgument(double d);
        public native String returnStringArgument(String s);
        public native WString returnWStringArgument(WString s);
        public native Pointer returnPointerArgument(Pointer p);
        public String returnStringArrayElement(String[] args, int which) {throw new UnsupportedOperationException();}
        public WString returnWideStringArrayElement(WString[] args, int which) {throw new UnsupportedOperationException();}
        public Pointer returnPointerArrayElement(Pointer[] args, int which) {throw new UnsupportedOperationException();}
        public TestPointerType returnPointerArrayElement(TestPointerType[] args, int which) {throw new UnsupportedOperationException();}
        public CheckFieldAlignment returnPointerArrayElement(CheckFieldAlignment.ByReference[] args, int which) {throw new UnsupportedOperationException();}
        public int returnRotatedArgumentCount(String[] args) {throw new UnsupportedOperationException();}

        public native long checkInt64ArgumentAlignment(int i, long j, int i2, long j2);
        public native double checkDoubleArgumentAlignment(float i, double j, float i2, double j2);
        public native Pointer testStructurePointerArgument(CheckFieldAlignment p);
        public native int testStructureByValueArgument(CheckFieldAlignment.ByValue p);
        public int testStructureArrayInitialization(CheckFieldAlignment[] p, int len) {
            throw new UnsupportedOperationException();
        }
        public void modifyStructureArray(CheckFieldAlignment[] p, int length) {
            throw new UnsupportedOperationException(); 
        }
            
        public native int fillInt8Buffer(byte[] buf, int len, byte value);
        public native int fillInt16Buffer(short[] buf, int len, short value);
        public native int fillInt32Buffer(int[] buf, int len, int value);
        public native int fillInt64Buffer(long[] buf, int len, long value);
        public native int fillFloatBuffer(float[] buf, int len, float value);
        public native int fillDoubleBuffer(double[] buf, int len, double value);

        // ByteBuffer alternative definitions
        public native int fillInt8Buffer(ByteBuffer buf, int len, byte value);
        public native int fillInt16Buffer(ByteBuffer buf, int len, short value);
        public native int fillInt32Buffer(ByteBuffer buf, int len, int value);
        public native int fillInt64Buffer(ByteBuffer buf, int len, long value);
        public native int fillFloatBuffer(ByteBuffer buf, int len, float value);
        public native int fillDoubleBuffer(ByteBuffer buf, int len, double value);
        
        // {Short|Int|Long|Float|Double}Buffer alternative definitions        
        public native int fillInt16Buffer(ShortBuffer buf, int len, short value);
        public native int fillInt32Buffer(IntBuffer buf, int len, int value);
        public native int fillInt64Buffer(LongBuffer buf, int len, long value);
        public native int fillFloatBuffer(FloatBuffer buf, int len, float value);
        public native int fillDoubleBuffer(DoubleBuffer buf, int len, double value);

        // dummy to avoid causing Native.register to fail
        public boolean returnBooleanArgument(Object arg) {throw new IllegalArgumentException();}

        public native Pointer testStructurePointerArgument(MinTestStructure s);
        public native String returnStringFromVariableSizedStructure(VariableSizedStructure s);
        public native void setCallbackInStruct(CbStruct s);
        public native TestUnion testUnionByValueCallbackArgument(UnionCallback cb, TestUnion arg);

        static {
            Native.register("testlib");
        }

		public int testStructureByReferenceArrayInitialization(ByReference[] p,
				int len) {
			// TODO Auto-generated method stub
			return 0;
		}
		public void modifyStructureByReferenceArray(ByReference[] p, int length) {
			// TODO Auto-generated method stub
			
		}
    }

    /* Override original. */
    protected void setUp() {
        lib = new DirectTestLibrary();
    }
    
    public static class DirectNativeMappedLibrary implements NativeMappedLibrary {
        public native int returnInt32Argument(Custom arg);
        static {
            Native.register("testlib");
        }
    }
    protected NativeMappedLibrary loadNativeMappedLibrary() {
        return new DirectNativeMappedLibrary();
    }

    public static void main(java.lang.String[] argList) {
        junit.textui.TestRunner.run(DirectBufferArgumentsMarshalTest.class);
    }
    
}

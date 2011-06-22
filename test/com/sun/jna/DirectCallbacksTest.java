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

import java.util.Map;

import com.sun.jna.ptr.IntByReference;

/** Exercise callback-related functionality.
 *
 * @author twall@users.sf.net
 */
public class DirectCallbacksTest extends CallbacksTest {

    public static class DirectTestLibrary implements TestLibrary {
        public native void callVoidCallback(VoidCallbackCustom c);
        public native boolean callBooleanCallback(BooleanCallback c, boolean arg, boolean arg2);
        public native byte callInt8Callback(ByteCallback c, byte arg, byte arg2);
        public native short callInt16Callback(ShortCallback c, short arg, short arg2);
        public native int callInt32Callback(Int32Callback c, int arg, int arg2);
        public native NativeLong callNativeLongCallback(NativeLongCallback c, NativeLong arg, NativeLong arg2);
        public native long callInt64Callback(Int64Callback c, long arg, long arg2);
        public native float callFloatCallback(FloatCallback c, float arg, float arg2);
        public native double callDoubleCallback(DoubleCallback c, double arg, double arg2);
        public native SmallTestStructure callStructureCallback(StructureCallback c, SmallTestStructure arg);
        public native String callStringCallback(StringCallback c, String arg);
        public native WString callWideStringCallback(WideStringCallback c, WString arg);
        public Pointer callStringArrayCallback(StringArrayCallback c, String[] arg) { throw new UnsupportedOperationException(); }
        public native int callCallbackWithByReferenceArgument(CopyArgToByReference cb, int arg, IntByReference result);
        public native TestStructure.ByValue callCallbackWithStructByValue(TestStructure.TestCallback callback, TestStructure.ByValue cbstruct);
        public native CbCallback callCallbackWithCallback(CbCallback cb);
        public native Int32CallbackX returnCallback();
        public native Int32CallbackX returnCallbackArgument(Int32CallbackX cb);
        public native void callVoidCallback(VoidCallback c);

        public native int callInt32Callback(CustomCallback cb, int arg1, int arg2);
        public native void callCallbackInStruct(CbStruct s);

        static {
            Native.register("testlib");
        }
    }

    protected void setUp() {
        lib = new DirectTestLibrary();
    }
    
    public static class DirectCallbackTestLibrary implements CallbackTestLibrary {
        public native double callInt32Callback(DoubleCallback c, double arg, double arg2);
        public native float callInt64Callback(FloatCallback c, float arg, float arg2);
        static {
            Native.register(NativeLibrary.getInstance("testlib", _OPTIONS));
        }
    }

    protected CallbackTestLibrary loadCallbackTestLibrary() {
        return new DirectCallbackTestLibrary();
    }

    // Currently unsupported tests
    public void testCallStringArrayCallback() { }
    public void testCallbackExceptionHandlerWithCallbackProxy() { }

    public static void main(java.lang.String[] argList) {
        junit.textui.TestRunner.run(DirectCallbacksTest.class);
    }
}

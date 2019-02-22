/* Copyright (c) 2009 Timothy Wall, All Rights Reserved
 *
 * The contents of this file is dual-licensed under 2
 * alternative Open Source/Free licenses: LGPL 2.1 or later and
 * Apache License 2.0. (starting with JNA version 4.0.0).
 *
 * You can freely decide which license you want to apply to
 * the project.
 *
 * You may obtain a copy of the LGPL License at:
 *
 * http://www.gnu.org/licenses/licenses.html
 *
 * A copy is also included in the downloadable source code package
 * containing JNA, in file "LGPL2.1".
 *
 * You may obtain a copy of the Apache License at:
 *
 * http://www.apache.org/licenses/
 *
 * A copy is also included in the downloadable source code package
 * containing JNA, in file "AL2.0".
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
        @Override
        public native void callVoidCallback(VoidCallbackCustom c);
        @Override
        public native boolean callBooleanCallback(BooleanCallback c, boolean arg, boolean arg2);
        @Override
        public native byte callInt8Callback(ByteCallback c, byte arg, byte arg2);
        @Override
        public native short callInt16Callback(ShortCallback c, short arg, short arg2);
        @Override
        public native int callInt32Callback(Int32Callback c, int arg, int arg2);
        @Override
        public native NativeLong callNativeLongCallback(NativeLongCallback c, NativeLong arg, NativeLong arg2);
        @Override
        public native long callInt64Callback(Int64Callback c, long arg, long arg2);
        @Override
        public native float callFloatCallback(FloatCallback c, float arg, float arg2);
        @Override
        public native double callDoubleCallback(DoubleCallback c, double arg, double arg2);
        @Override
        public native SmallTestStructure callStructureCallback(StructureCallback c, SmallTestStructure arg);
        @Override
        public native String callStringCallback(StringCallback c, String arg, String arg2);
        @Override
        public native WString callWideStringCallback(WideStringCallback c, WString arg, WString arg2);
        @Override
        public Pointer callStringArrayCallback(StringArrayCallback c, String[] arg) { throw new UnsupportedOperationException(); }
        @Override
        public native int callCallbackWithByReferenceArgument(CopyArgToByReference cb, int arg, IntByReference result);
        @Override
        public native TestStructure.ByValue callCallbackWithStructByValue(TestStructure.TestCallback callback, TestStructure.ByValue cbstruct);
        @Override
        public native CbCallback callCallbackWithCallback(CbCallback cb);
        @Override
        public native Int32CallbackX returnCallback();
        @Override
        public native Int32CallbackX returnCallbackArgument(Int32CallbackX cb);
        @Override
        public native void callVoidCallback(VoidCallback c);
        @Override
        public native void callVoidCallbackThreaded(VoidCallback c, int count, int ms, String name, int stacksize);

        @Override
        public native int callInt32Callback(CustomCallback cb, int arg1, int arg2);
        @Override
        public native void callCallbackInStruct(CbStruct s);
        @Override
        public native TestUnion testUnionByValueCallbackArgument(UnionCallback cb, TestUnion arg);

        static {
            Native.register("testlib");
        }
    }

    @Override
    protected void setUp() {
        lib = new DirectTestLibrary();
    }

    @Override
    protected Map<Callback, CallbackReference> callbackCache() {
        return CallbackReference.directCallbackMap;
    }

    public static class DirectCallbackTestLibrary implements CallbackTestLibrary {
        @Override
        public native double callInt32Callback(DoubleCallback c, double arg, double arg2);
        @Override
        public native float callInt64Callback(FloatCallback c, float arg, float arg2);
        @Override
        public native String callWideStringCallback(WStringCallback c, String arg, String arg2);
        static {
            Native.register(NativeLibrary.getInstance("testlib", _OPTIONS));
        }
    }

    @Override
    protected CallbackTestLibrary loadCallbackTestLibrary() {
        return new DirectCallbackTestLibrary();
    }

    // Currently unsupported tests
    @Override
    public void testCallStringArrayCallback() { }
    @Override
    public void testCallbackExceptionHandlerWithCallbackProxy() { }

    public static void main(java.lang.String[] argList) {
        junit.textui.TestRunner.run(DirectCallbacksTest.class);
    }
}

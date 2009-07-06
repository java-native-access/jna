/* Copyright (c) 2007-2008 Timothy Wall, All Rights Reserved
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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import junit.framework.TestCase;

import com.sun.jna.Callback.UncaughtExceptionHandler;
import com.sun.jna.CallbacksTest.TestLibrary.CbCallback;
import com.sun.jna.ptr.IntByReference;

/** Exercise callback-related functionality.
 *
 * @author twall@users.sf.net
 */
public class CallbacksTest extends TestCase {

    private static final double DOUBLE_MAGIC = -118.625d;
    private static final float FLOAT_MAGIC = -118.625f;

    public static class SmallTestStructure extends Structure {
        public double value;
    }
    public static class TestStructure extends Structure {
        public static class ByValue extends TestStructure implements Structure.ByValue { }
        public static interface TestCallback extends Callback {
            TestStructure.ByValue callback(TestStructure.ByValue s);
        }
        public byte c;
        public short s;
        public int i;
        public long j;
        public SmallTestStructure inner;
    }
    public static interface TestLibrary extends Library {
        interface NoMethodCallback extends Callback {
        }
        interface CustomMethodCallback extends Callback {
            void invoke();
        }
        interface TooManyMethodsCallback extends Callback {
            void invoke();
            void invoke2();
        }
        interface MultipleMethodsCallback extends Callback {
            void invoke();
            void callback();
        }
        interface VoidCallback extends Callback {
            void callback();
        }
        void callVoidCallback(VoidCallback c);
        interface VoidCallbackCustom extends Callback {
            void customMethodName();
        }
        abstract class VoidCallbackCustomAbstract implements VoidCallbackCustom {
            public void customMethodName() { }
        }
        class VoidCallbackCustomDerived extends VoidCallbackCustomAbstract { }
        void callVoidCallback(VoidCallbackCustom c);
        interface BooleanCallback extends Callback {
            boolean callback(boolean arg, boolean arg2);
        }
        boolean callBooleanCallback(BooleanCallback c, boolean arg, boolean arg2);
        interface ByteCallback extends Callback {
            byte callback(byte arg, byte arg2);
        }
        byte callInt8Callback(ByteCallback c, byte arg, byte arg2);
        interface ShortCallback extends Callback {
            short callback(short arg, short arg2);
        }
        short callInt16Callback(ShortCallback c, short arg, short arg2);
        interface Int32Callback extends Callback {
            int callback(int arg, int arg2);
        }
        int callInt32Callback(Int32Callback c, int arg, int arg2);
        interface NativeLongCallback extends Callback {
            NativeLong callback(NativeLong arg, NativeLong arg2);
        }
        NativeLong callNativeLongCallback(NativeLongCallback c, NativeLong arg, NativeLong arg2);
        interface Int64Callback extends Callback {
            long callback(long arg, long arg2);
        }
        long callInt64Callback(Int64Callback c, long arg, long arg2);
        interface FloatCallback extends Callback {
            float callback(float arg, float arg2);
        }
        float callFloatCallback(FloatCallback c, float arg, float arg2);
        interface DoubleCallback extends Callback {
            double callback(double arg, double arg2);
        }
        double callDoubleCallback(DoubleCallback c, double arg, double arg2);
        interface StructureCallback extends Callback {
            SmallTestStructure callback(SmallTestStructure arg);
        }
        SmallTestStructure callStructureCallback(StructureCallback c, SmallTestStructure arg);
        interface StringCallback extends Callback {
            String callback(String arg);
        }
        String callStringCallback(StringCallback c, String arg);
        interface WideStringCallback extends Callback {
            WString callback(WString arg);
        }
        WString callWideStringCallback(WideStringCallback c, WString arg);
        interface CopyArgToByReference extends Callback {
        	int callback(int arg, IntByReference result);
        }
        interface StringArrayCallback extends Callback {
            String[] callback(String[] arg);
        }
        Pointer callStringArrayCallback(StringArrayCallback c, String[] arg);
        int callCallbackWithByReferenceArgument(CopyArgToByReference cb, int arg, IntByReference result);
        TestStructure.ByValue callCallbackWithStructByValue(TestStructure.TestCallback callback, TestStructure.ByValue cbstruct);
        interface CbCallback extends Callback {
            CbCallback callback(CbCallback arg);
        }
        CbCallback callCallbackWithCallback(CbCallback cb);

        interface Int32CallbackX extends Callback {
            public int callback(int arg);
        }
        Int32CallbackX returnCallback();
        Int32CallbackX returnCallbackArgument(Int32CallbackX cb);

        interface CustomCallback extends Callback {
            Custom callback(Custom arg1, Custom arg2);
        }
        int callInt32Callback(CustomCallback cb, int arg1, int arg2);

        class CbStruct extends Structure {
            public Callback cb;
        }
        void callCallbackInStruct(CbStruct cbstruct);
    }

    TestLibrary lib;
    protected void setUp() {
        lib = (TestLibrary)Native.loadLibrary("testlib", TestLibrary.class);
    }
    
    protected void tearDown() {
        lib = null;
    }

    public void testLookupNullCallback() {
        assertNull("NULL pointer should result in null callback",
                   CallbackReference.getCallback(null, null));
        try {
            CallbackReference.getCallback(TestLibrary.VoidCallback.class, new Pointer(0));
            fail("Null pointer lookup should fail");
        }
        catch(NullPointerException e) {
        }
    }

    public void testLookupNonCallbackClass() {
        try {
            CallbackReference.getCallback(String.class, new Pointer(0));
            fail("Request for non-Callback class should fail");
        }
        catch(IllegalArgumentException e) {
        }
    }

    public void testNoMethodCallback() {
        try {
            CallbackReference.getCallback(TestLibrary.NoMethodCallback.class, new Pointer(1));
            fail("Callback with no callback method should fail");
        }
        catch(IllegalArgumentException e) {
        }
    }

    public void testCustomMethodCallback() {
        CallbackReference.getCallback(TestLibrary.CustomMethodCallback.class, new Pointer(1));
    }

    public void testTooManyMethodsCallback() {
        try {
            CallbackReference.getCallback(TestLibrary.TooManyMethodsCallback.class, new Pointer(1));
            fail("Callback lookup with too many methods should fail");
        }
        catch(IllegalArgumentException e) {
        }
    }

    public void testMultipleMethodsCallback() {
        CallbackReference.getCallback(TestLibrary.MultipleMethodsCallback.class, new Pointer(1));
    }

    public void testNativeFunctionPointerStringValue() {
        Callback cb = CallbackReference.getCallback(TestLibrary.VoidCallback.class, new Pointer(1));
        Class cls = CallbackReference.findCallbackClass(cb.getClass());
        assertTrue("toString should include Java Callback type: " + cb + " ("
                   + cls + ")", cb.toString().indexOf(cls.getName()) != -1);
    }

    public void testLookupSameCallback() {
        Callback cb = CallbackReference.getCallback(TestLibrary.VoidCallback.class, new Pointer(1));
        Callback cb2 = CallbackReference.getCallback(TestLibrary.VoidCallback.class, new Pointer(1));
        
        assertEquals("Callback lookups for same pointer should return same Callback object", cb, cb2);
    }

    public void testGCCallback() throws Exception {
        final boolean[] called = { false };
        TestLibrary.VoidCallback cb = new TestLibrary.VoidCallback() {
            public void callback() {
                called[0] = true;
            }
        };
        lib.callVoidCallback(cb);
        assertTrue("Callback not called", called[0]);
        
        Map refs = new WeakHashMap(CallbackReference.callbackMap);
        refs.putAll(CallbackReference.directCallbackMap);
        assertTrue("Callback not cached", refs.containsKey(cb));
        CallbackReference ref = (CallbackReference)refs.get(cb);
        refs = ref.proxy != null ? CallbackReference.callbackMap
            : CallbackReference.directCallbackMap;
        Pointer cbstruct = ref.cbstruct;
        
        cb = null;
        System.gc();
        for (int i = 0; i < 100 && (ref.get() != null || refs.containsValue(ref)); ++i) {
            try {
                Thread.sleep(10); // Give the GC a chance to run
            } finally {}
        }
        assertNull("Callback not GC'd", ref.get());
        assertFalse("Callback still in map", refs.containsValue(ref));
        
        ref = null;
        System.gc();
        for (int i = 0; i < 100 && (cbstruct.peer != 0 || refs.size() > 0); ++i) {
            // Flush weak hash map
            refs.size();
            try {
                Thread.sleep(10); // Give the GC a chance to run
                System.gc();
            } finally {}
        }
        assertEquals("Callback trampoline not freed", 0, cbstruct.peer);
    }
    
    public void testFindCallbackInterface() {
        TestLibrary.Int32Callback cb = new TestLibrary.Int32Callback() {
            public int callback(int arg, int arg2) {
                return arg + arg2;
            }
        };
        assertEquals("Wrong callback interface",
                     TestLibrary.Int32Callback.class,
                     CallbackReference.findCallbackClass(cb.getClass()));
    }

    public void testCallInt32Callback() {
        final int MAGIC = 0x11111111;
        final boolean[] called = { false };
        TestLibrary.Int32Callback cb = new TestLibrary.Int32Callback() {
            public int callback(int arg, int arg2) {
                called[0] = true;
                return arg + arg2;
            }
        };
        final int EXPECTED = MAGIC*3;
        int value = lib.callInt32Callback(cb, MAGIC, MAGIC*2);
        assertTrue("Callback not called", called[0]);
        assertEquals("Wrong callback value", Integer.toHexString(EXPECTED), 
                     Integer.toHexString(value));
        
        value = lib.callInt32Callback(cb, -1, -2);
        assertEquals("Wrong callback return", -3, value);
    }
    
    public void testCallInt64Callback() {
        final long MAGIC = 0x1111111111111111L;
        final boolean[] called = { false };
        TestLibrary.Int64Callback cb = new TestLibrary.Int64Callback() {
            public long callback(long arg, long arg2) {
                called[0] = true;
                return arg + arg2;
            }
        };
        final long EXPECTED = MAGIC*3;
        long value = lib.callInt64Callback(cb, MAGIC, MAGIC*2);
        assertTrue("Callback not called", called[0]);
        assertEquals("Wrong callback value", Long.toHexString(EXPECTED), 
                     Long.toHexString(value));
        
        value = lib.callInt64Callback(cb, -1, -2);
        assertEquals("Wrong callback return", -3, value);
    }
    
    public void testCallFloatCallback() {
        final boolean[] called = { false };
        final float[] args = { 0, 0 };
        TestLibrary.FloatCallback cb = new TestLibrary.FloatCallback() {
            public float callback(float arg, float arg2) {
                called[0] = true;
                args[0] = arg;
                args[1] = arg2;
                return arg + arg2;
            }
        };
        final float EXPECTED = FLOAT_MAGIC*3;
        float value = lib.callFloatCallback(cb, FLOAT_MAGIC, FLOAT_MAGIC*2);
        assertTrue("Callback not called", called[0]);
        assertEquals("Wrong first argument", FLOAT_MAGIC, args[0], 0);
        assertEquals("Wrong second argument", FLOAT_MAGIC*2, args[1], 0);
        assertEquals("Wrong callback value", EXPECTED, value, 0);
        
        value = lib.callFloatCallback(cb, -1f, -2f);
        assertEquals("Wrong callback return", -3f, value, 0);
    }
    
    public void testCallDoubleCallback() {
        final boolean[] called = { false };
        final double[] args = { 0, 0 };
        TestLibrary.DoubleCallback cb = new TestLibrary.DoubleCallback() {
            public double callback(double arg, double arg2) {
                called[0] = true;
                args[0] = arg;
                args[1] = arg2;
                return arg + arg2;
            }
        };
        final double EXPECTED = DOUBLE_MAGIC*3;
        double value = lib.callDoubleCallback(cb, DOUBLE_MAGIC, DOUBLE_MAGIC*2);
        assertTrue("Callback not called", called[0]);
        assertEquals("Wrong first argument", DOUBLE_MAGIC, args[0], 0);
        assertEquals("Wrong second argument", DOUBLE_MAGIC*2, args[1], 0);
        assertEquals("Wrong callback value", EXPECTED, value, 0); 
        
        value = lib.callDoubleCallback(cb, -1d, -2d);
        assertEquals("Wrong callback return", -3d, value, 0);
    }
    
    public void testCallStructureCallback() {
        final boolean[] called = {false};
        final Structure[] cbarg = { null };
        final SmallTestStructure s = new SmallTestStructure();
        TestLibrary.StructureCallback cb = new TestLibrary.StructureCallback() {
            public SmallTestStructure callback(SmallTestStructure arg) {
                called[0] = true;
                cbarg[0] = arg;
                return arg;
            }
        };
        SmallTestStructure value = lib.callStructureCallback(cb, s);
        assertTrue("Callback not called", called[0]);
        assertEquals("Wrong argument passed to callback", s, cbarg[0]);
        assertEquals("Wrong structure return", s, value);
    }
    
    public void testCallBooleanCallback() {
        final boolean[] called = {false};
        final boolean[] cbargs = { false, false };
        TestLibrary.BooleanCallback cb = new TestLibrary.BooleanCallback() {
            public boolean callback(boolean arg, boolean arg2) {
                called[0] = true;
                cbargs[0] = arg;
                cbargs[1] = arg2;
                return arg && arg2;
            }
        };
        boolean value = lib.callBooleanCallback(cb, true, false);
        assertTrue("Callback not called", called[0]);
        assertEquals("Wrong callback argument 1", true, cbargs[0]);
        assertEquals("Wrong callback argument 2", false, cbargs[1]);
        assertFalse("Wrong boolean return", value);
    }
    
    public void testCallInt8Callback() {
        final boolean[] called = {false};
        final byte[] cbargs = { 0, 0 };
        TestLibrary.ByteCallback cb = new TestLibrary.ByteCallback() {
            public byte callback(byte arg, byte arg2) {
                called[0] = true;
                cbargs[0] = arg;
                cbargs[1] = arg2;
                return (byte)(arg + arg2);
            }
        };
        final byte MAGIC = 0x11; 
        byte value = lib.callInt8Callback(cb, MAGIC, (byte)(MAGIC*2));
        assertTrue("Callback not called", called[0]);
        assertEquals("Wrong callback argument 1", 
                     Integer.toHexString(MAGIC), 
                     Integer.toHexString(cbargs[0]));
        assertEquals("Wrong callback argument 2", 
                     Integer.toHexString(MAGIC*2), 
                     Integer.toHexString(cbargs[1]));
        assertEquals("Wrong byte return", 
                     Integer.toHexString(MAGIC*3), 
                     Integer.toHexString(value));
        
        value = lib.callInt8Callback(cb, (byte)-1, (byte)-2);
        assertEquals("Wrong byte return (hi bit)", (byte)-3, value);
    }
    
    public void testCallInt16Callback() {
        final boolean[] called = {false};
        final short[] cbargs = { 0, 0 };
        TestLibrary.ShortCallback cb = new TestLibrary.ShortCallback() {
            public short callback(short arg, short arg2) {
                called[0] = true;
                cbargs[0] = arg;
                cbargs[1] = arg2;
                return (short)(arg + arg2);
            }
        };
        final short MAGIC = 0x1111;
        short value = lib.callInt16Callback(cb, MAGIC, (short)(MAGIC*2));
        assertTrue("Callback not called", called[0]);
        assertEquals("Wrong callback argument 1", 
                     Integer.toHexString(MAGIC), 
                     Integer.toHexString(cbargs[0]));
        assertEquals("Wrong callback argument 2", 
                     Integer.toHexString(MAGIC*2), 
                     Integer.toHexString(cbargs[1]));
        assertEquals("Wrong short return", 
                     Integer.toHexString(MAGIC*3), 
                     Integer.toHexString(value));

        value = lib.callInt16Callback(cb, (short)-1, (short)-2);
        assertEquals("Wrong short return (hi bit)", (short)-3, value);
    }
    
    public void testCallNativeLongCallback() {
        final boolean[] called = {false};
        final NativeLong[] cbargs = { null, null};
        TestLibrary.NativeLongCallback cb = new TestLibrary.NativeLongCallback() {
            public NativeLong callback(NativeLong arg, NativeLong arg2) {
                called[0] = true;
                cbargs[0] = arg;
                cbargs[1] = arg2;
                return new NativeLong(arg.intValue() +  arg2.intValue());
            }
        };
        NativeLong value = lib.callNativeLongCallback(cb, new NativeLong(1), new NativeLong(2));
        assertTrue("Callback not called", called[0]);
        assertEquals("Wrong callback argument 1", new NativeLong(1), cbargs[0]);
        assertEquals("Wrong callback argument 2", new NativeLong(2), cbargs[1]);
        assertEquals("Wrong boolean return", new NativeLong(3), value);
    }
    
    public static class Custom implements NativeMapped {
        private int value;
        public Custom() { }
        public Custom(int value) {
            this.value = value;
        }
        public Object fromNative(Object nativeValue, FromNativeContext context) {
            return new Custom(((Integer)nativeValue).intValue());
        }
        public Class nativeType() {
            return Integer.class;
        }
        public Object toNative() {
            return new Integer(value);
        }
        public boolean equals(Object o) {
            return o instanceof Custom && ((Custom)o).value == value;
        }
    }
    public void testCallNativeMappedCallback() {
        final boolean[] called = {false};
        final Custom[] cbargs = { null, null};
        TestLibrary.CustomCallback cb = new TestLibrary.CustomCallback() {
            public Custom callback(Custom arg, Custom arg2) {
                called[0] = true;
                cbargs[0] = arg;
                cbargs[1] = arg2;
                return new Custom(arg.value + arg2.value);
            }
        };
        int value = lib.callInt32Callback(cb, 1, 2);
        assertTrue("Callback not called", called[0]);
        assertEquals("Wrong callback argument 1", new Custom(1), cbargs[0]);
        assertEquals("Wrong callback argument 2", new Custom(2), cbargs[1]);
        assertEquals("Wrong NativeMapped return", 3, value);
    }
    
    public void testCallStringCallback() {
        final boolean[] called = {false};
        final String[] cbargs = { null };
        TestLibrary.StringCallback cb = new TestLibrary.StringCallback() {
            public String callback(String arg) {
                called[0] = true;
                cbargs[0] = arg;
                return arg;
            }
        };
        final String VALUE = "value";
        String value = lib.callStringCallback(cb, VALUE);
        assertTrue("Callback not called", called[0]);
        assertEquals("Wrong callback argument 1", VALUE, cbargs[0]);
        assertEquals("Wrong String return", VALUE, value);
    }
    
    public void testStringCallbackMemoryReclamation() throws InterruptedException {
        TestLibrary.StringCallback cb = new TestLibrary.StringCallback() {
            public String callback(String arg) {
                return arg;
            }
        };

        // A little internal groping
        Map m = CallbackReference.allocations;
        String arg = getName() + "1";
        String value = lib.callStringCallback(cb, arg);
        WeakReference ref = new WeakReference(value);
        
        arg = null;
        value = null;
        System.gc();
        for (int i = 0; i < 100 && (ref.get() != null || m.values().size() > 0); ++i) {
            try {
                Thread.sleep(10); // Give the GC a chance to run
                System.gc();
            } finally {}
        }
        assertNull("String reference not GC'd", ref.get());
        assertEquals("NativeString reference still held", 0, m.values().size());
    }

    public void testCallWideStringCallback() {
        final boolean[] called = {false};
        final WString[] cbargs = { null };
        TestLibrary.WideStringCallback cb = new TestLibrary.WideStringCallback() {
            public WString callback(WString arg) {
                called[0] = true;
                cbargs[0] = arg;
                return arg;
            }
        };
        final WString VALUE = new WString("value");
        WString value = lib.callWideStringCallback(cb, VALUE);
        assertTrue("Callback not called", called[0]);
        assertEquals("Wrong callback argument 1", VALUE, cbargs[0]);
        assertEquals("Wrong wide string return", VALUE, value);
    }
    
    public void testCallStringArrayCallback() {
        final boolean[] called = {false};
        final String[][] cbargs = { null };
        TestLibrary.StringArrayCallback cb = new TestLibrary.StringArrayCallback() {
            public String[] callback(String[] arg) {
                called[0] = true;
                cbargs[0] = arg;
                return arg;
            }
        };
        final String[] VALUE = { "value", null };
        Pointer value = lib.callStringArrayCallback(cb, VALUE);
        assertTrue("Callback not called", called[0]);
        assertEquals("Wrong callback argument 1", VALUE[0], cbargs[0][0]);
        String[] result = value.getStringArray(0);
        assertEquals("Wrong String return", VALUE[0], result[0]);
    }
    
    public void testCallCallbackWithByReferenceArgument() {
    	final boolean[] called = {false};
        TestLibrary.CopyArgToByReference cb = new TestLibrary.CopyArgToByReference() {
            public int callback(int arg, IntByReference result) {
                called[0] = true;
                result.setValue(arg);
                return result.getValue();
            }
        };
        final int VALUE = 0;
        IntByReference ref = new IntByReference(~VALUE);
        int value = lib.callCallbackWithByReferenceArgument(cb, VALUE, ref);
        assertEquals("Wrong value returned", VALUE, value);
        assertEquals("Wrong value in by reference memory", VALUE, ref.getValue());
    }
    
    public void testCallCallbackWithStructByValue() {
        final TestStructure.ByValue s = new TestStructure.ByValue();
        final TestStructure innerResult = new TestStructure();
        TestStructure.TestCallback cb = new TestStructure.TestCallback() {
            public TestStructure.ByValue callback(TestStructure.ByValue s) {
                // Copy the argument value for later comparison
                Pointer old = innerResult.getPointer();
                innerResult.useMemory(s.getPointer());
                innerResult.read();
                innerResult.useMemory(old);
                innerResult.write();
                return s;
            }
        };
        s.c = (byte)0x11;
        s.s = 0x2222;
        s.i = 0x33333333;
        s.j = 0x4444444444444444L;
        s.inner.value = 5;
        
        TestStructure result = lib.callCallbackWithStructByValue(cb, s);
        assertEquals("Wrong value passed to callback", s, innerResult);
        assertEquals("Wrong value for result", s, result);
    }
    
    public void testCallCallbackWithCallbackArgumentAndResult() {
        TestLibrary.CbCallback cb = new TestLibrary.CbCallback() {
            public CbCallback callback(CbCallback arg) {
                return arg;
            }
        };
        TestLibrary.CbCallback cb2 = lib.callCallbackWithCallback(cb);
        assertEquals("Callback reference should be reused", cb, cb2);
    }
    
    public void testDefaultCallbackExceptionHandler() {
        final RuntimeException ERROR = new RuntimeException(getName());
        PrintStream ps = System.err;
        ByteArrayOutputStream s = new ByteArrayOutputStream();
        System.setErr(new PrintStream(s));
        try {
            TestLibrary.CbCallback cb = new TestLibrary.CbCallback() {
                public CbCallback callback(CbCallback arg) {
                    throw ERROR;
                }
            };
            TestLibrary.CbCallback cb2 = lib.callCallbackWithCallback(cb);
            String output = s.toString();
            assertTrue("Default handler not called", output.length() > 0);
        }
        finally {
            System.setErr(ps);
        }
    }

    /* Most Callbacks are wrapped in DefaultCallbackProxy, which catches their
     * exceptions.
     */
    public void testCallbackExceptionHandler() {
        final RuntimeException ERROR = new RuntimeException(getName());
        final Throwable CAUGHT[] = { null };
        final Callback CALLBACK[] = { null };
        UncaughtExceptionHandler old = Native.getCallbackExceptionHandler();
        UncaughtExceptionHandler handler = new UncaughtExceptionHandler() {
            public void uncaughtException(Callback cb, Throwable e) {
                CALLBACK[0] = cb;
                CAUGHT[0] = e;
            }
        };
        Native.setCallbackExceptionHandler(handler);
        try {
            TestLibrary.CbCallback cb = new TestLibrary.CbCallback() {
                public CbCallback callback(CbCallback arg) {
                    throw ERROR;
                }
            };
            TestLibrary.CbCallback cb2 = lib.callCallbackWithCallback(cb);
            assertNotNull("Exception handler not called", CALLBACK[0]);
            assertEquals("Wrong callback argument to handler", cb, CALLBACK[0]);
            assertEquals("Wrong exception passed to handler",
                         ERROR, CAUGHT[0]);
        }
        finally {
            Native.setCallbackExceptionHandler(old);
        }
    }

    /* CallbackProxy is called directly from native. */
    public void testCallbackExceptionHandlerWithCallbackProxy() throws Throwable {
        final RuntimeException ERROR = new RuntimeException(getName());
        final Throwable CAUGHT[] = { null };
        final Callback CALLBACK[] = { null };
        UncaughtExceptionHandler old = Native.getCallbackExceptionHandler();
        UncaughtExceptionHandler handler = new UncaughtExceptionHandler() {
            public void uncaughtException(Callback cb, Throwable e) {
                CALLBACK[0] = cb;
                CAUGHT[0] = e;
            }
        };
        Native.setCallbackExceptionHandler(handler);
        try {
            class TestProxy implements CallbackProxy, TestLibrary.CbCallback {
                public CbCallback callback(CbCallback arg) {
                    throw new Error("Should never be called");
                }
                public Object callback(Object[] args) {
                    throw ERROR;
                }
                public Class[] getParameterTypes() {
                    return new Class[] { CbCallback.class };
                }
                public Class getReturnType() {
                    return CbCallback.class;
                }
            };
            TestLibrary.CbCallback cb = new TestProxy();
            TestLibrary.CbCallback cb2 = lib.callCallbackWithCallback(cb);
            assertNotNull("Exception handler not called", CALLBACK[0]);
            assertEquals("Wrong callback argument to handler", cb, CALLBACK[0]);
            assertEquals("Wrong exception passed to handler",
                         ERROR, CAUGHT[0]);
        }
        finally {
            Native.setCallbackExceptionHandler(old);
        }
    }

    public void testResetCallbackExceptionHandler() {
        Native.setCallbackExceptionHandler(null);
        assertNotNull("Should not be able to set callback EH null",
                      Native.getCallbackExceptionHandler());
    }

    public void testInvokeCallback() {
        TestLibrary.Int32CallbackX cb = lib.returnCallback();
        assertNotNull("Callback should not be null", cb);
        assertEquals("Callback should be callable", 1, cb.callback(1));
        
        TestLibrary.Int32CallbackX cb2 = new TestLibrary.Int32CallbackX() {
            public int callback(int arg) {
                return 0;
            }
        };
        assertSame("Java callback should be looked up",
                   cb2, lib.returnCallbackArgument(cb2));
        assertSame("Existing native function wrapper should be reused",
                   cb, lib.returnCallbackArgument(cb));
    }
    
    public void testCallCallbackInStructure() {
        final boolean[] flag = {false};
        final TestLibrary.CbStruct s = new TestLibrary.CbStruct();
        s.cb = new Callback() {
            public void callback() {
                flag[0] = true;
            }
        };
        lib.callCallbackInStruct(s);
        assertTrue("Callback not invoked", flag[0]);
    }

    public void testCustomCallbackMethodName() {
    	final boolean[] called = {false};
        TestLibrary.VoidCallbackCustom cb = new TestLibrary.VoidCallbackCustom() {
            public void customMethodName() {
                called[0] = true;
            }
            public String toString() {
                return "Some debug output";
            }
        };
        lib.callVoidCallback(cb);
        assertTrue("Callback with custom method name not called", called[0]);
    }

    public void testCustomCallbackVariedInheritance() {
    	final boolean[] called = {false};
        TestLibrary.VoidCallbackCustom cb =
            new TestLibrary.VoidCallbackCustomDerived();
        lib.callVoidCallback(cb);
    }

    public static interface CallbackTestLibrary extends Library {
        final TypeMapper _MAPPER = new DefaultTypeMapper() {
            {
                // Convert java doubles into native integers and back
                TypeConverter converter = new TypeConverter() {
                    public Object fromNative(Object value, FromNativeContext context) {
                        return new Double(((Integer)value).intValue());
                    }
                    public Class nativeType() {
                        return Integer.class;
                    }
                    public Object toNative(Object value, ToNativeContext ctx) {
                        return new Integer(((Double)value).intValue());
                    }
                };
                addTypeConverter(double.class, converter);
                converter = new TypeConverter() {
                    public Object fromNative(Object value, FromNativeContext context) {
                        return new Float(((Long)value).intValue());
                    }
                    public Class nativeType() {
                        return Long.class;
                    }
                    public Object toNative(Object value, ToNativeContext ctx) {
                        return new Long(((Float)value).longValue());
                    }
                };
                addTypeConverter(float.class, converter);
            }
        };
        final Map _OPTIONS = new HashMap() {
            {
                put(Library.OPTION_TYPE_MAPPER, _MAPPER);
            }
        };
        interface DoubleCallback extends Callback {
            double callback(double arg, double arg2);
        }
        double callInt32Callback(DoubleCallback c, double arg, double arg2);
        interface FloatCallback extends Callback {
            float callback(float arg, float arg2);
        }
        float callInt64Callback(FloatCallback c, float arg, float arg2);
    }

    protected CallbackTestLibrary loadCallbackTestLibrary() {
        return (CallbackTestLibrary)
            Native.loadLibrary("testlib", CallbackTestLibrary.class, CallbackTestLibrary._OPTIONS);
    }

    /** This test is here instead of NativeTest in order to facilitate running
        the exact same test on a direct-mapped library without the tests
        interfering with one another due to persistent/cached state in library
        loading. 
    */
    public void testCallbackUsesTypeMapper() throws Exception {
        CallbackTestLibrary lib = loadCallbackTestLibrary();

        final double[] ARGS = new double[2];

        CallbackTestLibrary.DoubleCallback cb = new CallbackTestLibrary.DoubleCallback() {
            public double callback(double arg, double arg2) {
                ARGS[0] = arg;
                ARGS[1] = arg2;
                return arg + arg2;
            }
        };
        assertEquals("Wrong type mapper for callback class", lib._MAPPER,
                     Native.getTypeMapper(CallbackTestLibrary.DoubleCallback.class));
        assertEquals("Wrong type mapper for callback object", lib._MAPPER,
                     Native.getTypeMapper(cb.getClass()));

        double result = lib.callInt32Callback(cb, -1, -1);
        assertEquals("Wrong callback argument 1", -1, ARGS[0], 0);
        assertEquals("Wrong callback argument 2", -1, ARGS[1], 0);
        assertEquals("Incorrect result of callback invocation", -2, result, 0);
    }

    public void testCallbackUsesTypeMapperWithDifferentReturnTypeSize() throws Exception {
        CallbackTestLibrary lib = loadCallbackTestLibrary();

        final float[] ARGS = new float[2];

        CallbackTestLibrary.FloatCallback cb = new CallbackTestLibrary.FloatCallback() {
            public float callback(float arg, float arg2) {
                ARGS[0] = arg;
                ARGS[1] = arg2;
                return arg + arg2;
            }
        };
        assertEquals("Wrong type mapper for callback class", lib._MAPPER,
                     Native.getTypeMapper(CallbackTestLibrary.FloatCallback.class));
        assertEquals("Wrong type mapper for callback object", lib._MAPPER,
                     Native.getTypeMapper(cb.getClass()));

        float result = lib.callInt64Callback(cb, -1, -1);
        assertEquals("Wrong callback argument 1", -1, ARGS[0], 0);
        assertEquals("Wrong callback argument 2", -1, ARGS[1], 0);
        assertEquals("Incorrect result of callback invocation", -2, result, 0);
    }

    public static void main(java.lang.String[] argList) {
        junit.textui.TestRunner.run(CallbacksTest.class);
    }
}

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

import java.util.Map;

import com.sun.jna.CallbacksTest.TestLibrary.CbCallback;
import com.sun.jna.ptr.IntByReference;

import junit.framework.TestCase;

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
        interface VoidCallback extends Callback {
            void callback();
        }
        void callVoidCallback(VoidCallback c);
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
    }

    TestLibrary lib;
    protected void setUp() {
        lib = (TestLibrary)Native.loadLibrary("testlib", TestLibrary.class);
    }
    
    protected void tearDown() {
        lib = null;
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
        
        Map refs = CallbackReference.callbackMap;
        assertTrue("Callback not cached", refs.containsKey(cb));
        CallbackReference ref = (CallbackReference)refs.get(cb);
        Pointer cbstruct = ref.cbstruct;
        
        cb = null;
        System.gc();
        for (int i = 0; i < 100 && (ref.get() != null || refs.containsValue(ref)); ++i) {
            try {
                Thread.sleep(1); // Give the GC a chance to run
            } finally {}
        }
        assertNull("Callback not GC'd", ref.get());
        assertFalse("Callback still in map", refs.containsValue(ref));
        
        ref = null;
        System.gc();
        for (int i = 0; i < 100 && cbstruct.peer != 0; ++i) {
            try {
                Thread.sleep(1); // Give the GC a chance to run
            } finally {}
        }
        assertEquals("Callback trampoline not freed", 0, cbstruct.peer);
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
        TestLibrary.FloatCallback cb = new TestLibrary.FloatCallback() {
            public float callback(float arg, float arg2) {
                called[0] = true;
                return arg + arg2;
            }
        };
        final float EXPECTED = FLOAT_MAGIC*3;
        float value = lib.callFloatCallback(cb, FLOAT_MAGIC, FLOAT_MAGIC*2);
        assertTrue("Callback not called", called[0]);
        assertEquals("Wrong callback value", EXPECTED, value, 0);
        
        value = lib.callFloatCallback(cb, -1f, -2f);
        assertEquals("Wrong callback return", -3f, value, 0);
    }
    
    public void testCallDoubleCallback() {
        final boolean[] called = { false };
        TestLibrary.DoubleCallback cb = new TestLibrary.DoubleCallback() {
            public double callback(double arg, double arg2) {
                called[0] = true;
                return arg + arg2;
            }
        };
        final double EXPECTED = DOUBLE_MAGIC*3;
        double value = lib.callDoubleCallback(cb, DOUBLE_MAGIC, DOUBLE_MAGIC*2);
        assertTrue("Callback not called", called[0]);
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
        assertEquals("Wrong callback argument", s, cbarg[0]);
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
    public static interface CustomCallback extends Callback {
        Custom callback(Custom arg1, Custom arg2);
    }
    public static interface NativeMappedLibrary extends Library {
        int callInt32Callback(CustomCallback cb, int arg1, int arg2);
    }
    public void testCallNativeMappedCallback() {
        final boolean[] called = {false};
        final Custom[] cbargs = { null, null};
        CustomCallback cb = new CustomCallback() {
            public Custom callback(Custom arg, Custom arg2) {
                called[0] = true;
                cbargs[0] = arg;
                cbargs[1] = arg2;
                return new Custom(arg.value + arg2.value);
            }
        };
        NativeMappedLibrary lib = (NativeMappedLibrary)
            Native.loadLibrary("testlib", NativeMappedLibrary.class);
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
    
    public static void main(java.lang.String[] argList) {
        junit.textui.TestRunner.run(CallbacksTest.class);
    }
}

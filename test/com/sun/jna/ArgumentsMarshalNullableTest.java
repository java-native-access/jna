/* Copyright (c) 2018 Matthias Bläsing, All Rights Reserved
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


import java.util.Collections;
import junit.framework.TestCase;

/**
 * Test that all method call options for native calls work with NULL values
 * and result in deterministic behaviour and not in a JVM crash.
 */
public class ArgumentsMarshalNullableTest extends TestCase {
    public static class Int32Integer extends IntegerType {

        public Int32Integer() {
            super(4);
        }

        public Int32Integer(long value) {
            super(4, value);
        }

    }


    public static class Int32NativeMapped implements NativeMapped {
        private int value;

        public Int32NativeMapped() {};

        public Int32NativeMapped(int value) {
            this.value = value;
        }

        public Object fromNative(Object nativeValue, FromNativeContext context) {
            if(nativeValue instanceof Integer) {
                return new Int32NativeMapped((Integer) nativeValue);
            } else {
                return null;
            }
        }

        public Object toNative() {
            return value;
        }

        public Class<?> nativeType() {
            return int.class;
        }

        @Override
        public String toString() {
            return "Int32NativeMapped{" + "value=" + value + '}';
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 59 * hash + this.value;
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Int32NativeMapped other = (Int32NativeMapped) obj;
            if (this.value != other.value) {
                return false;
            }
            return true;
        }
    }

    public static class Int32 {
        public static final FromNativeConverter fromNative = new FromNativeConverter() {
            public Object fromNative(Object nativeValue, FromNativeContext context) {
                if (nativeValue instanceof Integer) {
                    return new Int32((Integer) nativeValue);
                } else {
                    return null;
                }
            }

            public Class<?> nativeType() {
                return int.class;
            }
        };

        public static final ToNativeConverter toNative = new ToNativeConverter() {
            public Object toNative(Object value, ToNativeContext context) {
                if(value == null) {
                    return 0;
                } else {
                    return ((Int32) value).value;
                }
            }

            public Class<?> nativeType() {
                return int.class;
            }
        };

        private int value;

        public Int32() {
        }

        public Int32(int value) {
            this.value = value;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 53 * hash + this.value;
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Int32 other = (Int32) obj;
            if (this.value != other.value) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "Int32{" + "value=" + value + '}';
        }
    }

    public static interface TestLibrary extends Library {
        Int32NativeMapped returnInt32Argument(Int32NativeMapped i);
        Int32Integer returnInt32Argument(Int32Integer i);
        Int32 returnInt32Argument(Int32 i);
    }

    TestLibrary lib;
    @Override
    protected void setUp() {
        lib = Native.load("testlib", TestLibrary.class,
                Collections.singletonMap(Library.OPTION_TYPE_MAPPER, new TypeMapper() {
                    public FromNativeConverter getFromNativeConverter(Class<?> javaType) {
                        if(javaType == Int32.class) {
                            return Int32.fromNative;
                        } else {
                            return null;
                        }
                    }

                    public ToNativeConverter getToNativeConverter(Class<?> javaType) {
                        if(javaType == Int32.class) {
                            return Int32.toNative;
                        } else {
                            return null;
                        }
                    }
                }));
    }

    @Override
    protected void tearDown() {
        lib = null;
    }


    public void testNativeMapped() {
        assertEquals("Basic non-null call", new Int32NativeMapped(42), lib.returnInt32Argument(new Int32NativeMapped(42)));
        assertEquals("null call", new Int32NativeMapped(0), lib.returnInt32Argument((Int32NativeMapped) null));
    }

    public void testIntegerType() {
        assertEquals("Basic non-null call", new Int32Integer(42), lib.returnInt32Argument(new Int32Integer(42)));
        assertEquals("null call", new Int32Integer(0), lib.returnInt32Argument((Int32Integer) null));
    }

    public void testTypeMapper() {
        assertEquals("Basic non-null call", new Int32(42), lib.returnInt32Argument(new Int32(42)));
        assertEquals("null call", new Int32(0), lib.returnInt32Argument((Int32) null));
    }

    public static void main(java.lang.String[] argList) {
        junit.textui.TestRunner.run(ArgumentsMarshalNullableTest.class);
    }

}

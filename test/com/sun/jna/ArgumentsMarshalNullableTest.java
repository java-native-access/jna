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
    public static class Int16Integer extends IntegerType {

        public Int16Integer() {
            super(2);
        }

        public Int16Integer(long value) {
            super(2, value);
        }

    }


    public static class Int16NativeMapped implements NativeMapped {
        private short value;

        public Int16NativeMapped() {};

        public Int16NativeMapped(int value) {
            this.value = (short) value;
        }

        public Object fromNative(Object nativeValue, FromNativeContext context) {
            if(nativeValue instanceof Number) {
                return new Int16NativeMapped(((Number) nativeValue).shortValue());
            } else {
                return null;
            }
        }

        public Object toNative() {
            return value;
        }

        public Class<?> nativeType() {
            return short.class;
        }

        @Override
        public String toString() {
            return "Int16NativeMapped{" + "value=" + value + '}';
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
            final Int16NativeMapped other = (Int16NativeMapped) obj;
            if (this.value != other.value) {
                return false;
            }
            return true;
        }
    }

    public static class Int16 {
        public static final FromNativeConverter fromNative = new FromNativeConverter() {
            public Object fromNative(Object nativeValue, FromNativeContext context) {
                if (nativeValue instanceof Number) {
                    return new Int16(((Number) nativeValue).shortValue());
                } else {
                    return null;
                }
            }

            public Class<?> nativeType() {
                return short.class;
            }
        };

        public static final ToNativeConverter toNative = new ToNativeConverter() {
            public Object toNative(Object value, ToNativeContext context) {
                if(value == null) {
                    return 0;
                } else {
                    return ((Int16) value).value;
                }
            }

            public Class<?> nativeType() {
                return short.class;
            }
        };

        private short value;

        public Int16() {
        }

        public Int16(int value) {
            this.value = (short) value;
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
            final Int16 other = (Int16) obj;
            if (this.value != other.value) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "Int16{" + "value=" + value + '}';
        }
    }

    public static interface TestLibrary extends Library {
        Int16NativeMapped returnInt16Argument(Int16NativeMapped i);
        Int16Integer returnInt16Argument(Int16Integer i);
        Int16 returnInt16Argument(Int16 i);
    }

    TestLibrary lib;
    @Override
    protected void setUp() {
        lib = Native.load("testlib", TestLibrary.class,
                Collections.singletonMap(Library.OPTION_TYPE_MAPPER, new TypeMapper() {
                    public FromNativeConverter getFromNativeConverter(Class<?> javaType) {
                        if(javaType == Int16.class) {
                            return Int16.fromNative;
                        } else {
                            return null;
                        }
                    }

                    public ToNativeConverter getToNativeConverter(Class<?> javaType) {
                        if(javaType == Int16.class) {
                            return Int16.toNative;
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
        assertEquals("Basic non-null call", new Int16NativeMapped(42), lib.returnInt16Argument(new Int16NativeMapped(42)));
        assertEquals("Negative value call", new Int16NativeMapped(-42), lib.returnInt16Argument(new Int16NativeMapped(-42)));
        assertEquals("null call", new Int16NativeMapped(0), lib.returnInt16Argument((Int16NativeMapped) null));
    }

    public void testIntegerType() {
        assertEquals("Basic non-null call", new Int16Integer(42), lib.returnInt16Argument(new Int16Integer(42)));
        assertEquals("Negative value call", new Int16Integer(-42), lib.returnInt16Argument(new Int16Integer(-42)));
        assertEquals("null call", new Int16Integer(0), lib.returnInt16Argument((Int16Integer) null));
    }

    public void testTypeMapper() {
        assertEquals("Basic non-null call", new Int16(42), lib.returnInt16Argument(new Int16(42)));
        assertEquals("Negative value call", new Int16(-42), lib.returnInt16Argument(new Int16(-42)));
        assertEquals("null call", new Int16(0), lib.returnInt16Argument((Int16) null));
    }

    public static void main(java.lang.String[] argList) {
        junit.textui.TestRunner.run(ArgumentsMarshalNullableTest.class);
    }

}

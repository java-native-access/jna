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
import java.util.HashMap;
import java.util.Map;

/**
 * Test native calls where the parameters and return types are bound as wrappers.
 *
 * <p>This variant of {@link ArgumentsWrappersMarshalTest} modified to use
 * direct mapping.</p>
 *
 * <p>The {@code PrimitiveConverter} was not made part of the core codebase,
 * as the usage would mean multiple trips through the C&lt;-&gt;Java barrier.
 * The converter is run on the java side, the invocation happens from the C side.
 * As the primary reason for direct mapping is performance, it is strongly
 * recommed not to go down this route, but map via java primitives.</p>
 */
public class DirectArgumentsWrappersMarshalTest extends ArgumentsWrappersMarshalTest {

    public static class DirectTestLibrary implements TestLibrary {
        @Override
        public native Boolean returnBooleanArgument(Boolean arg);
        @Override
        public native Byte returnInt8Argument(Byte arg);
        @Override
        public native Character returnWideCharArgument(Character arg);
        @Override
        public native Short returnInt16Argument(Short arg);
        @Override
        public native Integer returnInt32Argument(Integer i);
        @Override
        public native Long returnInt64Argument(Long l);
        @Override
        public native Float returnFloatArgument(Float f);
        @Override
        public native Double returnDoubleArgument(Double d);

        @Override
        public native Long checkInt64ArgumentAlignment(Integer i, Long j, Integer i2, Long j2);
        @Override
        public native Double checkDoubleArgumentAlignment(Float i, Double j, Float i2, Double j2);

        static {
            class PrimitiveConverter implements FromNativeConverter, ToNativeConverter {
                private final Class nativeType;

                public PrimitiveConverter(Class nativeType) {
                    this.nativeType = nativeType;
                }

                public Object fromNative(Object nativeValue, FromNativeContext context) {
                    if(nativeValue == null) {
                        return 0;
                    } else {
                        return nativeValue;
                    }
                }

                public Class<?> nativeType() {
                    return nativeType;
                }

                public Object toNative(Object value, ToNativeContext context) {
                    if(value == null) {
                        return 0;
                    } else {
                        return value;
                    }
                }
            }
            final Map<Class,PrimitiveConverter> converters = new HashMap<Class,PrimitiveConverter>();
            converters.put(Boolean.class, new PrimitiveConverter(boolean.class));
            converters.put(Byte.class, new PrimitiveConverter(byte.class));
            converters.put(Short.class, new PrimitiveConverter(short.class));
            converters.put(Character.class, new PrimitiveConverter(char.class));
            converters.put(Integer.class, new PrimitiveConverter(int.class));
            converters.put(Long.class, new PrimitiveConverter(long.class));
            converters.put(Float.class, new PrimitiveConverter(float.class));
            converters.put(Double.class, new PrimitiveConverter(double.class));
            TypeMapper tm = new TypeMapper() {
                public FromNativeConverter getFromNativeConverter(Class<?> javaType) {
                    return converters.get(javaType);
                }

                public ToNativeConverter getToNativeConverter(Class<?> javaType) {
                    return converters.get(javaType);
                }
            };
            NativeLibrary testlib = NativeLibrary.getInstance("testlib",
                    Collections.singletonMap(Library.OPTION_TYPE_MAPPER, tm));
            Native.register(testlib);
        }
    }

    /* Override original. */
    @Override
    protected void setUp() {
        lib = new DirectTestLibrary();
    }

    public static void main(java.lang.String[] argList) {
        junit.textui.TestRunner.run(DirectArgumentsWrappersMarshalTest.class);
    }

}

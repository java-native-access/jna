package com.sun.jna;

import junit.framework.TestCase;

public class NativedMappedConverterTest extends TestCase {

    public void testDefaultValueForClass() {
        NativeMappedConverter converter = new NativeMappedConverter(NativeMappedTestClass.class);

        assertTrue(converter.defaultValue() instanceof NativeMappedTestClass);
    }

    public void testDefaultValueForEnum() {
        NativeMappedConverter converter = new NativeMappedConverter(TestEnum.class);

        assertSame(converter.defaultValue(), TestEnum.VALUE1);
    }

    private enum TestEnum implements NativeMapped { VALUE1, VALUE2;

        @Override
        public Object fromNative(Object nativeValue, FromNativeContext context) {
            return values()[(Integer) nativeValue];
        }

        @Override
        public Object toNative() {
            return ordinal();
        }

        @Override
        public Class<?> nativeType() {
            return Integer.class;
        }
    }

}

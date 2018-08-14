package com.sun.jna;

class NativeMappedTestClass implements NativeMapped {

    private String name;

    public NativeMappedTestClass() {}

    @Override
    public Object fromNative(Object nativeValue, FromNativeContext context) {
        NativeMappedTestClass object = new NativeMappedTestClass();
        object.name = (String) nativeValue;

        return object;
    }

    @Override
    public Object toNative() {
        return name;
    }

    @Override
    public Class<?> nativeType() {
        return String.class;
    }
}

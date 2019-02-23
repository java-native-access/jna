/*
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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import junit.framework.TestCase;

public class AnnotatedLibraryTest extends TestCase {

    @Retention(RetentionPolicy.RUNTIME)
    public @interface TestAnnotation {
    }

    public interface AnnotatedLibrary extends Library {
        @TestAnnotation boolean isAnnotated();
    }

    public class TestInvocationHandler implements InvocationHandler {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return Boolean.valueOf(method.getAnnotations().length == 1);
        }
    }

    // There's a rumor that some VMs don't copy annotation information to
    // dynamically generated proxies.  Detect it here.
    public void testProxyMethodHasAnnotations() throws Exception {
        AnnotatedLibrary a = (AnnotatedLibrary)
            Proxy.newProxyInstance(getClass().getClassLoader(),
                                   new Class[] { AnnotatedLibrary.class },
                                   new TestInvocationHandler());
        assertTrue("Proxy method not annotated", a.isAnnotated());
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public static @interface FooBoolean {}
    public static interface AnnotationTestLibrary extends Library {
        @FooBoolean
        boolean returnInt32Argument(boolean b);
    }
    public void testAnnotationsOnMethods() throws Exception {
        final int MAGIC = 0xABEDCF23;
        final boolean[] hasAnnotation = {false, false};
        DefaultTypeMapper mapper = new DefaultTypeMapper();
        mapper.addTypeConverter(Boolean.class, new TypeConverter() {
            @Override
            public Object toNative(Object value, ToNativeContext ctx) {
                MethodParameterContext mcontext = (MethodParameterContext)ctx;
                hasAnnotation[0] = mcontext.getMethod().getAnnotation(FooBoolean.class) != null;
                return Integer.valueOf(Boolean.TRUE.equals(value) ? MAGIC : 0);
            }
            @Override
            public Object fromNative(Object value, FromNativeContext context) {
                MethodResultContext mcontext = (MethodResultContext)context;
                hasAnnotation[1] = mcontext.getMethod().getAnnotation(FooBoolean.class) != null;
                return Boolean.valueOf(((Integer) value).intValue() == MAGIC);
            }
            @Override
            public Class<?> nativeType() {
                return Integer.class;
            }
        });

        AnnotationTestLibrary lib =
                Native.load("testlib", AnnotationTestLibrary.class, Collections.singletonMap(Library.OPTION_TYPE_MAPPER, mapper));
        assertEquals("Failed to convert integer return to boolean TRUE", true,
                     lib.returnInt32Argument(true));
        assertTrue("Failed to get annotation from ParameterContext", hasAnnotation[0]);
        assertTrue("Failed to get annotation from ResultContext", hasAnnotation[1]);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(AnnotatedLibraryTest.class);
    }
}

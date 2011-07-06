package com.sun.jna;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import junit.framework.TestCase;

public class LibraryTest extends TestCase {

    @Retention(RetentionPolicy.RUNTIME)
    public @interface TestAnnotation {
    }

    public interface Library { } 

    public interface AnnotatedLibrary extends Library {
        @TestAnnotation boolean isAnnotated();
    }
    
    public class TestInvocationHandler implements InvocationHandler {
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
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(LibraryTest.class);
    }
}

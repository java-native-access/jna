package com.sun.jna.win32;

import com.sun.jna.FunctionMapper;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import junit.framework.TestCase;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class W32OrdinalNumberFunctionTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(W32OrdinalNumberFunctionTest.class);
    }

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface FuncName {
        String value();
    }

    public interface CLibrary extends Library {
        @FuncName("#1206")
        double sin(double x);
    }

    CLibrary c;

    public class MyFunctionMapper implements FunctionMapper {
        @Override
        public String getFunctionName(NativeLibrary library, Method method) {
            FuncName fn = method.getAnnotation(FuncName.class);
            return fn == null ? method.getName() : fn.value();
        }
    }

    @Override
    protected void setUp() throws Exception {
        Map map = new HashMap();
        map.put(Library.OPTION_FUNCTION_MAPPER, new MyFunctionMapper());
        c = (CLibrary) Native.loadLibrary("msvcrt", CLibrary.class, map);
    }

    @Override
    protected void tearDown() throws Exception {
        c = null;
    }

    public void testOrdinalNumberFunction() {
        double y = c.sin(Math.PI / 2);
        assertTrue(Math.abs(y - 1) < 0.0001);
    }
}

/* This library is free software; you can redistribute it and/or
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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/** Derive from this interface for all native library definitions.
 *
 * Define an instance of your library like this:
 * <code><pre>
 * MyNativeLibrary INSTANCE = (MyNativeLibrary)
 *     Native.loadLibrary("mylib", MyNativeLibrary.class);
 * </pre></code>
 * <p>
 * By convention, method names are identical to the native names, although you
 * can map java names to different native names by supplying a map to the
 * {@link Native#loadLibrary(String, Class, Map)} call.
 * <p>
 * Although the names for structures and structure fields may be chosen 
 * arbitrarily, they should correspond as closely as possible to the native 
 * definitions.  The same is true for parameter names.
 * <p>
 * This interface supports multiple, concurrent invocations of any library
 * methods on the Java side.  Check your library documentation for its
 * multithreading requirements on the native side.  If a library is not safe
 * for multithreaded use, consider using {@link Native#synchronizedLibrary}
 * to prevent multithreaded access to the native code.
 * <p>
 * <b>Optional fields</b><br>
 * Interface options will be automatically propagated to structures defined
 * within the library if an <b>INSTANCE</b> field is defined holding the 
 * results of a {@link Native#loadLibrary(String,Class,Map)} call.  If no
 * instance is defined, the {@link Structure} constructor will look for
 * fields named <code>TYPE_MAPPER</code> and <code>STRUCTURE_ALIGNMENT</code>
 * to obtain non-default values for those options.
 * 
 * @author  Todd Fast, todd.fast@sun.com
 * @author twall@users.sf.net
 */
public interface Library {
    /** Option key for a {@link TypeMapper} for the library. */
    String OPTION_TYPE_MAPPER = "type-mapper";
    /** Option key for a {@link FunctionMapper} for the library. */
    String OPTION_FUNCTION_MAPPER = "function-mapper";
    /** Option key for structure alignment type ({@link Integer}). */
    String OPTION_STRUCTURE_ALIGNMENT = "structure-alignment";
    String OPTION_INVOKING_METHOD = "invoking-method";
    
    static class Handler implements InvocationHandler {
        
        static final Method OBJECT_TOSTRING;
        static final Method OBJECT_HASHCODE;
        static final Method OBJECT_EQUALS;
        
        static {
            try {
                OBJECT_TOSTRING = Object.class.getMethod("toString", new Class[0]);
                OBJECT_HASHCODE= Object.class.getMethod("hashCode", new Class[0]);
                OBJECT_EQUALS = Object.class.getMethod("equals", new Class[] { Object.class });
            }
            catch (Exception e) {
                throw new Error("Error retrieving Object.toString() method");
            }
        }

        private static class FunctionNameMap implements FunctionMapper {
            private final Map map;
            public FunctionNameMap(Map map) {
                this.map = new HashMap(map);
            }
            public String getFunctionName(NativeLibrary library, Method method) {
                String name = method.getName();
                if (map.containsKey(name)) {
                    return (String)map.get(name);
                }
                return name;
            }
        }

        private final NativeLibrary nativeLibrary;
        private final Class interfaceClass;
        // Library invocation options
        private final Map options;
        private FunctionMapper functionMapper;
        private final Map functions = new WeakHashMap();        
        public Handler(String libname, Class interfaceClass, Map options) {

            if (libname == null || "".equals(libname.trim())) {
                throw new IllegalArgumentException("Invalid library name \""
                                                   + libname + "\"");
            }

            this.nativeLibrary = NativeLibrary.getInstance(libname);
            this.interfaceClass = interfaceClass;
            this.options = options;
            functionMapper = (FunctionMapper)options.get(OPTION_FUNCTION_MAPPER);
            if (functionMapper == null) {
                // backward compatibility; passed-in map is itself the name map
                functionMapper = new FunctionNameMap(options);
            }
        }

        public NativeLibrary getNativeLibrary() {
            return nativeLibrary;
        }
        
        public String getLibraryName() {
            return nativeLibrary.getName();
        }

        public Class getInterfaceClass() {
            return interfaceClass;
        }
        private static class FunctionInfo {
            Function function;
            boolean isVarArgs;
            Map options;
        }
        public Object invoke(Object proxy, Method method, Object[] inArgs)
            throws Throwable {

            // Intercept Object methods
            if (OBJECT_TOSTRING.equals(method)) {
                return "Proxy interface to " + nativeLibrary;
            }
            else if (OBJECT_HASHCODE.equals(method)) {
                return new Integer(hashCode());
            }
            else if (OBJECT_EQUALS.equals(method)) {
                Object o = inArgs[0];
                if (o != null && Proxy.isProxyClass(o.getClass())) {
                    return Boolean.valueOf(Proxy.getInvocationHandler(o) == this);
                }
                return Boolean.FALSE;
            }
            
            FunctionInfo f = null;            
            synchronized(functions) {
                f = (FunctionInfo)functions.get(method);
                if (f == null) {
                    // Find the function to invoke
                    String methodName = 
                        functionMapper.getFunctionName(nativeLibrary, method);
                    if (methodName == null) {
                        // Just in case the function mapper screwed up
                        methodName = method.getName();
                    }
                    int callingConvention = 
                        proxy instanceof AltCallingConvention
                        ? Function.ALT_CONVENTION : Function.C_CONVENTION;
                    f = new FunctionInfo();
                    f.function = nativeLibrary.getFunction(methodName, callingConvention);
                    f.isVarArgs = Function.isVarArgs(method);                    
                    f.options = new HashMap(this.options);
                    //
                    // Pass in the original method from the Library interface
                    // subclass so annotations present in the interface get
                    // passed on. 
                    //
                    f.options.put(Library.OPTION_INVOKING_METHOD, 
                        interfaceClass.getMethod(method.getName(), method.getParameterTypes()));
                    functions.put(method, f);
                }                
            }
            if (f.isVarArgs) {
                inArgs = Function.concatenateVarArgs(inArgs);
            }
            return f.function.invoke(method.getReturnType(), inArgs, f.options);
        }
    }
}

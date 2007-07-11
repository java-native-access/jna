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
 * multithreading requirements on the native side.
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

    static class Handler implements InvocationHandler {
        
        private static final Method OBJECT_TOSTRING;
        
        static {
            try {
                OBJECT_TOSTRING = Object.class.getMethod("toString", null);
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

        private NativeLibrary nativeLibrary;
        private Class interfaceClass;
        // Library invocation options
        private Map options;
        private FunctionMapper functionMapper;
        private Map functions = new WeakHashMap();
        
        public Handler(String libname, Class interfaceClass, Map options) {

            if (libname == null || libname.trim().length() == 0) {
                throw new IllegalArgumentException("Invalid library name \""
                                                   + libname + "\"");
            }

            if (!Library.class.isAssignableFrom(interfaceClass)) {
                throw new IllegalArgumentException("Invalid interface class \""
                                                   + interfaceClass + "\"");
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

        public String getLibraryName() {
            return nativeLibrary.getName();
        }

        public Class getInterfaceClass() {
            return interfaceClass;
        }

        public Object invoke(Object proxy, Method method, Object[] inArgs)
            throws Throwable {
            
            // Check for any toString() calls on the proxy
            if (method == OBJECT_TOSTRING) {
                return "Proxy interface to " + nativeLibrary.toString();
            }

            Function f = null;
            synchronized(functions) {
                f = (Function)functions.get(method);
                if (f == null) {
                    // Find the function to invoke
                    String methodName = 
                        functionMapper.getFunctionName(nativeLibrary, method);
                    int callingConvention = 
                        proxy instanceof AltCallingConvention
                        ? Function.ALT_CONVENTION : Function.C_CONVENTION;
                    f = nativeLibrary.getFunction(methodName, callingConvention);
                    functions.put(method, f);
                }
            }
            return f.invoke(method.getReturnType(), inArgs, options);
        }
    }
}

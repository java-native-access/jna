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

import java.lang.ref.ReferenceQueue;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import com.sun.jna.ptr.ByReference;

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
 * @author  Todd Fast, todd.fast@sun.com
 * @author twall@users.sf.net
 */
public interface Library {

    static class Handler implements InvocationHandler {

        private NativeLibrary nativeLibrary;
        private Class interfaceClass;

        // Map java names to native function names
        private Map functionMap;
        
        public Handler(String libname, Class interfaceClass, Map functionMap) {

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
            this.functionMap = functionMap;
        }

        public String getLibraryName() {
            return nativeLibrary.getName();
        }

        public Class getInterfaceClass() {
            return interfaceClass;
        }
        
        public Object invoke(Object proxy, Method method, Object[] inArgs)
            throws Throwable {
            
            // Find the function to invoke
            String methodName = method.getName();
            if (functionMap.containsKey(methodName)) {
                methodName = (String)functionMap.get(methodName);
            }
            int callingConvention = 
                proxy instanceof AltCallingConvention
                ? Function.ALT_CONVENTION : Function.C_CONVENTION;
            Function f = nativeLibrary.getFunction(methodName, callingConvention);
            return f.invoke(method.getReturnType(), inArgs);
        }
    }
}

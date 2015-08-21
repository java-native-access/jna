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
 * <pre><code>
 * MyNativeLibrary INSTANCE = (MyNativeLibrary)
 *     Native.loadLibrary("mylib", MyNativeLibrary.class);
 * </code></pre>
 * <p>
 * By convention, method names are identical to the native names, although you
 * can map java names to different native names by providing a 
 * {@link FunctionMapper} as a value for key {@link #OPTION_FUNCTION_MAPPER}
 * in the options map passed to the
 * {@link Native#loadLibrary(String, Class, Map)} call.
 * <p>
 * Although the names for structures and structure fields may be chosen 
 * arbitrarily, they should correspond as closely as possible to the native 
 * definitions.  The same is true for parameter names.
 * <p>
 * This interface supports multiple, concurrent invocations of any library
 * methods on the Java side.  Check your library documentation for its
 * multi-threading requirements on the native side.  If a library is not safe
 * for simultaneous multi-threaded access, consider using 
 * {@link Native#synchronizedLibrary} to prevent simultaneous multi-threaded 
 * access to the native code.  
 * <p>
 * <b>Optional fields</b><br>
 * Interface options will be automatically propagated to structures defined
 * within the library provided a call to 
 * {@link Native#loadLibrary(String,Class,Map)} is made prior to instantiating
 * any of those structures.  One common way of ensuring this is to declare
 * an <b>INSTANCE</b> field in the interface which holds the 
 * <code>loadLibrary</code> result.
 * <p>
 * <b>OPTIONS</b> (an instance of {@link Map}),
 * <b>TYPE_MAPPER</b> (an instance of {@link TypeMapper}),
 * <b>STRUCTURE_ALIGNMENT</b> (one of the alignment types defined in 
 * {@link Structure}), and <b>STRING_ENCODING</b> (a {@link String}) may also
 * be defined.  If no instance of the interface has been instantiated, these
 * fields will be used to determine customization settings for structures and
 * methods defined within the interface. 
 * <p>
 * 
 * @author  Todd Fast, todd.fast@sun.com
 * @author  Timothy Wall, twalljava@dev.java.net
 */
public interface Library {
    /** Option key for a {@link TypeMapper} for the library. */
    String OPTION_TYPE_MAPPER = "type-mapper";
    /** Option key for a {@link FunctionMapper} for the library. */
    String OPTION_FUNCTION_MAPPER = "function-mapper";
    /** Option key for an {@link InvocationMapper} for the library. */
    String OPTION_INVOCATION_MAPPER = "invocation-mapper";
    /** Option key for structure alignment type ({@link Integer}), which should
     * be one of the predefined alignment types in {@link Structure}. 
     */
    String OPTION_STRUCTURE_ALIGNMENT = "structure-alignment";
    /** <p>Option key for per-library String encoding.  This affects conversions
     * between Java unicode and native (<code>const char*</code>) strings (as
     * arguments or Structure fields).
     * </p>
     * Defaults to {@link Native#getDefaultStringEncoding()}.
     */
    String OPTION_STRING_ENCODING = "string-encoding";
    /** Option key for a boolean flag to allow any Java class instance as a
        parameter.  If no type mapper is found, the object is passed as a
        pointer.
        <em>NOTE:</em> This is for use with raw JNI interactions via the
        JNIEnv data structure.
    */
    String OPTION_ALLOW_OBJECTS = "allow-objects";
    /** Calling convention for the entire library. */
    String OPTION_CALLING_CONVENTION = "calling-convention";
    /** Flags to use when opening the native library (see {@link Native#open(String,int)}) */
    String OPTION_OPEN_FLAGS = "open-flags";
    /** <p>Class loader to use when searching for native libraries on the
     * resource path (classpath).  If not provided the current thread's
     * context class loader is used.</p>
     * If extracted from the resource path (i.e. bundled in a jar file), the
     * loaded library's lifespan will mirror that of the class loader, which
     * means you can use the same library in isolated contexts without
     * conflict.
     */
    String OPTION_CLASSLOADER = "classloader";

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

        private final NativeLibrary nativeLibrary;
        private final Class interfaceClass;
        // Library invocation options
        private final Map options;
        private final InvocationMapper invocationMapper;
        private final Map functions = new WeakHashMap();
        public Handler(String libname, Class interfaceClass, Map options) {

            if (libname != null && "".equals(libname.trim())) {
                throw new IllegalArgumentException("Invalid library name \""
                                                   + libname + "\"");
            }

            this.interfaceClass = interfaceClass;
            options = new HashMap(options);
            int callingConvention = 
                AltCallingConvention.class.isAssignableFrom(interfaceClass)
                ? Function.ALT_CONVENTION : Function.C_CONVENTION;
            if (options.get(OPTION_CALLING_CONVENTION) == null) {
                options.put(OPTION_CALLING_CONVENTION,
                            new Integer(callingConvention));
            }
            if (options.get(OPTION_CLASSLOADER) == null) {
                options.put(OPTION_CLASSLOADER, interfaceClass.getClassLoader());
            }
            this.options = options;
            this.nativeLibrary = NativeLibrary.getInstance(libname, options);
            invocationMapper = (InvocationMapper)options.get(OPTION_INVOCATION_MAPPER);
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
        
        /**
         * FunctionInfo has to be immutable to to make the object visible 
         * to other threads fully initialized. This is a prerequisite for
         * using the class in the double checked locking scenario of {@link Handler#invoke(Object, Method, Object[])}
         */
        private static final class FunctionInfo {
            
            FunctionInfo(InvocationHandler handler, Function function, boolean isVarArgs, Map options) {
                super();
                this.handler = handler;
                this.function = function;
                this.isVarArgs = isVarArgs;
                this.options = options;
            }
            
            final InvocationHandler handler;
            final Function function;
            final boolean isVarArgs;
            final Map options;
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
                    return Function.valueOf(Proxy.getInvocationHandler(o) == this);
                }
                return Boolean.FALSE;
            }
            
            // Using the double-checked locking pattern to speed up function calls
            FunctionInfo f = (FunctionInfo)functions.get(method);
            if(f == null) {
                synchronized(functions) {
                    f = (FunctionInfo)functions.get(method);
                    if (f == null) {
                        boolean isVarArgs = Function.isVarArgs(method);
                        InvocationHandler handler = null;
                        if (invocationMapper != null) {
                            handler = invocationMapper.getInvocationHandler(nativeLibrary, method);
                        }
                        Function function = null;
                        Map options = null;
                        if (handler == null) {
                            // Find the function to invoke
                            function = nativeLibrary.getFunction(method.getName(), method);
                            options = new HashMap(this.options);
                            options.put(Function.OPTION_INVOKING_METHOD, method);
                        }
                        f = new FunctionInfo(handler, function, isVarArgs, options);
                        functions.put(method, f);
                    }
                }
            }
            if (f.isVarArgs) {
                inArgs = Function.concatenateVarArgs(inArgs);
            }
            if (f.handler != null) {
                return f.handler.invoke(proxy, method, inArgs);
            }
            return f.function.invoke(method.getReturnType(), inArgs, f.options);
        }
    }
}

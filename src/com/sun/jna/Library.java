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

import com.sun.jna.internal.ReflectionUtils;
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
 *     Native.load("mylib", MyNativeLibrary.class);
 * </code></pre>
 * <p>
 * By convention, method names are identical to the native names, although you
 * can map java names to different native names by providing a
 * {@link FunctionMapper} as a value for key {@link #OPTION_FUNCTION_MAPPER}
 * in the options map passed to the
 * {@link Native#load(String, Class, Map)} call.
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
 * {@link Native#load(String,Class,Map)} is made prior to instantiating
 * any of those structures.  One common way of ensuring this is to declare
 * an <b>INSTANCE</b> field in the interface which holds the
 * <code>load</code> result.
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
                OBJECT_TOSTRING = Object.class.getMethod("toString");
                OBJECT_HASHCODE= Object.class.getMethod("hashCode");
                OBJECT_EQUALS = Object.class.getMethod("equals", Object.class);
            } catch (Exception e) {
                throw new Error("Error retrieving Object.toString() method");
            }
        }

        /**
         * FunctionInfo has to be immutable to to make the object visible
         * to other threads fully initialized. This is a prerequisite for
         * using the class in the double checked locking scenario of {@link Handler#invoke(Object, Method, Object[])}
         */
        private static final class FunctionInfo {
            final InvocationHandler handler;
            final Function function;
            final boolean isVarArgs;
            final Object methodHandle;
            final Map<String, ?> options;
            final Class<?>[] parameterTypes;

            FunctionInfo(Object mh) {
                this.handler = null;
                this.function = null;
                this.isVarArgs = false;
                this.options = null;
                this.parameterTypes = null;
                this.methodHandle = mh;
            }

            FunctionInfo(InvocationHandler handler, Function function, Class<?>[] parameterTypes, boolean isVarArgs, Map<String, ?> options) {
                this.handler = handler;
                this.function = function;
                this.isVarArgs = isVarArgs;
                this.options = options;
                this.parameterTypes = parameterTypes;
                this.methodHandle = null;
            }
        }

        private final NativeLibrary nativeLibrary;
        private final Class<?> interfaceClass;
        // Library invocation options
        private final Map<String, Object> options;
        private final InvocationMapper invocationMapper;
        private final Map<Method, FunctionInfo> functions = new WeakHashMap<Method, FunctionInfo>();
        public Handler(String libname, Class<?> interfaceClass, Map<String, ?> options) {

            if (libname != null && "".equals(libname.trim())) {
                throw new IllegalArgumentException("Invalid library name \"" + libname + "\"");
            }

            if (!interfaceClass.isInterface()) {
                throw new IllegalArgumentException(libname + " does not implement an interface: " + interfaceClass.getName());
            }

            this.interfaceClass = interfaceClass;
            this.options = new HashMap<String, Object>(options);
            int callingConvention = AltCallingConvention.class.isAssignableFrom(interfaceClass)
                                  ? Function.ALT_CONVENTION
                                  : Function.C_CONVENTION;
            if (this.options.get(OPTION_CALLING_CONVENTION) == null) {
                this.options.put(OPTION_CALLING_CONVENTION, Integer.valueOf(callingConvention));
            }
            if (this.options.get(OPTION_CLASSLOADER) == null) {
                this.options.put(OPTION_CLASSLOADER, interfaceClass.getClassLoader());
            }
            this.nativeLibrary = NativeLibrary.getInstance(libname, this.options);
            invocationMapper = (InvocationMapper)this.options.get(OPTION_INVOCATION_MAPPER);
        }

        public NativeLibrary getNativeLibrary() {
            return nativeLibrary;
        }

        public String getLibraryName() {
            return nativeLibrary.getName();
        }

        public Class<?> getInterfaceClass() {
            return interfaceClass;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] inArgs)
            throws Throwable {

            // Intercept Object methods
            if (OBJECT_TOSTRING.equals(method)) {
                return "Proxy interface to " + nativeLibrary;
            } else if (OBJECT_HASHCODE.equals(method)) {
                return Integer.valueOf(hashCode());
            } else if (OBJECT_EQUALS.equals(method)) {
                Object o = inArgs[0];
                if (o != null && Proxy.isProxyClass(o.getClass())) {
                    return Function.valueOf(Proxy.getInvocationHandler(o) == this);
                }
                return Boolean.FALSE;
            }

            // Using the double-checked locking pattern to speed up function calls
            FunctionInfo f = functions.get(method);
            if(f == null) {
                synchronized(functions) {
                    f = functions.get(method);
                    if (f == null) {
                        boolean isDefault = ReflectionUtils.isDefault(method);
                        if(! isDefault) {
                            boolean isVarArgs = Function.isVarArgs(method);
                            InvocationHandler handler = null;
                            if (invocationMapper != null) {
                                handler = invocationMapper.getInvocationHandler(nativeLibrary, method);
                            }
                            Function function = null;
                            Class<?>[] parameterTypes = null;
                            Map<String, Object> options = null;
                            if (handler == null) {
                                // Find the function to invoke
                                function = nativeLibrary.getFunction(method.getName(), method);
                                parameterTypes = method.getParameterTypes();
                                options = new HashMap<String, Object>(this.options);
                                options.put(Function.OPTION_INVOKING_METHOD, method);
                            }
                            f = new FunctionInfo(handler, function, parameterTypes, isVarArgs, options);
                        } else {
                            f = new FunctionInfo(ReflectionUtils.getMethodHandle(method));
                        }
                        functions.put(method, f);
                    }
                }
            }
            if (f.methodHandle != null) {
                return ReflectionUtils.invokeDefaultMethod(proxy, f.methodHandle, inArgs);
            } else {
                if (f.isVarArgs) {
                    inArgs = Function.concatenateVarArgs(inArgs);
                }
                if (f.handler != null) {
                    return f.handler.invoke(proxy, method, inArgs);
                }
                return f.function.invoke(method, f.parameterTypes, method.getReturnType(), inArgs, f.options);
            }
        }
    }
}

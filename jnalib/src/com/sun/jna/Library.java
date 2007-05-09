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
import java.lang.ref.WeakReference;
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
    /** Maximum number of allowable arguments in a mapped function. */
    int MAX_NARGS = 32;

    static class Handler implements InvocationHandler {

        static class CallbackReference extends WeakReference {
            Pointer cbstruct;
            public CallbackReference(Callback callback, Pointer cbstruct) {
                super(callback);
                this.cbstruct = cbstruct;
            }
            public Pointer getTrampoline() {
                return cbstruct.getPointer(0);
            }
            protected void finalize() {
                Function.freeCallback(cbstruct.peer);
                cbstruct.peer = 0;
            }
        }
        static final Map callbackMap = new WeakHashMap();
        
        private String libname;
        private Class interfaceClass;
        private Map functions = new HashMap();
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

            this.libname = libname;
            this.interfaceClass = interfaceClass;
            this.functionMap = functionMap;
        }

        public String getLibraryName() {
            return libname;
        }

        public Class getInterfaceClass() {
            return interfaceClass;
        }
        
        private Pointer createCallback(Library library,  Callback obj) {
            Method[] mlist = obj.getClass().getMethods();
            for (int i=0;i < mlist.length;i++) {
                if (Callback.METHOD_NAME.equals(mlist[i].getName())) {
                    Method m = mlist[i];
                    Class[] paramTypes = m.getParameterTypes();
                    Class rtype = m.getReturnType();
                    if (paramTypes.length > MAX_NARGS) {
                        String msg = "Method signature exceeds the maximum "
                            + "parameter count: " + m;
                        throw new IllegalArgumentException(msg);
                    }
                    return Function.createCallback(library, obj, m, paramTypes, rtype);
                }
            }
            String msg = "Callback must implement method named '"
                + Callback.METHOD_NAME + "'";
            throw new IllegalArgumentException(msg);
        }

        public Object invoke(Object proxy, Method method, Object[] inArgs)
            throws Throwable {
            Object result=null;

            // Clone the argument array
            Object[] args = { };
            if (inArgs != null) {
                args = new Object[inArgs.length];
                System.arraycopy(inArgs, 0, args, 0, args.length);
            }

            // String arguments are converted to native pointers here rather
            // than in native code so that the values will be valid until
            // this method returns.  At one point the conversion was in native
            // code, which left the pointer values invalid before this method
            // returned (so you couldn't do something like strstr).
            for (int i=0; i < args.length; i++) {
                Object arg = args[i];
                if (arg == null 
                    || (arg.getClass().isArray() 
                        && arg.getClass().getComponentType().isPrimitive())) { 
                    continue;
                }
                
                // Convert Structures to native pointers 
                if (arg instanceof Structure) {
                    Structure struct = (Structure)arg;
                    struct.write();
                    args[i] = struct.getPointer();
                }
                // Convert reference class to pointer
                else if (arg instanceof ByReference) {
                    args[i] = ((ByReference)arg).getPointer();
                }
                // Convert Callback to Pointer
                else if (arg instanceof Callback) {
                    CallbackReference cbref;
                    synchronized(callbackMap) {
                        cbref = (CallbackReference)callbackMap.get(arg);
                        if (cbref == null) {
                            Pointer cbstruct = createCallback((Library)proxy, (Callback)arg);
                            cbref = new CallbackReference((Callback)arg, cbstruct);
                            callbackMap.put(arg, cbref);
                        }
                    }
                    // Use pointer to trampoline (callback->insns, see dispatch.h)
                    args[i] = cbref.getTrampoline();
                }
                // Convert String to native pointer (const)
                else if (arg instanceof String) {
                    args[i] = new NativeString((String)arg, false).getPointer();
                }
                // Convert WString to native pointer (const)
                else if (arg instanceof WString) {
                    args[i] = new NativeString(arg.toString(), true).getPointer();
                }
                // Convert boolean to int
                // NOTE: this is specifically for BOOL on w32; most other 
                // platforms simply use an 'int' or 'char' argument.
                else if (arg instanceof Boolean) {
                    args[i] = new Integer(Boolean.TRUE.equals(arg) ? -1 : 0);
                }
                else if (arg.getClass().isArray()) {
                    throw new IllegalArgumentException("Unsupported array type: " + arg.getClass());
                }
            }

            // Find the function to invoke
            String methodName = method.getName();
            if (functionMap.containsKey(methodName)) {
                methodName = (String)functionMap.get(methodName);
            }
            Function function;
            synchronized(functions) {
                function = (Function)functions.get(methodName);
                if (function == null) {
                    if (AltCallingConvention.class.isAssignableFrom(interfaceClass)) {
                        function = new Function(libname, methodName,
                                                Function.ALT_CONVENTION);
                    }
                    else {
                        function = new Function(libname, methodName);
                    }
                    functions.put(methodName, function);
                }
            }
            Class returnType = method.getReturnType();
            if (returnType==Void.TYPE || returnType==Void.class) {
                function.invoke(args);
            }
            else if (returnType==Boolean.TYPE || returnType==Boolean.class) {
                result = new Boolean(function.invokeBoolean(args));
            }
            else if (returnType==Byte.TYPE || returnType==Byte.class) {
                result = new Byte((byte)function.invokeInt(args));
            }
            else if (returnType==Short.TYPE || returnType==Short.class) {
                result = new Short((short)function.invokeInt(args));
            }
            else if (returnType==Integer.TYPE || returnType==Integer.class) {
                result = new Integer(function.invokeInt(args));
            }
            else if (returnType==Long.TYPE || returnType==Long.class) {
                result = new Long(function.invokeLong(args));
            }
            else if (returnType==Float.TYPE || returnType==Float.class) {
                result = new Float(function.invokeFloat(args));
            }
            else if (returnType==Double.TYPE || returnType==Double.class) {
                result = new Double(function.invokeDouble(args));
            }
            else if (returnType==String.class) {
                result = function.invokeString(args, false);
            }
            else if (returnType==WString.class) {
                result = new WString(function.invokeString(args, true));
            }
            else if (Pointer.class.isAssignableFrom(returnType)) {
                result = function.invokePointer(args);
            }
            else if (Structure.class.isAssignableFrom(returnType)) {
                result = function.invokePointer(args);
                Structure s = (Structure)returnType.newInstance();
                s.useMemory((Pointer)result);
                s.read();
                result = s;
            }
            else {
                throw new IllegalArgumentException("Unsupported return type "
                                                   + returnType);
            }

            // Sync java fields in structures to native memory after invocation
            if (inArgs != null) {
                for (int i=0; i < inArgs.length; i++) {
                    Object arg = inArgs[i];
                    if (arg instanceof Structure) {
                        ((Structure)arg).read();
                    }
                }
            }
			
            return result;
        }
    }
}

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

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import com.sun.jna.ptr.ByReference;

/**
 * An abstraction for a native function pointer.  An instance of 
 * <code>Function</code> repesents a pointer to some native function.  
 * <code>invokeXXX</code> methods provide means to call the function; select a 
 * <code>XXX</code> variant based on the return type of the Java interface
 * method. 
 *
 * @author Sheng Liang, originator
 * @author Todd Fast, suitability modifications
 * @see Pointer
 */
public class Function extends Pointer {
    /** Maximum number of arguments supported by a JNA function call. */
    public static final int MAX_NARGS = 32;

    /** Standard C calling convention. */
    public static final int C_CONVENTION = 0;
    /** Alternate convention (currently used only for w32 stdcall). */
    public static final int ALT_CONVENTION = 1;

    private NativeLibrary library;
    private String functionName;
    private int callingConvention;

    /**
     * Create a new {@link Function} that is linked with a native 
     * function that follows the standard "C" calling convention.
     * 
     * <p>The allocated instance represents a pointer to the named native 
     * function from the named library, called with the standard "C" calling
     * convention.
     *
     * @param	libraryName
     *			Library in which to find the native function
     * @param	functionName
     *			Name of the native function to be linked with
     */
    public Function(String libraryName, String functionName) {
        this(libraryName, functionName, C_CONVENTION);
    }


    /**
     * Create a new @{link Function} that is linked with a native 
     * function that follows a given calling convention.
     * 
     * <p>The allocated instance represents a pointer to the named native 
     * function from the named library, called with the named calling 
     * convention.
     *
     * @param	libraryName
     *			Library in which to find the function
     * @param	functionName
     *			Name of the native function to be linked with
     * @param	callingConvention
     *			Calling convention used by the native function
     */
    public Function(String libraryName, String functionName, 
                    int callingConvention) {
        this(NativeLibrary.getInstance(libraryName), functionName, callingConvention);
    }
    
    /**
     * Create a new @{link Function} that is linked with a native 
     * function that follows a given calling convention.
     * 
     * <p>The allocated instance represents a pointer to the named native 
     * function from the supplied library, called with the named calling 
     * convention.
     *
     * @param  library
     *                 {@link NativeLibrary} in which to find the function
     * @param  functionName
     *                 Name of the native function to be linked with
     * @param  callingConvention
     *                 Calling convention used by the native function
     */
    Function(NativeLibrary library, String functionName, int callingConvention) {
        checkCallingConvention(callingConvention);
        this.library = library;
        this.functionName = functionName;
        this.callingConvention = callingConvention;
        peer = library.getFunctionAddress(functionName);
    }
    
    private void checkCallingConvention(int convention)
        throws IllegalArgumentException {
        switch(convention) {
        case C_CONVENTION:
        case ALT_CONVENTION:
            break;
        default:
            throw new IllegalArgumentException("Unrecognized calling convention: " 
                                               + convention);
        }
    }

    public String getLibraryName() {
        return library.getName();
    }

    public String getName() {
        return functionName;
    }


    public int getCallingConvention() {
        return callingConvention;
    }

    /** Invoke the native function with the given arguments, returning the
     * native result as an Object.
     */
    public Object invoke(Class returnType, Object[] inArgs) {
        // This will be set to the full set of arguments if a varargs
        // argument is encountered
        Object[] fullArgs = null;
        Object result = null;

        // Clone the argument array
        Object[] args = { };
        if (inArgs != null) {
            if (inArgs.length > MAX_NARGS) {
                throw new UnsupportedOperationException("Maximum argument count is " + MAX_NARGS);
            }
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
            if (arg == null || isPrimitiveArray(arg.getClass())) { 
                continue;
            }
            
            Class argClass = arg.getClass();
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
                CallbackReference cbref = CallbackReference.getInstance((Callback)arg, callingConvention);
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
            else if (arg instanceof NativeLong) {
                args[i] = ((NativeLong)arg).asNativeValue();
            }
            // Convert boolean to int
            // NOTE: this is specifically for BOOL on w32; most other 
            // platforms simply use an 'int' or 'char' argument.
            else if (arg instanceof Boolean) {
                args[i] = new Integer(Boolean.TRUE.equals(arg) ? -1 : 0);
            }
            else if (isStructureArray(argClass)) {
                // Initialize uninitialized arrays of Structure to point
                // to a single block of memory
                Structure[] ss = (Structure[])arg;
                if (ss.length == 0) {
                    args[i] = null;
                }
                else if (ss[0] == null) {
                    Class type = argClass.getComponentType();
                    try {
                        Structure struct = (Structure)type.newInstance(); 
                        int size = struct.size();
                        Memory m = new Memory(size * ss.length);
                        struct.useMemory(m);
                        Structure[] tmp = struct.toArray(ss.length);
                        for (int si=0;si < ss.length;si++) {
                            ss[si] = tmp[si];
                        }
                    }
                    catch(InstantiationException e) {
                        throw new IllegalArgumentException("Instantiation of "
                                                           + type + " failed: " 
                                                           + e);
                    }
                    catch(IllegalAccessException e) {
                        throw new IllegalArgumentException("Not allowed to instantiate "
                                                           + type + ": " + e);
                    }
                    args[i] = ss[0].getPointer();
                }
                else {
                    Pointer base = ss[0].getPointer();
                    int size = ss[0].size();
                    for (int si=1;si < ss.length;si++) {
                        try {
                            Pointer p = base.share(size*si, size);
                            if (ss[si].getPointer().peer != p.peer) {
                                throw new RuntimeException();
                            }
                        }
                        catch(RuntimeException e) {
                            String msg = "Structure array elements must use"
                                + " contiguous memory: " + si;     
                            throw new IllegalArgumentException(msg);
                        }
                    }
                    args[i] = base;
                }
            }
            else if (argClass.isArray()) {
                // An object array as the final argument is interpreted as
                // varargs input
                if (i == inArgs.length - 1 && fullArgs == null) {
                    Object[] varargs = (Object[]) arg;
                    int fixedCount = inArgs.length - 1;
                    int argCount = fixedCount + varargs.length; 
                    if (argCount > MAX_NARGS) {
                        throw new UnsupportedOperationException("Maximum argument count is " + MAX_NARGS);
                    }
                    Object[] newArgs = new Object[argCount];
                    // Copy the original arg array (less the current arg)
                    System.arraycopy(args, 0, newArgs, 0, fixedCount);
                    
                    // Copy the varargs array onto the end of the main args array
                    System.arraycopy(varargs, 0, newArgs, fixedCount, varargs.length);
                    fullArgs = args = newArgs;
                    --i; // Jump back and process the first arg of the varargs
                }
                else {
                    throw new IllegalArgumentException("Unsupported array type: " 
                                                       + argClass.getComponentType());
                }
            }
        }

        if (returnType==Void.TYPE || returnType==Void.class) {
            invokeVoid(callingConvention, args);
        }
        else if (returnType==Boolean.TYPE || returnType==Boolean.class) {
            result = new Boolean(invokeInt(callingConvention, args) != 0);
        }
        else if (returnType==Byte.TYPE || returnType==Byte.class) {
            result = new Byte((byte)invokeInt(callingConvention, args));
        }
        else if (returnType==Short.TYPE || returnType==Short.class) {
            result = new Short((short)invokeInt(callingConvention, args));
        }
        else if (returnType==Integer.TYPE || returnType==Integer.class) {
            result = new Integer(invokeInt(callingConvention, args));
        }
        else if (returnType==Long.TYPE || returnType==Long.class) {
            result = new Long(invokeLong(callingConvention, args));
        }
        else if (returnType==NativeLong.class) {
            result = new NativeLong(NativeLong.SIZE == 8
                                    ? invokeLong(callingConvention, args)
                                    : invokeInt(callingConvention, args));
        }
        else if (returnType==Float.TYPE || returnType==Float.class) {
            result = new Float(invokeFloat(callingConvention, args));
        }
        else if (returnType==Double.TYPE || returnType==Double.class) {
            result = new Double(invokeDouble(callingConvention, args));
        }
        else if (returnType==String.class) {
            result = invokeString(args, false);
        }
        else if (returnType==WString.class) {
            result = new WString(invokeString(args, true));
        }
        else if (Pointer.class.isAssignableFrom(returnType)) {
            result = invokePointer(callingConvention, args);
        }
        else if (Structure.class.isAssignableFrom(returnType)) {
            result = invokePointer(callingConvention, args);
            if (result != null) {
                try {
                    Structure s = (Structure)returnType.newInstance();
                    s.useMemory((Pointer)result);
                    s.read();
                    result = s;
                }
                catch(InstantiationException e) {
                    throw new IllegalArgumentException("Instantiation of "
                                                       + returnType + " failed: " 
                                                       + e);
                }
                catch(IllegalAccessException e) {
                    throw new IllegalArgumentException("Not allowed to instantiate "
                                                       + returnType + ": " + e);
                }
            }
        }
        else {
            throw new IllegalArgumentException("Unsupported return type "
                                               + returnType);
        }

        // Sync java fields in structures to native memory after invocation
        if (inArgs != null) {
            if (fullArgs != null)
                inArgs = fullArgs;
            for (int i=0; i < inArgs.length; i++) {
                Object arg = inArgs[i];
                if (arg == null)
                    continue;
                if (arg instanceof Structure) {
                    ((Structure)arg).read();
                }
                else if (isStructureArray(arg.getClass())) {
                    Structure[] ss = (Structure[])arg;
                    for (int si=0;si < ss.length;si++) {
                        ss[si].read();
                    }
                }
            }
        }
                        
        return result;
    }


    private boolean isStructureArray(Class argClass) {
        return argClass.isArray()
            && Structure.class.isAssignableFrom(argClass.getComponentType());
    }


    private boolean isPrimitiveArray(Class argClass) {
        return argClass.isArray() 
            && argClass.getComponentType().isPrimitive();
    }
    
    /**
     * Call the native function being represented by this object
     *
     * @param	args
     *			Arguments to pass to the native function
     * @return	The value returned by the target function
     */
    public int invokeInt(Object[] args) {
        return ((Integer)invoke(Integer.class, args)).intValue();
    }


    /**
     * Call the native function being represented by this object
     *
     * @param	args
     *			Arguments to pass to the native function
     * @return	The value returned by the target native function
     */
    public native int invokeInt(int callingConvention, Object[] args);

    /**
     * Call the native function being represented by this object
     *
     * @param	args
     *			Arguments to pass to the native function
     * @return	The value returned by the target function
     */
    public long invokeLong(Object[] args) {
        return ((Long)invoke(Long.class, args)).longValue();
    }


    /**
     * Call the native function being represented by this object
     *
     * @param	args
     *			Arguments to pass to the native function
     * @return	The value returned by the target native function
     */
    public native long invokeLong(int callingConvention, Object[] args);

    /**
     * Call the native function being represented by this object
     *
     * @param	args
     *			Arguments to pass to the native function
     * @return	The value returned by the target function
     */
    public boolean invokeBoolean(Object[] args) {
        return Boolean.TRUE.equals(invoke(Boolean.class, args));
    }


    /**
     * Call the native function being represented by this object
     *
     * @param	args
     *			Arguments to pass to the native function
     */
    public void invoke(Object[] args) {
        invoke(Void.class, args);
    }


    /**
     * Call the native function being represented by this object
     *
     * @param	args
     *			Arguments to pass to the native function
     */
    public native void invokeVoid(int callingConvention, Object[] args);

    /**
     * Call the native function being represented by this object
     *
     * @param	args
     *			Arguments to pass to the native function
     * @return	The value returned by the target native function
     */
    public float invokeFloat(Object[] args) {
        return ((Float)invoke(Float.class, args)).floatValue();
    }


    /**
     * Call the native function being represented by this object
     *
     * @param	args
     *			Arguments to pass to the native function
     * @return	The value returned by the target native function
     */
    public native float invokeFloat(int callingConvention, Object[] args);

    /**
     * Call the native function being represented by this object
     *
     * @param	args
     *			Arguments to pass to the native function
     * @return	The value returned by the target native function
     */
    public double invokeDouble(Object[] args) {
        return ((Double)invoke(Double.class, args)).doubleValue();
    }


    /**
     * Call the native function being represented by this object
     *
     * @param	args
     *			Arguments to pass to the native function
     * @return	The value returned by the target native function
     */
    public native double invokeDouble(int callingConvention, Object[] args);

    /**
     * Call the native function being represented by this object
     *
     * @param	args
     *			Arguments to pass to the native function
     * @return	The value returned by the target native function
     */
    public String invokeString(Object[] args, boolean wide) {
        Pointer ptr = invokePointer(args);
        String s = null;
        if (ptr != null) {
            s = ptr.getString(0, wide);
        }
        return s;
    }

    /**
     * Call the native function being represented by this object
     *
     * @param	args
     *			Arguments to pass to the native function
     * @return	The native pointer returned by the target native function
     */
    public Pointer invokePointer(Object[] args) {
        return (Pointer)invoke(Pointer.class, args);
    }


    /**
     * Call the native function being represented by this object
     *
     * @param	args
     *			Arguments to pass to the native function
     * @return	The native pointer returned by the target native function
     */
    public native Pointer invokePointer(int callingConvention, Object[] args);

    /** Create a callback function pointer. */
    static native Pointer createCallback(Callback callback, Method method, 
                                         Class[] parameterTypes, 
                                         Class returnType,
                                         int callingConvention);
    /** Free the given callback function pointer. */
    static native void freeCallback(long ptr);
    
    public String toString() {
        return "Function: " + functionName + "(" + library.getName() + ")";
    }
}

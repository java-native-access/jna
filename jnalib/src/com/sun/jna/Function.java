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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.sun.jna.ptr.ByReference;

/**
 * An abstraction for a native function pointer.  An instance of 
 * <code>Function</code> repesents a pointer to some native function.  
 * {@link #invoke(Class,Object[],Map)} is the primary means to call
 * the function. 
 *
 * @author Sheng Liang, originator
 * @author Todd Fast, suitability modifications
 * @see Pointer
 */
public class Function extends Pointer {
    /** Maximum number of arguments supported by a JNA function call. */
    // NOTE: this may be different with libffi
    public static final int MAX_NARGS = 32;

    /** Standard C calling convention. */
    public static final int C_CONVENTION = 0;
    /** First alternate convention (currently used only for w32 stdcall). */
    public static final int ALT_CONVENTION = 1;

    private String libName;
    
    // Keep a reference to the NativeLibrary so it does not get garbage collected
    // until the function is
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
     * function that follows the given calling convention.
     * 
     * <p>The allocated instance represents a pointer to the named native 
     * function from the supplied library, called with the given calling 
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
        this.libName= library.getName();
        this.functionName = functionName;
        this.callingConvention = callingConvention;
        this.peer = library.getFunctionAddress(functionName);        
    }
    
    /**
     * Create a new @{link Function} that is linked with a native 
     * function that follows the given calling convention.
     * 
     * <p>The allocated instance represents a pointer to the given 
     * function address, called with the given calling 
     * convention.
     *
     * @param  functionAddress
     *                 Address of the native function 
     * @param  callingConvention
     *                 Calling convention used by the native function
     */
    Function(Pointer functionAddress, int callingConvention) {
        checkCallingConvention(callingConvention);
        this.libName = "<undefined>";
        this.functionName = functionAddress.toString();
        this.callingConvention = callingConvention;
        this.peer = functionAddress.peer;
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
        return invoke(returnType, inArgs, Collections.EMPTY_MAP);
    }    
    
    /** Invoke the native function with the given arguments, returning the
     * native result as an Object.
     */
    public Object invoke(Class returnType, Object[] inArgs, Map options) {

        // Clone the argument array to obtain a scratch space for modified
        // types/values
        Object[] args = { };
        if (inArgs != null) {
            if (inArgs.length > MAX_NARGS) {
                throw new UnsupportedOperationException("Maximum argument count is " + MAX_NARGS);
            }
            args = new Object[inArgs.length];
            System.arraycopy(inArgs, 0, args, 0, args.length);
        }

        TypeMapper mapper = 
            (TypeMapper)options.get(Library.OPTION_TYPE_MAPPER);
        
        for (int i=0; i < args.length; i++) {
            args[i] = convertArgument(args[i], mapper);
        }
        
        Class nativeType = returnType;
        FromNativeConverter resultConverter = null;
        if (NativeMapped.class.isAssignableFrom(returnType)) {
            NativeMappedConverter tc = new NativeMappedConverter(returnType);
            resultConverter = tc;
            nativeType = tc.nativeType();
        }
        else if (mapper != null) {
            resultConverter = mapper.getFromNativeConverter(returnType);
            if (resultConverter != null) {
                nativeType = resultConverter.nativeType();
            }
        }
        Object result = invoke(args, nativeType);

        // Convert the result to a custom value/type if appropriate
        if (resultConverter != null) {
            FromNativeContext context;
            Method m = (Method)options.get(Library.OPTION_INVOKING_METHOD);
            if (m != null) {
                context = new MethodResultContext(returnType, this, inArgs, m);
            } else {
                context = new FunctionResultContext(returnType, this, inArgs);
            }
            result = resultConverter.fromNative(result, context);
        }

        // Sync all memory which might have been modified by the native call
        if (inArgs != null) {
            for (int i=0; i < inArgs.length; i++) {
                Object arg = inArgs[i];
                if (arg == null)
                    continue;
                if (arg instanceof Structure) {
                    ((Structure)arg).read();
                }
                else if (String[].class == arg.getClass()) {
                    // Copy back the string values, just in case they were
                    // modified
                    StringArray buf = (StringArray)args[i];
                    String[] array = (String[])arg;
                    for (int si=0;si < array.length;si++) {
                        array[si] = buf.getPointer(si * Pointer.SIZE).getString(0);
                    }
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

    private Object invoke(Object[] args, Class returnType) {
        Object result = null;
        if (returnType == null || returnType==void.class || returnType==Void.class) {
            invokeVoid(callingConvention, args);
            result = null;
        }
        else if (returnType==boolean.class || returnType==Boolean.class) {
            result = Boolean.valueOf(invokeInt(callingConvention, args) != 0);
        }
        else if (returnType==byte.class || returnType==Byte.class) {
            result = Byte.valueOf((byte)invokeInt(callingConvention, args));
        }
        else if (returnType==short.class || returnType==Short.class) {
            result = Short.valueOf((short)invokeInt(callingConvention, args));
        }
        else if (returnType==char.class || returnType==Character.class) {
            result = Character.valueOf((char)invokeInt(callingConvention, args));
        }
        else if (returnType==int.class || returnType==Integer.class) {
            result = Integer.valueOf(invokeInt(callingConvention, args));
        }
        else if (returnType==long.class || returnType==Long.class) {
            result = Long.valueOf(invokeLong(callingConvention, args));
        }
        else if (returnType==float.class || returnType==Float.class) {
            result = Float.valueOf(invokeFloat(callingConvention, args));
        }
        else if (returnType==double.class || returnType==Double.class) {
            result = Double.valueOf(invokeDouble(callingConvention, args));
        }
        else if (returnType==String.class) {
            result = invokeString(callingConvention, args, false);
        }
        else if (returnType==WString.class) {
            result = new WString(invokeString(callingConvention, args, true));
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
        return result;
    }
    
    private Object convertArgument(Object arg, TypeMapper mapper) { 
        if (arg != null) {
            Class type = arg.getClass();
            ToNativeConverter converter = null;
            if (NativeMapped.class.isAssignableFrom(type)) {
                converter = new NativeMappedConverter(type);
            }
            else if (mapper != null) {
                converter = mapper.getToNativeConverter(type);
            }
            if (converter != null) {
                arg = converter.toNative(arg);
            }
        }
        if (arg == null || isPrimitiveArray(arg.getClass())) { 
            return arg;
        }
        Class argClass = arg.getClass();
        // Convert Structures to native pointers 
        if (arg instanceof Structure) {
            Structure struct = (Structure)arg;
            struct.write();
            return struct.getPointer();
        }
        // Convert reference class to pointer
        else if (arg instanceof ByReference) {
            return ((ByReference)arg).getPointer();
        }
        // Convert Callback to Pointer
        else if (arg instanceof Callback) {
            CallbackReference cbref = CallbackReference.getInstance((Callback)arg);
            // Use pointer to trampoline (see dispatch.h)
            return cbref.getTrampoline();
        }
        // String arguments are converted to native pointers here rather
        // than in native code so that the values will be valid until
        // this method returns.  At one point the conversion was in native
        // code, which left the pointer values invalid before this method
        // returned (so you couldn't do something like strstr).
        // Convert String to native pointer (const)
        else if (arg instanceof String) {
            return new NativeString((String)arg, false).getPointer();
        }
        // Convert WString to native pointer (const)
        else if (arg instanceof WString) {
            return new NativeString(arg.toString(), true).getPointer();
        }
        // Default conversion of boolean to int; if you want something
        // different, use a ToNativeConverter
        else if (arg instanceof Boolean) {
            return Integer.valueOf(Boolean.TRUE.equals(arg) ? -1 : 0);
        }
        else if (String[].class == argClass) {
            return new StringArray((String[])arg);
        }
        else if (isStructureArray(argClass)) {
            // Initialize uninitialized arrays of Structure to point
            // to a single block of memory
            Structure[] ss = (Structure[])arg;
            if (ss.length == 0) {
                return null;
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
                return ss[0].getPointer();
            }
            else {
                Pointer base = ss[0].getPointer();
                int size = ss[0].size();
                ss[0].write();
                for (int si=1;si < ss.length;si++) {
                    try {
                        Pointer p = base.share(size*si, size);
                        if (ss[si].getPointer().peer != p.peer) {
                            throw new RuntimeException();
                        }
                        ss[si].write();
                    }
                    catch(RuntimeException e) {
                        String msg = "Structure array elements must use"
                            + " contiguous memory: " + si;     
                        throw new IllegalArgumentException(msg);
                    }
                }
                return base;
            }
        }
        else if (argClass.isArray()){
            throw new IllegalArgumentException("Unsupported array argument type: " 
                                               + argClass.getComponentType());
        }
        return arg;
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
     * @param   callingConvention calling convention to be used
     * @param	args
     *			Arguments to pass to the native function
     * @return	The value returned by the target native function
     */
    private  native int invokeInt(int callingConvention, Object[] args);

    /**
     * Call the native function being represented by this object
     *
     * @param   callingConvention calling convention to be used
     * @param	args
     *			Arguments to pass to the native function
     * @return	The value returned by the target native function
     */
    private native long invokeLong(int callingConvention, Object[] args);

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
     * @param   callingConvention calling convention to be used
     * @param	args
     *			Arguments to pass to the native function
     */
    private native void invokeVoid(int callingConvention, Object[] args);

    /**
     * Call the native function being represented by this object
     *
     * @param   callingConvention calling convention to be used
     * @param	args
     *			Arguments to pass to the native function
     * @return	The value returned by the target native function
     */
    private native float invokeFloat(int callingConvention, Object[] args);

    /**
     * Call the native function being represented by this object
     *
     * @param   callingConvention calling convention to be used
     * @param	args
     *			Arguments to pass to the native function
     * @return	The value returned by the target native function
     */
    private native double invokeDouble(int callingConvention, Object[] args);

    /**
     * Call the native function being represented by this object
     *
     * @param   callingConvention calling convention to be used
     * @param	args
     *			Arguments to pass to the native function
     * @param   wide whether the native string uses <code>wchar_t</code>;
     * if false, <code>char</code> is assumed
     * @return	The value returned by the target native function, as a String
     */
    private String invokeString(int callingConvention, Object[] args, boolean wide) {
        Pointer ptr = invokePointer(callingConvention, args);
        String s = null;
        if (ptr != null) {
            if (wide)
                s = ptr.getString(0, wide);
            else
                s = ptr.getString(0);
        }
        return s;
    }

    /**
     * Call the native function being represented by this object
     *
     * @param   callingConvention calling convention to be used
     * @param	args
     *			Arguments to pass to the native function
     * @return	The native pointer returned by the target native function
     */
    private native Pointer invokePointer(int callingConvention, Object[] args);

    /** Provide a human-readable representation of this object. */
    public String toString() {
        return "native function " + functionName + "(" + libName
            + ")@0x" + Long.toHexString(peer);
    }
    
    /** Handle native array of char* type by managing allocation/disposal of 
     * native strings within an array of pointers.  Always NULL-terminates
     * the array. 
     */
    private class StringArray extends Memory {
        private List natives = new ArrayList();
        public StringArray(String[] strings) { 
            super((strings.length + 1) * Pointer.SIZE);
            for (int i=0;i < strings.length;i++) {
                NativeString ns = new NativeString(strings[i]);
                natives.add(ns);
                setPointer(Pointer.SIZE * i, ns.getPointer());
            }
            setPointer(Pointer.SIZE * strings.length, null);
        }
    }
    
    // The following convenience methods are provided for using a Function
    // instance directly
    
    /** Convenience method for 
     * {@link #invoke(Class,Object[]) invoke(Pointer.class, args)}.
     */
    public Pointer invokePointer(Object[] args) {
        return (Pointer)invoke(Pointer.class, args);
    }
    /** Convenience method for
     * {@link #invoke(Class,Object[]) invoke(String.class, args)}
     * or {@link #invoke(Class,Object[]) invoke(WString.class, args)}
     * @param args Arguments passed to native function
     * @param wide Whether the return value is of type <code>wchar_t*</code>;
     * if false, the return value is of type <code>char*</code>.
     */
    public String invokeString(Object[] args, boolean wide) {
        Object o = invoke(wide ? WString.class : String.class, args);
        return o != null ? o.toString() : null;
    }

    /** Convenience method for 
     * {@link #invoke(Class,Object[]) invoke(Integer.class, args)}.
     */
    public int invokeInt(Object[] args) {
        return ((Integer)invoke(Integer.class, args)).intValue();
    }
    /** Convenience method for 
     * {@link #invoke(Class,Object[]) invoke(Long.class, args)}.
     */
    public long invokeLong(Object[] args) {
        return ((Long)invoke(Long.class, args)).longValue();
    }
    /** Convenience method for 
     * {@link #invoke(Class,Object[]) invoke(Float.class, args)}.
     */
    public float invokeFloat(Object[] args) {
        return ((Float)invoke(Float.class, args)).floatValue();
    }
    /** Convenience method for 
     * {@link #invoke(Class,Object[]) invoke(Double.class, args)}.
     */
    public double invokeDouble(Object[] args) {
        return ((Double)invoke(Double.class, args)).doubleValue();
    }
    /** Convenience method for 
     * {@link #invoke(Class,Object[]) invoke(Void.class, args)}.
     */
    public void invokeVoid(Object[] args) {
        invoke(Void.class, args);
    }


    /** Concatenate varargs with normal args to obtain a simple argument 
     * array. 
     */
    static Object[] concatenateVarArgs(Object[] inArgs) {
        // If the final argument is an array of something other than
        // primitives, Structure, or String, treat it as varargs and 
        // concatenate the previous arguments with the varargs elements.
        if (inArgs != null && inArgs.length > 0) {
            Object lastArg = inArgs[inArgs.length-1];
            Class argType = lastArg != null ? lastArg.getClass() : null;
            if (argType != null && argType.isArray()) {
                Object[] varArgs = (Object[])lastArg;
                Object[] fullArgs = new Object[inArgs.length+varArgs.length];
                System.arraycopy(inArgs, 0, fullArgs, 0, inArgs.length-1);
                System.arraycopy(varArgs, 0, fullArgs, inArgs.length-1, varArgs.length);
                // For convenience, always append a NULL argument to the end
                // of varargs, whether the called API requires it or not. If
                // it is not needed, it will be ignored, but if it *is* 
                // required, it avoids forcing the Java client to always
                // explicitly add it.
                fullArgs[fullArgs.length-1] = null;
                inArgs = fullArgs;
            }
        }
        return inArgs;
    }


    /** Varargs are only supported on 1.5+. */
    static boolean isVarArgs(Method m) {
        try {
            Method v = m.getClass().getMethod("isVarArgs", new Class[0]);
            return Boolean.TRUE.equals(v.invoke(m, new Object[0]));
        }
        catch (SecurityException e) {
        }
        catch (NoSuchMethodException e) {
        }
        catch (IllegalArgumentException e) {
        }
        catch (IllegalAccessException e) {
        }
        catch (InvocationTargetException e) {
        }
        return false;
    }
}

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
import java.util.Collections;
import java.util.Map;

/**
 * An abstraction for a native function pointer.  An instance of 
 * <code>Function</code> represents a pointer to some native function.  
 * {@link #invoke(Class,Object[],Map)} is the primary means to call
 * the function. <p/>
 * <a name=callflags></a>
 * Function call behavior may be modified by passing one of the following call
 * flags: 
 * <ul>
 * <li>{@link Function#C_CONVENTION} Use C calling convention (default)
 * <li>{@link Function#ALT_CONVENTION} Use alternate calling convention (e.g. stdcall)
 * <li>{@link Function#THROW_LAST_ERROR} Throw a {@link LastErrorException} if
 * the native function sets the system error to a non-zero value (errno or
 * GetLastError).  Setting this flag will cause the system error to be cleared
 * prior to native function invocation.
 * </ul>
 *
 * @author Sheng Liang, originator
 * @author Todd Fast, suitability modifications
 * @author Timothy Wall
 * @see Pointer
 */
public class Function extends Pointer {
    /** Any argument which implements this interface will have the
     * {@link #read} method called immediately after function invocation.
     */
    public interface PostCallRead {
        /** Perform any necessary post-call synchronization.  Normally this
         * just means reading from native memory any changes made by
         * the native function call.
         */
        void read();
    }
    
    /** Maximum number of arguments supported by a JNA function call. */
    public static final int MAX_NARGS = 256;

    /** Standard C calling convention. */
    public static final int C_CONVENTION = 0;
    /** First alternate convention (currently used only for w32 stdcall). */
    public static final int ALT_CONVENTION = 1;

    private static final int MASK_CC = 0x3;
    /** Whether to throw an exception if last error is non-zero after call. */
    public static final int THROW_LAST_ERROR = (1<<2);

    static final Integer INTEGER_TRUE = new Integer(-1);
    static final Integer INTEGER_FALSE = new Integer(0);

    /** 
     * Obtain a <code>Function</code> representing a native 
     * function that follows the standard "C" calling convention.
     * 
     * <p>The allocated instance represents a pointer to the named native 
     * function from the named library, called with the standard "C" calling
     * convention.
     *
     * @param   libraryName
     *                  Library in which to find the native function
     * @param   functionName
     *                  Name of the native function to be linked with
     * @throws {@link UnsatisfiedLinkError} if the library is not found or
     * the given function name is not found within the library.
     */
    public static Function getFunction(String libraryName, String functionName) {
        return NativeLibrary.getInstance(libraryName).getFunction(functionName);
    }
    
    /**
     * Obtain a <code>Function</code> representing a native 
     * function.
     * 
     * <p>The allocated instance represents a pointer to the named native 
     * function from the named library.
     *
     * @param   libraryName
     *                  Library in which to find the function
     * @param   functionName
     *                  Name of the native function to be linked with
     * @param   callFlags
     *                  Function <a href="#callflags">call flags</a>
     *                  
     * @throws {@link UnsatisfiedLinkError} if the library is not found or
     * the given function name is not found within the library.
     */
    public static Function getFunction(String libraryName, String functionName, int callFlags) {
        return NativeLibrary.getInstance(libraryName).getFunction(functionName, callFlags, null);
    }
    
    /**
     * Obtain a <code>Function</code> representing a native 
     * function.
     * 
     * <p>The allocated instance represents a pointer to the named native 
     * function from the named library.
     *
     * @param   libraryName
     *                  Library in which to find the function
     * @param   functionName
     *                  Name of the native function to be linked with
     * @param   callFlags
     *                  Function <a href="#callflags">call flags</a>
     * @param   encoding
     *                  Encoding to use for conversion between Java and native
     *                  strings.
     *                  
     * @throws {@link UnsatisfiedLinkError} if the library is not found or
     * the given function name is not found within the library.
     */
    public static Function getFunction(String libraryName, String functionName, int callFlags, String encoding) {
        return NativeLibrary.getInstance(libraryName).getFunction(functionName, callFlags, encoding);
    }
    
    /**
     * Obtain a <code>Function</code> representing a native 
     * function pointer.  In general, this function should be used by dynamic
     * languages; Java code should allow JNA to bind to a specific Callback
     * interface instead by defining a return type or Structure field type.
     * 
     * <p>The allocated instance represents a pointer to the native 
     * function pointer.
     *
     * @param   p       Native function pointer
     */
    public static Function getFunction(Pointer p) {
        return getFunction(p, 0);
    }

    /**
     * Obtain a <code>Function</code> representing a native 
     * function pointer.  In general, this function should be used by dynamic
     * languages; Java code should allow JNA to bind to a specific Callback
     * interface instead by defining a return type or Structure field type. 
     * 
     * <p>The allocated instance represents a pointer to the native 
     * function pointer.
     *
     * @param   p
     *                  Native function pointer
     * @param   callFlags
     *                  Function <a href="#callflags">call flags</a>
     */
    public static Function getFunction(Pointer p, int callFlags) {
        return new Function(p, callFlags, null);
    }

    // Keep a reference to the NativeLibrary so it does not get garbage
    // collected until the function is
    private NativeLibrary library;
    private final String functionName;
    final String encoding;
    final int callFlags;
    final Map options;

    /** For internal JNA use. */
    static final String OPTION_INVOKING_METHOD = "invoking-method";

    /**
     * Create a new <code>Function</code> that is linked with a native 
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
     * @param  callFlags
     *                 Function <a href="#callflags">call flags</a>
     * @param  encoding
     *                 Encoding for conversion between Java and native strings.
     * @throws {@link UnsatisfiedLinkError} if the given function name is
     * not found within the library.
     */
    Function(NativeLibrary library, String functionName, int callFlags, String encoding) {
        checkCallingConvention(callFlags & MASK_CC);
        if (functionName == null)
            throw new NullPointerException("Function name must not be null");
        this.library = library;
        this.functionName = functionName;
        this.callFlags = callFlags;
        this.options = library.options;
        this.encoding = encoding != null
            ? encoding : Native.getDefaultStringEncoding();
        try {
            this.peer = library.getSymbolAddress(functionName);
        }
        catch(UnsatisfiedLinkError e) {
            throw new UnsatisfiedLinkError("Error looking up function '" 
                                           + functionName + "': " 
                                           + e.getMessage());
        }
    }
    
    /**
     * Create a new <code>Function</code> that is linked with a native 
     * function that follows the given calling convention.
     * 
     * <p>The allocated instance represents a pointer to the given 
     * function address, called with the given calling 
     * convention.
     *
     * @param  functionAddress
     *                 Address of the native function 
     * @param  callFlags
     *                 Function <a href="#callflags">call flags</a>
     * @param  encoding
     *                 Encoding for conversion between Java and native strings.
     */
    Function(Pointer functionAddress, int callFlags, String encoding) {
        checkCallingConvention(callFlags & MASK_CC);
        if (functionAddress == null
            || functionAddress.peer == 0) {
            throw new NullPointerException("Function address may not be null");
        }
        this.functionName = functionAddress.toString();
        this.callFlags = callFlags;
        this.peer = functionAddress.peer;
        this.options = Collections.EMPTY_MAP;
        this.encoding = encoding != null
            ? encoding : Native.getDefaultStringEncoding();
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
        return callFlags & MASK_CC;
    }

    /** Invoke the native function with the given arguments, returning the
     * native result as an Object.
     */
    public Object invoke(Class returnType, Object[] inArgs) {
        return invoke(returnType, inArgs, this.options);
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
        Method invokingMethod = (Method)options.get(OPTION_INVOKING_METHOD);
        Class[] paramTypes = invokingMethod != null ? invokingMethod.getParameterTypes() : null;
        boolean allowObjects = Boolean.TRUE.equals(options.get(Library.OPTION_ALLOW_OBJECTS));
        boolean isVarArgs = args.length > 0 && invokingMethod != null ? isVarArgs(invokingMethod) : false;
        for (int i=0; i < args.length; i++) {
            Class paramType = invokingMethod != null
                ? (isVarArgs && i >= paramTypes.length-1
                   ? paramTypes[paramTypes.length-1].getComponentType()
                   : paramTypes[i])
                : null;
            args[i] = convertArgument(args, i, invokingMethod,
                                      mapper, allowObjects, paramType);
        }
        
        Class nativeType = returnType;
        FromNativeConverter resultConverter = null;
        if (NativeMapped.class.isAssignableFrom(returnType)) {
            NativeMappedConverter tc = NativeMappedConverter.getInstance(returnType);
            resultConverter = tc;
            nativeType = tc.nativeType();
        }
        else if (mapper != null) {
            resultConverter = mapper.getFromNativeConverter(returnType);
            if (resultConverter != null) {
                nativeType = resultConverter.nativeType();
            }
        }

        Object result = invoke(args, nativeType, allowObjects);

        // Convert the result to a custom value/type if appropriate
        if (resultConverter != null) {
            FromNativeContext context;
            
            if (invokingMethod != null) {
                context = new MethodResultContext(returnType, this, inArgs, invokingMethod);
            } else {
                context = new FunctionResultContext(returnType, this, inArgs);
            }
            result = resultConverter.fromNative(result, context);
        }

        // Sync all memory which might have been modified by the native call
        if (inArgs != null) {
            for (int i=0; i < inArgs.length; i++) {
                Object inArg = inArgs[i];
                if (inArg == null)
                    continue;
                if (inArg instanceof Structure) {
                    if (!(inArg instanceof Structure.ByValue)) {
                        ((Structure)inArg).autoRead();
                    }
                }
                else if (args[i] instanceof PostCallRead) {
                    ((PostCallRead)args[i]).read();
                    if (args[i] instanceof PointerArray) {
                        PointerArray array = (PointerArray)args[i];
                        if (Structure.ByReference[].class.isAssignableFrom(inArg.getClass())) {
                            Class type = inArg.getClass().getComponentType();
                            Structure[] ss = (Structure[])inArg;
                            for (int si=0;si < ss.length;si++) {
                                Pointer p = array.getPointer(Pointer.SIZE * si);
                                ss[si] = Structure.updateStructureByReference(type, ss[si], p);
                            }
                        }
                    }
                }
                else if (Structure[].class.isAssignableFrom(inArg.getClass())) {
                    Structure.autoRead((Structure[])inArg);
                }
            }
        }
                        
        return result;
    }

    /** @see NativeLibrary#NativeLibrary(String,String,long,Map) implementation */
    Object invoke(Object[] args, Class returnType, boolean allowObjects) {
        Object result = null;
        if (returnType == null || returnType==void.class || returnType==Void.class) {
            Native.invokeVoid(peer, callFlags, args);
            result = null;
        }
        else if (returnType==boolean.class || returnType==Boolean.class) {
            result = valueOf(Native.invokeInt(peer, callFlags, args) != 0);
        }
        else if (returnType==byte.class || returnType==Byte.class) {
            result = new Byte((byte)Native.invokeInt(peer, callFlags, args));
        }
        else if (returnType==short.class || returnType==Short.class) {
            result = new Short((short)Native.invokeInt(peer, callFlags, args));
        }
        else if (returnType==char.class || returnType==Character.class) {
            result = new Character((char)Native.invokeInt(peer, callFlags, args));
        }
        else if (returnType==int.class || returnType==Integer.class) {
            result = new Integer(Native.invokeInt(peer, callFlags, args));
        }
        else if (returnType==long.class || returnType==Long.class) {
            result = new Long(Native.invokeLong(peer, callFlags, args));
        }
        else if (returnType==float.class || returnType==Float.class) {
            result = new Float(Native.invokeFloat(peer, callFlags, args));
        }
        else if (returnType==double.class || returnType==Double.class) {
            result = new Double(Native.invokeDouble(peer, callFlags, args));
        }
        else if (returnType==String.class) {
            result = invokeString(callFlags, args, false);
        }
        else if (returnType==WString.class) {
            String s = invokeString(callFlags, args, true);
            if (s != null) {
                result = new WString(s);
            }
        }
        else if (Pointer.class.isAssignableFrom(returnType)) {
            return invokePointer(callFlags, args);
        }
        else if (Structure.class.isAssignableFrom(returnType)) {
            if (Structure.ByValue.class.isAssignableFrom(returnType)) {
                Structure s = 
                    Native.invokeStructure(peer, callFlags, args,
                                           Structure.newInstance(returnType));
                s.autoRead();
                result = s;
            }
            else {
                result = invokePointer(callFlags, args);
                if (result != null) {
                    Structure s = Structure.newInstance(returnType, (Pointer)result);
                    s.conditionalAutoRead();
                    result = s;
                }
            }
        }
        else if (Callback.class.isAssignableFrom(returnType)) {
            result = invokePointer(callFlags, args);
            if (result != null) {
                result = CallbackReference.getCallback(returnType, (Pointer)result);
            }
        }
        else if (returnType==String[].class) {
            Pointer p = invokePointer(callFlags, args);
            if (p != null) {
                result = p.getStringArray(0, encoding);
            }
        }
        else if (returnType==WString[].class) {
            Pointer p = invokePointer(callFlags, args);
            if (p != null) {
                String[] arr = p.getWideStringArray(0);
                WString[] warr = new WString[arr.length];
                for (int i=0;i < arr.length;i++) {
                    warr[i] = new WString(arr[i]);
                }
                result = warr;
            }
        }
        else if (returnType==Pointer[].class) {
            Pointer p = invokePointer(callFlags, args);
            if (p != null) {
                result = p.getPointerArray(0);
            }
        }
        else if (allowObjects) {
            result = Native.invokeObject(peer, callFlags, args);
            if (result != null
                && !returnType.isAssignableFrom(result.getClass())) {
                throw new ClassCastException("Return type " + returnType
                                             + " does not match result "
                                             + result.getClass());
            }
        }
        else {
            throw new IllegalArgumentException("Unsupported return type "
                                               + returnType
                                               + " in function " + getName());
        }
        return result;
    }
    
    private Pointer invokePointer(int callFlags, Object[] args) {
        long ptr = Native.invokePointer(peer, callFlags, args);
        return ptr == 0 ? null : new Pointer(ptr);
    }

    private Object convertArgument(Object[] args, int index,
                                   Method invokingMethod, TypeMapper mapper,
                                   boolean allowObjects, Class expectedType) { 
        Object arg = args[index];
        if (arg != null) {
            Class type = arg.getClass();
            ToNativeConverter converter = null;
            if (NativeMapped.class.isAssignableFrom(type)) {
                converter = NativeMappedConverter.getInstance(type);
            }
            else if (mapper != null) {
                converter = mapper.getToNativeConverter(type);
            }
            if (converter != null) {
                ToNativeContext context;
                if (invokingMethod != null) {
                    context = new MethodParameterContext(this, args, index, invokingMethod) ;
                }
                else {
                    context = new FunctionParameterContext(this, args, index);
                }
                arg = converter.toNative(arg, context);
            }
        }
        if (arg == null || isPrimitiveArray(arg.getClass())) { 
            return arg;
        }
        Class argClass = arg.getClass();
        // Convert Structures to native pointers 
        if (arg instanceof Structure) {
            Structure struct = (Structure)arg;
            struct.autoWrite();
            if (struct instanceof Structure.ByValue) {
            	// Double-check against the method signature, if available
                Class ptype = struct.getClass();
            	if (invokingMethod != null) {
                    Class[] ptypes = invokingMethod.getParameterTypes();
                    if (isVarArgs(invokingMethod)) {
                        if (index < ptypes.length-1) {
                            ptype = ptypes[index];
                        }
                        else {
                            Class etype = ptypes[ptypes.length-1].getComponentType();
                            if (etype != Object.class) {
                                ptype = etype;
                            }
                        }
                    }
                    else {
                        ptype = ptypes[index];
                    }
                }
                if (Structure.ByValue.class.isAssignableFrom(ptype)) {
                    return struct;
                }
            }
            return struct.getPointer();
        }
        // Convert Callback to Pointer
        else if (arg instanceof Callback) {
            return CallbackReference.getFunctionPointer((Callback)arg);
        }
        // String arguments are converted to native pointers here rather
        // than in native code so that the values will be valid until
        // this method returns.  
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
            return Boolean.TRUE.equals(arg) ? INTEGER_TRUE : INTEGER_FALSE;
        }
        else if (String[].class == argClass) {
            return new StringArray((String[])arg, encoding);
        }
        else if (WString[].class == argClass) {
            return new StringArray((WString[])arg);
        }
        else if (Pointer[].class == argClass) {
            return new PointerArray((Pointer[])arg);
        }
        else if (NativeMapped[].class.isAssignableFrom(argClass)) {
            return new NativeMappedArray((NativeMapped[])arg);
        }
        else if (Structure[].class.isAssignableFrom(argClass)) {
            // If the signature is Structure[], disallow
            // Structure.ByReference[] and Structure.ByReference elements
            Structure[] ss = (Structure[])arg;
            Class type = argClass.getComponentType();
            boolean byRef = Structure.ByReference.class.isAssignableFrom(type);
            if (expectedType != null) {
                if (!Structure.ByReference[].class.isAssignableFrom(expectedType)) {
                    if (byRef) {
                        throw new IllegalArgumentException("Function " + getName()
                                                           + " declared Structure[] at parameter "
                                                           + index + " but array of "
                                                           + type + " was passed");
                    }
                    for (int i=0;i < ss.length;i++) {
                        if (ss[i] instanceof Structure.ByReference) {
                            throw new IllegalArgumentException("Function " + getName()
                                                               + " declared Structure[] at parameter "
                                                               + index + " but element " + i
                                                               + " is of Structure.ByReference type");
                        }
                    }
                }
            }
            if (byRef) {
                Structure.autoWrite(ss);
                Pointer[] pointers = new Pointer[ss.length + 1];
                for (int i=0;i < ss.length;i++) {
                    pointers[i] = ss[i] != null ? ss[i].getPointer() : null;
                }
                return new PointerArray(pointers);
            }
            else if (ss.length == 0) {
                throw new IllegalArgumentException("Structure array must have non-zero length");
            }
            else if (ss[0] == null) {
                Structure.newInstance(type).toArray(ss);
                return ss[0].getPointer();
            }
            else {
                Structure.autoWrite(ss);
                return ss[0].getPointer();
            }
        }
        else if (argClass.isArray()){
            throw new IllegalArgumentException("Unsupported array argument type: " 
                                               + argClass.getComponentType());
        }
        else if (allowObjects) {
            return arg;
        }
        else if (!Native.isSupportedNativeType(arg.getClass())) {
            throw new IllegalArgumentException("Unsupported argument type "
                                               + arg.getClass().getName()
                                               + " at parameter " + index
                                               + " of function " + getName());
        }
        return arg;
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
     */
    public void invoke(Object[] args) {
        invoke(Void.class, args);
    }


    /**
     * Call the native function being represented by this object
     *
     * @param   callFlags calling convention to be used
     * @param	args
     *			Arguments to pass to the native function
     * @param   wide whether the native string uses <code>wchar_t</code>;
     * if false, <code>char</code> is assumed
     * @return	The value returned by the target native function, as a String
     */
    private String invokeString(int callFlags, Object[] args, boolean wide) {
        Pointer ptr = invokePointer(callFlags, args);
        String s = null;
        if (ptr != null) {
            if (wide) {
                s = ptr.getWideString(0);
            }
            else {
                s = ptr.getString(0, encoding);
            }
        }
        return s;
    }

    /** Provide a human-readable representation of this object. */
    public String toString() {
        if (library != null) {
            return "native function " + functionName + "(" + library.getName()
                + ")@0x" + Long.toHexString(peer);
        }
        return "native function@0x" + Long.toHexString(peer);
    }

    /** Convenience method for
     * {@link #invoke(Class,Object[]) invokeObject(Object.class, args)}.
     */
    public Object invokeObject(Object[] args) {
        return invoke(Object.class, args);
    }

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
    
    /** Two function pointers are equal if they share the same peer address
     * and calling convention.
     */
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null) return false;
        if (o.getClass() == getClass()) {
            Function other = (Function)o;
            return other.callFlags == this.callFlags
                && other.options.equals(this.options)
                && other.peer == this.peer;
        }
        return false;
    }

    /** Provide a unique hash code for {@link Function}s which are
        equivalent.
    */
    public int hashCode() {
        return callFlags + options.hashCode() + super.hashCode();
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

    /**
     * Reference to Method.isVarArgs
     */
    private static Method isVarArgsMethod = getIsVarArgsMethod();
    
    /**
     * If possible returns the Method.isVarArgs method via reflection
     * @return Method.isVarArgs method
     */
    private static Method getIsVarArgsMethod() {
        try {
            return Method.class.getMethod("isVarArgs", new Class[0]);
        } catch (SecurityException e) {
            return null;
        } catch (NoSuchMethodException e) {
            return null;
        }
    }
    
    /** Varargs are only supported on 1.5+. */
    static boolean isVarArgs(Method m) {
        if(isVarArgsMethod != null) {
            try {
                return Boolean.TRUE.equals(isVarArgsMethod.invoke(m, new Object[0]));
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {
            }
        }
        return false;
    }
    
    private static class NativeMappedArray extends Memory implements PostCallRead {
        private final NativeMapped[] original;
        public NativeMappedArray(NativeMapped[] arg) {
            super(Native.getNativeSize(arg.getClass(), arg));
            this.original = arg;
            setValue(0, original, original.getClass());
        }
        public void read() {
            getValue(0, original.getClass(), original);
        }
    }

    private static class PointerArray extends Memory implements PostCallRead {
        private final Pointer[] original;
        public PointerArray(Pointer[] arg) {
            super(Pointer.SIZE * (arg.length+1));
            this.original = arg;
            for (int i=0;i < arg.length;i++) {
                setPointer(i*Pointer.SIZE, arg[i]);
            }
            setPointer(Pointer.SIZE*arg.length, null);
        }
        public void read() {
            read(0, original, 0, original.length);
        }
    }
    
    /** Implementation of Boolean.valueOf for older VMs. */
    static Boolean valueOf(boolean b) {
        return b ? Boolean.TRUE : Boolean.FALSE;
    }
}

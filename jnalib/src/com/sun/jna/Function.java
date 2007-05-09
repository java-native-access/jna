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

    /** Standard C calling convention. */
    public static final int C_CONVENTION = 0;
    /** Alternate convention (currently used only for w32 stdcall). */
    public static final int ALT_CONVENTION = 1;

    private static String[] sys_paths;
    private static String[] usr_paths;
    private static String[] jna_paths;
    private static Map libraries = new HashMap();
    
    private int callingConvention;
    private String libraryName;
    private String functionName;

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
        checkCallingConvention(callingConvention);
        this.libraryName = libraryName;
        this.functionName = functionName;
        this.callingConvention = callingConvention;
        String libPath = getAbsoluteLibraryPath(libraryName);
        peer = find(libPath, functionName);
    }
    
    private synchronized static String getAbsoluteLibraryPath(String libName) {
        String path = (String)libraries.get(libName);
        if (path == null) {
            path = findLibrary(libName);
            libraries.put(libName, path);
        }
        return path;
    }
    
    private static String[] initPaths(String key) {
        return System.getProperty(key, "").split(File.pathSeparator);      
    }

    /** Use standard library search paths to find the library. */
    private static String findLibrary(String libName) {
        synchronized(Function.class) {
            if (sys_paths == null) {
                sys_paths = initPaths("sun.boot.library.path");
                usr_paths = initPaths("java.library.path");
                jna_paths = initPaths("jna.library.path");
            }
        }
        String name = mapLibraryName(libName);
        String path = findPath(sys_paths, name);
        if (path != null)
            return path;
        if ((path = findPath(usr_paths, name)) != null)
            return path;
        if ((path = findPath(jna_paths, name)) != null)
            return path;
        
        return libName;
    }

    private static String mapLibraryName(String libName) {
        String name = System.mapLibraryName(libName);
        if (System.getProperty("os.name").startsWith("Mac")) {
            // On OSX, we want dylib, not jnilib
            if (name.endsWith(".jnilib")) {
                name = name.substring(0, name.lastIndexOf(".jnilib")) + ".dylib";
            }
        }
        return name;
    }
    
    private static String findPath(String[] paths, String name) {
        for (int i=0;i < paths.length;i++) {
            File file = new File(paths[i], name);
            if (file.exists()) {
                return file.getAbsolutePath();
            }
        }
        return null;
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
        return libraryName;
    }

    public String getName() {
        return functionName;
    }


    public int getCallingConvention() {
        return callingConvention;
    }

    /**
     * Call the native function being represented by this object
     *
     * @param	args
     *			Arguments to pass to the native function
     * @return	The value returned by the target function
     */
    public int invokeInt(Object[] args) {
        return invokeInt(callingConvention, args);
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
        return invokeLong(callingConvention, args);
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
        return invokeInt(callingConvention, args) != 0;
    }


    /**
     * Call the native function being represented by this object
     *
     * @param	args
     *			Arguments to pass to the native function
     */
    public void invoke(Object[] args) {
        invokeVoid(callingConvention, args);
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
        return invokeFloat(callingConvention, args);
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
        return invokeDouble(callingConvention, args);
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
        Pointer ptr = invokePointer(callingConvention, args);
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
        return invokePointer(callingConvention, args);
    }


    /**
     * Call the native function being represented by this object
     *
     * @param	args
     *			Arguments to pass to the native function
     * @return	The native pointer returned by the target native function
     */
    public native Pointer invokePointer(int callingConvention, Object[] args);

    /**
     * Find named function in the named library.  Note, this may also be useful
     * to obtain the pointer to a function and pass it back into native code.
     * The library name argument should be the full path to the library file,
     * otherwise the library lookup will use a search algorithm dependent on 
     * the native shared library loading implementation.
     */
    public native long find(String libraryPath, String fname);
    
    /** Create a callback function pointer. */
    static native Pointer createCallback(Library library,
                                         Callback callback, Method method, 
                                         Class[] parameterTypes, 
                                         Class returnType);
    /** Free the given callback function pointer. */
    static native void freeCallback(long ptr);
    
    public String toString() {
        return functionName + "(" + libraryName + ")";
    }
}

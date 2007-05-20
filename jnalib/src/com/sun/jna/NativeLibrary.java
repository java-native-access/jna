/* Copyright (c) 2007 Wayne Meissner, All Rights Reserved
 *
 * This library is free software; you can redistribute it and/or
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
import java.io.FilenameFilter;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.regex.Pattern;

/**
 * Provides management of native library resources.  One instance of this 
 * class corresponds to a single loaded native library.
 * <p>
 * <b>Library Search Paths</b>
 * A search for a given library will scan the following locations:
 * <ol>
 * <li><code>jna.library.path</code> User-customizable path
 * <li><code>jna.platform.library.path</code> Platform-specific paths
 * <li><code>java.library.path</code> Default Java library search path
 * </ol>
 * @author Wayne Meissner, split library loading from Function.java
 */
public class NativeLibrary {

    private long handle;
    private String libraryName;
    private String libraryPath;
    private final Map functions = new HashMap();
    
    private static final Map libraries = new HashMap();
    private static final Map searchPaths = Collections.synchronizedMap(new HashMap());
    private static final List librarySearchPath = new LinkedList();

    // Dummy to force load of the jnidispatch library
    private static final Pointer NULL = Pointer.NULL;

    private NativeLibrary(String libraryName) {
        this.libraryName = getLibraryName(libraryName);
        List searchPath = new LinkedList(librarySearchPath);
        
        //
        // Prepend any custom search paths specifically for this library
        //
        List customPaths = (List) searchPaths.get(libraryName);
        if (customPaths != null) {
            synchronized (customPaths) {
                searchPath.addAll(0, customPaths);
            }
        }
        libraryPath = findLibraryPath(libraryName, searchPath);
        handle = open(libraryPath);
        if (handle == 0) {
            //
            // Failed to load the library normally - try to match libfoo.so.*
            //
            if (Platform.isLinux()) {
                libraryPath = matchLibrary(libraryName, searchPath);
                if (libraryPath != null) {
                    handle = open(libraryPath);
                }
            }
            if (handle == 0) {
                throw new UnsatisfiedLinkError("Unable to load library '" + libraryName + "'");
            }
        }
    }
    
    private String getLibraryName(String libraryName) {
        String simplified = libraryName;
        final String BASE = "---";
        String template = mapLibraryName(BASE);
        int prefix = template.indexOf(BASE);
        if (prefix != 0) {
            simplified = simplified.substring(prefix);
        }
        String end = template.substring(prefix + BASE.length());
        int suffix = simplified.indexOf(end);
        if (suffix != -1) {
            simplified = simplified.substring(0, suffix);
        }
        return simplified;
    }
    
    /**
     * Returns an instance of NativeLibrary for the specified name.  
     * The library is loaded if not already loaded.  If already loaded, the 
     * existing instance is returned.<p>
     * More than one name may map to the same NativeLibrary instance; only
     * a single instance will be provided for any given unique file path.
     * 
     * @param libraryName The library name to load.
     *      This can be short form (e.g. "c"), 
     *      an explicit version (e.g. "libc.so.6"), or
     *      the full path to the library (e.g. "/lib/libc.so.6").
     */
    public static final NativeLibrary getInstance(String libraryName) {
        synchronized (libraries) {
            WeakReference ref = (WeakReference)libraries.get(libraryName);
            NativeLibrary library = ref != null ? (NativeLibrary)ref.get() : null;
            if (library == null) {
                library = new NativeLibrary(libraryName);
                ref = new WeakReference(library);
                libraries.put(library.getName(), ref);
                libraries.put(library.getFile().getAbsolutePath(), ref);
                libraries.put(library.getFile().getName(), ref);
            }
            return library;
        }
    }
    
    /**
     * Add a path to search for the specified library, ahead of any system paths
     * 
     * @param libraryName The name of the library to use the path for
     * @param path The path to use when trying to load the library
     */  
    public static final void addSearchPath(String libraryName, String path) {
        synchronized (searchPaths) {
            List customPaths = (List) searchPaths.get(libraryName);
            if (customPaths == null) {
                customPaths = Collections.synchronizedList(new LinkedList());
                searchPaths.put(libraryName, customPaths);
            }
            
            customPaths.add(path);
        }
    }
    /**
     * Create a new {@link Function} that is linked with a native
     * function that follows the standard "C" calling convention.
     *
     * <p>The allocated instance represents a pointer to the named native
     * function from the library, called with the standard "C" calling
     * convention.
     *
     * @param	functionName
     *			Name of the native function to be linked with
     */
    public Function getFunction(String functionName) {
        return getFunction(functionName, Function.C_CONVENTION);
    }
    
    /**
     * Create a new  @{link Function} that is linked with a native
     * function that follows a given calling convention.
     *
     * <p>The allocated instance represents a pointer to the named native
     * function from the library, called with the named calling convention.
     *
     * @param	functionName
     *			Name of the native function to be linked with
     * @param	callingConvention
     *			Calling convention used by the native function
     */
    public Function getFunction(String functionName, int callingConvention) {
        synchronized (functions) {
            Function function = (Function) functions.get(functionName);
            if (function == null) {
                function = new Function(this, functionName, callingConvention);
                functions.put(functionName, function);
            }
            return function;
        }
    }
    
    /**
     * Used by the Function class to locate a symbol
     */
    long getFunctionAddress(String functionName) {
        long func = findSymbol(handle, functionName);
        if (func == 0) {
            throw new UnsatisfiedLinkError("Cannot locate function '" + functionName + "'");
        }
        return func;
    }
    public String toString() {
        return "Native Library <" + libraryPath + "@" + handle + ">";
    }
    /** Returns the simple name of this library. */
    public String getName() {
        return libraryName;
    }
    /** Returns the file on disk corresponding to this NativeLibrary instacne. 
     */
    public File getFile() {
        return new File(libraryPath);
    }
    /** Close the library when it is no longer referenced. */
    protected void finalize() {
        close(handle);
    }
    
    private static List initPaths(String key) {
        String[] paths = System.getProperty(key, "").split(File.pathSeparator);
        return Arrays.asList(paths);
    }
    
    /** Use standard library search paths to find the library. */
    private static String findLibraryPath(String libName, List searchPath) {
        
        //
        // If a full path to the library was specified, don't search for it
        //
        if (new File(libName).isAbsolute()) {
            return libName;
        }
        
        //
        // Get the system name for the library (e.g. libfoo.so)
        //
        String name = mapLibraryName(libName);
        
        // Search in the JNA paths for it
        for (Iterator it = searchPath.iterator(); it.hasNext(); ) {
            File file = new File(new File((String) it.next()), name);
            if (file.exists()) {
                return file.getAbsolutePath();
            }
        }
        //
        // Default to returning the mapped library name and letting the system
        // search for it
        //
        return name;
    }
    private static String mapLibraryName(String libName) {
        
        if (Platform.isMac()) {
            if (libName.matches("lib.*\\.(dylib|jnilib)$")) {
                return libName;
            }
            String name = System.mapLibraryName(libName);
            // On MacOSX, System.mapLibraryName() returns the .jnilib extension
            // (the suffix for JNI libraries); ordinarily shared libraries have 
            // a .dylib suffix
            if (name.endsWith(".jnilib")) {
                return name.substring(0, name.lastIndexOf(".jnilib")) + ".dylib";
            }
            return name;
        } else if (Platform.isLinux()) {
            //
            // A specific version was requested - use as is for search
            //
            if (libName.matches("lib.*\\.so\\.[0-9]+$")) {
                return libName;
            }
        } 
        
        return System.mapLibraryName(libName);
    }
    
    /**
     * matchLibrary is very Linux specific.  It is here to deal with the case
     * where there is no /usr/lib/libc.so, or it is not a valid symlink to
     * /lib/libc.so.6.
     */
    private static String matchLibrary(final String libName, List searchPath) {
        
        FilenameFilter filter = new FilenameFilter() {
            Pattern p = Pattern.compile("lib" + libName + "\\.so\\.[0-9]+$");
            public boolean accept(File dir, String name) {
                return p.matcher(name).matches();
            }
        };
        
        List matches = new LinkedList();
        for (Iterator it = searchPath.iterator(); it.hasNext(); ) {
            File[] files = new File((String) it.next()).listFiles(filter);
            if (files != null && files.length > 0) {
                matches.addAll(Arrays.asList(files));
            }
        }
        
        //
        // Search through the results and return the highest numbered version
        // i.e. libc.so.6 is preferred over libc.so.5
        //
        int version = 0;
        String bestMatch = null;
        for (Iterator it = matches.iterator(); it.hasNext(); ) {
            String path = ((File) it.next()).getAbsolutePath();
            String num = path.substring(path.lastIndexOf('.') + 1);
            try {
                if (Integer.parseInt(num) >= version) {
                    bestMatch = path;
                }
            } catch (NumberFormatException e) {} // Just skip if not a number
        }
        return bestMatch;
    }
    
    private static native long open(String name);
    private static native void close(long handle);
    private static native long findSymbol(long handle, String name);
    static {
        
        librarySearchPath.addAll(initPaths("jna.library.path"));
        if (System.getProperty("jna.platform.library.path") == null) {
            if (new File("/lib").exists() 
                || new File("/usr/lib").exists()
                || new File("/lib64").exists()
                || new File("/usr/lib64").exists()) {
                String platformPath = "/usr/lib:/lib";
                if (Pointer.SIZE == 8) {
                    platformPath = "/usr/lib64:/lib64";
                }
                System.setProperty("jna.platform.library.path", platformPath);
            }
        }
        librarySearchPath.addAll(initPaths("jna.platform.library.path"));
        librarySearchPath.addAll(initPaths("java.library.path"));
    }
}

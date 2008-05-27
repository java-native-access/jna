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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Provides management of native library resources.  One instance of this
 * class corresponds to a single loaded native library.
 * <p>
 * <b>Library Search Paths</b>
 * A search for a given library will scan the following locations:
 * <ol>
 * <li><code>jna.library.path</code> User-customizable path
 * <li><code>jna.platform.library.path</code> Platform-specific paths
 * </ol>
 * @author Wayne Meissner, split library loading from Function.java
 */
public class NativeLibrary {

    private long handle;
    private final String libraryName;
    private final String libraryPath;
    private final Map functions = new HashMap();

    private static final Map libraries = new HashMap();
    private static final Map searchPaths = Collections.synchronizedMap(new HashMap());
    private static final List librarySearchPath = new LinkedList();

    static {
        // Force initialization of native library
        if (Native.POINTER_SIZE == 0)
            throw new Error("Native library not initialized");
    }

    private NativeLibrary(String libraryName, String libraryPath, long handle) {
        this.libraryName = getLibraryName(libraryName);
        this.libraryPath = libraryPath;
        this.handle = handle;
        // Special workaround for w32 kernel32.GetLastError
        // Short-circuit the function to use built-in GetLastError access
        if (Platform.isWindows() && "kernel32".equals(this.libraryName.toLowerCase())) {
            synchronized(functions) {
                Function f = new Function(this, "GetLastError", Function.ALT_CONVENTION) {
                    Object invoke(Object[] args, Class returnType) {
                        return new Integer(Native.getLastError());
                    }
                };
                functions.put("GetLastError", f);
            }
        }
    }

    private static NativeLibrary loadLibrary(String libraryName) {
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
        String libraryPath = findLibraryPath(libraryName, searchPath);
        long handle = 0;
        try {
            handle = open(libraryPath);
        }
        catch(UnsatisfiedLinkError e) {
            if (Platform.isLinux()) {
                //
                // Failed to load the library normally - try to match libfoo.so.*
                //
                libraryPath = matchLibrary(libraryName, searchPath);
                if (libraryPath != null) {
                    try { handle = open(libraryPath); }
                    catch(UnsatisfiedLinkError e2) { e = e2; }
                }
            }
            // Search framework libraries on OS X
            else if (Platform.isMac() && !libraryName.endsWith(".dylib")) {
                libraryPath = "/System/Library/Frameworks/" + libraryName
                    + ".framework/" + libraryName;
                if (new File(libraryPath).exists()) {
                    try { handle = open(libraryPath); }
                    catch(UnsatisfiedLinkError e2) { e = e2; }
                }
            }
            if (handle == 0) {
                throw new UnsatisfiedLinkError("Unable to load library '" + libraryName + "': "
                                               + e.getMessage());
            }
        }
        return new NativeLibrary(libraryName, libraryPath, handle);
    }

    private String getLibraryName(String libraryName) {
        String simplified = libraryName;
        final String BASE = "---";
        String template = mapLibraryName(BASE);
        int prefixEnd = template.indexOf(BASE);
        if (prefixEnd > 0 && simplified.startsWith(template.substring(0, prefixEnd))) {
            simplified = simplified.substring(prefixEnd);
        }
        String suffix = template.substring(prefixEnd + BASE.length());
        int suffixStart = simplified.indexOf(suffix);
        if (suffixStart != -1) {
            simplified = simplified.substring(0, suffixStart);
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
        if (libraryName == null)
            throw new NullPointerException("Library name may not be null");

        synchronized (libraries) {
            WeakReference ref = (WeakReference)libraries.get(libraryName);
            NativeLibrary library = ref != null ? (NativeLibrary)ref.get() : null;
            if (library == null) {
                library = loadLibrary(libraryName);
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
     * @throws   UnsatisfiedLinkError if the function is not found
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
     * @throws   UnsatisfiedLinkError if the function is not found
     */
    public Function getFunction(String functionName, int callingConvention) {
        if (functionName == null)
            throw new NullPointerException("Function name may not be null");
        synchronized (functions) {
            Function function = (Function) functions.get(functionName);
            if (function == null) {
                function = new Function(this, functionName, callingConvention);
                functions.put(functionName, function);
            }
            return function;
        }
    }

    /** Look up the given global variable within this library.
     * @param symbolName
     * @return Pointer representing the global variable address
     * @throws UnsatisfiedLinkError if the symbol is not found
     */
    public Pointer getGlobalVariableAddress(String symbolName) {
        try {
            return new Pointer(getSymbolAddress(symbolName));
        }
        catch(UnsatisfiedLinkError e) {
            throw new UnsatisfiedLinkError("Error looking up '"
                                           + symbolName + "': "
                                           + e.getMessage());
        }
    }

    /**
     * Used by the Function class to locate a symbol
     * @throws UnsatisfiedLinkError if the symbol can't be found
     */
    long getSymbolAddress(String name) {
        if (handle == 0) {
            throw new UnsatisfiedLinkError("Library has been unloaded");
        }
        return findSymbol(handle, name);
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
        dispose();
    }

    public void dispose() {
        synchronized(libraries) {
            libraries.remove(getName());
            libraries.remove(getFile().getAbsolutePath());
            libraries.remove(getFile().getName());
        }
        synchronized(this) {
            if (handle != 0) {
                close(handle);
                handle = 0;
            }
        }
    }

    private static List initPaths(String key) {
        String value = System.getProperty(key, "");
        if ("".equals(value)) {
            return Collections.EMPTY_LIST;
        }
        StringTokenizer st = new StringTokenizer(value, File.pathSeparator);
        List list = new ArrayList();
        while (st.hasMoreTokens()) {
            String path = st.nextToken();
            if (!"".equals(path)) {
                list.add(path);
            }
        }
        return list;
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
            if (libName.startsWith("lib")
                && (libName.endsWith(".dylib")
                    || libName.endsWith(".jnilib"))) {
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
        }
        else if (Platform.isLinux()) {
            if (isVersionedName(libName)) {
                // A specific version was requested - use as is for search
                return libName;
            }
        }

        return System.mapLibraryName(libName);
    }

    private static boolean isVersionedName(String name) {
        if (name.startsWith("lib")) {
            int so = name.lastIndexOf(".so.");
            if (so != -1 && so + 4 < name.length()) {
                for (int i=so+4;i < name.length();i++) {
                    char ch = name.charAt(i);
                    if (!Character.isDigit(ch) && ch != '.') {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * matchLibrary() is very Linux specific.  It is here to deal with the case
     * where /usr/lib/libc.so does not exist, or it is not a valid symlink to
     * a versioned file (e.g. /lib/libc.so.6).
     */
    static String matchLibrary(final String libName, List searchPath) {
    	File lib = new File(libName);
        if (lib.isAbsolute()) {
            searchPath = Arrays.asList(new String[] { lib.getParent() });
        }
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String filename) {
                return (filename.startsWith("lib" + libName + ".so")
                        || (filename.startsWith(libName + ".so")
                            && libName.startsWith("lib")))
                    && isVersionedName(filename);
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
        double bestVersion = -1;
        String bestMatch = null;
        for (Iterator it = matches.iterator(); it.hasNext(); ) {
            String path = ((File) it.next()).getAbsolutePath();
            String ver = path.substring(path.lastIndexOf(".so.") + 4);
            double version = parseVersion(ver);
            if (version >= bestVersion) {
                bestVersion = version;
                bestMatch = path;
            }
        }
        return bestMatch;
    }

    static double parseVersion(String ver) {
    	double v = 0;
    	double divisor = 1;
    	int dot = ver.indexOf(".");
    	while (ver != null) {
    		String num;
    		if (dot != -1) {
    			num = ver.substring(0, dot);
    			ver = ver.substring(dot + 1);
        		dot = ver.indexOf(".");
    		}
    		else {
    			num = ver;
    			ver = null;
    		}
    		try {
    			v += Integer.parseInt(num) / divisor;
    		}
    		catch(NumberFormatException e) {
    			return 0;
    		}
    		divisor *= 100;
    	}

    	return v;
    }

    private static native long open(String name);
    private static native void close(long handle);
    private static native long findSymbol(long handle, String name);
    static {

        librarySearchPath.addAll(initPaths("jna.library.path"));
        String webstartPath = Native.getWebStartLibraryPath("jnidispatch");
        if (webstartPath != null) {
            librarySearchPath.add(webstartPath);
        }
        if (System.getProperty("jna.platform.library.path") == null
            && !Platform.isWindows()) {
            // Add default path lookups for unix-like systems
            String platformPath = "";
            String sep = "";
            String archPath = "";

            //
            // Search first for an arch specific path if one exists, but always
            // include the generic paths if they exist.
            // NOTES (wmeissner):
            // Some older linux amd64 distros did not have /usr/lib64, and 32bit
            // distros only have /usr/lib.  FreeBSD also only has /usr/lib by
            // default, with /usr/lib32 for 32bit compat.
            // Solaris seems to have both, but defaults to 32bit userland even on
            // 64bit machines, so we have to explicitly search the 64bit one when
            // running a 64bit JVM.
            //
            if (Platform.isLinux() || Platform.isSolaris() || Platform.isFreeBSD()) {
                // Linux & FreeBSD use /usr/lib32, solaris uses /usr/lib/32
                archPath = (Platform.isSolaris() ? "/" : "") + Pointer.SIZE * 8;
            }
            String[] paths = {
                "/usr/lib" + archPath,
                "/lib" + archPath,
                "/usr/lib",
                "/lib",
            };
            for (int i=0;i < paths.length;i++) {
                File dir = new File(paths[i]);
                if (dir.exists() && dir.isDirectory()) {
                    platformPath += sep + paths[i];
                    sep = File.pathSeparator;
                }
            }
            if (!"".equals(platformPath)) {
                System.setProperty("jna.platform.library.path", platformPath);
            }
        }
        librarySearchPath.addAll(initPaths("jna.platform.library.path"));
    }
}

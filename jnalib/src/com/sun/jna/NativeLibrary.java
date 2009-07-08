/* Copyright (c) 2007 Wayne Meissner, All Rights Reserved
 * Copyright (c) 2007, 2008, 2009 Timothy Wall, All Rights Reserved
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
import java.lang.reflect.Method;
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
 * class corresponds to a single loaded native library.  May also be used
 * to map to the current process (see {@link NativeLibrary#getProcess()}).
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
    final int callFlags;
    final Map options;

    private static final Map libraries = new HashMap();
    private static final Map searchPaths = Collections.synchronizedMap(new HashMap());
    private static final List librarySearchPath = new LinkedList();
    private static final List userSearchPath = new LinkedList();

    static {
        // Force initialization of native library
        if (Native.POINTER_SIZE == 0)
            throw new Error("Native library not initialized");
    }

    private static String functionKey(String name, int flags) {
        return name + "|" + flags;
    }

    private NativeLibrary(String libraryName, String libraryPath, long handle, Map options) {
        this.libraryName = getLibraryName(libraryName);
        this.libraryPath = libraryPath;
        this.handle = handle;
        Object option = options.get(Library.OPTION_CALLING_CONVENTION);
        int callingConvention = option instanceof Integer
            ? ((Integer)option).intValue() : Function.C_CONVENTION;
        this.callFlags = callingConvention;
        this.options = options;

        // Special workaround for w32 kernel32.GetLastError
        // Short-circuit the function to use built-in GetLastError access
        if (Platform.isWindows() && "kernel32".equals(this.libraryName.toLowerCase())) {
            synchronized(functions) {
                Function f = new Function(this, "GetLastError", Function.ALT_CONVENTION) {
                    Object invoke(Object[] args, Class returnType, boolean b) {
                        return new Integer(Native.getLastError());
                    }
                };
                functions.put(functionKey("GetLastError", callFlags), f);
            }
        }
    }

    private static NativeLibrary loadLibrary(String libraryName, Map options) {
        List searchPath = new LinkedList();

        // Append web start path, if available.  Note that this does not
        // attempt any library name variations
        String webstartPath = Native.getWebStartLibraryPath(libraryName);
        if (webstartPath != null) {
            searchPath.add(webstartPath);
        }

        //
        // Prepend any custom search paths specifically for this library
        //
        List customPaths = (List) searchPaths.get(libraryName);
        if (customPaths != null) {
            synchronized (customPaths) {
                searchPath.addAll(0, customPaths);
            }
        }
        
        searchPath.addAll(userSearchPath);
        String libraryPath = findLibraryPath(libraryName, searchPath);
        long handle = 0;
        //
        // Only search user specified paths first.  This will also fall back
        // to dlopen/LoadLibrary() since findLibraryPath returns the mapped
        // name if it cannot find the library.
        //
        try {
            handle = open(libraryPath);
        }
        catch(UnsatisfiedLinkError e) {
            // Add the system paths back for all fallback searching
            searchPath.addAll(librarySearchPath);
        }
        try {
            if (handle == 0) {
                libraryPath = findLibraryPath(libraryName, searchPath);
                handle = open(libraryPath);
            }
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
            // Try the same library with a "lib" prefix
            else if (Platform.isWindows()) {
                libraryPath = findLibraryPath("lib" + libraryName, searchPath);
                try { handle = open(libraryPath); }
                catch(UnsatisfiedLinkError e2) { e = e2; }
            }
            if (handle == 0) {
                throw new UnsatisfiedLinkError("Unable to load library '" + libraryName + "': "
                                               + e.getMessage());
            }
        }
        return new NativeLibrary(libraryName, libraryPath, handle, options);
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
        return getInstance(libraryName, Collections.EMPTY_MAP);
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
     * @param options native library options for the given library (see {@link
     * Library}).
     */
    public static final NativeLibrary getInstance(String libraryName, Map options) {
        options = new HashMap(options);
        if (options.get(Library.OPTION_CALLING_CONVENTION) == null) {
            options.put(Library.OPTION_CALLING_CONVENTION, new Integer(Function.C_CONVENTION));
        }

        synchronized (libraries) {
            WeakReference ref = (WeakReference)libraries.get(libraryName + options);
            NativeLibrary library = ref != null ? (NativeLibrary)ref.get() : null;

            if (library == null) {
                if (libraryName == null) {
                    library = new NativeLibrary("<process>", null, open(null), options);
                }
                else {
                    library = loadLibrary(libraryName, options);
                }
                ref = new WeakReference(library);
                libraries.put(library.getName() + options, ref);
                File file = library.getFile();
                if (file != null) {
                    libraries.put(file.getAbsolutePath() + options, ref);
                    libraries.put(file.getName() + options, ref);
                }
            }
            return library;
        }
    }

    /**
     * Returns an instance of NativeLibrary which refers to the current
     * process.  This is useful for accessing functions which were already
     * mapped by some other mechanism, without having to reference or even
     * know the exact name of the native library.
     */
    public static synchronized final NativeLibrary getProcess() {
        return getInstance(null);
    }

    /**
     * Returns an instance of NativeLibrary which refers to the current
     * process.  This is useful for accessing functions which were already
     * mapped by some other mechanism, without having to reference or even
     * know the exact name of the native library.
     */
    public static synchronized final NativeLibrary getProcess(Map options) {
        return getInstance(null, options);
    }

    /**
     * Add a path to search for the specified library, ahead of any system
     * paths.
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
     * function that follows the NativeLibrary's calling convention.
     *
     * <p>The allocated instance represents a pointer to the named native
     * function from the library.
     *
     * @param	functionName
     *			Name of the native function to be linked with
     * @throws   UnsatisfiedLinkError if the function is not found
     */
    public Function getFunction(String functionName) {
        return getFunction(functionName, callFlags);
    }

    /**
     * Create a new {@link Function} that is linked with a native
     * function that follows the NativeLibrary's calling convention.
     *
     * <p>The allocated instance represents a pointer to the named native
     * function from the library.
     *
     * @param	name
     *			Name of the native function to be linked with
     * @param	method
     *			Method to which the native function is to be mapped
     * @throws   UnsatisfiedLinkError if the function is not found
     */
    Function getFunction(String name, Method method) {
        int flags = this.callFlags;
        Class[] etypes = method.getExceptionTypes();
        for (int i=0;i < etypes.length;i++) {
            if (LastErrorException.class.isAssignableFrom(etypes[i])) {
                flags |= Function.THROW_LAST_ERROR;
            }
        }
        return getFunction(name, flags);
    }

    /**
     * Create a new  @{link Function} that is linked with a native
     * function that follows a given calling flags.
     *
     * @param	functionName
     *			Name of the native function to be linked with
     * @param	callFlags
     *			Flags affecting the function invocation
     * @throws   UnsatisfiedLinkError if the function is not found
     */
    public Function getFunction(String functionName, int callFlags) {
        if (functionName == null)
            throw new NullPointerException("Function name may not be null");
        synchronized (functions) {
            String key = functionKey(functionName, callFlags);
            Function function = (Function) functions.get(key);
            if (function == null) {
                function = new Function(this, functionName, callFlags);
                functions.put(key, function);
            }
            return function;
        }
    }

    /** Returns this native library instance's options. */
    public Map getOptions() {
        return options;
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
    /** 
     * Returns the file on disk corresponding to this NativeLibrary instance.
     * If this NativeLibrary represents the current process, this function will return null. 
     */
    public File getFile() {
        if (libraryPath == null)
            return null;
        return new File(libraryPath);
    }
    /** Close the library when it is no longer referenced. */
    protected void finalize() {
        dispose();
    }

    public void dispose() {
        synchronized(libraries) {
            libraries.remove(getName() + options);
            File file = getFile();
            if (file != null) {
                libraries.remove(file.getAbsolutePath() + options);
                libraries.remove(file.getName() + options);
            }
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
            if (isVersionedName(libName) || libName.endsWith(".so")) {
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
            if (version > bestVersion) {
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
        userSearchPath.addAll(initPaths("jna.library.path"));
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
            // Linux 64-bit does not use /lib or /usr/lib
            if (Platform.isLinux() && Pointer.SIZE == 8) {
                paths = new String[] {
                    "/usr/lib" + archPath,
                    "/lib" + archPath,
                };
            }
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

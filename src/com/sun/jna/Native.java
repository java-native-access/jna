/* Copyright (c) 2007-2013 Timothy Wall, All Rights Reserved
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

import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Window;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.WeakHashMap;

import com.sun.jna.Callback.UncaughtExceptionHandler;
import com.sun.jna.Structure.FFIType;

/** Provides generation of invocation plumbing for a defined native
 * library interface.  Also provides various utilities for native operations.
 * <p>
 * {@link #getTypeMapper} and {@link #getStructureAlignment} are provided
 * to avoid having to explicitly pass these parameters to {@link Structure}s,
 * which would require every {@link Structure} which requires custom mapping
 * or alignment to define a constructor and pass parameters to the superclass.
 * To avoid lots of boilerplate, the base {@link Structure} constructor
 * figures out these properties based on its enclosing interface.<p>
 * <a name=library_loading></a>
 * <h2>Library Loading</h2>
 * When JNA classes are loaded, the native shared library (jnidispatch) is
 * loaded as well.  An attempt is made to load it from the any paths defined
 * in <code>jna.boot.library.path</code> (if defined), then the system library
 * path using {@link System#loadLibrary}, unless <code>jna.nosys=true</code>.
 * If not found, the appropriate library will be extracted from the class path
 * (into a temporary directory if found within a jar file) and loaded from
 * there, unless <code>jna.noclasspath=true</code>.  If your system has
 * additional security constraints regarding execution or load of files
 * (SELinux, for example), you should  probably install the native library in
 * an accessible location and configure  your system accordingly, rather than
 * relying on JNA to extract the library  from its own jar file.<p/>
 * To avoid the automatic unpacking (in situations where you want to force a
 * failure if the JNA native library is not properly installed on the system),
 * set the system property <code>jna.nounpack=true</code>.
 * <p/>
 * NOTE: all native functions are provided within this class to ensure that
 * all other JNA-provided classes and objects are GC'd and/or
 * finalized/disposed before this class is disposed and/or removed from
 * memory (most notably Memory and any other class which by default frees its
 * resources in a finalizer).<p/>
 * <a name=native_library_loading</a>
 * <h2>Native Library Loading</h2>
 * Native libraries loaded via {@link #loadLibrary(Class)} may be found in
 * <a href="NativeLibrary.html#library_search_paths">several locations</a>.
 * @see Library
 * @author Todd Fast, todd.fast@sun.com
 * @author twall@users.sf.net
 */
public final class Native implements Version {

    public static final String DEFAULT_ENCODING = "utf8";
    static final boolean DEBUG_LOAD = Boolean.getBoolean("jna.debug_load");
    static final boolean DEBUG_JNA_LOAD = Boolean.getBoolean("jna.debug_load.jna");

    // Used by tests, do not remove
    static String jnidispatchPath = null;
    private static Map options = new WeakHashMap();
    private static Map libraries = new WeakHashMap();
    private static final UncaughtExceptionHandler DEFAULT_HANDLER =
        new UncaughtExceptionHandler() {
            public void uncaughtException(Callback c, Throwable e) {
                System.err.println("JNA: Callback " + c + " threw the following exception:");
                e.printStackTrace();
            }
        };
    private static UncaughtExceptionHandler callbackExceptionHandler = DEFAULT_HANDLER;

    /** The size of a native pointer (<code>void*</code>) on the current
     * platform, in bytes.
     */
    public static final int POINTER_SIZE;
    /** Size of a native <code>long</code> type, in bytes. */
    public static final int LONG_SIZE;
    /** Size of a native <code>wchar_t</code> type, in bytes. */
    public static final int WCHAR_SIZE;
    /** Size of a native <code>size_t</code> type, in bytes. */
    public static final int SIZE_T_SIZE;

    private static final int TYPE_VOIDP = 0;
    private static final int TYPE_LONG = 1;
    private static final int TYPE_WCHAR_T = 2;
    private static final int TYPE_SIZE_T = 3;

    static final int MAX_ALIGNMENT;
    static final int MAX_PADDING;

    static {
        loadNativeDispatchLibrary();
        POINTER_SIZE = sizeof(TYPE_VOIDP);
        LONG_SIZE = sizeof(TYPE_LONG);
        WCHAR_SIZE = sizeof(TYPE_WCHAR_T);
        SIZE_T_SIZE = sizeof(TYPE_SIZE_T);

        // Perform initialization of other JNA classes until *after*
        // initializing the above final fields
        initIDs();
        if (Boolean.getBoolean("jna.protected")) {
            setProtected(true);
        }
        String version = getNativeVersion();
        if (!VERSION_NATIVE.equals(version)) {
            String LS = System.getProperty("line.separator");
            throw new Error(LS + LS
                            + "There is an incompatible JNA native library installed on this system" + LS
                            + (jnidispatchPath != null
                               ? "(at " + jnidispatchPath + ")" : System.getProperty("java.library.path"))
                            + "." + LS
                            + "To resolve this issue you may do one of the following:" + LS
                            + " - remove or uninstall the offending library" + LS
                            + " - set the system property jna.nosys=true" + LS
                            + " - set jna.boot.library.path to include the path to the version of the " + LS
                            + "   jnidispatch library included with the JNA jar file you are using" + LS);
        }
	MAX_ALIGNMENT = Platform.isSPARC() || Platform.isWindows()
            || (Platform.isLinux() && (Platform.isARM() || Platform.isPPC()))
            || Platform.isAIX()
            || Platform.isAndroid()
            ? 8 : LONG_SIZE;
        MAX_PADDING = (Platform.isMac() && Platform.isPPC()) ? 8 : MAX_ALIGNMENT;
    }

    /** Force a dispose when this class is GC'd. */
    private static final Object finalizer = new Object() {
        protected void finalize() {
            dispose();
        }
    };

    /** Properly dispose of JNA functionality. */
    private static void dispose() {
        NativeLibrary.disposeAll();
        jnidispatchPath = null;
    }

    /** Remove any automatically unpacked native library.

        This will fail on windows, which disallows removal of any file that is
        still in use, so an alternative is required in that case.  Mark
        the file that could not be deleted, and attempt to delete any
        temporaries on next startup.

        Do NOT force the class loader to unload the native library, since
        that introduces issues with cleaning up any extant JNA bits
        (e.g. Memory) which may still need use of the library before shutdown.
     */
    static boolean deleteLibrary(File lib) {
        if (lib.delete()) {
            return true;
        }

        // Couldn't delete it, mark for later deletion
        markTemporaryFile(lib);

        return false;
    }

    private Native() { }

    private static native void initIDs();

    /** Set whether native memory accesses are protected from invalid
     * accesses.  This should only be set true when testing or debugging,
     * and should not be considered reliable or robust for applications
     * where JNA native calls are occurring on multiple threads.
     * Protected mode will be automatically set if the
     * system property <code>jna.protected</code> has a value of "true"
     * when the JNA library is first loaded.<p>
     * If not supported by the underlying platform, this setting will
     * have no effect.<p>
     * NOTE: On platforms which support signals (non-Windows), JNA uses
     * signals to trap errors.  This may interfere with the JVM's own use of
     * signals.  When protected mode is enabled, you should make use of the
     * jsig library, if available (see <a href="http://download.oracle.com/javase/6/docs/technotes/guides/vm/signal-chaining.html">Signal Chaining</a>).
     * In short, set the environment variable <code>LD_PRELOAD</code> to the
     * path to <code>libjsig.so</code> in your JRE lib directory
     * (usually ${java.home}/lib/${os.arch}/libjsig.so) before launching your
     * Java application.
     */
    public static synchronized native void setProtected(boolean enable);

    /** Returns whether protection is enabled.  Check the result of this method
     * after calling {@link #setProtected setProtected(true)} to determine
     * if this platform supports protecting memory accesses.
     */
    public static synchronized native boolean isProtected();

    /** This method is obsolete.  The last error value is always preserved.
     * <p/>
     * NOTE: The preferred method of obtaining the last error result is
     * to declare your mapped method to throw {@link LastErrorException}.
     *
     * @deprecated Last error is always preserved
     */
    public static void setPreserveLastError(boolean enable) { }

    /** Indicates whether the system last error result is preserved
     * after every invocation.  Always returns <code>true</code><p>
     * NOTE: The preferred method of obtaining the last error result is
     * to declare your mapped method to throw {@link LastErrorException}.
     *
     * @deprecated Last error is always preserved
     */
    public static boolean getPreserveLastError() { return true; }

    /** Utility method to get the native window ID for a Java {@link Window}
     * as a <code>long</code> value.
     * This method is primarily for X11-based systems, which use an opaque
     * <code>XID</code> (usually <code>long int</code>) to identify windows.
     * @throws HeadlessException if the current VM is running headless
     */
    public static long getWindowID(Window w) throws HeadlessException {
        return AWT.getWindowID(w);
    }

    /** Utility method to get the native window ID for a heavyweight Java
     * {@link Component} as a <code>long</code> value.
     * This method is primarily for X11-based systems, which use an opaque
     * <code>XID</code> (usually <code>long int</code>) to identify windows.
     * @throws HeadlessException if the current VM is running headless
     */
    public static long getComponentID(Component c) throws HeadlessException {
        return AWT.getComponentID(c);
    }

    /** Utility method to get the native window pointer for a Java
     * {@link Window} as a {@link Pointer} value.  This method is primarily for
     * w32, which uses the <code>HANDLE</code> type (actually
     * <code>void *</code>) to identify windows.
     * @throws HeadlessException if the current VM is running headless
     */
    public static Pointer getWindowPointer(Window w) throws HeadlessException {
        return new Pointer(AWT.getWindowID(w));
    }

    /** Utility method to get the native window pointer for a heavyweight Java
     * {@link Component} as a {@link Pointer} value.  This method is primarily
     * for w32, which uses the <code>HWND</code> type (actually
     * <code>void *</code>) to identify windows.
     * @throws HeadlessException if the current VM is running headless
     */
    public static Pointer getComponentPointer(Component c) throws HeadlessException {
        return new Pointer(AWT.getComponentID(c));
    }

    static native long getWindowHandle0(Component c);

    /** Convert a direct {@link Buffer} into a {@link Pointer}.
     * @throws IllegalArgumentException if the buffer is not direct.
     */
    public static Pointer getDirectBufferPointer(Buffer b) {
        long peer = _getDirectBufferPointer(b);
        return peer == 0 ? null : new Pointer(peer);
    }

    private static native long _getDirectBufferPointer(Buffer b);

    /** Obtain a Java String from the given native byte array.  If there is
     * no NUL terminator, the String will comprise the entire array.  The
     * encoding is obtained from {@link #getDefaultStringEncoding()}.
     */
    public static String toString(byte[] buf) {
        return toString(buf, getDefaultStringEncoding());
    }

    /** Obtain a Java String from the given native byte array, using the given
     * encoding.  If there is no NUL terminator, the String will comprise the
     * entire array.  If the <code>encoding</code> parameter is null,
     * the platform default encoding will be used.
     */
    public static String toString(byte[] buf, String encoding) {
        String s = null;
        if (encoding != null) {
            try {
                s = new String(buf, encoding);
            }
            catch(UnsupportedEncodingException e) {
                System.err.println("JNA Warning: Encoding '"
                                   + encoding + "' is unsupported");
            }
        }
        if (s == null) {
            System.err.println("JNA Warning: Decoding with fallback " + System.getProperty("file.encoding"));
            s = new String(buf);
        }
        int term = s.indexOf(0);
        if (term != -1) {
            s = s.substring(0, term);
        }
        return s;
    }

    /** Obtain a Java String from the given native wchar_t array.  If there is
     * no NUL terminator, the String will comprise the entire array.  
     */
    public static String toString(char[] buf) {
        String s = new String(buf);
        int term = s.indexOf(0);
        if (term != -1) {
            s = s.substring(0, term);
        }
        return s;
    }

    /**
     * Converts a null-terminated sequence of null-terminated strings into an array of Java {@link String}s.
     * 
     * @param buffer
     *            Contains a null-terminated sequence of null-terminated strings.
     * @return {@code buffer}'s content converted to Java {@link String}s.
     */
    public static final String[] toStrings(final char[] buffer) {
        return new String(buffer).split("\0");
    }

    /** Map a library interface to the current process, providing
     * the explicit interface class.
     * Native libraries loaded via this method may be found in
     * <a href="NativeLibrary.html#library_search_paths">several locations</a>.
     * @param interfaceClass
     * @return an instance of the requested interface, mapped to the current
     * process. 
     * @throws UnsatisfiedLinkError if the library cannot be found or
     * dependent libraries are missing.
     */
    public static Object loadLibrary(Class interfaceClass) {
        return loadLibrary(null, interfaceClass);
    }

    /** Map a library interface to the current process, providing
     * the explicit interface class.  Any options provided for the library are
     * cached and associated with the library and any of its defined
     * structures and/or functions.
     * Native libraries loaded via this method may be found in
     * <a href="NativeLibrary.html#library_search_paths">several locations</a>.
     * @param interfaceClass
     * @param options Map of library options
     * @return an instance of the requested interface, mapped to the current
     * process. 
     * @throws UnsatisfiedLinkError if the library cannot be found or
     * dependent libraries are missing.
     */
    public static Object loadLibrary(Class interfaceClass, Map options) {
        return loadLibrary(null, interfaceClass, options);
    }

    /** Map a library interface to the given shared library, providing
     * the explicit interface class.
     * If <code>name</code> is null, attempts to map onto the current process.
     * Native libraries loaded via this method may be found in
     * <a href="NativeLibrary.html#library_search_paths">several locations</a>.
     * @param name
     * @param interfaceClass
     * @return an instance of the requested interface, mapped to the indicated
     * native library. 
     * @throws UnsatisfiedLinkError if the library cannot be found or
     * dependent libraries are missing.
     */
    public static Object loadLibrary(String name, Class interfaceClass) {
        return loadLibrary(name, interfaceClass, Collections.EMPTY_MAP);
    }

    /** Load a library interface from the given shared library, providing
     * the explicit interface class and a map of options for the library.
     * If no library options are detected the map is interpreted as a map
     * of Java method names to native function names.<p>
     * If <code>name</code> is null, attempts to map onto the current process.
     * Native libraries loaded via this method may be found in
     * <a href="NativeLibrary.html#library_search_paths">several locations</a>.
     * @param name
     * @param interfaceClass
     * @param options Map of library options
     * @return an instance of the requested interface, mapped to the indicated
     * native library. 
     * @throws UnsatisfiedLinkError if the library cannot be found or
     * dependent libraries are missing.
     */
    public static Object loadLibrary(String name,
                                     Class interfaceClass,
                                     Map options) {
        Library.Handler handler =
            new Library.Handler(name, interfaceClass, options);
        ClassLoader loader = interfaceClass.getClassLoader();
        Library proxy = (Library)
            Proxy.newProxyInstance(loader, new Class[] {interfaceClass},
                                   handler);
        cacheOptions(interfaceClass, options, proxy);
        return proxy;
    }

    /** Attempts to force initialization of an instance of the library interface
     * by loading a public static field of the requisite type.
     * Returns whether an instance variable was instantiated.
     * Expects that lock on libraries is already held
     */
    private static void loadLibraryInstance(Class cls) {
        synchronized(libraries) {
            if (cls != null && !libraries.containsKey(cls)) {
                try {
                    Field[] fields = cls.getFields();
                    for (int i=0;i < fields.length;i++) {
                        Field field = fields[i];
                        if (field.getType() == cls
                            && Modifier.isStatic(field.getModifiers())) {
                            // Ensure the field gets initialized by reading it
                            libraries.put(cls, new WeakReference(field.get(null)));
                            break;
                        }
                    }
                }
                catch (Exception e) {
                    throw new IllegalArgumentException("Could not access instance of "
                                                       + cls + " (" + e + ")");
                }
            }
        }
    }

    /** Find the library interface corresponding to the given class.  Checks
     * all ancestor classes and interfaces for a declaring class which
     * implements {@link Library}.
     */
    static Class findEnclosingLibraryClass(Class cls) {
        if (cls == null) {
            return null;
        }
        // Check for direct-mapped libraries, which won't necessarily
        // implement com.sun.jna.Library.
        synchronized(libraries) {
            if (options.containsKey(cls)) {
                return cls;
            }
        }
        if (Library.class.isAssignableFrom(cls)) {
            return cls;
        }
        if (Callback.class.isAssignableFrom(cls)) {
            cls = CallbackReference.findCallbackClass(cls);
        }
        Class declaring = cls.getDeclaringClass();
        Class fromDeclaring = findEnclosingLibraryClass(declaring);
        if (fromDeclaring != null) {
            return fromDeclaring;
        }
        return findEnclosingLibraryClass(cls.getSuperclass());
    }


    /** Return the preferred native library configuration options for the given
     * class.  First attempts to load any field of the interface type within
     * the interface mapping, then checks the cache for any specified library
     * options.  If none found, a set of library options will be generated
     * from the fields (by order of precedence) <code>OPTIONS</code> (a {@link
     * Map}), <code>TYPE_MAPPER</code> (a {@link TypeMapper}),
     * <code>STRUCTURE_ALIGNMENT</code> (an {@link Integer}), and
     * <code>STRING_ENCODING</code> (a {@link String}).
     * @see Library
     */
    public static Map getLibraryOptions(Class type) {
        synchronized(libraries) {
            if (options.containsKey(type)) {
                return (Map)options.get(type);
            }
        }
        Class mappingClass = findEnclosingLibraryClass(type);
        if (mappingClass != null) {
            loadLibraryInstance(mappingClass);
        }
        else {
            mappingClass = type;
        }
        synchronized(libraries) {
            if (options.containsKey(mappingClass)) {
                Map libraryOptions = (Map)options.get(mappingClass);
                options.put(type, libraryOptions);
                return libraryOptions;
            }
            Map libraryOptions = null;
            try {
                Field field = mappingClass.getField("OPTIONS");
                field.setAccessible(true);
                libraryOptions = (Map)field.get(null);
            }
            catch (NoSuchFieldException e) {
                libraryOptions = Collections.EMPTY_MAP;
            }
            catch (Exception e) {
                throw new IllegalArgumentException("OPTIONS must be a public field of type java.util.Map ("
                                                   + e + "): " + mappingClass);
            }
            // Make a clone of the original options
            libraryOptions = new HashMap(libraryOptions);
            if (!libraryOptions.containsKey(Library.OPTION_TYPE_MAPPER)) {
                libraryOptions.put(Library.OPTION_TYPE_MAPPER, lookupField(mappingClass, "TYPE_MAPPER", TypeMapper.class));
            }
            if (!libraryOptions.containsKey(Library.OPTION_STRUCTURE_ALIGNMENT)) {
                libraryOptions.put(Library.OPTION_STRUCTURE_ALIGNMENT, lookupField(mappingClass, "STRUCTURE_ALIGNMENT", Integer.class));
            }
            if (!libraryOptions.containsKey(Library.OPTION_STRING_ENCODING)) {
                libraryOptions.put(Library.OPTION_STRING_ENCODING, lookupField(mappingClass, "STRING_ENCODING", String.class));
            }
            options.put(mappingClass, libraryOptions);
            // Store the original lookup class, if different from the mapping class
            if (type != mappingClass) {
                options.put(type, libraryOptions);
            }
            return libraryOptions;
        }
    }

    private static Object lookupField(Class mappingClass, String fieldName, Class resultClass) {
        try {
            Field field = mappingClass.getField(fieldName);
            field.setAccessible(true);
            return field.get(null);
        }
        catch (NoSuchFieldException e) {
            return null;
        }
        catch (Exception e) {
            throw new IllegalArgumentException(fieldName + " must be a public field of type " 
                                               + resultClass.getName() + " ("
                                               + e + "): " + mappingClass);
        }
    }

    /** Return the preferred {@link TypeMapper} for the given native interface.
     * See {@link com.sun.jna.Library#OPTION_TYPE_MAPPER}.
     */
    public static TypeMapper getTypeMapper(Class cls) {
        return (TypeMapper)getLibraryOptions(cls).get(Library.OPTION_TYPE_MAPPER);
    }

    /** Return the preferred Strring encoding for the given native interface. 
     * If there is no setting, defaults to the {@link
     * #getDefaultStringEncoding()}. 
     * See {@link com.sun.jna.Library#OPTION_STRING_ENCODING}.
     */
    public static String getStringEncoding(Class cls) {
        String encoding = (String)getLibraryOptions(cls).get(Library.OPTION_STRING_ENCODING);
        return encoding != null ? encoding : getDefaultStringEncoding();
    }

    /** Return the default string encoding.  Returns the value of the system
     * property <code>jna.encoding</code> or {@link Native#DEFAULT_ENCODING}.
     */
    public static String getDefaultStringEncoding() {
        return System.getProperty("jna.encoding", DEFAULT_ENCODING);
    }

    /** Return the preferred structure alignment for the given native interface.
     * See {@link com.sun.jna.Library#OPTION_STRUCTURE_ALIGNMENT}.
     */
    public static int getStructureAlignment(Class cls) {
        Integer alignment = (Integer)getLibraryOptions(cls).get(Library.OPTION_STRUCTURE_ALIGNMENT);
        return alignment == null ? Structure.ALIGN_DEFAULT : alignment.intValue();
    }

    /** Return a byte array corresponding to the given String.  The encoding
     * used is obtained from {@link #getDefaultStringEncoding()}.
     */
    static byte[] getBytes(String s) {
        return getBytes(s, getDefaultStringEncoding());
    }

    /** Return a byte array corresponding to the given String, using the given
        encoding.  If the encoding is not found default to the platform native
        encoding. 
    */
    static byte[] getBytes(String s, String encoding) {
        if (encoding != null) {
            try {
                return s.getBytes(encoding);
            }
            catch(UnsupportedEncodingException e) {
                System.err.println("JNA Warning: Encoding '"
                                   + encoding + "' is unsupported");
            }
        }
        System.err.println("JNA Warning: Encoding with fallback "
                           + System.getProperty("file.encoding"));
        return s.getBytes();
    }

    /** Obtain a NUL-terminated byte buffer equivalent to the given String,
        using the encoding returned by {@link #getDefaultStringEncoding()}.
    */
    public static byte[] toByteArray(String s) {
        return toByteArray(s, getDefaultStringEncoding());
    }

    /** Obtain a NUL-terminated byte buffer equivalent to the given String,
        using the given encoding.
     */
    public static byte[] toByteArray(String s, String encoding) {
        byte[] bytes = getBytes(s, encoding);
        byte[] buf = new byte[bytes.length+1];
        System.arraycopy(bytes, 0, buf, 0, bytes.length);
        return buf;
    }

    /** Obtain a NUL-terminated wide character buffer equivalent to the given
        String.
    */
    public static char[] toCharArray(String s) {
        char[] chars = s.toCharArray();
        char[] buf = new char[chars.length+1];
        System.arraycopy(chars, 0, buf, 0, chars.length);
        return buf;
    }

    /**
     * Loads the JNA stub library.
     * First tries jna.boot.library.path, then the system path, then from the
     * jar file.
     */
    private static void loadNativeDispatchLibrary() {

        if (!Boolean.getBoolean("jna.nounpack")) {
            try {
                removeTemporaryFiles();
            }
            catch(IOException e) {
                System.err.println("JNA Warning: IOException removing temporary files: " + e.getMessage());
            }
        }

        String libName = System.getProperty("jna.boot.library.name", "jnidispatch");
        String bootPath = System.getProperty("jna.boot.library.path");
        if (bootPath != null) {
            // String.split not available in 1.4
            StringTokenizer dirs = new StringTokenizer(bootPath, File.pathSeparator);
            while (dirs.hasMoreTokens()) {
                String dir = dirs.nextToken();
                File file = new File(new File(dir), System.mapLibraryName(libName).replace(".dylib", ".jnilib"));
                String path = file.getAbsolutePath();
		if (DEBUG_JNA_LOAD) {
		    System.out.println("Looking in " + path);
		}
                if (file.exists()) {
                    try {
			if (DEBUG_JNA_LOAD) {
			    System.out.println("Trying " + path);
			}
                        System.setProperty("jnidispatch.path", path);
                        System.load(path);
                        jnidispatchPath = path;
			if (DEBUG_JNA_LOAD) {
			    System.out.println("Found jnidispatch at " + path);
			}
                        return;
                    } catch (UnsatisfiedLinkError ex) {
                        // Not a problem if already loaded in anoteher class loader
                        // Unfortunately we can't distinguish the difference...
                        //System.out.println("File found at " + file + " but not loadable: " + ex.getMessage());
                    }
                }
                if (Platform.isMac()) {
                    String orig, ext;
                    if (path.endsWith("dylib")) {
                        orig = "dylib";
                        ext = "jnilib";
                    } else {
                        orig = "jnilib";
                        ext = "dylib";
                    }
                    path = path.substring(0, path.lastIndexOf(orig)) + ext;
		    if (DEBUG_JNA_LOAD) {
			System.out.println("Looking in " + path);
		    }
                    if (new File(path).exists()) {
                        try {
			    if (DEBUG_JNA_LOAD) {
				System.out.println("Trying " + path);
			    }
                            System.setProperty("jnidispatch.path", path);
                            System.load(path);
                            jnidispatchPath = path;
                            if (DEBUG_JNA_LOAD) {
                                System.out.println("Found jnidispatch at " + path);
                            }
                            return;
                        } catch (UnsatisfiedLinkError ex) {
                            System.err.println("File found at " + path + " but not loadable: " + ex.getMessage());
                        }
                    }
                }
            }
        }
        if (!Boolean.getBoolean("jna.nosys")) {
            try {
		if (DEBUG_JNA_LOAD) {
		    System.out.println("Trying (via loadLibrary) " + libName);
		}
                System.loadLibrary(libName);
                if (DEBUG_JNA_LOAD) {
                    System.out.println("Found jnidispatch on system path");
                }
                return;
            }
            catch(UnsatisfiedLinkError e) {
            }
        }
        if (!Boolean.getBoolean("jna.noclasspath")) {
            loadNativeDispatchLibraryFromClasspath();
        }
        else {
            throw new UnsatisfiedLinkError("Unable to locate JNA native support library");
        }
    }

    static final String JNA_TMPLIB_PREFIX = "jna";
    /**
     * Attempts to load the native library resource from the filesystem,
     * extracting the JNA stub library from jna.jar if not already available.
     */
    private static void loadNativeDispatchLibraryFromClasspath() {
        try {
            String libName = "/com/sun/jna/" + Platform.RESOURCE_PREFIX + "/" + System.mapLibraryName("jnidispatch").replace(".dylib", ".jnilib");
            File lib = extractFromResourcePath(libName, Native.class.getClassLoader());
            if (lib == null) {
                if (lib == null) {
                    throw new UnsatisfiedLinkError("Could not find JNA native support");
                }
            }
	    if (DEBUG_JNA_LOAD) {
		System.out.println("Trying " + lib.getAbsolutePath());
	    }
            System.setProperty("jnidispatch.path", lib.getAbsolutePath());
	    System.load(lib.getAbsolutePath());
            jnidispatchPath = lib.getAbsolutePath();
            if (DEBUG_JNA_LOAD) {
                System.out.println("Found jnidispatch at " + jnidispatchPath);
            }
            // Attempt to delete immediately once jnidispatch is successfully
            // loaded.  This avoids the complexity of trying to do so on "exit",
            // which point can vary under different circumstances (native
            // compilation, dynamically loaded modules, normal application, etc).
            if (isUnpacked(lib)
                && !Boolean.getBoolean("jnidispatch.preserve")) {
                deleteLibrary(lib);
            }
        }
        catch(IOException e) {
            throw new UnsatisfiedLinkError(e.getMessage());
        }
    }

    /** Identify temporary files unpacked from classpath jar files. */
    static boolean isUnpacked(File file) {
        return file.getName().startsWith(JNA_TMPLIB_PREFIX);
    }

    /** Attempt to extract a native library from the current resource path,
     * using the current thread context class loader.
     * @param name Base name of native library to extract.  May also be an
     * absolute resource path (i.e. starts with "/"), in which case the
     * no transformations of the library name are performed.  If only the base
     * name is given, the resource path is attempted both with and without
     * {@link Platform#RESOURCE_PREFIX}, after mapping the library name via
     * {@link NativeLibrary#mapSharedLibraryName(String)}.
     * @return File indicating extracted resource on disk
     * @throws IOException if resource not found
     */
    public static File extractFromResourcePath(String name) throws IOException {
        return extractFromResourcePath(name, null);
    }

    /** Attempt to extract a native library from the resource path using the
     * given class loader.  
     * @param name Base name of native library to extract.  May also be an
     * absolute resource path (i.e. starts with "/"), in which case the
     * no transformations of the library name are performed.  If only the base
     * name is given, the resource path is attempted both with and without
     * {@link Platform#RESOURCE_PREFIX}, after mapping the library name via
     * {@link NativeLibrary#mapSharedLibraryName(String)}.
     * @param loader Class loader to use to load resources
     * @return File indicating extracted resource on disk
     * @throws IOException if resource not found
     */
    public static File extractFromResourcePath(String name, ClassLoader loader) throws IOException {
        final boolean DEBUG = DEBUG_LOAD
            || (DEBUG_JNA_LOAD && name.indexOf("jnidispatch") != -1);
        if (loader == null) {
            loader = Thread.currentThread().getContextClassLoader();
            // Context class loader is not guaranteed to be set
            if (loader == null) {
                loader = Native.class.getClassLoader();
            }
        }
	if (DEBUG) {
	    System.out.println("Looking in classpath from " + loader + " for " + name);
	}
        String libname = name.startsWith("/") ? name : NativeLibrary.mapSharedLibraryName(name);
        String resourcePath = name.startsWith("/") ? name : Platform.RESOURCE_PREFIX + "/" + libname;
        if (resourcePath.startsWith("/")) {
            resourcePath = resourcePath.substring(1);
        }
        URL url = loader.getResource(resourcePath);
        if (url == null && resourcePath.startsWith(Platform.RESOURCE_PREFIX)) {
            // If not found with the standard resource prefix, try without it
            url = loader.getResource(libname);
        }
        if (url == null) {
            String path = System.getProperty("java.class.path");
            if (loader instanceof URLClassLoader) {
                path = Arrays.asList(((URLClassLoader)loader).getURLs()).toString();
            }
            throw new IOException("Native library (" + resourcePath + ") not found in resource path (" + path + ")");
        }
        if (DEBUG) {
            System.out.println("Found library resource at " + url);
        }

        File lib = null;
        if (url.getProtocol().toLowerCase().equals("file")) {
            try {
                lib = new File(new URI(url.toString()));
            }
            catch(URISyntaxException e) {
                lib = new File(url.getPath());
            }
	    if (DEBUG) {
		System.out.println("Looking in " + lib.getAbsolutePath());
	    }
            if (!lib.exists()) {
                throw new IOException("File URL " + url + " could not be properly decoded");
            }
        }
        else if (!Boolean.getBoolean("jna.nounpack")) {
            InputStream is = loader.getResourceAsStream(resourcePath);
            if (is == null) {
                throw new IOException("Can't obtain InputStream for " + resourcePath);
            }

            FileOutputStream fos = null;
            try {
                // Suffix is required on windows, or library fails to load
                // Let Java pick the suffix, except on windows, to avoid
                // problems with Web Start.
                File dir = getTempDir();
                lib = File.createTempFile(JNA_TMPLIB_PREFIX, Platform.isWindows()?".dll":null, dir);
                if (!Boolean.getBoolean("jnidispatch.preserve")) {
                    lib.deleteOnExit();
                }
                fos = new FileOutputStream(lib);
                int count;
                byte[] buf = new byte[1024];
                while ((count = is.read(buf, 0, buf.length)) > 0) {
                    fos.write(buf, 0, count);
                }
            }
            catch(IOException e) {
                throw new IOException("Failed to create temporary file for " + name + " library: " + e.getMessage());
            }
            finally {
                try { is.close(); } catch(IOException e) { }
                if (fos != null) {
                    try { fos.close(); } catch(IOException e) { }
                }
            }
        }
        return lib;
    }

    /**
     * Initialize field and method IDs for native methods of this class.
     * Returns the size of a native pointer.
     **/
    private static native int sizeof(int type);

    private static native String getNativeVersion();
    private static native String getAPIChecksum();

    /** Retrieve the last error set by the OS.  This corresponds to
     * <code>GetLastError()</code> on Windows, and <code>errno</code> on
     * most other platforms.  The value is preserved per-thread, but whether
     * the original value is per-thread depends on the underlying OS.  The
     * result is undefined if {@link #getPreserveLastError} is
     * <code>false</code>.<p>
     * The preferred method of obtaining the last error result is
     * to declare your mapped method to throw {@link LastErrorException}
     * instead.
     */
    public static native int getLastError();

    /** Set the OS last error code.  The value will be saved on a per-thread
     * basis. 
     */
    public static native void setLastError(int code);

    /**
     * Returns a synchronized (thread-safe) library backed by the specified
     * library.  This wrapping will prevent simultaneous invocations of any
     * functions mapped to a given {@link NativeLibrary}.  Note that the
     * native library may still be sensitive to being called from different
     * threads.
     * <p>
     * @param  library the library to be "wrapped" in a synchronized library.
     * @return a synchronized view of the specified library.
     */
    public static Library synchronizedLibrary(final Library library) {
        Class cls = library.getClass();
        if (!Proxy.isProxyClass(cls)) {
            throw new IllegalArgumentException("Library must be a proxy class");
        }
        InvocationHandler ih = Proxy.getInvocationHandler(library);
        if (!(ih instanceof Library.Handler)) {
            throw new IllegalArgumentException("Unrecognized proxy handler: " + ih);
        }
        final Library.Handler handler = (Library.Handler)ih;
        InvocationHandler newHandler = new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                synchronized(handler.getNativeLibrary()) {
                    return handler.invoke(library, method, args);
                }
            }
        };
        return (Library)Proxy.newProxyInstance(cls.getClassLoader(),
                                               cls.getInterfaces(),
                                               newHandler);
    }

    /** If running web start, determine the location of a given native
     * library.  This value may be used to properly set
     * <code>jna.library.path</code> so that JNA can load libraries identified
     * by the &lt;nativelib&gt; tag in the JNLP configuration file.  Returns
     * <code>null</code> if the Web Start native library cache location can not
     * be determined.  Note that the path returned may be different for any
     * given library name.
     * <p>
     * Use <code>System.getProperty("javawebstart.version")</code> to detect
     * whether your code is running under Web Start.
     * @throws UnsatisfiedLinkError if the library can't be found by the
     * Web Start class loader, which usually means it wasn't included as
     * a <code>&lt;nativelib&gt;</code> resource in the JNLP file.
     * @return null if unable to query the web start loader.
     */
    public static String getWebStartLibraryPath(final String libName) {
        if (System.getProperty("javawebstart.version") == null)
            return null;
        try {

            final ClassLoader cl = Native.class.getClassLoader();
            Method m = (Method)AccessController.doPrivileged(new PrivilegedAction() {
                public Object run() {
                    try {
                        Method m = ClassLoader.class.getDeclaredMethod("findLibrary", new Class[] { String.class });
                        m.setAccessible(true);
                        return m;
                    }
                    catch(Exception e) {
                        return null;
                    }
                }
            });
            String libpath = (String)m.invoke(cl, new Object[] { libName });
            if (libpath != null) {
                return new File(libpath).getParent();
            }
            return null;
        }
        catch (Exception e) {
            return null;
        }
    }

    /** Perform cleanup of automatically unpacked native shared library.
     */
    static void markTemporaryFile(File file) {
        // If we can't force an unload/delete, flag the file for later
        // deletion
        try {
            File marker = new File(file.getParentFile(), file.getName() + ".x");
            marker.createNewFile();
        }
        catch(IOException e) { e.printStackTrace(); }
    }

    /** Obtain a directory suitable for writing JNA-specific temporary files.
        Override with <code>jna.tmpdir</code>
    */
    static File getTempDir() throws IOException {
        File jnatmp;
        String prop = System.getProperty("jna.tmpdir");
        if (prop != null) {
            jnatmp = new File(prop);
            jnatmp.mkdirs();
        }
        else {
            File tmp = new File(System.getProperty("java.io.tmpdir"));
            // Loading DLLs via System.load() under a directory with a unicode
            // name will fail on windows, so use a hash code of the user's
            // name in case the user's name contains non-ASCII characters
            jnatmp = new File(tmp, "jna-" + System.getProperty("user.name").hashCode());
            jnatmp.mkdirs();
            if (!jnatmp.exists() || !jnatmp.canWrite()) {
                jnatmp = tmp;
            }
        }
        if (!jnatmp.exists()) {
            throw new IOException("JNA temporary directory '" + jnatmp + "' does not exist");
        }
        if (!jnatmp.canWrite()) {
            throw new IOException("JNA temporary directory '" + jnatmp + "' is not writable");
        }
        return jnatmp;
    }

    /** Remove all marked temporary files in the given directory. */
    static void removeTemporaryFiles() throws IOException {
        File dir = getTempDir();
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".x") && name.startsWith(JNA_TMPLIB_PREFIX);
            }
        };
        File[] files = dir.listFiles(filter);
        for (int i=0;files != null && i < files.length;i++) {
            File marker = files[i];
            String name = marker.getName();
            name = name.substring(0, name.length()-2);
            File target = new File(marker.getParentFile(), name);
            if (!target.exists() || target.delete()) {
                marker.delete();
            }
        }
    }

    /** Returns the native size of the given class, in bytes.
     * For use with arrays.
     */
    public static int getNativeSize(Class type, Object value) {
        if (type.isArray()) {
            int len = Array.getLength(value);
            if (len > 0) {
                Object o = Array.get(value, 0);
                return len * getNativeSize(type.getComponentType(), o);
            }
            // Don't process zero-length arrays
            throw new IllegalArgumentException("Arrays of length zero not allowed: " + type);
        }
        if (Structure.class.isAssignableFrom(type)
            && !Structure.ByReference.class.isAssignableFrom(type)) {
            return Structure.size(type, (Structure)value);
        }
        try {
            return getNativeSize(type);
        }
        catch(IllegalArgumentException e) {
            throw new IllegalArgumentException("The type \"" + type.getName()
                                               + "\" is not supported: "
                                               + e.getMessage());
        }
    }

    /** Returns the native size for a given Java class.  Structures are
     * assumed to be <code>struct</code> pointers unless they implement
     * {@link Structure.ByValue}.
     */
    public static int getNativeSize(Class cls) {
        if (NativeMapped.class.isAssignableFrom(cls)) {
            cls = NativeMappedConverter.getInstance(cls).nativeType();
        }
        // boolean defaults to 32 bit integer if not otherwise mapped
        if (cls == boolean.class || cls == Boolean.class) return 4;
        if (cls == byte.class || cls == Byte.class) return 1;
        if (cls == short.class || cls == Short.class) return 2;
        if (cls == char.class || cls == Character.class) return WCHAR_SIZE;
        if (cls == int.class || cls == Integer.class) return 4;
        if (cls == long.class || cls == Long.class) return 8;
        if (cls == float.class || cls == Float.class) return 4;
        if (cls == double.class || cls == Double.class) return 8;
        if (Structure.class.isAssignableFrom(cls)) {
            if (Structure.ByValue.class.isAssignableFrom(cls)) {
                return Structure.size(cls);
            }
            return POINTER_SIZE;
        }
        if (Pointer.class.isAssignableFrom(cls)
            || (Platform.HAS_BUFFERS && Buffers.isBuffer(cls))
            || Callback.class.isAssignableFrom(cls)
            || String.class == cls
            || WString.class == cls) {
            return POINTER_SIZE;
        }
        throw new IllegalArgumentException("Native size for type \"" + cls.getName()
                                           + "\" is unknown");
    }

    /** Indicate whether the given class is supported as a native argument
     * type.
     */
    public static boolean isSupportedNativeType(Class cls) {
        if (Structure.class.isAssignableFrom(cls)) {
            return true;
        }
        try {
            return getNativeSize(cls) != 0;
        }
        catch(IllegalArgumentException e) {
            return false;
        }
    }

    /** Set the default handler invoked when a callback throws an uncaught
     * exception.  If the given handler is <code>null</code>, the default
     * handler will be reinstated.
     */
    public static void setCallbackExceptionHandler(UncaughtExceptionHandler eh) {
        callbackExceptionHandler = eh == null ? DEFAULT_HANDLER : eh;
    }

    /** Returns the current handler for callback uncaught exceptions. */
    public static UncaughtExceptionHandler getCallbackExceptionHandler() {
        return callbackExceptionHandler;
    }

    /** When called from a class static initializer, maps all native methods
     * found within that class to native libraries via the JNA raw calling
     * interface.
     * @param libName library name to which functions should be bound
     */
    public static void register(String libName) {
        register(findDirectMappedClass(getCallingClass()), libName);
    }

    /** When called from a class static initializer, maps all native methods
     * found within that class to native libraries via the JNA raw calling
     * interface.
     * @param lib native library to which functions should be bound
     */
    public static void register(NativeLibrary lib) {
        register(findDirectMappedClass(getCallingClass()), lib);
    }

    /** Find the nearest enclosing class with native methods. */
    static Class findDirectMappedClass(Class cls) {
        Method[] methods = cls.getDeclaredMethods();
        for (int i=0;i < methods.length;i++) {
            if ((methods[i].getModifiers() & Modifier.NATIVE) != 0) {
                return cls;
            }
        }
        int idx = cls.getName().lastIndexOf("$");
        if (idx != -1) {
            String name = cls.getName().substring(0, idx);
            try {
                return findDirectMappedClass(Class.forName(name, true, cls.getClassLoader()));
            }
            catch(ClassNotFoundException e) {
            }
        }
        throw new IllegalArgumentException("Can't determine class with native methods from the current context (" + cls + ")");
    }

    /** Try to determine the class context in which a {@link #register(String)} call
        was made.
    */
    static Class getCallingClass() {
        Class[] context = new SecurityManager() {
            public Class[] getClassContext() {
                return super.getClassContext();
            }
        }.getClassContext();
        if (context == null) {
            throw new IllegalStateException("The SecurityManager implementation on this platform is broken; you must explicitly provide the class to register");
        }
        if (context.length < 4) {
            throw new IllegalStateException("This method must be called from the static initializer of a class");
        }
        return context[3];
    }

    /** Set a thread initializer for the given callback.
        The thread initializer indicates desired thread configuration when the
        given Callback is invoked on a native thread not yet attached to the
        VM.
     */
    public static void setCallbackThreadInitializer(Callback cb, CallbackThreadInitializer initializer) {
        CallbackReference.setCallbackThreadInitializer(cb, initializer);
    }


    private static Map registeredClasses = new HashMap();
    private static Map registeredLibraries = new HashMap();
    private static Object unloader = new Object() {
        protected void finalize() {
            synchronized(registeredClasses) {
                for (Iterator i=registeredClasses.entrySet().iterator();i.hasNext();) {
                    Map.Entry e = (Map.Entry)i.next();
                    unregister((Class)e.getKey(), (long[])e.getValue());
                    i.remove();
                }
            }
        }
    };

    /** Remove all native mappings for the calling class.
        Should only be called if the class is no longer referenced and about
        to be garbage collected.
     */
    public static void unregister() {
        unregister(findDirectMappedClass(getCallingClass()));
    }

    /** Remove all native mappings for the given class.
        Should only be called if the class is no longer referenced and about
        to be garbage collected.
     */
    public static void unregister(Class cls) {
        synchronized(registeredClasses) {
            if (registeredClasses.containsKey(cls)) {
                unregister(cls, (long[])registeredClasses.get(cls));
                registeredClasses.remove(cls);
                registeredLibraries.remove(cls);
            }
        }
    }

    /** Unregister the native methods for the given class. */
    private static native void unregister(Class cls, long[] handles);

    private static String getSignature(Class cls) {
        if (cls.isArray()) {
            return "[" + getSignature(cls.getComponentType());
        }
        if (cls.isPrimitive()) {
            if (cls == void.class) return "V";
            if (cls == boolean.class) return "Z";
            if (cls == byte.class) return "B";
            if (cls == short.class) return "S";
            if (cls == char.class) return "C";
            if (cls == int.class) return "I";
            if (cls == long.class) return "J";
            if (cls == float.class) return "F";
            if (cls == double.class) return "D";
        }
        return "L" + replace(".", "/", cls.getName()) + ";";
    }

    // No String.replace available in 1.4
    static String replace(String s1, String s2, String str) {
        StringBuffer buf = new StringBuffer();
        while (true) {
            int idx = str.indexOf(s1);
            if (idx == -1) {
                buf.append(str);
                break;
            }
            else {
                buf.append(str.substring(0, idx));
                buf.append(s2);
                str = str.substring(idx + s1.length());
            }
        }
        return buf.toString();
    }

    /** Indicates whether the callback has an initializer. */
    static final int CB_HAS_INITIALIZER = 1;

    private static final int CVT_UNSUPPORTED = -1;
    private static final int CVT_DEFAULT = 0;
    private static final int CVT_POINTER = 1;
    private static final int CVT_STRING = 2;
    private static final int CVT_STRUCTURE = 3;
    private static final int CVT_STRUCTURE_BYVAL = 4;
    private static final int CVT_BUFFER = 5;
    private static final int CVT_ARRAY_BYTE = 6;
    private static final int CVT_ARRAY_SHORT = 7;
    private static final int CVT_ARRAY_CHAR = 8;
    private static final int CVT_ARRAY_INT = 9;
    private static final int CVT_ARRAY_LONG = 10;
    private static final int CVT_ARRAY_FLOAT = 11;
    private static final int CVT_ARRAY_DOUBLE = 12;
    private static final int CVT_ARRAY_BOOLEAN = 13;
    private static final int CVT_BOOLEAN = 14;
    private static final int CVT_CALLBACK = 15;
    private static final int CVT_FLOAT = 16;
    private static final int CVT_NATIVE_MAPPED = 17;
    private static final int CVT_WSTRING = 18;
    private static final int CVT_INTEGER_TYPE = 19;
    private static final int CVT_POINTER_TYPE = 20;
    private static final int CVT_TYPE_MAPPER = 21;

    private static int getConversion(Class type, TypeMapper mapper) {
        if (type == Boolean.class) type = boolean.class;
        else if (type == Byte.class) type = byte.class;
        else if (type == Short.class) type = short.class;
        else if (type == Character.class) type = char.class;
        else if (type == Integer.class) type = int.class;
        else if (type == Long.class) type = long.class;
        else if (type == Float.class) type = float.class;
        else if (type == Double.class) type = double.class;
        else if (type == Void.class) type = void.class;

        if (mapper != null
            && (mapper.getFromNativeConverter(type) != null
                || mapper.getToNativeConverter(type) != null)) {
            return CVT_TYPE_MAPPER;
        }

        if (Pointer.class.isAssignableFrom(type)) {
            return CVT_POINTER;
        }
        if (String.class == type) {
            return CVT_STRING;
        }
        if (WString.class.isAssignableFrom(type)) {
            return CVT_WSTRING;
        }
        if (Platform.HAS_BUFFERS && Buffers.isBuffer(type)) {
            return CVT_BUFFER;
        }
        if (Structure.class.isAssignableFrom(type)) {
            if (Structure.ByValue.class.isAssignableFrom(type)) {
                return CVT_STRUCTURE_BYVAL;
            }
            return CVT_STRUCTURE;
        }
        if (type.isArray()) {
            switch(type.getName().charAt(1)) {
            case 'Z': return CVT_ARRAY_BOOLEAN;
            case 'B': return CVT_ARRAY_BYTE;
            case 'S': return CVT_ARRAY_SHORT;
            case 'C': return CVT_ARRAY_CHAR;
            case 'I': return CVT_ARRAY_INT;
            case 'J': return CVT_ARRAY_LONG;
            case 'F': return CVT_ARRAY_FLOAT;
            case 'D': return CVT_ARRAY_DOUBLE;
            default: break;
            }
        }
        if (type.isPrimitive()) {
            return type == boolean.class ? CVT_BOOLEAN : CVT_DEFAULT;
        }
        if (Callback.class.isAssignableFrom(type)) {
            return CVT_CALLBACK;
        }
        if (IntegerType.class.isAssignableFrom(type)) {
            return CVT_INTEGER_TYPE;
        }
        if (PointerType.class.isAssignableFrom(type)) {
            return CVT_POINTER_TYPE;
        }
        if (NativeMapped.class.isAssignableFrom(type)) {
            return CVT_NATIVE_MAPPED;
        }
        return CVT_UNSUPPORTED;
    }

    /** When called from a class static initializer, maps all native methods
     * found within that class to native libraries via the JNA raw calling
     * interface.  Uses the class loader of the given class to search for the
     * native library in the resource path if it is not found in the system
     * library load path or <code>jna.library.path</code>.
     * @param cls Class with native methods to register
     * @param libName name of or path to native library to which functions
     * should be bound 
     */
    public static void register(Class cls, String libName) {
        Map options = new HashMap();
        options.put(Library.OPTION_CLASSLOADER, cls.getClassLoader());
        register(cls, NativeLibrary.getInstance(libName, options));
    }

    /** When called from a class static initializer, maps all native methods
     * found within that class to native libraries via the JNA raw calling
     * interface.
     * @param cls Class with native methods to register
     * @param lib library to which functions should be bound
     */
    // TODO: derive options from annotations (per-class or per-method)
    // options: read parameter type mapping (long/native long),
    // method name, library name, call conv
    public static void register(Class cls, NativeLibrary lib) {
        Method[] methods = cls.getDeclaredMethods();
        List mlist = new ArrayList();
        TypeMapper mapper = (TypeMapper)
            lib.getOptions().get(Library.OPTION_TYPE_MAPPER);

        for (int i=0;i < methods.length;i++) {
            if ((methods[i].getModifiers() & Modifier.NATIVE) != 0) {
                mlist.add(methods[i]);
            }
        }
        long[] handles = new long[mlist.size()];
        for (int i=0;i < handles.length;i++) {
            Method method = (Method)mlist.get(i);
            String sig = "(";
            Class rclass = method.getReturnType();
            long rtype, closure_rtype;
            Class[] ptypes = method.getParameterTypes();
            long[] atypes = new long[ptypes.length];
            long[] closure_atypes = new long[ptypes.length];
            int[] cvt = new int[ptypes.length];
            ToNativeConverter[] toNative = new ToNativeConverter[ptypes.length];
            FromNativeConverter fromNative = null;
            int rcvt = getConversion(rclass, mapper);
            boolean throwLastError = false;
            switch (rcvt) {
            case CVT_UNSUPPORTED:
                throw new IllegalArgumentException(rclass + " is not a supported return type (in method " + method.getName() + " in " + cls + ")");
            case CVT_TYPE_MAPPER:
                fromNative = mapper.getFromNativeConverter(rclass);
                closure_rtype = FFIType.get(rclass).peer;
                rtype = FFIType.get(fromNative.nativeType()).peer;
                break;
            case CVT_NATIVE_MAPPED:
            case CVT_INTEGER_TYPE:
            case CVT_POINTER_TYPE:
                closure_rtype = FFIType.get(Pointer.class).peer;
                rtype = FFIType.get(NativeMappedConverter.getInstance(rclass).nativeType()).peer;
                break;
            case CVT_STRUCTURE:
                closure_rtype = rtype = FFIType.get(Pointer.class).peer;
                break;
            case CVT_STRUCTURE_BYVAL:
                closure_rtype = FFIType.get(Pointer.class).peer;
                rtype = FFIType.get(rclass).peer;
                break;
            default:
                closure_rtype = rtype = FFIType.get(rclass).peer;
                break;
            }
            for (int t=0;t < ptypes.length;t++) {
                Class type = ptypes[t];
                sig += getSignature(type);
                cvt[t] = getConversion(type, mapper);
                if (cvt[t] == CVT_UNSUPPORTED) {
                    throw new IllegalArgumentException(type + " is not a supported argument type (in method " + method.getName() + " in " + cls + ")");
                }
                if (cvt[t] == CVT_NATIVE_MAPPED
                    || cvt[t] == CVT_INTEGER_TYPE) {
                    type = NativeMappedConverter.getInstance(type).nativeType();
                }
                else if (cvt[t] == CVT_TYPE_MAPPER) {
                    toNative[t] = mapper.getToNativeConverter(type);
                }
                // Determine the type that will be passed to the native
                // function, as well as the type to be passed
                // from Java initially
                switch(cvt[t]) {
                case CVT_STRUCTURE_BYVAL:
                case CVT_INTEGER_TYPE:
                case CVT_POINTER_TYPE:
                case CVT_NATIVE_MAPPED:
                    atypes[t] = FFIType.get(type).peer;
                    closure_atypes[t] = FFIType.get(Pointer.class).peer;
                    break;
                case CVT_TYPE_MAPPER:
                    if (type.isPrimitive())
                        closure_atypes[t] = FFIType.get(type).peer;
                    else
                        closure_atypes[t] = FFIType.get(Pointer.class).peer;
                    atypes[t] = FFIType.get(toNative[t].nativeType()).peer;
                    break;
                case CVT_DEFAULT:
                    closure_atypes[t] = atypes[t] = FFIType.get(type).peer;
                    break;
                default:
                    closure_atypes[t] = atypes[t] = FFIType.get(Pointer.class).peer;
                    break;
                }
            }
            sig += ")";
            sig += getSignature(rclass);

            Class[] etypes = method.getExceptionTypes();
            for (int e=0;e < etypes.length;e++) {
                if (LastErrorException.class.isAssignableFrom(etypes[e])) {
                    throwLastError = true;
                    break;
                }
            }

            Function f = lib.getFunction(method.getName(), method);
            try {
                handles[i] = registerMethod(cls, method.getName(),
                                            sig, cvt,
                                            closure_atypes, atypes, rcvt,
                                            closure_rtype, rtype,
                                            rclass,
                                            f.peer, f.getCallingConvention(),
                                            throwLastError,
                                            toNative, fromNative,
                                            f.encoding);
            }
            catch(NoSuchMethodError e) {
                throw new UnsatisfiedLinkError("No method " + method.getName() + " with signature " + sig + " in " + cls);
            }
        }
        synchronized(registeredClasses) {
            registeredClasses.put(cls, handles);
            registeredLibraries.put(cls, lib);
        }
        cacheOptions(cls, lib.getOptions(), null);
    }

    /** Take note of options used for a given library mapping, to facilitate
        looking them up later.
    */
    private static void cacheOptions(Class cls, Map libOptions, Object proxy) {
        libOptions = new HashMap(libOptions);
        synchronized(libraries) {
            options.put(cls, libOptions);
            if (proxy != null) {
                libraries.put(cls, new WeakReference(proxy));
            }

            // If it's a direct mapping, AND implements a Library interface,
            // cache the library interface as well, so that any nested
            // classes get the appropriate associated options
            if (!cls.isInterface()
                && Library.class.isAssignableFrom(cls)) {
                Class ifaces[] = cls.getInterfaces();
                for (int i=0;i < ifaces.length;i++) {
                    if (Library.class.isAssignableFrom(ifaces[i])) {
                        cacheOptions(ifaces[i], libOptions, proxy);
                        break;
                    }
                }
            }
        }
    }

    private static native long registerMethod(Class cls,
                                              String name,
                                              String signature,
                                              int[] conversions,
                                              long[] closure_arg_types,
                                              long[] arg_types,
                                              int rconversion,
                                              long closure_rtype,
                                              long rtype,
                                              Class rclass,
                                              long fptr,
                                              int callingConvention,
                                              boolean throwLastError,
                                              ToNativeConverter[] toNative,
                                              FromNativeConverter fromNative,
                                              String encoding);


    // Called from native code
    private static NativeMapped fromNative(Class cls, Object value) {
        // NOTE: technically should be either CallbackParameterContext or
        // FunctionResultContext
        return (NativeMapped)NativeMappedConverter.getInstance(cls).fromNative(value, new FromNativeContext(cls));
    }
    // Called from native code
    private static Class nativeType(Class cls) {
        return NativeMappedConverter.getInstance(cls).nativeType();
    }
    // Called from native code
    private static Object toNative(ToNativeConverter cvt, Object o) {
        // NOTE: technically should be either CallbackResultContext or
        // FunctionParameterContext
        return cvt.toNative(o, new ToNativeContext());
    }
    // Called from native code
    private static Object fromNative(FromNativeConverter cvt, Object o, Class cls) {
        // NOTE: technically should be FunctionResultContext
        return cvt.fromNative(o, new FromNativeContext(cls));
    }

    /** Create a new cif structure. */
    public static native long ffi_prep_cif(int abi, int nargs, long ffi_return_type, long ffi_types);
    /** Make an FFI function call. */
    public static native void ffi_call(long cif, long fptr, long resp, long args);
    public static native long ffi_prep_closure(long cif, ffi_callback cb);
    public static native void ffi_free_closure(long closure);

    /** Returns the size (calculated by libffi) of the given type. */
    static native int initialize_ffi_type(long type_info);

    public interface ffi_callback {
        void invoke(long cif, long resp, long argp);
    }

    /** Prints JNA library details to the console. */
    public static void main(String[] args) {
        final String DEFAULT_TITLE = "Java Native Access (JNA)";
        final String DEFAULT_VERSION = VERSION;
        final String DEFAULT_BUILD = VERSION + " (package information missing)";
        Package pkg = Native.class.getPackage();
        String title = pkg != null
            ? pkg.getSpecificationTitle() : DEFAULT_TITLE;
        if (title == null) title = DEFAULT_TITLE;
        String version = pkg != null
            ? pkg.getSpecificationVersion() : DEFAULT_VERSION;
        if (version == null) version = DEFAULT_VERSION;
        title += " API Version " + version;
        System.out.println(title);
        version = pkg != null
            ? pkg.getImplementationVersion() : DEFAULT_BUILD;
        if (version == null) version = DEFAULT_BUILD;
        System.out.println("Version: " + version);
        System.out.println(" Native: " + getNativeVersion() + " ("
                           + getAPIChecksum() + ")");
        System.out.println(" Prefix: " + Platform.RESOURCE_PREFIX);
    }

    /** Free the given callback trampoline. */
    static synchronized native void freeNativeCallback(long ptr);

    /** Use direct mapping for callback. */
    static final int CB_OPTION_DIRECT = 1;
    /** Return a DLL-resident fucntion pointer. */
    static final int CB_OPTION_IN_DLL = 2;

    /** Create a native trampoline to delegate execution to the Java callback.
     */
    static synchronized native long createNativeCallback(Callback callback,
                                                         Method method,
                                                         Class[] parameterTypes,
                                                         Class returnType,
                                                         int callingConvention,
                                                         int flags,
                                                         String encoding);

    /**
     * Call the native function being represented by this object
     * @param fp function pointer
     * @param   callFlags calling convention to be used
     * @param	args
     *			Arguments to pass to the native function
     *
     * @return	The value returned by the target native function
     */
    static  native int invokeInt(long fp, int callFlags, Object[] args);

    /**
     * Call the native function being represented by this object
     * @param fp function pointer
     * @param   callFlags calling convention to be used
     * @param	args
     *			Arguments to pass to the native function
     *
     * @return	The value returned by the target native function
     */
    static native long invokeLong(long fp, int callFlags, Object[] args);

    /**
     * Call the native function being represented by this object
     * @param fp function pointer
     * @param   callFlags calling convention to be used
     * @param	args
     *			Arguments to pass to the native function
     */
    static native void invokeVoid(long fp, int callFlags, Object[] args);

    /**
     * Call the native function being represented by this object
     * @param fp function pointer
     * @param   callFlags calling convention to be used
     * @param	args
     *			Arguments to pass to the native function
     *
     * @return	The value returned by the target native function
     */
    static native float invokeFloat(long fp, int callFlags, Object[] args);

    /**
     * Call the native function being represented by this object
     * @param fp function pointer
     * @param   callFlags calling convention to be used
     * @param	args
     *			Arguments to pass to the native function
     *
     * @return	The value returned by the target native function
     */
    static native double invokeDouble(long fp, int callFlags, Object[] args);

    /**
     * Call the native function being represented by this object
     * @param fp function pointer
     * @param   callFlags calling convention to be used
     * @param	args
     *			Arguments to pass to the native function
     *
     * @return	The native pointer returned by the target native function
     */
    static native long invokePointer(long fp, int callFlags, Object[] args);

    /**
     * Call the native function being represented by this object, returning
     * a struct by value.
     * @param fp function pointer
     * @param   callFlags calling convention to be used
     * @param   args Arguments to pass to the native function
     * @param   memory Memory for pre-allocated structure to hold the result
     * @param   typeInfo Native type information for the Structure
     */
    private static native void invokeStructure(long fp, int callFlags,
                                               Object[] args, long memory,
                                               long type_info);

    /**
     * Call the native function being represented by this object, returning
     * a struct by value.
     * @param fp function pointer
     * @param   callFlags calling convention to be used
     * @param   args Arguments to pass to the native function
     * @return the passed-in Structure
     */
    static Structure invokeStructure(long fp, int callFlags, Object[] args,
                                     Structure s) {
        invokeStructure(fp, callFlags, args, s.getPointer().peer,
                        s.getTypeInfo().peer);
        return s;
    }

    /**
     * Call the native function being represented by this object, returning
     * a Java <code>Object</code>.
     * @param fp function pointer
     * @param   callFlags calling convention to be used
     * @param   args Arguments to pass to the native function
     *
     * @return  The returned Java <code>Object</code>
     */
    static native Object invokeObject(long fp, int callFlags, Object[] args);

    /** Open the requested native library with default options. */
    static long open(String name) {
        return open(name, -1);
    }

    /** Open the requested native library with the specified platform-specific
     * otions.
     */
    static native long open(String name, int flags);

    /** Close the given native library. */
    static native void close(long handle);

    static native long findSymbol(long handle, String name);

    static native long indexOf(long addr, byte value);

    static native void read(long addr, byte[] buf, int index, int length);

    static native void read(long addr, short[] buf, int index, int length);

    static native void read(long addr, char[] buf, int index, int length);

    static native void read(long addr, int[] buf, int index, int length);

    static native void read(long addr, long[] buf, int index, int length);

    static native void read(long addr, float[] buf, int index, int length);

    static native void read(long addr, double[] buf, int index, int length);

    static native void write(long addr, byte[] buf, int index, int length);

    static native void write(long addr, short[] buf, int index, int length);

    static native void write(long addr, char[] buf, int index, int length);

    static native void write(long addr, int[] buf, int index, int length);

    static native void write(long addr, long[] buf, int index, int length);

    static native void write(long addr, float[] buf, int index, int length);

    static native void write(long addr, double[] buf, int index, int length);

    static native byte getByte(long addr);

    static native char getChar(long addr);

    static native short getShort(long addr);

    static native int getInt(long addr);

    static native long getLong(long addr);

    static native float getFloat(long addr);

    static native double getDouble(long addr);

    static Pointer getPointer(long addr) {
        long peer = _getPointer(addr);
        return peer == 0 ? null : new Pointer(peer);
    }

    private static native long _getPointer(long addr);

    static native String getWideString(long addr);

    static String getString(long addr) {
        return getString(addr, getDefaultStringEncoding());
    }

    static String getString(long addr, String encoding) {
        byte[] data = getStringBytes(addr);
        if (encoding != null) {
            try {
                return new String(data, encoding);
            }
            catch(UnsupportedEncodingException e) {
            }
        }
        return new String(data);
    }

    static native byte[] getStringBytes(long addr);

    static native void setMemory(long addr, long length, byte value);

    static native void setByte(long addr, byte value);

    static native void setShort(long addr, short value);

    static native void setChar(long addr, char value);

    static native void setInt(long addr, int value);

    static native void setLong(long addr, long value);

    static native void setFloat(long addr, float value);

    static native void setDouble(long addr, double value);

    static native void setPointer(long addr, long value);

    static native void setWideString(long addr, String value);

    /**
     * Call the real native malloc
     * @param size size of the memory to be allocated
     * @return native address of the allocated memory block; zero if the
     * allocation failed.
     */
    public static native long malloc(long size);

    /**
     * Call the real native free
     * @param ptr native address to be freed; a value of zero has no effect,
     * passing an already-freed pointer will cause pain.
     */
    public static native void free(long ptr);

    /**
     * Get a direct ByteBuffer mapped to the memory pointed to by the pointer.
     * This method calls through to the JNA NewDirectByteBuffer method.
     *
     * @param addr base address of the JNA-originated memory
     * @param length Length of ByteBuffer
     * @return a direct ByteBuffer that accesses the memory being pointed to,
     */
    public static native ByteBuffer getDirectByteBuffer(long addr, long length);

    /** Indicate whether the JVM should detach the current native thread when
        the current Java code finishes execution.  Generally this is used to
        avoid detaching native threads when it is known that a given thread
        will be relatively long-lived and call back to Java code frequently.
        <p/>
        This call is lightweight; it only results in an additional JNI
        crossing if the desired state changes from its last setting.

        @throws IllegalStateException if {@link #detach detach(true)} is
        called on a thread created by the JVM.
     */
    public static void detach(boolean detach) {
        Thread thread = Thread.currentThread();
        if (detach) {
            // If a CallbackThreadInitializer was used to avoid detach,
            // we won't have put that thread into the nativeThreads map.
            // Performance is not as critical in that case, and since
            // detach is the default behavior, force an update of the detach
            // state every time.  Clear the termination flag, since it's not
            // needed when the native thread is detached normally.
            nativeThreads.remove(thread);
            Pointer p = (Pointer)nativeThreadTerminationFlag.get();
            setDetachState(true, 0);
        }
        else {
            if (!nativeThreads.containsKey(thread)) {
                Pointer p = (Pointer)nativeThreadTerminationFlag.get();
                nativeThreads.put(thread, p);
                setDetachState(false, p.peer);
            }
        }
    }

    static Pointer getTerminationFlag(Thread t) {
        return (Pointer)nativeThreads.get(t);
    }

    private static Map nativeThreads = Collections.synchronizedMap(new WeakHashMap());

    private static ThreadLocal nativeThreadTerminationFlag = 
        new ThreadLocal() {
            protected Object initialValue() {
                Memory m = new Memory(4);
                m.clear();
                return m;
            }
        };

    private static native void setDetachState(boolean detach, long terminationFlag);

    private static class Buffers {
        static boolean isBuffer(Class cls) {
            return Buffer.class.isAssignableFrom(cls);
        }
    }

    /** Provides separation of JAWT functionality for the sake of J2ME
     * ports which do not include AWT support.
     */
    private static class AWT {
        static long getWindowID(Window w) throws HeadlessException {
            return getComponentID(w);
        }
        // Declaring the argument as Object rather than Component avoids class not
        // found errors on phoneME foundation profile.
        static long getComponentID(Object o) throws HeadlessException {
            if (GraphicsEnvironment.isHeadless()) {
                throw new HeadlessException("No native windows when headless");
            }
            Component c = (Component)o;
            if (c.isLightweight()) {
                throw new IllegalArgumentException("Component must be heavyweight");
            }
            if (!c.isDisplayable())
                throw new IllegalStateException("Component must be displayable");
            // On X11 VMs prior to 1.5, the window must be visible
            if (Platform.isX11()
                && System.getProperty("java.version").startsWith("1.4")) {
                if (!c.isVisible()) {
                    throw new IllegalStateException("Component must be visible");
                }
            }
            // By this point, we're certain that Toolkit.loadLibraries() has
            // been called, thus avoiding AWT/JAWT link errors
            // (see http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6539705).
            return Native.getWindowHandle0(c);
        }
    }
}

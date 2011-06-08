Getting Started with JNA
========================

Java Native Access (JNA) has a single component, `jna.jar`; the supporting native library (jnidispatch) is included in the jar file. JNA is capable of extracting and loading the native library on its own, so you don't need additional configuration. JNA falls back to extraction if the native library is not already installed on the local system somwhere accessible to `System.loadLibrary`. The native library is also available in platform-specific jar files for use with Java Web Start.

Begin by downloading the latest release of JNA and referencing `jna.jar` in your project's `CLASSPATH`.

The following example maps the printf function from the standard C library and calls it. 

    package com.sun.jna.examples;

    import com.sun.jna.Library;
    import com.sun.jna.Native;
    import com.sun.jna.Platform;

    /** Simple example of JNA interface mapping and usage. */
    public class HelloWorld {

        // This is the standard, stable way of mapping, which supports extensive
        // customization and mapping of Java to native types.

        public interface CLibrary extends Library {
            CLibrary INSTANCE = (CLibrary)
                Native.loadLibrary((Platform.isWindows() ? "msvcrt" : "c"),
                                   CLibrary.class);

            void printf(String format, Object... args);
        }

        public static void main(String[] args) {
            CLibrary.INSTANCE.printf("Hello, World\n");
            for (int i=0;i < args.length;i++) {
                CLibrary.INSTANCE.printf("Argument %d: %s\n", i, args[i]);
            }
        }
    }

Identify a native target library that you want to use. This can be any shared library with exported functions. Many examples of mappings for common system libraries, especially on Windows, may be found in the platform package.

Make your target library available to your Java program. There are two ways to do this:

* The preferred method is to set the `jna.library.path` system property to the path to your target library. This property is similar to `java.library.path`, but only applies to libraries loaded by JNA.
* Change the appropriate library access environment variable before launching the VM. This is `PATH` on Windows, `LD_LIBRARY_PATH` on Linux, and `DYLD_LIBRARY_PATH` on OSX.

Declare a Java interface to hold the native library methods by extending the Library interface.

Following is an example of mapping for the Windows kernel32 library.

    package com.sun.jna.examples.win32;

    import com.sun.jna.*;

    // kernel32.dll uses the __stdcall calling convention (check the function
    // declaration for "WINAPI" or "PASCAL"), so extend StdCallLibrary
    // Most C libraries will just extend com.sun.jna.Library,
    public interface Kernel32 extends StdCallLibrary { 
        // Method declarations, constant and structure definitions go here
    }

Within this interface, define an instance of the native library using the Native.loadLibrary(Class) method, providing the native library interface you defined previously.

    Kernel32 INSTANCE = (Kernel32)
        Native.loadLibrary("kernel32", Kernel32.class);
    // Optional: wraps every call to the native library in a
    // synchronized block, limiting native calls to one at a time
    Kernel32 SYNC_INSTANCE = (Kernel32)
        Native.synchronizedLibrary(INSTANCE);

The `INSTANCE` variable is for convenient reuse of a single instance of the library. Alternatively, you can load the library into a local variable so that it will be available for garbage collection when it goes out of scope. A Map of options may be provided as the third argument to loadLibrary to customize the library behavior; some of these options are explained in more detail below. The `SYNC_INSTANCE` is also optional; use it if you need to ensure that your native library has only one call to it at a time.

Declare methods that mirror the functions in the target library by defining Java methods with the same name and argument types as the native function (refer to the basic mappings below or the detailed table of type mappings). You may also need to declare native structures to pass to your native functions. To do this, create a class within the interface definition that extends Structure and add public fields (which may include arrays or nested structures). 

    public static class SYSTEMTIME extends Structure {
        public short wYear;
        public short wMonth;
        public short wDayOfWeek;
        public short wDay;
        public short wHour;
        public short wMinute;
        public short wSecond;
        public short wMilliseconds;
    }

    void GetSystemTime(SYSTEMTIME result);

You can now invoke methods on the library instance just like any other Java class.

    Kernel32 lib = Kernel32.INSTANCE;
    SYSTEMTIME time = new SYSTEMTIME();
    lib.GetSystemTime(time);

    System.out.println("Today's integer value is " + time.wDay);

Alternatively, you may declare a class to hold your native methods, declare any number of methods with the `native` qualifier, and invoke `Native.register(String)` in the class static initializer with your library's name. See [JNA Direct Mapping](DirectMapping.md) for an example.

If the C header files for your library are available, you can auto-generate a library mapping by using Olivier Chafik's excellent [JNAerator](http://jnaerator.googlecode.com/) utility. This is especially useful if your library uses long or complicated structures where translating by hand can be error-prone.


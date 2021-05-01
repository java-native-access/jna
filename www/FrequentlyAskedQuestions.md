Frequently Asked Questions
==========================

I'm having trouble generating correct library mappings
------------------------------------------------------
Make sure you've read [this page](https://github.com/java-native-access/jna/blob/master/www/Mappings.md) and [this one](http://java-native-access.github.io/jna/4.2.1/overview-summary.html#overview.description).  Try [JNAerator](https://github.com/nativelibs4java/JNAerator).  If you find its output too verbose, delete the mappings you don't need, or copy out the ones you do need.

JNA is missing function XXX in its platform library mappings
------------------------------------------------------------
No, it's not, it's just waiting for you to add it :)
```java
public interface MyUser32 extends User32 {
    // DEFAULT_OPTIONS is critical for W32 API functions to simplify ASCII/UNICODE details
    MyUser32 INSTANCE = (MyUser32)Native.load("user32", W32APIOptions.DEFAULT_OPTIONS);
    void ThatFunctionYouReallyNeed();
}
```
That's all it takes. If you'd like to submit the change back to JNA, make sure you provide a change log entry and corresponding test that invokes the function to prove that the mapping works.  We don't really care what the API actually does, the call can be a very minimal invocation, but should ensure all the parameters are correctly passed and that you get a reasonable return value.

Calling `Native.load()` causes an UnsatisfiedLinkError
------------------------------------------------------

Set the system property `jna.debug_load=true`, and JNA will print its library 
search steps to the console. `jna.debug_load.jna` will trace the search for 
JNA's own native support.

If the native prefix value is misdetected. It can be overriden by the 
`jna.prefix` system property. For example if the binary running the JVM follows
the arm softfloat ABI, you can specify the `armel` prefix like this:

```
java -Djna.prefix=linux-armel <normalCall>
```

My library mapping causes an UnsatisfiedLinkError
-------------------------------------------------

Use a dump utility to examine the names of your exported functions to make sure they match (nm on linux, [depends](http://www.dependencywalker.com/) on Windows). On Windows, if the functions have a suffix of the form "@NN", you need to pass a `StdCallFunctionMapper` as an option when initializing your library interface. In general, you can use a function mapper (`FunctionMapper`) to change the name of the looked-up method, or an invocation mapper (`InvocationMapper`) for more extensive control over the method invocation.

How do I map a native `long` type?
----------------------------------

Actually, no one ever asks this question, but they really need the answer. **Do not** use Java `long`!

On Windows, you can use a Java `int`, since the native long type is always 32 bits. On any other platform, the type may be 32 or 64 bits, so you should use the `NativeLong` type to ensure the proper size is used.

When should I use `Structure.ByReference`? `Structure.ByValue`? `Structure[]`?
------------------------------------------------------------------------------

Find your corresponding native declaration below:
```c
typedef struct _simplestruct {
  int myfield;
} simplestruct;

typedef struct _outerstruct {
  simplestruct nested; // use Structure
} outerstruct;

typedef struct _outerstruct2 {
  simplestruct *byref; // use Structure.ByReference
} outerstruct2;

typedef struct _outerstruct3 {
  simplestruct array[4]; // use Structure[]
} outerstruct3;

typedef struct _outerstruct4 {
  simplestruct* ptr_array[4]; // use Structure.ByReference[]
} outerstruct4;

// Field is a pointer to an array of struct
typedef struct _outerstruct5 {
  simplestruct* ptr_to_array; // use Structure.ByReference, and use
                              // Structure.toArray() to allocate the array, 
                              // then assign the first array element to the field
} outerstruct5;

// struct pointers as return value or argument
simplestruct *myfunc(); // use Structure
void myfunc(simplestruct* data); // use Structure
void myfunc(simplestruct* data_array, int count); // use Structure[], and use Structure.toArray() to generate the array
void myfunc(simplestruct** data_array, int count); // use Structure.ByReference[]

// struct (by value) as return value or argument
// use Structure.ByValue
simplestruct myfunc();
void myfunc(simplestruct);
```

If you need a `ByValue` or `ByReference` class, define them within your main `Structure` definition like this:
```java
public class MyStructure extends Structure {
  public static class ByValue extends MyStructure implements Structure.ByValue { }
  public static class ByReference extends MyStructure implements Structure.ByReference { }
}
```
How do I read back a function's string result?
----------------------------------------------

Suppose you have a function:
```c
// Example A: Returns the number of characters written to the buffer
int getString(char* buffer, int bufsize);
// Example B: Returns the number of characters written to the buffer
int getUnicodeString(wchar_t* buffer, int bufsize);
```
```java
// Mapping A:
int getString(byte[] buf, int bufsize);
// Mapping B:
int getUnicodeString(char[] buf, int bufsize);

byte[] buf = new byte[256];
int len = getString(buf, buf.length);
String normalCString = Native.toString(buf);
String embeddedNULs = new String(buf, 0, len);
```
The native code is expecting a fixed-size buffer, which it will fill in with the requested data. A Java `String` is not appropriate here, since Strings are immutable. Nor is a Java `StringBuffer`, since the native code only fills the buffer and does not change its size. The appropriate argument type would be either `byte[]`, `Memory`, or an NIO Buffer, with the size of the object passed as the second argument. The method `Native.toString(byte[])` may then be used to convert the array of byte into a Java String.
```c
// Example A: Returns a C string directly
const char* getString();
// Example B: Returns a wide character C string directly
const wchar_t* getString();
```
     
If the string is returned directly, your Java mapping can use the `String` or `WString` type as a return value (as appropriate).
Note that if the native code allocates memory for the string, you should return `Pointer` instead so that you can free the memory
at some later point.
```java
// Mapping A
String getString();
// Mapping B
WString getString();
// Mapping C, if native code allocates memory
// Use Pointer.getString(0) to extract the String data,
// then call the recommended native method with the Pointer
// value to free the memory
Pointer getString();
```
My library sometimes causes a VM crash
--------------------------------------

Double check the signature of the method causing the crash to ensure all arguments are of the appropriate size and type. Be especially careful with native pointer variations. See also information on debugging structure definitions.

My Windows library mapping causes a VM crash on every call
----------------------------------------------------------

If your library uses the stdcall calling convention, your interface should extend the `StdCallLibrary` interface. Using the wrong calling convention for a library will usually cause a VM crash.

How do I get an arbitrary Pointer value?
----------------------------------------

First, you probably don't actually want an arbitrary value. Ask yourself what you're really trying to do. Remember, type safety is your friend.

`Pointer.createConstant()` should be used when you need a special value that is not really a pointer (`NULL` usually serves this purpose, but some C programmers like to check pointers for special integer values instead). The `Pointer` produced by this function can't actually be used to access memory. `Pointer.share()` can be used to generate a new `Pointer` as an offset from an existing one. `java.nio.Buffer` can be used to wrap a Java array with a different offset and length than the original.

Clean up the sloppy C code by declaring an appropriate function interface. If your function in C takes either a `Pointer` or an integer type, simply declare both method signatures in your JNA interface. They will both invoke the same function, but you get the added benefit of type checking on the arguments.

If you really, really, *have* to convert an integer value into a `Pointer`, use the `Pointer(long)` constructor.


Debugging Structure Definitions
-------------------------------

Normally when you invoke `toString` on a `Structure`, it will print each defined field with its calculated memory offset. If when launching the VM, you pass it `"-Djna.dump_memory=true"`, `toString` will also dump the contents of the corresponding native memory. This is useful to determine if you've added or omitted a field, or chosen an incorrect size. Viewing the memory as bytes usually makes it clear where field boundaries should be, assuming the memory has been initialized by native code.

Does JNA work with J2ME/Windows CE/Mobile?
------------------------------------------

There is an implementation included in the regular JNA distribution built with cegcc and tested against phoneME.

Why does the VM sometimes crash in my shutdown hook on Windows?
---------------------------------------------------------------

If you are using direct mapping, make sure you keep a reference to the JNA class `com.sun.jna.Native` until your shutdown hook completes. If you are using interface mapping, your library proxy will be keeping a reference internally, so an explicit reference is not required.

If JNA unpacks its native code from its own jar file, it saves it in a temporary location and attempts to remove it when the `Native` class is finalized (which may or may not happen as the VM exits). In order to do so, it must first unload its native library from memory.

Alternatively, if the `jnidispatch.dll` native library is found in the system library load path, JNA will not attempt to unload it, although your shutdown hook must still ensure that the JNA classes you wish to use have not been GC'd.

I get an UnsatisfiedLinkError on OSX when I provide my native library via Java Web Start
----------------------------------------------------------------------------------------

Libraries loaded via the JNLP class loader on OSX must be named with a .jnilib suffix. The class loader won't find resources included with the `nativelib` tag if they have a .dylib suffix.

How does JNA performance compare to custom JNI?
-----------------------------------------------

JNA direct mapping can provide performance near that of custom JNI. Nearly all the type mapping features of interface mapping are available, although automatic type conversion will likely incur some overhead.

The calling overhead for a single native call using JNA interface mapping can be an order of magnitude (~10X) greater time than equivalent custom JNI (whether it actually does in the context of your application is a different question). In raw terms, the calling overhead is on the order of hundreds of microseconds instead of tens of microseconds. Note that that's the call overhead, not the total call time. This magnitude is typical of the difference between systems using dynamically-maintained type information and systems where type information is statically compiled. JNI hard-codes type information in the method invocation, where JNA interface mapping dynamically determines type information at runtime.

You might expect a speedup of about an order of magnitude moving to JNA direct mapping, and a factor of two or three moving from there to custom JNI. The actual difference will vary depending on usage and function signatures. As with any optimization process, you should determine first where you need a speed increase, and then see how much difference there is by performing targeted optimizations. The ease of programming everything in Java usually outweighs small performance gains when using custom JNI.

JNA COM support
---------------
There are two implementations to support COM in conjunction with JNA directly.
Please see the [PlatformLibrary](https://github.com/java-native-access/jna/blob/master/www/PlatformLibrary.md)
documentation for more information and use the jna user group to report your 
experience with the JNA COM support. 

Additional resource that should be checked are JACOB or com4j, both of which can 
parse a COM interface definition and generate a Java object to match it.  
JNAerator is also working on generating COM bindings.

JNA on Android
--------------

To use JNA on Android add the following dependency to gradle (note the `@aar`):

```
compile 'net.java.dev.jna:jna:4.4.0@aar'
```

If you're using Proguard, you should also add the following to your Proguard rules:

```
-dontwarn java.awt.*
-keep class com.sun.jna.* { *; }
-keepclassmembers class * extends com.sun.jna.* { public *; }
```

On Windows, MSDN shows that TheFuncName is in somelib.dll but JNA throws UnsatisifiedLinkError: The specified procedure could not be found.
-------------------------------------------------------------------------------------------------------------------------------------------

Make sure you're on a version of Windows that contains the function.

Some windows functions are documented as TheFuncName but are actually implemented 
as TheFuncNameA and TheFuncNameW in the library. You can see this with 
Dependency Walker (depends.exe) from http://dependencywalker.com when you open 
the DLL. 

The convention is, that the “A” suffix indicates a function expecting ANSI/
windows code page encoding and a “W” suffix indicates a function expecting wide
(unicode, UTF-16) strings.

JNA won't automatically resolve one or the other variant. You should use a
a combination of TypeMapper and FunctionMapper (see 
`com.sun.jna.win32.W32APIOptions.DEFAULT_OPTIONS`) so that you can leave off the 
“-A” or “-W” suffix (you never need to use both simultaneously) and use 
“String” rather than explicit “WString”.

Does JNA publish a module descriptor (module-info.java) to support the Java Module System (JPMS)?
-------------------------------------------------------------------------------------------------------------------------------------------

Since version 5.8.0, JNA publishes an additional JAR with a `module-info` class alongside
the main project JAR, using an artifact with `-jpms` appended, e.g., `jna-jpms-5.8.0.jar`
and `jna-platform-jpms-5.8.0.jar`.  For a Maven build, use the following dependency statement:
```
<dependency>
  <groupId>net.java.dev.jna</groupId>
  <artifactId>jna-jpms</artifactId>
  <version>5.8.0</version>
</dependency>
```
and include `requires com.sun.jna;` in your module descriptor in your `module-info.java` file.

If you use the `jna-platform` user-contributed mappings:
```
<dependency>
  <groupId>net.java.dev.jna</groupId>
  <artifactId>jna-platform-jpms</artifactId>
  <version>5.8.0</version>
</dependency>
```
and include `requires com.sun.jna.platform;` in your module descriptor.

If you have a library that may be consumed by downstream users, consider making these managed dependencies.

In addition to adding the `requires` directive, note that some JNA classes designed
for inheritance make use of reflection to access constructors and/or fields of the subclass.
Reflection is disabled by the module system's strong encapsulation.  It may be necessary to
make packages which include subclasses of JNA's classes (such as `Structure` and
`PointerType` among others) accessible via reflection to the `com.sun.jna` module
using either an `open` module, `opens`, or `opens ... to` directive, or an `exports` 
or `exports ... to` directive, depending on the particular application and level of 
access required. If migrating an existing project, `opens` replicates the full
non-modular (classpath) reflective access.

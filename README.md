![JNA](https://github.com/twall/jna/raw/master/www/images/jnalogo.jpg "Java Native Access (JNA)")

Java Native Access (JNA)
========================

JNA provides Java programs easy access to native shared libraries (DLLs on Windows) without writing anything but Java code—no JNI or native code is required. This functionality is comparable to Windows' Platform/Invoke and Python's ctypes. Access is dynamic at runtime without code generation.

JNA allows you to call directly into native functions using natural Java method invocation. The Java call looks just like it does in native code. Most calls require no special handling or configuration; no boilerplate or generated code is required.

The JNA library uses a small native library stub to dynamically invoke native code. The developer uses a Java interface to describe functions and structures in the target native library. This makes it quite easy to take advantage of native platform features without incurring the high overhead of configuring and building JNI code for multiple platforms.

While some attention is paid to performance, correctness and ease of use take priority.

JNA includes a platform library with many native functions already mapped as well as a set of utility interfaces that simplify native access.

Features
========

* Automatic mapping from Java to native functions, with simple mappings for all primitive data types
* Runs on most platforms which support Java
* Automatic conversion between C and Java strings, with customizable encoding/decoding
* Structure and Union arguments/return values, by reference and by value
* Function Pointers, (callbacks from native code to Java) as arguments and/or members of a struct
* Auto-generated Java proxies for native function pointers
* By-reference (pointer-to-type) arguments
* Java array and NIO Buffer arguments (primitive types and pointers) as pointer-to-buffer
* Nested structures and arrays
* Wide (wchar_t-based) strings
* Native long support (32- or 64-bit as appropriate)
* Demo applications
* Supported on 1.4 or later JVMs (earlier VMs may work with stubbed NIO support)
* Customizable marshalling/unmarshalling (argument and return value conversions)
* Customizable mapping from Java method to native function name, and customizable invocation to simulate C preprocessor function macros
* Support for automatic Windows ASCII/UNICODE function mappings
* Varargs support
* Type-safety for native pointers
* VM crash protection (optional)
* Optimized direct mapping for high-performance applications.

Community
=========

* TODO

Using the Library
=================

* [Getting Started](jna/tree/master/www/GettingStarted.md)
* [Mapping between Java and Native](jna/tree/master/www/Mappings.md)
* [Using Pointers and Arrays](jna/tree/master/www/PointersAndArrays.md)
* [Using Structures and Unions](jna/tree/master/www/StructuresAndUnions.md)
* [Using By-Reference Arguments](jna/tree/master/www/ByRefArguments.md)
* [Customization](jna/tree/master/www/Customization.md)
* [Callbacks/Closures](jna/tree/master/www/Callbacks.md)
* [JRuby/Jython Usage](jna/tree/master/www/JRubyJython.md)
* [Frequently Asked Questions (FAQ)](jna/tree/master/www/FAQ.md)
* [Direct Method Mapping](jna/tree/master/www/DirectMapping.md)

Contributing 
============

* TODO

License
=======

This library is provided under the LGPL, version 2.1 or later.

*NOTE: Oracle is not sponsoring this project, even though the package name (com.sun.jna) might imply otherwise.*


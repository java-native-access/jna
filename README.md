![Java Native Access - JNA](https://github.com/twall/jna/raw/master/www/images/jnalogo.jpg "Java Native Access - JNA")

[![Build Status](https://travis-ci.org/twall/jna.svg?branch=master)](https://travis-ci.org/twall/jna)

Java Native Access (JNA)
========================

The definitive JNA reference (including an overview and usage details) is in the [JavaDoc](http://twall.github.com/jna/4.1.0).  Please read the [overview](http://twall.github.io/jna/4.1.0/overview-summary.html#overview_description).  Questions, comments, or exploratory conversations should begin on the [mailing list](http://groups.google.com/group/jna-users), although you may find it easier to find answers to already-solved problems on [StackOverflow](http://stackoverflow.com/questions/tagged/jna).

JNA provides Java programs easy access to native shared libraries without writing anything but Java code - no JNI or native code is required. This functionality is comparable to Windows' Platform/Invoke and Python's ctypes.

JNA allows you to call directly into native functions using natural Java method invocation. The Java call looks just like the call does in native code. Most calls require no special handling or configuration; no boilerplate or generated code is required.

JNA uses a small JNI library stub to dynamically invoke native code. The developer uses a Java interface to describe functions and structures in the target native library. This makes it quite easy to take advantage of native platform features without incurring the high overhead of configuring and building JNI code for multiple platforms.

While significant attention has been paid to performance, correctness and ease of use take priority.

In addition, JNA includes a platform library with many native functions already mapped as well as a set of utility interfaces that simplify native access.

Projects Using JNA
==================
JNA is a mature library with dozens of contributors and hundreds of commercial and non-commercial projects that use it.  If you're using JNA, feel free to [tell us about it](http://groups.google.com/group/jna-users).  Include some details about your company, project name, purpose and size and tell us how you use the library.

Download
========

Version 4.1.0

* [jna.jar](https://maven.java.net/content/repositories/releases/net/java/dev/jna/jna/4.1.0/jna-4.1.0.jar)
* [jna-platform.jar](https://maven.java.net/content/repositories/releases/net/java/dev/jna/jna-platform/4.1.0/jna-platform-4.1.0.jar)

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
* [Demo applications/examples](https://github.com/twall/jna/tree/master/contrib)
* Supported on 1.4 or later JVMs, including JavaME (earlier VMs may work with stubbed NIO support)
* Customizable marshalling/unmarshalling (argument and return value conversions)
* Customizable mapping from Java method to native function name, and customizable invocation to simulate C preprocessor function macros
* Support for automatic Windows ASCII/UNICODE function mappings
* Varargs support
* Type-safety for native pointers
* VM crash protection (optional)
* Optimized direct mapping for high-performance applications.
* COM support for early and late binding.
* COM/Typelib java code generator.

Community and Support
=====================

All questions should be posted to the [jna-users Google group](http://groups.google.com/group/jna-users). Issues can be submitted [here on Github](https://github.com/twall/jna/issues).

When posting to the mailing list, please include the following:

* What OS/CPU/architecture you're using (e.g. Windows 7 64-bit)
* Reference to your native interface definitions (i.e. C headers), if available
* The JNA mapping you're trying to use
* VM crash logs, if any
* Example native usage, and your attempted Java usage

It's nearly impossible to indicate proper Java usage when there's no native
reference to work from.

For commercial support, please contact twalljava [at] java [dot] net.

Using the Library
=================

* [Getting Started](https://github.com/twall/jna/blob/master/www/GettingStarted.md)
* [Mapping between Java and Native](https://github.com/twall/jna/blob/master/www/Mappings.md)
* [Using Pointers and Arrays](https://github.com/twall/jna/blob/master/www/PointersAndArrays.md)
* [Using Structures and Unions](https://github.com/twall/jna/blob/master/www/StructuresAndUnions.md)
* [Using By-Reference Arguments](https://github.com/twall/jna/blob/master/www/ByRefArguments.md)
* [Customization of Type Mapping](https://github.com/twall/jna/blob/master/www/CustomMappings.md)
* [Callbacks/Function Pointers/Closures](https://github.com/twall/jna/blob/master/www/CallbacksAndClosures.md)
* [Dynamically Typed Languages (JRuby/Jython)](https://github.com/twall/jna/blob/master/www/DynamicallyTypedLanguages.md)
* [Platform Library](https://github.com/twall/jna/blob/master/www/PlatformLibrary.md)
* [Direct Method Mapping](https://github.com/twall/jna/blob/master/www/DirectMapping.md) (Optimization)
* [Frequently Asked Questions (FAQ)](https://github.com/twall/jna/blob/master/www/FrequentlyAskedQuestions.md)
* [Avoiding Crashes](http://twall.github.com/jna/4.1.0/javadoc/overview-summary.html#crash-protection)

Primary Documentation (JavaDoc)
===============================

The definitive JNA reference is in the [JavaDoc](http://twall.github.com/jna/4.1.0/).

Developers
==========

* [Contributing to JNA](https://github.com/twall/jna/blob/master/www/Contributing.md)
* [Setting up a Windows Development Environment](https://github.com/twall/jna/blob/master/www/WindowsDevelopmentEnvironment.md)
* [Setting up an Android Development Environment](https://github.com/twall/jna/blob/master/www/AndroidDevelopmentEnvironment.md)
* [Releasing JNA](https://github.com/twall/jna/blob/master/www/ReleasingJNA.md)
* [Publishing to Maven Central](https://github.com/twall/jna/blob/master/www/PublishingToMavenCentral.md)

Contributing 
============

You're encouraged to contribute to JNA. Fork the code from [https://github.com/twall/jna](https://github.com/twall/jna) and submit pull requests.

For more information on setting up a development environment see [Contributing to JNA](https://github.com/twall/jna/blob/master/www/Contributing.md).

If you are interested in paid support, feel free to say so on the [jna-users mailing list](http://groups.google.com/group/jna-users). Most simple questions will be answered on the list, but more complicated work, new features or target platforms can be negotiated with any of the JNA developers (this is how several of JNA's features came into being). You may even encounter other users with the same need and be able to cost share the new development.

License
=======

This library is licensed under the LGPL, version 2.1 or later, and (from version 4.0 onward) the Apache Software License, version 2.0. Commercial license arrangements are negotiable.

*NOTE: Oracle is not sponsoring this project, even though the package name (com.sun.jna) might imply otherwise.*



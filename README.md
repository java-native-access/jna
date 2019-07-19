![Java Native Access - JNA](https://github.com/java-native-access/jna/raw/master/www/images/jnalogo.jpg "Java Native Access - JNA")

[![Build Status](https://travis-ci.org/java-native-access/jna.svg?branch=master)](https://travis-ci.org/java-native-access/jna)
[![Build status](https://ci.appveyor.com/api/projects/status/j6vmpjrw5iktb8iu/branch/master?svg=true)](https://ci.appveyor.com/project/dblock/jna-gsxuq/branch/master)

Java Native Access (JNA)
========================

The definitive JNA reference (including an overview and usage details) is in the [JavaDoc](http://java-native-access.github.io/jna/5.4.0/javadoc/).  Please read the [overview](http://java-native-access.github.io/jna/5.4.0/javadoc/overview-summary.html#overview_description).  Questions, comments, or exploratory conversations should begin on the [mailing list](http://groups.google.com/group/jna-users), although you may find it easier to find answers to already-solved problems on [StackOverflow](http://stackoverflow.com/questions/tagged/jna).

JNA provides Java programs easy access to native shared libraries without writing anything but Java code - no JNI or native code is required. This functionality is comparable to Windows' Platform/Invoke and Python's ctypes.

JNA allows you to call directly into native functions using natural Java method invocation. The Java call looks just like the call does in native code. Most calls require no special handling or configuration; no boilerplate or generated code is required.

JNA uses a small JNI library stub to dynamically invoke native code. The developer uses a Java interface to describe functions and structures in the target native library. This makes it quite easy to take advantage of native platform features without incurring the high overhead of configuring and building JNI code for multiple platforms.  Read this [more in-depth description](https://github.com/java-native-access/jna/blob/master/www/FunctionalDescription.md).

While significant attention has been paid to performance, correctness and ease of use take priority.

In addition, JNA includes a platform library with many native functions already mapped as well as a set of utility interfaces that simplify native access.

Projects Using JNA
==================
JNA is a mature library with dozens of contributors and hundreds of commercial and non-commercial projects that use it.  If you're using JNA, feel free to [tell us about it](http://groups.google.com/group/jna-users).  Include some details about your company, project name, purpose and size and tell us how you use the library.

- [Apache Cassandra](http://cassandra.apache.org): Large-scale NoSQL data store.
- [Rococoa](https://github.com/iterate-ch/rococoa): Java bindings to the Mac OS X Cocoa framework, by Duncan McGregor.
- [jna-posix](http://kenai.com/projects/jna-posix): Common POSIX Functions for Java.
- [JNAerator](https://github.com/nativelibs4java/JNAerator): Pronounced "generator", auto-generates JNA mappings from C headers, by Olivier Chafik.
- [Freedom for Media in Java](http://fmj.sf.net) by Ken Larson/Dieter Krachtus.
- [gstreamer for Java](http://code.google.com/p/gstreamer-java) by Wayne Meissner.
- [Videolan](http://trac.videolan.org/jvlc/): JVLC Java Multimedia Library.
- [SVNKit](http://svnkit.com): Pure Java Subversion client library.
- [OmegaT Computer-Aided Translation](http://omegat.sf.net).
- [IntelliJ IDEA](http://jetbrains.com) by JetBrains.
- [NetBeans IDE](http://netbeans.org).
- [Athena Backup](http://www.athenabackup.com) by Doug Patriarche.
- [FileBot Media Renamer](http://www.filebot.net) by Reinhard Pointner.
- [USB for Java](https://launchpad.net/libusb4j) by Mario Boikov.
- [Waffle](https://github.com/dblock/waffle): Enables SSO on Windows in Java applications, by Daniel Doubrovkine.
- [leveldb-jna](https://github.com/protonail/leveldb-jna): Cross-platform JNA based adapter for [LevelDB](https://github.com/google/leveldb) (used in [Keylord](http://protonail.com)).
- [bolt-jna](https://github.com/protonail/bolt-jna): Cross-platform JNA based adapter for [Bolt](https://github.com/boltdb/bolt) (used in [Keylord](http://protonail.com)). It is show how to use JNA for binding to Go library.
- [JVM OpenVR Bindings](https://github.com/kotlin-graphics/openvr).
- [Apache Ignite](https://ignite.apache.org/): Direct IO plugin

*Interesting Investigations/Experiments*

- [Drive Lego Mindstorm NXT](http://epirsch.blogspot.com/2008/02/jna-love-nxt.html) by Emmanuel Pirsch.
- [Detect User Inactivity](http://ochafik.free.fr/blog/?p=98) by Olivier Chafik.
- [IAXClient Applet](http://callino.cc/jiaxcapplet/) provides VOIP for Java, by Wolfgang Pichler.

There are also a number of examples and projects within the `contrib` directory of the JNA project itself.

Supported Platforms
===================
JNA will build on most linux-like platforms with a reasonable set of GNU tools and a JDK.  See the native [Makefile](https://raw.githubusercontent.com/java-native-access/jna/master/native/Makefile) for native configurations that have been built and tested.  If your platform is supported by [libffi](http://en.wikipedia.org/wiki/Libffi), then chances are you can build JNA for it.

Pre-built platform support may be found [here](https://github.com/java-native-access/jna/tree/master/lib/native).

Download
========

Version 5.4.0

JNA
---

[![Maven Central](https://img.shields.io/maven-central/v/net.java.dev.jna/jna.svg?label=Maven%20Central)](https://search.maven.org/artifact/net.java.dev.jna/jna/5.4.0/jar)&nbsp;[jna-5.4.0.jar](http://repo1.maven.org/maven2/net/java/dev/jna/jna/5.4.0/jna-5.4.0.jar)

This is the core artifact of JNA and contains only the binding library and the
core helper classes.

JNA Platform
------------

[![Maven Central](https://img.shields.io/maven-central/v/net.java.dev.jna/jna-platform.svg?label=Maven%20Central)](https://search.maven.org/artifact/net.java.dev.jna/jna-platform/5.4.0/jar)&nbsp;[jna-platform-5.4.0.jar](http://repo1.maven.org/maven2/net/java/dev/jna/jna-platform/5.4.0/jna-platform-5.4.0.jar)

This artifact holds cross-platform mappings and mappings for a number of commonly used platform 
functions, including a large number of Win32 mappings as well as a set of utility classes 
that simplify native access. The code is tested and the utility interfaces ensure that
native memory management is taken care of correctly.

See [PlatformLibrary.md](https://github.com/java-native-access/jna/blob/master/www/PlatformLibrary.md) for details.

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
* [Demo applications/examples](https://github.com/java-native-access/jna/tree/master/contrib)
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

All questions should be posted to the [jna-users Google group](http://groups.google.com/group/jna-users). Issues can be submitted [here on Github](https://github.com/java-native-access/jna/issues).

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

* [Getting Started](https://github.com/java-native-access/jna/blob/master/www/GettingStarted.md)
* [Functional Description](https://github.com/java-native-access/jna/blob/master/www/FunctionalDescription.md).
* [Mapping between Java and Native](https://github.com/java-native-access/jna/blob/master/www/Mappings.md)
* [Using Pointers and Arrays](https://github.com/java-native-access/jna/blob/master/www/PointersAndArrays.md)
* [Using Structures and Unions](https://github.com/java-native-access/jna/blob/master/www/StructuresAndUnions.md)
* [Using By-Reference Arguments](https://github.com/java-native-access/jna/blob/master/www/ByRefArguments.md)
* [Customization of Type Mapping](https://github.com/java-native-access/jna/blob/master/www/CustomMappings.md)
* [Callbacks/Function Pointers/Closures](https://github.com/java-native-access/jna/blob/master/www/CallbacksAndClosures.md)
* [Dynamically Typed Languages (JRuby/Jython)](https://github.com/java-native-access/jna/blob/master/www/DynamicallyTypedLanguages.md)
* [Platform Library](https://github.com/java-native-access/jna/blob/master/www/PlatformLibrary.md)
* [Direct Method Mapping](https://github.com/java-native-access/jna/blob/master/www/DirectMapping.md) (Optimization)
* [Frequently Asked Questions (FAQ)](https://github.com/java-native-access/jna/blob/master/www/FrequentlyAskedQuestions.md)
* [Avoiding Crashes](http://java-native-access.github.io/jna/5.4.0/javadoc/overview-summary.html#crash-protection)

Primary Documentation (JavaDoc)
===============================

The definitive JNA reference is in the [JavaDoc](http://java-native-access.github.io/jna/5.4.0/javadoc/).

Developers
==========

* [Contributing to JNA](https://github.com/java-native-access/jna/blob/master/www/Contributing.md)
* [Setting up a Windows Development Environment](https://github.com/java-native-access/jna/blob/master/www/WindowsDevelopmentEnvironment.md)
* [Setting up an Android Development Environment](https://github.com/java-native-access/jna/blob/master/www/AndroidDevelopmentEnvironment.md)
* [Setting up a RaspberryPi Development Environment](https://github.com/java-native-access/jna/blob/master/www/RaspberryPiDevelopmentEnvironment.md)
* [Setting up a Mac Development Environment](https://github.com/java-native-access/jna/blob/master/www/MacDevelopmentEnvironment.md)
* [Releasing JNA](https://github.com/java-native-access/jna/blob/master/www/ReleasingJNA.md)
* [Publishing to Maven Central](https://github.com/java-native-access/jna/blob/master/www/PublishingToMavenCentral.md)

Contributing
============

You're encouraged to contribute to JNA. Fork the code from [https://github.com/java-native-access/jna](https://github.com/java-native-access/jna) and submit pull requests.

For more information on setting up a development environment see [Contributing to JNA](https://github.com/java-native-access/jna/blob/master/www/Contributing.md).

If you are interested in paid support, feel free to say so on the [jna-users mailing list](http://groups.google.com/group/jna-users). Most simple questions will be answered on the list, but more complicated work, new features or target platforms can be negotiated with any of the JNA developers (this is how several of JNA's features came into being). You may even encounter other users with the same need and be able to cost share the new development.

License
=======

This library is licensed under the LGPL, version 2.1 or later, and (from version 4.0 onward) the Apache Software License, version 2.0. Commercial license arrangements are negotiable.

*NOTE: Oracle is not sponsoring this project, even though the package name (com.sun.jna) might imply otherwise.*



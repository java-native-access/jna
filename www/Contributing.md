Contributing to JNA
===================

JNA contains work from many developers. You're encouraged to contribute to both JNA's core `jna.jar` as well as platform-specific `platform.jar` libraries.

- Install Git and configure it to work with Github
- Fork the code from [github.com/java-native-access/jna](https://github.com/java-native-access/jna)
- Check out the code with `git clone git@github.com:username/jna.git`
- Ensure you can build the project with `ant dist test test-platform`
- Make your code changes, write tests, build
- Add entry to CHANGES.md describing the change
- Submit pull requests, forks and/or topical branches are encouraged.

Build Environment
=================
Most non-windows environments should work out of the box if you have make,
gcc, autotools (for libffi), ant (1.8+), a JDK (1.4+), and a few other typical
command-line utilities available.  Feel free to report any issues, we'll
generally pull build fixes immediately. 

Native bits are built by invoking `ant native`.  The build system is configured
to rebuild the native library automaticly if necessary. It's
safe to skip the native build as long as your modifications are restricted to
Java code.

For debian-style installs,

    % apt-get install git ant openjdk-6-jdk make autotools gcc

For most unix-like systems:

    % git clone git@github.com:java-native-access/jna
    % ant dist test test-platform

For Windows, see [Windows Development Environment](WindowsDevelopmentEnvironment.md).

For windows CE/Mobile 6.x, you'll need cegcc (http://gitorious.org/cegcc) for
cross-compiling and a JavaME implementation (phoneME (http://davy.preuveneers.be/phoneme) works well).

For Android, see [Android Development Environment](AndroidDevelopmentEnvironment.md).

Code Conventions
================

JNA is a community maintained project. 

Code conventions (and their enforcement) were not established early, and implementing new standards now is just not practical. With thousands of commits by more than a hundred committers over more than a decade, there are a variety of styles/conventions used and it may be difficult for new contributors to ascertain code formatting, naming, or other conventions. Strive for consistency when committing, and consider style/naming/formatting requests from maintainers as an attempt to adhere to some sort of unwritten standard learned through experience.

While difficult to explicitly define and enforce, the following general guidelines should help your contributions succeed:
 - Strive to avoid changes unrelated your own submission. Occasional minor re-ordering of imports or whitespace adjustments near your contribution are inevitable, but avoid situations where the "noise" of your contribution exceeds the substantive change.
 - When mapping native functions, attempt to use the same naming convention for the function name and its parameters. This may violate traditional Java naming conventions of casing or use of underscores. While not always possible, consider alignment with the native mapping a higher priority.
   - Similarly, extract the substantive parts of the native method documentation and include it in the javadocs.
 - Many mappings benefit from helper/utility functions. When considering where to place these:
   - If the method will only ever be used with a single class, place it in the class.  For "getter" helper methods, prefer Java Bean (`getFoo()`) syntax to record-style (`foo()`) syntax which implies shallow immutability. JNA relies on reflection and fields are necessarily public and thus not immutable.
   - If the function requires multiple method calls (e.g., to determine the size of output and allocate memory) place a helper method in a separate utility class. Do your best to minimize any user input/interpretation/casting requirements.
 - Respect backwards compatibility. The only acceptable compatibility changes (without a major version bump) are for functions that have demonstrably "never worked".
 - Realize that we still support JDK 6, and that users of the library still use JDK 6. While there are good arguments for moving to a higher minimum version with the next major version bump, your contribution is probably not one of those arguments.

Required Testing
================

Pull requests without tests will not be merged.

If you're struggling with a mapping test, consider that it's not really necessary to verify the proper functionality of the APIs that are being mapped. It may be sufficient to exercise the mapping with parameters that produce an expected platform error code, therefore demonstrating that the mapping actually works.

For mappings that may produce a nondeterministic result (e.g., a boolean dependent on environment), verifying that the called function does not throw an exception may be sufficient.

Copyright Headers in Files
==========================

If you're creating a new file, add a copyright notice and an LGPL 2.1 and AL2.0
license notice with your name or company on top of it.

      /* Copyright (c) 2011 Timothy Wall, All Rights Reserved
       * 
       * The contents of this file is dual-licensed under 2 
       * alternative Open Source/Free licenses: LGPL 2.1 or later and 
       * Apache License 2.0. (starting with JNA version 4.0.0).
       * 
       * You can freely decide which license you want to apply to 
       * the project.
       * 
       * You may obtain a copy of the LGPL License at:
       * 
       * http://www.gnu.org/licenses/licenses.html
       * 
       * A copy is also included in the downloadable source code package
       * containing JNA, in file "LGPL2.1".
       * 
       * You may obtain a copy of the Apache License at:
       * 
       * http://www.apache.org/licenses/
       * 
       * A copy is also included in the downloadable source code package
       * containing JNA, in file "AL2.0".
       */

If you're adding to an existing file, don't make any changes to the copyright.

Mapping Documentation
=====================

It's a good practice to copy a mapping's documentation and to edit it to describe Java types. For example, NULL-terminated strings are not a concern with JNA, therefore any notes referring to the fact that the return of a Win32 mapping is a null-terminated string is unnecessary.

Contributing Windows Win32 API Mappings
=======================================

Windows system mappings live in the `com.sun.jna.platform.win32` namespace. There're several rules of thumb to follow for mapping Win32 functions.

DLL functions are mapped into Unicode interfaces of the same name, like this

``` java
  /**
   * Advapi32.dll Interface.
   * @author dblock[at]dblock.org
   */
  public interface Advapi32 extends StdCallLibrary {
    Advapi32 INSTANCE = (Advapi32) Native.load("Advapi32", 
      Advapi32.class, W32APIOptions.UNICODE_OPTIONS);

    // function definitions go here

  }
```

Constants are defined in files matching the Platform SDK headers. For example, `CSIDL_DESKTOP` is defined in `shlobj.h` and is therefore declared in `ShlObj.java`.

``` java
  /**
   * Ported from ShlObj.h.
   * Microsoft Windows SDK 6.0A.
   * @author dblock[at]dblock.org
   */
  public interface ShlObj extends StdCallLibrary {
    public static final int CSIDL_DESKTOP = 0x0000; // desktop
  }
```

Utilities that wrap Win32 functions into more user-friendly implementations are defined in `Util` classes.

``` java
  /**
   * Winspool Utility API.
   * @author dblock[at]dblock.org
   */
  public abstract class WinspoolUtil {
    public static PRINTER_INFO_1[] getPrinterInfo1() {
      
    }
  }
```

Javadoc Pages
=============

Javadoc pages are published with [gh-pages](http://pages.github.com/) to a root branch. The official repository for JNA is [here](https://github.com/java-native-access/jna). The pages are located in the gh-pages branch. Here's how to pull and push the root branch to your local environment.

``` sh
git fetch origin
git checkout -b gh-pages origin/gh-pages
... update javadoc content ...
git push origin
```



Contributing to JNA
===================

JNA contains work from many developers. You're encouraged to contribute to both JNA's core `jna.jar` as well as platform-specific `platform.jar` libraries.

- Install Git and configure it to work with Github
- Fork the code from [github.com/twall/jna](https://github.com/twall/jna)
- Check out the code with `git clone git@github.com:username/jna.git`
- Ensure you can build the project with `ant dist test`
- Make your code changes, write tests, build
- Submit pull requests, forks and/or topical branches are encouraged.

Build Environment
=================
Most non-windows environments should work out of the box if you have make,
gcc, autotools (for libffi), ant (1.8+), a JDK (1.4+), and a few other typical
command-line utilities available.  Feel free to report any issues, we'll
generally pull build fixes immediately. 

Native bits are built by passing `-Dskip-native=false` to `ant`.  It's
safe to skip the native build as long as your modifications are restricted to
Java code.

For debian-style installs,

    % apt-get install git ant openjdk-6-jdk make autotools gcc

For most unix-like systems:

    % git clone git@github.com:twall/jna
    % ant dist test

For Windows, see [Windows Development Environment](WindowsDevelopmentEnvironment.md).

For windows CE/Mobile 6.x, you'll need cegcc (http://gitorious.org/cegcc) for
cross-compiling and a JavaME implementation (phoneME (http://davy.preuveneers.be/phoneme) works well).

For Android, see [Android Development Environment)(AndroidDevelopmentEnvironment.md).

Required Testing
================

Pull requests without tests will not be merged.

If you're struggling with a mapping test, consider that it's not really necessary to verify the proper functionality of the APIs that are being mapped. It may be sufficient to exercise the mapping with parameters that produce an expected platform error code, therefore demonstrating that the mapping actually works.

Copyright Headers in Files
==========================

If you're creating a new file, add a copyright notice and an LGPL license notice with your name or company on top of it.

      /* Copyright (c) 2011 Timothy Wall, All Rights Reserved
       * 
       * This library is free software; you can redistribute it and/or
       * modify it under the terms of the GNU Lesser General Public
       * License as published by the Free Software Foundation; either
       * version 2.1 of the License, or (at your option) any later version.
       * 
       * This library is distributed in the hope that it will be useful,
       * but WITHOUT ANY WARRANTY; without even the implied warranty of
       * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
       * Lesser General Public License for more details.  
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
    Advapi32 INSTANCE = (Advapi32) Native.loadLibrary("Advapi32", 
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

Javadoc pages are published with [gh-pages](http://pages.github.com/) to a root branch. The official repository for JNA is [here](http://twall.github.com/jna). Here's how to pull and push the root branch to your local environment.

``` sh
git fetch origin
git checkout -b gh-pages origin/gh-pages
... update javadoc content ...
git push origin
```



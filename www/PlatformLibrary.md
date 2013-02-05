Platform Library
================

JNA includes `platform.jar` that has cross-platform mappings and mappings for a number of commonly used platform functions, including a large number of Win32 mappings as well as a set of utility classes that simplify native access. The code is tested and the utility interfaces ensure that native memory management is taken care of correctly.

Before you map your own functions, check the platform package documentation for an already mapped one.

Platform-specific structures are mapped by header. For example, `ShlObj.h` structures can be found in `com.sun.jna.platform.win32.ShlObj`. Platform functions are mapped by library. For example, `Advapi32.dll` functions can be found in `com.sun.jna.platform.win32.Advapi32`. Simplified interfaces (wrappers) for `Advapi32.dll` functions can be found in `com.sun.jna.platform.win32.Advapi32Util`.

Cross-platform functions and structures are implemented in `com.sun.jna.platform`. These currently include the following.

* `FileMonitor`: a cross-platform file system watcher
* `FileUtils`: a cross-platform set of file-related functions, such as move to the recycle bin
* `KeyboardUtils`: a cross-platform set of keyboard functions, such as finding out whether a key is pressed
* `WindowUtils`: a cross-platform set of window functions, providing non-rectangular shaped and transparent windows.


COM support
===========

late-time-binding
-----------------

JNA includes basic Microsoft COM support in the package `com.sun.jna.platform.win32.COM.*`.
The COM support is being implemented with late-time-binding, vtable support is not yet available.
The straightforward approach is to extend the class `COMObject.class` as base class for all COM enabled java applications.

* `COMObject(String progId, boolean useActiveInstance)`: first parameter describes the `ProgID` (e.g. WinWord -> `Word.Application`), second parameter if a current running instance should be used.

* `COMObject.oleMethod`: provides the possability to call any kind of COM method like `Properties` and `Methods`. The 'oleMethod' should be used to create a COM method in the custom java wrapper, 

e.g. (part of the MSWord sample)   
`this.oleMethod(OleAuto.DISPATCH_PROPERTYPUT, result, this.iDispatch, "Visible", new VARIANT(bVisible));`

* `ITypeLib.java`: a wrapper for a `TypeLib` definition
* `IUnknown.java`: a wrapper for the `IUnknown` interface
* COMException.java: used as exception 
* `COMObject.java`:  base class for all COM enabled applications
* `COMUtils.java`: utility class
* `IDispatch.java`:  a wrapper for the `IDispatch` interface
* `IRecordInfo.java`:  a wrapper for the `IRecordInfo` interface
* `ITypeComp.java`:  a wrapper for the `ITypeComp` interface
* `ITypeInfo.java`:  a wrapper for the `ITypeInfo` interface, which describes a COM interface itself


Typelib parsing
---------------


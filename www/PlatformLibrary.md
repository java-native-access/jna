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

JNA contains two different approaches to binding COM object for access from 
java. Both mainly focus on late-time-binding/invoke usage. 

The first and older variant is found in the `com.sun.jna.platform.win32.COM`
package. The straightforward approach is to extend the class `COMBindingBaseObject` 
as base class for all COM enabled java applications.

* `COMBindingBaseObject(String clsid, boolean useActiveInstance)`: first parameter describes the `ProgID` (e.g. WinWord -> `Word.Application`), second parameter if a current running instance should be used.
* `COMBindingBaseObject#oleMethod`: provides the possability to call any kind of COM method like `Properties` and `Methods`. The 'oleMethod' should be used to create a COM method in the custom java wrapper, 

e.g. (part of the MSWord sample)   
`this.oleMethod(OleAuto.DISPATCH_PROPERTYPUT, result, this.iDispatch, "Visible", new VARIANT(bVisible));`

The approach means, that each method that is to be called needs to be manually
wrapped and all potential parameters need to be marshalled and return values
need to be manually unmarshalled.

While very flexible, the first approach has the drawback, that the same operation:
marshalling and unmarshalling needs to be done manually each time. Following
the example set by JNA itself, this approach is based on interfaces and using
a [dynamic proxy](https://docs.oracle.com/javase/7/docs/api/java/lang/reflect/Proxy.html) and an [InvocationHandler](https://docs.oracle.com/javase/7/docs/api/java/lang/reflect/InvocationHandler.html)
to centralize the marshalling and demarshalling.

The support for the second approach is demonstrated in the `com.sun.jna.platform.win32.COM.util`
package.

Both approaches are demonstrated in the [msoffice contrib project](https://github.com/java-native-access/jna/tree/master/contrib/msoffice).

A minimal VTable based call sample can be found in `com.sun.jna.platform.win32.COM.COMInvoker`.


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

For both above described approaches code generators exist. Included is a generator
for the first approach. Here is a sample call:

```
java -cp "dist/jna.jar;dist/jna-platform.jar" com.sun.jna.platform.win32.COM.tlb.TlbImp -tlb.id {50A7E9B0-70EF-11D1-B75A-00A0C90564FE} -tlb.major.version 1 -tlb.minor.version 0 -bind.mode dispId -output.dir outputdir
```

That call generates the bindings for the Microsoft Shell Controls.

For the second approach a code generator exists out of tree:

https://github.com/matthiasblaesing/TlbCodeGenerator

That code generator is implemented as a maven plugin.

That code generator was used to generate the bindings located in this repository:

https://github.com/matthiasblaesing/COMTypelibraries

There are bindings for:

- Microsoft Excel
- Microsoft Outlook
- Microsoft Word
- Microsoft Visual Basic for Applications Extensibility (vbide)
- Microsoft Office 15.0 Object Library
- OLE Automation (stdole)
- Microsoft Internet Controls (shdocvw)
- Microsoft Windows Image Acquisition Library
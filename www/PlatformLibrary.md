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


Release 3.3.x
=============

Features
--------
* linux/ppc 32-bit support
* linux multi-arch support (kohsuke)
* Add `platform.win32.Kernel32.CopyFile`, `platform.win32.Kernel32.MoveFile`, `platform.win32.Kernel32.MoveFileEx`, `platform.win32.Kernel32.CreateProcess`, `platform.win32.Kernel32.SetEnvironmentVariables`, `platform.win32.Kernel32.GetFileTime`, `platform.win32.Kernel32.SetFileTime`, `platform.win32.Kernel32.SetFileAttributes`, `platform.win32.Kernel32.DeviceIoControl`, `platform.win32.Kernel32.GetDiskFreeSpaceEx`, `platform.win32.Kernel32.CreateToolhelp32Snapshot`, `platform.win32.Kernel32.Process32First`, `platform.win32.Kernel32.Process32Next`.
* Add `platform.win32.Msi.MsiGetComponentPath`, `platform.win32.Msi.MsiLocateComponent`, `platform.win32.Msi.MsiGetProductCode`, `platform.win32.Msi.MsiEnumComponents`.
* Add `platform.win32.User32.RegisterHotKey`, `platform.win32.User32.UnregisterHotKey`, `platform.unix.x11.XGRabKey`, `platform.unix.x11.XUngrabKey`, `platform.unix.x11.XSetErrorHandler`, `platform.mac.Carbon.GetEventDispatcherTarget`, `platform.mac.Carbon.InstallEventHandler`, `platform.mac.Carbon.RegisterEventHotKey`, `platform.mac.Carbon.GetEventParameter`, `platform.mac.Carbon.RemoveEventHandler`, `platform.mac.Carbon.UnregisterEventHotKey`
* Add `platform.win32.SetupApi.SetupDiGetClassDevs`, `platform.win32.SetupApi.SetupDiDestroyDeviceInfoList`, `platform.win32.SetupApi.SetupDiEnumDeviceInterfaces`, `platform.win32.SetupApi.SetupDiGetDeviceInterfaceDetail`, `platform.win32.SetupApi.SetupDiGetDeviceRegistryProperty`.
* Add `platform.win32.Shell32.ShellExecute`.
* Add `platform.win32.User32.SetParent`, `platform.win32.User32.IsWindowVisible`, `platform.win32.User32.MoveWindow`, `platform.win32.User32.SetWindowPos`, `platform.win32.User32.AttachInputThread`, `platform.win32.User32.SetForegroundWindow`, `platform.win32.User32.GetForegroundWindow`, `platform.win32.User32.SetFocus`, `platform.win32.User32.SendInput`, `platform.win32.User32.WaitForInputIdle`, `platform.win32.User32.InvalidateRect`, `platform.win32.User32.RedrawWindow`, `platform.win32.User32.GetWindow`, `platform.win32.User32.UpdateWindow`, `platform.win32.User32.ShowWindow`, `platform.win32.User32.CloseWindow`.
* Add `platform.win32.Version.GetFileVersionInfoSize`, `platform.win32.Version.GetFileVersionInfo`, `platform.win32.Version.VerQueryValue`.

Bug Fixes
--------
* Revise cleanup of in-use temporary files on win32 (issue 6)
* Fix structure alignment issues on linux/ppc

Release 3.3.0
=============

Features
--------

* Facilitate `Memory` subclasses (jbellis).
* Allow multiple fields of the same type in Unions (Francis Barber).
* Add `platform.win32.Advapi32.AdjustTokenPrivileges`, `platform.win32.Advapi32.LookupPrivilegeName`, `platform.win32.Advapi32.LookupPrivilegeValue`, `platform.win32.Advapi32.ImpersonateSelf`.
* Add `platform.win32.Advapi32.DuplicateTokenEx`, `platform.win32.Advapi32.CreateProcessAsUser`, `platform.win32.Kernel32.GetExitCodeProcess`, `platform.win32.Kernel32.TerminateProcess`, `platform.win32.Kernel32.ReadFile`, `platform.win32.Kernel32.CreatePipe`, `platform.win32.Kernel32.SetHandleInformation` and related constants / structures in `platform.win32.WinBase` and `platform.win32.WinNT`. Please note that the `SECURITY_ATTRIBUTES` structure has been moved from `platform.win32.WinNT` to `platform.win32.WinBase`.
* Add `platform.win32.Kernel32.DeleteFile` and `platform.win32.Kernel32Util.deleteFile`.
* Add `platform.win32.Kernel32.GetFileAttributes` and `platform.win32.Kernel32Util.getFileAttributes`.
* Add `platform.win32.Kernel32.GetTickCount`.
* Add Win32 Service functions to `platform.win32.Advapi32`.
* Add `platform.win32.W32ServiceManager` and `W32Service`.
* Add Win32 Event Logging functions to `platform.win32.Advapi32` and `platform.win32.Advapi32Util.EventLogIterator`.
* `platform.win32.Advapi32Util.registryCreateKey` returns `true` if key was created, `false` if it already exists.
* Add `REG_BINARY`, `REG_EXPAND_SZ` and `REG_MULTI_SZ` support to `platform.win32.Advapi32Util` registry functions.
* Reduce JNI crossings in a number of native methods, moving object creation out into pure Java code.

Bug Fixes
---------

* Move all native functions into `com.sun.jna.Native`, to ensure that all dependent classes must be disposed before the `Native` class is unloaded.  Note that this change is incompatible with all previous JNA native libraries.
* Fix `platform.win32.Kernel32.GetNativeSystemInfo` and `GetSystemInfo` AV on Win64.
* Fix several potential minor bugs as reported by TvT.
* Fix bug in Structure.StructureSet.toString (Blair Zajac), exposed by Tomcat ThreadLocal cleanup.
* Fix several bugs when using Structure(Pointer) ctor and array fields (Samuel Audet).

Release 3.2.7
=============

Features
--------

* Add native peer value accessors for Pointer
* The `jna.library.path` property is now re-evaluated whenever a native library is loaded.  Previously this value was cached when the JNA classes loaded.
* `Native.loadLibrary` can now load `.drv` files.
* Refactor `com.sun.jna.platform.win32.WINBASE` into `WinDef`, `WinNT` and `BaseTSD`, matching Windows SDK headers.
* Refactor constants from `com.sun.jna.platform.win32.GDI32` into `WinGDI`, matching Windows SDK headers.
* Refactor constants from `com.sun.jna.platform.win32.User32` into `WinUser`, matching Windows SDK headers.
* Refactor `platform.win32.WinNT.LARGE_INTEGER` into a union.
* Add `platform.win32.ObjBase`, `com.sun.jna.platform.win32.Ole32.CoInitializeEx`, `CoUninitialize`, and `CoCreateInstance`.
* Add `platform.win32.Oleaut32.SysAllocString` and `SysFreeString`.
* Add `platform.win32.Secur32.ImpersonateSecurityContext` and `RevertSecurityContext`.
* Add `platform.win32.WinNT.WELL_KNOWN_SID_TYPE`, `SECURITY_MAX_SID_SIZE` and other related SID-related constants.
* Add `platform.win32.Advapi32.CreateWellKnownSid` and `IsWellKnownSid` and `com.sun.jna.platform.win32.Advapi32Util.isWellKnownSid`.
* Add `platform.win32.Kernel32.GetVersion`, `GetVersionEx`, `GetSystemInfo`, `GetNativeSystemInfo`, `GlobalMemoryStatusEx`, `GetLogicalDriveStrings` and `IsWow64Process`.
* Add `platform.win32.Kernel32Util.getLogicalDriveStrings`.
* Add `platform.win32.User32.GetSystemMetrics`.
* Add `platform.win32.BaseTSD.DWORD_PTR`.
* Add `platform.win32.WinBase.SYSTEM_INFO` and `MEMORYSTATUSEX`.
* Add `platform.win32.WinNT.OSVERSIONINFOEX`, `VER` constants.
* Add `platform.win32.WinDef.ULONGLONG` and `DWORDLONG`.
* Add `platform.win32.Shell32.SHGetDesktopFolder` (prep work for Com4JNA).
* Add `platform.win32.Winspool.GetPrinterInfo`.
* Add `platform.win32.WinspoolUtil.getPrinterInfo1`.
* Add `platform.win32.GDI32.GetDeviceCaps`.
* Add `platform.win32.GDI32.GetDIBits`.

Bug Fixes
---------

* Fix `ClassCastException` in `Structure.equals` (issue 152).
* Fix bug initializing a structure object from existing memory when the structure has initialized fields (issue 133).
* Fix NPE reading an array of string from a pointer when an element of the array is `NULL` (issue 151).
* Avoid calling `UnregisterNatives` in native code (issue 154).
* Compare unpacked library path against canonical (long) filename (issue 156).
* Fix `read()` of uninitialized memory in `platform.win32.Advapi32Util.getTokenGroups` and `getTokenAccount`.
* Fix `com.sun.jna.platform.win32.Secur32.QuerySecurityContextToken` to take a `CtxtHandle` instead of `PSecHandle`.
* Fix definition of BITMAPINFO (platform/win32).

Release 3.2.5
=============

Features
--------

* Split code in examples.jar into a contrib platform.jar package and individual packages for demos.
* Fix Eclipse build and added Eclipse projects for all contrib samples, import projects from jnalib and contrib.
* Ensure Structure fields correctly ordered when inherited.
* Use explicit Structure field whenever provided, regardless of whether the VM requires it.
* Add Win32 mappings for two dozen functions from Kernel32.dll, Advapi32.dll, Netapi32.dll, Secur32.dll, NtDll.dll, Ole32.dll, Shell32.dll and Crypt32.dll to com.sun.jna.platform.win32.
* Port parts of WinError.h, WinNT.h, LMAccess.h, LMCons.h, LMErr.h, LMJoin.h, NTStatus.h, ShlObj.h, WinDef.h, ShellApi.h, Wdm.h, WinReg.h, WinCrypt.h, Sspi.h, Guid.h, NtSecApi.h and DsGetDc.h.
* Add Win32 simplified utility interfaces Kernel32Util, Advapi32Util, Netapi32Util, Crypt32Util, NtDllUtil, Shell32Util, Ole32Util and Secur32Util to com.sun.jna.platform.win32.
* Support unicode paths in W32FileUtils.
* Fix exception during dispose in W32FileMonitor.

Bug Fixes
---------

* Provide String.replace for 1.4 compatibility.
* Avoid allocating memory when Structure is provided a pointer in the ctor.
* Ensure proper value returned in Pointer.getValue() for non-null, unchanged NIO Buffer values.
* Use 1.4-compatible URI generation (issue 149).

Release 3.2.4
=============

Features
--------

* Make Pointer ctor public.
* Provide access to Function objects for arbitrary Pointer values.
* Add linux/ia64 binaries (bpiwowar).  See issue 134 patch.

Bug Fixes
---------

* Use a more robust method to decode a file-based URL (issue 135).

Release 3.2.3
=============

Features
--------

* Include version information in code in case package information lost.

Bug Fixes
---------

* Fix WindowUtils exception on mouse over TrayIcon.
* Fix bug toggling windows transparent/opaque (win32/OSX).
* Avoid overwriting unchanged Pointer values in arrays (function calls with Pointer[] and Structure.read). 
* Ensure Structure fields marked `final` are never written.
* Fix bug preventing proper population Structure.ByReference fields on Structure read.
* Ensure double buffering is disabled in components added to a transparent window.
* Fix UnsatisfiedLinkError attempting to load system libraries under Web Start.
* Fix loading Web Start-provided libraries on OSX (libraries must have a .jnilib suffix under Web Start).
* Properly include sources in Maven zip file (Issue 129).

Release 3.2.2
=============

Features
--------

* Provide length-specified Pointer.getStringArray()

Bug Fixes
---------

* Fix crash with direct mapping if NULL struct* used (Issue 125).
* Fix case where null-valued Structure fields would get non-null values on write. 
* Synch callback Structure/Structure[] arguments on callback return.
* Fix NPE when mapping an interface to the current process.
* Automatically load proper C library version from current process on Linux (avoids crashing bug on Ubuntu with libc-i686 packages active).
* Avoid scanning structure contents in Structure.toString if contents aren't actually used.

Release 3.2.1
==========

Features
--------

* Add HRESULT, LONG mapping to W32API (marc strapetz).


Bug Fixes
---------

* Fix definition of HWND_BROADCAST in W32API.
* Fix memory alignment checking (Issue 121).
* Fix Structure equals/hashCode implementation, based on current Java fields rather than strictly native memory contents.  Avoid using equals/hashCode when avoiding recursive reads/writes.

Release 3.2.0
=============

Features
--------

* Handle String, Structure, Callback, Buffer, and primitive arrays in direct mappings.  Handle NativeMapped and TypeMapper, with optimized paths for IntegerType and PointerType.
* Optionally throw errno/GetLastError as an exception.  This is preferred to (and more efficient than) calling Native.getLastError().
* Unload/delete native library unpacked from jna.jar if Native class is garbage collected.  Only install shutdown hook if using the system class loader. 
* Auto-write contiguous Structure arrays when first element is written.
* Support NativeMapped[] as function arguments for interface-mapped libraries (Issue 90).
* Enable function lookup within current process on Windows.

Bug Fixes
---------

* Restrict recursive structure reads/writes by thread instead of globally. This avoids potentially missed reads/writes with concurrent access (Issue 120).
* Ensure Memory is not GC'd and freed if direct NIO buffers mapped to it are extant.
* Allow types derived from java.nio.Buffer as Structure fields.

Release 3.1.0
=============

Features
--------

* Add raw JNI mapping of static Java methods.  Performance is about 10X that of traditional JNA interface mapping, although with less type conversion functionality. 
* Add library option to allow passing/return of Java Objects.
* Allow handling of uncaught callback exceptions (Issue 63).
* Object oriented interface to X server (see contrib/x11)
* Make Memory class more accessible.
* Provide Structure ctor with Pointer argument (issue 102).
* Allow implicit library access to current process on linux (issue 98).
* Open all shared libraries with RTLD_GLOBAL, if applicable.  This was the default behavior on OSX and changes the default behavior on linux.
* Allow NIO Buffer as Structure field (with limitations) (Issue 57)
* Add `size_t` size.

Bug Fixes
---------

* Run tests with libjsig.so, if available, which fixes some crashes when running tests on 64-bit platforms. 
* Fix Issue 104.
* Fix Issue 94 (Java 1.6 update 10 regression).
* Fix Issue 51 (Java 1.6 update 10 regression).
* Fix Issue 95.
* Fix Issue 101.
* Fix Issue 111, memory leak with String-returning Callback.
* Fix missing storage of union type information (affects usage of struct/union by value as argument and return type).
* Remove non-functional Structure ctors requiring explicit size.

Release 3.0.9
=============

Bug Fixes
---------

*  Fix issue 93 by only manually searching jna.library.path, then falling back to passing the mapped library name to dlopen/LoadLibrary.  This fixes an issue in JRUBY where the incorrect libc.so.6 was being loaded.

Release 3.0.8
==========

Features
--------

* Auto-map Pointer[]/String[]/WString[] return values.
* Provide utility functions to convert String to primitive array.
* Add jna.library.boot.path property to define the directory that the native stub library is loaded from

Release 3.0.7
==========

Features
--------

* Improve Win32 loading of libraries with dependencies.

Bug Fixes
---------

* Fix bug reading structures with PointerType fields, introduced with Pointer field preservation fix.

Release 3.0.6
=============

Features
--------

* Allow arbitrary callback method names if only one method is defined in the class which implements Callback (colinwalters).
* Allow specification of callback type mappers by using a TYPE_MAPPER field (colinwalters).
* Allow uninitialized (null-valued) boxed primitives in Structures (colinwalters).
* Add convenience methods to set active Union field and value simultaneously (xylo).
* Augment Union read/writeField to set the active field.
* Allow Structure auto-synch across native calls to be disabled.
* Win64 support.

Bug Fixes
---------

* Avoid overwriting unchanged Structure fields of type Pointer.
* Avoid more content dragging on OSX or warn if it's too late.
* Fix UnsatisfiedLinkError using transparent window on Win2K.
* Fix memory leak with callbacks called from native threads with no Java context (johnwallace).  
* Defer structure size calculation if type mapper not yet set, allowing type mapper to be set in derived constructors (colinwalters).
* Ensure structure memory is allocated in Structure.read/writeField.

Release 3.0.5
=============

Features
--------

* Allow explicit declaration of field order for VMs which have an unpredictable field order.
* Check for w32 libraries with a "lib" prefix in addition to normal lookup.
* Allow String[]/WString[] as callback argument/return value (assume NULL-terminated array).
* Add Solaris8 compatibility to sunos-sparc build (Corey Puffalt).
* Look up libraries using web start library path, if appropriate (Corey Puffalt).
* Use constants to return integer boolean values.

Bug Fixes
---------

* Properly track cursor on alpha-masked windows.
* Avoid searching /lib or /usr/lib on 64-bit Linux.
* Avoid using incorrect version of a library when both 32- and 64-bit versions are found.
* Avoid transparent window events always dragging window bug on OSX.
* Fix division by zero error calculating structure size on OSX/ppc.
* Avoid overwriting initialized NativeMapped Structure fields when calculating structure size.
* Fix NPE reading back into StringArray.

Release 3.0.4
=============

Features
--------

* Automatically write contents of Structure.ByReference fields on Structure.write().
* Use the actual parameter type in Function invocations if no parameter type information is available (whether method is missing or untyped varargs).
* Augmented X11 library mappings (xylo).
* Support read/write of NativeMapped arrays within Structure (notably NativeLong).

Bug Fixes
---------

* Fix library load error when /usr/lib32 and /usr/lib both exist (linux) (Marek Slama).
* Avoid incorrect matches against libraries named with the same prefix (e.g. libc-client.so vs libc.so) (xylo).
* Properly handle arrays of NativeMapped (e.g. NativeLong) as a Structure field (stefan endrullis).
* Ensure structure size calculated prior to setting union active type.
* XID is 64-bits on 64-bit X clients (xylo).
* Ensure proper arch name is used on Debian (amd64 instead of x86_64).

Release 3.0.3
=============

Features
--------

* Enable build/run using IBM's J9 VM (leonardo).
* Make StdCallFunctionMapper attempt a leading underscore if the simpler mapping doesn't work.
* Allow Structure.read to overwrite final fields (may not work on some 1.4 VMs). 

Bug Fixes
---------

* Fix NPE when passing an array of Structure.ByReference.
* Compare entire linux library version when finding a match.
* Don't pass struct by value unless the method signature declares it.
* Restrict custom first element structure alignment to OSX/ppc.
* Improve performance and reduce memory footprint for window masks. Optimize polygon-based masks on w32.  Use XFillRectangles on X11.
* Fix linkage settings on sunos-amd64 to avoid relocation errors.
* Fix callback allocation code on w32, solaris, freebsd, darwin (libffi was misconfigured).
* Fix bug when NativeMapped fields are used in a Structure.ByValue instance.
* Fix NPE calling Structure.read() before memory is initialized.
* Fix NPE calling Structure.read/write with uninitialized NativeMapped fields. 

Release 3.0.2
=============

Features
--------

* Attempt to force unload of jnidispatch library prior to deleting it (w32).
* Added amd64 targets for OSX, FreeBSD, and Solaris.

Bug Fixes
---------

* Reduce space allocated for invocation arguments.
* Fix NPE when NativeMapped type is used in a Structure.
* Fix some X11 type mappings for 64-bit.
* Fix OSX Leopard/JRE1.5+ window transparency.
* Fix window alpha compositing on X11.
* Fix loading of libraries with unicode names on OSX.

Release 3.0.1
=============

Features
--------

* Improve transparent window drawing performance on w32
* Use closure allocation from libffi

Bug Fixes
---------

* Ensure nested structure arrays initialized with Structure.toArray use the appropriate native memory.
* Ensure structure size is calculated prior to converting to array
* Avoid creating new windows when setting a window mask
* Fix bug in Pointer.setChar.

Release 3.0
===========

Features
--------

* More supported platforms, via GCC's libffi (wmeissner)
* Support struct by value as parameter and return value (duncan)
* Support struct by reference within structures
* Provide access to native peer for java.awt.Component 
* Provide access to native peer on OS X.
* Support MINGW32 builds (fullung)
* Allow per-field Structure read/write by field name
* Avoid writing Structure fields marked 'volatile'
* Read and wrap function pointers in Structure fields when read with a Java proxy to allow easy Java-side invocation (Ken Larson)
* Support array-backed Buffers as arguments (wmeissner)
* Auto-conversion of custom types (wmeissner)
* Allow pointer type-safety
* Optional VM crash protection, via Native.setProtected(boolean)
* Auto-convert WString[]
* Provide library synchronization wrapper similar to Collections.synchronizedX
* Support lookup of OSX framework libraries by name
* Explicit access to shared library global data
* Invocation interception to facilitate translation of C preprocessor macros and inline functions
* Provide utility to determine Web Start native library cache location; auto-include this path if jnidispatch is included as a &lt;nativelib&gt; (robertengels) 
* Provide access to aligned memory
* Versioning information embedded in jna.jar and native library

Bug Fixes
---------

* Avoid attempts to free native library if it failed to load (wmeissner)
* Explicitly check method signatures for varargs instead of heuristically guessing (wmeissner)
* Disallow declaring Pointer-derived fields in Structures (Function, Memory)
* Ensure Object.toString/hashCode/equals methods are intercepted on proxyied interfaces
* Update X11 library for 64-bit use (wmeissner)
* Properly map arrays of char*/wchar_t* under w32
* Allow Pointer[] as a Structure field and Function argument
* Fix some misleading Structure error messages
* Properly preserve/return GetLastError/errno after native calls
* Allocate executable memory on w32 to avoid errors with hardware-enforced data execution protection (DEP)
* Fix VM crash on w32 stdcall callbacks
* Use long offsets and sizes rather than ints (64-bit safe)
* Properly clean up references and release closure memory on JNI_Unload
* Use simpler AWT/JAWT library loading workaround
* Avoid changing array references within a Structure on read

Release 2.5
===========

Features
--------

* Unions
* Optimized shaped windows (chris deckers & olivier chafik); instantiation time improved by about 2-3 orders of magnitude for large, mostly contiguous shapes
* Provide type mapping in callback arguments/results
* Provide access to ByteBuffer direct address as a Pointer
* Provide customization of native string encoding with jna.encoding system property 

Bug Fixes
---------

* Properly handle VMs with reversed Structure member storage
* Avoid making window undecorated when clearing window mask on X11
* Fix structure alignment bug on OSX/PPC when first element is > 4 bytes in size
* Clearing OSX window mask by setting to MASK_NONE now works properly
* Avoid index exceptions if native buffers are not NUL-terminated on string conversions
* Write initialized Structure[] argument memory prior to function calls
* Fix IllegalArgumentException reading WString into a Structure
* Clear memory when allocating a structure block (fixes VM crash)
* Remove versioned JAWT dependency on OSX, allowing use on 10.3/JRE1.4.

Release 2.4
===========

Features
--------

* Explicitly support unaligned structures
* Auto-reallocate structure arrays
* Automatic handling of w32 UNICODE/ASCII variants
* Automatic mapping of decorated w32 stdcall function names
* Customizable, automatic type conversion of arguments and results (wmeissner)
* Support char*[] arguments as Java String[] 
* Structure supports Callback members (wmeissner)
* getByteBuffer from Pointer/Memory (wmeissner)
* Allow GC of native libraries
* Facilitate use from non-Java contexts (JRuby et al.) (wmeissner)
* Improve library path searching (wmeissner)
* Handle Structure[] arguments
* Handle native long arguments and return values
* Handle direct and array-based ByteBuffer arguments (wmeissner)
* Change default w32 build to use GCC (it's free, yo)

Bug Fixes
---------

* Structure.toArray failed to initialize members
* Disallow explicit free of Structure/Memory
* Ensure native libraries are only loaded once until released
* Properly handle NULL when the return value is a Structure
* Proper conversion to wchar_t on linux
* Copy full length of Java strings to C strings instead of stopping when a NUL character is encountered

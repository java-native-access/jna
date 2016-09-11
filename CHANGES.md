NOTE: as of JNA 4.0, JNA is now dual-licensed under LGPL and AL 2.0 (see LICENSE).

NOTE: JNI native support is typically incompatible between minor versions, and almost always incompatible between major versions.

Release 4.3 (Next release)
===========

Features
--------
* [#526](https://github.com/java-native-access/jna/pull/526): Added initialization and conversion between Windows SYSTEMTIME and Java Calendar - [@lgoldstein](https://github.com/lgoldstein).
* [#532](https://github.com/java-native-access/jna/pull/529): Added `com.sun.jna.platform.win32.Mpr`, `com.sun.jna.platform.win32.LmShare`, and `com.sun.jna.platform.win32.Winnetwk` - [@amarcionek](https://github.com/amarcionek).
* [#532](https://github.com/java-native-access/jna/pull/529): Added `ACCESS_*` definitions to `com.sun.jna.platform.win32.LmAccess` - [@amarcionek](https://github.com/amarcionek).
* [#532](https://github.com/java-native-access/jna/pull/529): Added `NetShareAdd` and `NetShareDel` to `com.sun.jna.platform.win32.Netapi32` - [@amarcionek](https://github.com/amarcionek).
* [#535](https://github.com/java-native-access/jna/pull/535): Added `CreateProcessWithLogonW` to `com.sun.jna.platform.win32.Advapi32` - [@mlfreeman2](https://github.com/mlfreeman2).
* [#535](https://github.com/java-native-access/jna/pull/535): Added `CertAddEncodedCertificateToSystemStore` to `com.sun.jna.platform.win32.Crypt32` - [@mlfreeman2](https://github.com/mlfreeman2).
* [#535](https://github.com/java-native-access/jna/pull/535): Added `BitBlt` to `com.sun.jna.platform.win32.GDI32`, Added `com.sun.jna.platform.win32.GDI32Util` and added `getScreenshot()` to it - [@mlfreeman2](https://github.com/mlfreeman2).
* [#535](https://github.com/java-native-access/jna/pull/535): Added `SHEmptyRecycleBin`, `ShellExecuteEx` to `com.sun.jna.platform.win32.Shell32` - [@mlfreeman2](https://github.com/mlfreeman2).
* [#535](https://github.com/java-native-access/jna/pull/535): Added `GetDesktopWindow` to `com.sun.jna.platform.win32.User32` - [@mlfreeman2](https://github.com/mlfreeman2).
* [#540](https://github.com/java-native-access/jna/pull/539): Added Missing Windows kernel32 method: QueryFullProcessImageName - [@yossieilaty](https://github.com/yossieilaty).
* [#543](https://github.com/java-native-access/jna/pull/543): Added `ProcessIdToSessionId`, `LoadLibraryEx`, `FreeLibrary` and `Find/Load/Lock/SizeofResource` to `com.sun.jna.platform.win32.Kernel32` - [@mlfreeman2](https://github.com/mlfreeman2).
* [#545](https://github.com/java-native-access/jna/pull/545): Added `EnumResourceTypes` and `EnumResourceNames` to `com.sun.jna.platform.win32.Kernel32` - [@mlfreeman2](https://github.com/mlfreeman2).
* [#547](https://github.com/java-native-access/jna/pull/547): Added `GetSystemTimes` to `com.sun.jna.platform.win32.Kernel32` - [@dbwiddis](https://github.com/dbwiddis).
* [#548](https://github.com/java-native-access/jna/pull/548): Return 64-bit unsigned integer from `com.sun.jna.platform.win32.WinBase.FILETIME` - [@dbwiddis](https://github.com/dbwiddis).
* [#524](https://github.com/java-native-access/jna/pull/524): Added IShellFolder interface plus necessary utility functions to Windows platform, and a sample for enumerating objects in My Computer - [@lwahonen](https://github.com/lwahonen).
* [#471](https://github.com/java-native-access/jna/issues/471): Determine size of native `bool` - [@twall](https://github.com/twall).
* [#484](https://github.com/java-native-access/jna/pull/484): Added `XFetchName` to `X11` interface - [@pinaf](https://github.com/pinaf).
* [#554](https://github.com/java-native-access/jna/pull/554): Initial code for a few Unix 'libc' API(s) [@lgoldstein](https://github.com/lgoldstein)
* [#552](https://github.com/java-native-access/jna/pull/552): Added `Module32FirstW` and `Module32NextW` to `com.sun.jna.platform.win32.Kernel32` (and helper to `com.sun.jna.platform.win32.Kernel32Util`) and `MODULEENTRY32W` structure to `com.sun.jna.platform.win32.Tlhelp32` - [@mlfreeman2](https://github.com/mlfreeman2).
* [#564](https://github.com/java-native-access/jna/pull/564): Use generic definition of Native#loadLibrary [@lgoldstein](https://github.com/lgoldstein)
* [#562](https://github.com/java-native-access/jna/pull/562): Added `com.sun.jna.platform.win32.VersionUtil` with `getFileVersionInfo` utility method to get file major, minor, revision, and build version parts - [@mlfreeman2](https://github.com/mlfreeman2).
* [#563](https://github.com/java-native-access/jna/pull/563): Added `com.sun.jna.platform.win32.Wininet` with the following 4 methods: `FindFirstUrlCacheEntry`, `DeleteUrlCacheEntry`, `FindCloseUrlCache`, `FindNextUrlCacheEntry`, and the `INTERNET_CACHE_ENTRY_INFO` structure, and a helper in `com.sun.jna.platform.win32.WininetUtil` for parsing WinInet's cache - [@mlfreeman2](https://github.com/mlfreeman2).
* [#567](https://github.com/java-native-access/jna/pull/567): Added `PrintWindow`, `IsWindowEnabled`, `IsWindow`, `FindWindowEx`, `GetAncestor`, `GetCursorPos`, `SetCursorPos`, `SetWinEventHook`, `UnhookWinEvent`, `CopyIcon`, and `GetClassLong` to `com.sun.jna.platform.win32.User32` and supporting constants to `com.sun.jna.platform.win32.WinUser` - [@mlfreeman2](https://github.com/mlfreeman2).
* [#573](https://github.com/java-native-access/jna/pull/573): Added `EnumProcessModules`, `GetModuleInformation`, and `GetProcessImageFileName` to `com.sun.jna.platform.win32.Psapi` and added `ExtractIconEx` to `com.sun.jna.platform.win32.Shell32` - [@mlfreeman2](https://github.com/mlfreeman2).
* [#574](https://github.com/java-native-access/jna/pull/574): Using static final un-modifiable List of field names for structure(s) - [@lgoldstein](https://github.com/lgoldstein).
* [#577](https://github.com/java-native-access/jna/pull/577): Apply generic definitions wherever applicable - [@lgoldstein](https://github.com/lgoldstein).
* [#569](https://github.com/java-native-access/jna/pull/569): Added `com.sun.jna.platform.win32.Winspool.PRINTER_INFO_2` support. Added GetPrinter and ClosePrinter functions in `com.sun.jna.platform.win32.Winspool` - [@IvanRF](https://github.com/IvanRF).
* [#583](https://github.com/java-native-access/jna/pull/583): Added printer attributes and status - [@IvanRF](https://github.com/IvanRF).
* [#589](https://github.com/java-native-access/jna/pull/589): Use `com.sun.jna.MethodResultContext` in direct mapping (as done in interface mapping) - [@marco2357](https://github.com/marco2357).
* [#595](https://github.com/java-native-access/jna/pull/595): Allow calling COM methods/getters requiring hybrid calling (METHOD+PROPERTYGET) - [@matthiasblaesing](https://github.com/matthiasblaesing).
* [#582](https://github.com/java-native-access/jna/pull/582): Mavenize the build process - Phase 1: building the native code via Maven - [@lgoldstein](https://github.com/lgoldstein).
* [#606](https://github.com/java-native-access/jna/pull/606): Added Kernel32Util method to facilitate checking that calls to LocalFree/GlobalFree are successful - [@lgoldstein](https://github.com/lgoldstein).
* [#612](https://github.com/java-native-access/jna/pull/612): `Kernel32Util.freeLocalMemory()`/`Kernel32Util.freeGlobalMemory()` always throw `com.sun.jna.platform.win32.Win32Exception` if failed - [@lgoldstein](https://github.com/lgoldstein).
* [#608](https://github.com/java-native-access/jna/pull/608): Mavenize the build process - change parent and native pom artifactId/name to differentiate in IDE and build tools. - [@bhamail](https://github.com/bhamail)
* [#613](https://github.com/java-native-access/jna/pull/613): Make `com.sun.jna.platform.win32.Win32Exception` extend `com.sun.jna.LastErrorException` - [@lgoldstein](https://github.com/lgoldstein).
* [#614](https://github.com/java-native-access/jna/pull/614): Added standard `com.sun.jna.platform.win32.Kernel32Util.closeHandle()` method that throws a `com.sun.jna.platform.win32.Win32Exception` if failed to close the handle - [@lgoldstein](https://github.com/lgoldstein).
* [#618](https://github.com/java-native-access/jna/pull/618): Implement SAFEARRAY access and bugfix VARIANT - [@matthiasblaesing](https://github.com/matthiasblaesing).
* [#616](https://github.com/java-native-access/jna/pull/616): Allow access to base interfaces (most important IDispatch) via ProxyObject and improve binding by allowing to use dispId for the call - [@matthiasblaesing](https://github.com/matthiasblaesing).
* [#621](https://github.com/java-native-access/jna/pull/621): Added TYPEFLAGS-constants for `wTypeFlags` in `com.sun.jna.platform.win32.OaIdl.TYPEATTR` - [@SevenOf9Sleeper](https://github.com/SevenOf9Sleeper).
* [#625](https://github.com/java-native-access/jna/pull/625): Make conversion to/from java to/from VARIANT in `com.sun.jna.platform.win32.COM.util.Convert` more flexible and dependable - [@matthiasblaesing](https://github.com/matthiasblaesing).
* [#639](https://github.com/java-native-access/jna/pull/639): Add getloadavg() to OS X and Unix - [@dbwiddis](https://github.com/dbwiddis).
* [#640](https://github.com/java-native-access/jna/pull/640): Add `com.sun.jna.platform.win32.Psapi.GetPerformanceInfo()`, `com.sun.jna.platform.win32.Kernel32.GetTickCount64()`, and `com.sun.jna.platform.win32.Kernel32.SetErrorMode()` - [@dbwiddis](https://github.com/dbwiddis).
* [#642](https://github.com/java-native-access/jna/pull/642): COM calls with variable number of arguments (varargs) are now supported - [@SevenOf9Sleeper](https://github.com/SevenOf9Sleeper).
* [#644](https://github.com/java-native-access/jna/pull/644): New ant target 'install' for installing JNA artifacts in local m2-repository - [@SevenOf9Sleeper](https://github.com/SevenOf9Sleeper).
* [#649](https://github.com/java-native-access/jna/pull/649): Bugfix msoffice sample and add two samples taken from MSDN and translated from VisualBasic to Java  - [@matthiasblaesing](https://github.com/matthiasblaesing).
* [#654](https://github.com/java-native-access/jna/pull/654): Support named arguments for `com.sun.jna.platform.win32.COM.util.CallbackProxy` based callbacks - [@matthiasblaesing](https://github.com/matthiasblaesing).
* [#659](https://github.com/java-native-access/jna/issues/659): Enable LCID (locale) override for `com.sun.jna.platform.win32.COM.util.ProxyObject`-based COM calls - [@matthiasblaesing](https://github.com/matthiasblaesing).
* [#665](https://github.com/java-native-access/jna/pull/665): Added `XSetWMProtocols` and `XGetWMProtocols` to `com.sun.jna.platform.unix.X11` - [@zainab-ali](https://github.com/zainab-ali).
* [#667](https://github.com/java-native-access/jna/pull/667): Added SetFileSecurity, GetSecurityInfo and SetSecurityInfo to `com.sun.jna.platform.win32.Advapi32` - [@amarcionek](https://github.com/amarcionek).
* [#667](https://github.com/java-native-access/jna/pull/667): Added NtSetSecurityObject and NtQuerySecurityObject to `com.sun.jna.platform.win32.NtDll` - [@amarcionek](https://github.com/amarcionek).
* [#680](https://github.com/java-native-access/jna/pull/680): Added `SetCurrentProcessExplicitAppUserModelID` and `GetCurrentProcessExplicitAppUserModelID` to `com.sun.jna.platform.win32.Shell32` for setting the [System.AppUserModel.ID](https://msdn.microsoft.com/en-us/library/windows/desktop/dd391569.aspx) of the host process - [@rednoah](https://github.com/rednoah).
* [#693](https://github.com/java-native-access/jna/pull/693): Bind DDEML (Dynamic Data Exchange Management Library), add a thread implementation that runs a windows message loop - [@matthiasblaesing](https://github.com/matthiasblaesing).
* [#702](https://github.com/java-native-access/jna/pull/702): Added `GetClientRect` to `com/sun/jna/platform/win32/User32` - [@Jonatino](https://github.com/Jonatino).
* [#703](https://github.com/java-native-access/jna/pull/703): Added `mouse_event` to `com/sun/jna/platform/win32/User32` - [@Jonatino](https://github.com/Jonatino).

Bug Fixes
---------
* [#549](https://github.com/java-native-access/jna/pull/549): Fixed bug in types derived from XID - [@twall](https://github.com/twall).
* [#536](https://github.com/java-native-access/jna/pull/536): Fixed bug in determining the Library and options associated with types defined outside of a Library - [@twall](https://github.com/twall).
* [#531](https://github.com/java-native-access/jna/pull/531): Ensure direct-mapped callbacks use the right calling convention - [@twall](https://github.com/twall).
* [#566](https://github.com/java-native-access/jna/pull/566): Fix return type of Native#loadLibrary to match unconstrained generic [@lgoldstein](https://github.com/lgoldstein).
* [#584](https://github.com/java-native-access/jna/pull/584): Promote float varargs to double - [@marco2357](https://github.com/marco2357).
* [#588](https://github.com/java-native-access/jna/pull/588): Fix varargs calls on arm - [@twall](https://github.com/twall).
* [#593](https://github.com/java-native-access/jna/pull/593): Improve binding of TypeLib bindings - [@matthiasblaesing](https://github.com/matthiasblaesing).
* [#578](https://github.com/java-native-access/jna/pull/578): Fix COM CallbackHandlers, allow usage of VARIANTs directly in c.s.j.p.w.COM.util.ProxyObject and fix native memory leak in c.s.j.p.w.COM.util.ProxyObject - [@matthiasblaesing](https://github.com/matthiasblaesing)
* [#601](https://github.com/java-native-access/jna/pull/601): Remove COMThread and COM initialization from objects and require callers to initialize COM themselves. Asserts are added to guard correct usage. - [@matthiasblaesing](https://github.com/matthiasblaesing).
* [#602](https://github.com/java-native-access/jna/pull/602): Make sure SID related memory is properly released once no longer required [@lgoldstein](https://github.com/lgoldstein).
* [#610](https://github.com/java-native-access/jna/pull/610): Fixed issue #604: Kernel32#GetLastError() always returns ERROR_SUCCESS [@lgoldstein](https://github.com/lgoldstein).
* [#633](https://github.com/java-native-access/jna/pull/633): Restore default usage of platform native encoding for Java strings passed to native functions (was hard-coded to UTF-8 in 4.0 and later) [@amake](https://github.com/amake)
* [#634](https://github.com/java-native-access/jna/pull/634): Improve BSTR handling and add `SysStringByteLen` and `SysStringLen` to `com.sun.jna.platform.win32.OleAuto` - [@matthiasblaesing](https://github.com/matthiasblaesing).
* [#646](https://github.com/java-native-access/jna/issues/646): `platform.win32.COM.COMBindingBaseObject` swallows reason if instantiation fails - [@matthiasblaesing](https://github.com/matthiasblaesing).
* [#674](https://github.com/java-native-access/jna/pull/674): Update references to Apache License as requested by issue #673 [@bhamail](https://github.com/bhamail)
* [#636](https://github.com/java-native-access/jna/issues/636): Staticly link visual c++ runtime when building with MSVC - [@matthiasblaesing](https://github.com/matthiasblaesing).
* [#624](https://github.com/java-native-access/jna/issues/624): WinDef.DWORD getLow() & getHigh() using incorrect bit mask - [@matthiasblaesing](https://github.com/matthiasblaesing).
* [#668](https://github.com/java-native-access/jna/issues/668): Correct typemapper used for structures defined in `com.sun.jna.platform.win32.DsGetDC`, `com.sun.jna.platform.win32.LMAccess`, `com.sun.jna.platform.win32.LMShare`, `com.sun.jna.platform.win32.Sspi`, `com.sun.jna.platform.win32.WinBase`, `com.sun.jna.platform.win32.WinCrypt`, `com.sun.jna.platform.win32.WinUser` and `com.sun.jna.platform.win32.Winnetwk` - [@matthiasblaesing](https://github.com/matthiasblaesing).
* [#696](https://github.com/java-native-access/jna/issues/696): COMLateBindingObject.getAutomationProperty method that takes iDispatch parameter doesn't use it - [@matthiasblaesing](https://github.com/matthiasblaesing).

Release 4.2.1
=============

Features
--------
* [#504](https://github.com/java-native-access/jna/pull/504): Add support for linux-sparcv9 - [@alexvsimon](https://github.com/alexvsimon).
* [#510](https://github.com/java-native-access/jna/pull/510): Added `GetCommState`, `GetCommTimeouts` `SetCommState` and `SetCommTimeouts` to `com.sun.jna.platform.win32.Kernel32`. Added `DCB` structure to `com.sun.jna.platform.win32.WinBase` - [@MBollig](https://github.com/MBollig).
* [#512](https://github.com/java-native-access/jna/pull/512): Make loading debug flags mutable [@lwahonen](https://github.com/lwahonen).
* [#514](https://github.com/java-native-access/jna/pull/514): Added `host_processor_info` to `com.sun.jna.platform.mac.SystemB`  - [@dbwiddis](https://github.com/dbwiddis).
* [#519](https://github.com/java-native-access/jna/pull/519): Added JNA functional overview - [@twall](https://github.com/twall).
* [#528](https://github.com/java-native-access/jna/pull/528): Added idea-jar ant task that creates a convenience jar that contains all native dispatch libraries - [@lwahonen](https://github.com/lwahonen).

Bug Fixes
---------
* [#515](https://github.com/java-native-access/jna/issues/515): Update linux-arm natives omitted in 4.2 [@twall](https://github.com/twall).

Release 4.2
===========

Features
--------
* [#452](https://github.com/java-native-access/jna/pull/452): Added Mac OS X System.B library at `com.sun.jna.platform.mac.SystemB`  including support for `sysctl`, `sysctlbyname`, `sysctlnametomib`, `mach_host_self`, `host_page_size`, `host_statistics` and `host_statistics64` - [@dbwiddis](https://github.com/dbwiddis).
* [#446](https://github.com/java-native-access/jna/pull/446): Added `com.sun.jna.platform.win32.Advapi32.GetNamedSecurityInfo`, `SetNamedSecurityInfo`, `GetSecurityDescriptorLength`, `IsValidSecurityDescriptor`, `IsValidAcl` - [@amarcionek](https://github.com/amarcionek).
* [#387](https://github.com/java-native-access/jna/pull/397): Use of interfaces and annotations to provide easier implementation of COM interfaces (with `InvocationHandler`) - [@dhakehurst](https://github.com/dhakehurst).
* [#387](https://github.com/java-native-access/jna/pull/397): Support for COM event callbacks - [@dhakehurst](https://github.com/dhakehurst).
* [#387](https://github.com/java-native-access/jna/pull/397): Support for COM interface discovery by iteration over `RunningObjectTable` - [@dhakehurst](https://github.com/dhakehurst).
* Updated AIX natives and build - [@twall](https://github.com/twall).
* [#290](https://github.com/java-native-access/jna/pull/290): Improved the stacktrace for the exceptions thrown by `com.sun.jna.Structure` - [@ebourg](https://github.com/ebourg).
* [#332](https://github.com/java-native-access/jna/pull/332): Added Win32 Monitor Configuration API in `com.sun.jna.platform.win32.Dxva2` - [@msteiger](https://github.com/msteiger).
* Added Winspool monitor sample and updated Kernel32, WinBase, Winspool - [@wolftobias](https://github.com/wolftobias).
* [#333](https://github.com/java-native-access/jna/pull/333): Added `CoTaskMemAlloc`, `CoTaskMemRealloc` and `CoTaskMemFree` to `com.sun.jna.platform.win32.Ole32` - [@msteiger](https://github.com/msteiger).
* [#334](https://github.com/java-native-access/jna/pull/334): Added `com.sun.jna.platform.win32.Shell32.SHGetKnownFolderPath` and `KnownFolders` GUID constants - [@msteiger](https://github.com/msteiger).
* [#338](https://github.com/java-native-access/jna/pull/338): Added `com.sun.jna.platform.mac.XAttr` and `com.sun.jna.platform.mac.XAttrUtil` JNA wrapper for `<sys/xattr.h>` for Mac OS X - [@rednoah](https://github.com/rednoah).
* [#339](https://github.com/java-native-access/jna/pull/339): Added `GetWindowPlacement`, `SetWindowPlacement`, `AdjustWindowRect`, `AdjustWindowRectEx`, `ExitWindowsEx`, and `LockWorkstation` to `com.sun.jna.platform.win32.User32` - [@Timeroot](https://github.com/Timeroot).
* [#286](https://github.com/java-native-access/jna/pull/286): Added `CreateRemoteThread`, `WritePocessMemory` and `ReadProcessMemory` to `com.sun.jna.platform.win32.Kernel32` - [@sstokic-tgm](https://github.com/sstokic-tgm).
* [#350](https://github.com/java-native-access/jna/pull/350): Added `jnacontrib.x11.api.X.Window.getSubwindows` - [@rm5248](https://github.com/rm5248).
* Improved `contrib/msoffice` sample - [@wolftobias](https://github.com/wolftobias).
* [#352](https://github.com/java-native-access/jna/pull/352): Performance improvements due to reduced locking in `com.sun.jna.Library$Handler` and fewer vararg checks in `com.sun.jna.Function` - [@Boereck](https://github.com/Boereck).
* [#353](https://github.com/java-native-access/jna/pull/353): Performance improvements by improved collaboration between `com.sun.jna.Library$Handler` and `com.sun.jna.Function` - [@Boereck](https://github.com/Boereck).
* [#357](https://github.com/java-native-access/jna/pull/357): Added `com.sun.jna.platform.win32.Kernel32.SetSystemTime` - [@lgoldstein](https://github.com/lgoldstein), [@thomasjoulin](https://github.com/thomasjoulin).
* [#365](https://github.com/java-native-access/jna/pull/365): Added `com.sun.jna.platform.win32.Kernel32.GetComputerNameEx` support - [@lgoldstein](https://github.com/lgoldstein).
* [#368](https://github.com/java-native-access/jna/pull/368): Added `com.sun.jna.platform.win32.Kernel32.VirtualQueryEx`, `com.sun.jna.platform.win32.WinNT.MEMORY_BASIC_INFORMATION` and `MEM_COMMIT`, `MEM_FREE`, `MEM_RESERVE`, `MEM_IMAGE`, `MEM_MAPPED`, `MEM_PRIVATE` constants - [@apsk](https://github.com/apsk).
* Allow interoperation with JNI revision changes - [@twall](https://github.com/twall).
* [#376](https://github.com/java-native-access/jna/pull/373): Added `com.sun.jna.VarArgsChecker` for faster vararg checks. Used in `com.sun.jna.Function` - [@Boereck](https://github.com/Boereck).
* [#391](https://github.com/java-native-access/jna/pull/391): Added `com.sun.jna.platform.win32.Advapi3.EncryptFile`, `DecryptFile`, `FileEncryptionStatus`, `EncryptionDisable`, `OpenEncryptedFileRaw`, `ReadEncryptedFileRaw`, `WriteEncryptedFileRaw`, and `CloseEncryptedFileRaw` with related `Advapi32Util` helpers - [@khalidq](https://github.com/khalidq).
* [#400](https://github.com/java-native-access/jna/pull/400): Added `com.sun.jna.platform.WindowUtils.getAllWindows`, `getWindowIcon`, `getIconSize`, `getWindowTitle`, `getPRocessFilePath` and `getWindowLocationAndSize` - [@PAX523](https://github.com/PAX523).
* [#400](https://github.com/java-native-access/jna/pull/400): Added `com.sun.jna.platform.win32.Kernel32Util.getLastErrorMessage` - [@PAX523](https://github.com/PAX523).
* [#400](https://github.com/java-native-access/jna/pull/400): Added `com.sun.jna.platform.win32.GDI32.GetObject` - [@PAX523](https://github.com/PAX523).
* [#400](https://github.com/java-native-access/jna/pull/400): Added `com.sun.jna.platform.win32.Psapi.GetModuleFileNameEx`- [@PAX523](https://github.com/PAX523).
* [#400](https://github.com/java-native-access/jna/pull/400): Added `com.sun.jna.platform.win32.User32.GetIconInfo`, `SendMessageTimeout` and `GetClassLongPtr` - [@PAX523](https://github.com/PAX523).
* [#400](https://github.com/java-native-access/jna/pull/400): Added `com.sun.jna.platform.win32.WinGDI.ICONINFO` and `BITMAP` - [@PAX523](https://github.com/PAX523).
* [#400](https://github.com/java-native-access/jna/pull/400): Added process-specific access rights constants in `com.sun.jna.platform.win32.WinNT` - [@PAX523](https://github.com/PAX523).
* [#400](https://github.com/java-native-access/jna/pull/400): Added specific constants for request of icon settings in `com.sun.jna.platform.win32.WinUser` - [@PAX523](https://github.com/PAX523).
* [#400](https://github.com/java-native-access/jna/pull/400): Added constants for `GetClassLong`, `SendMessageTimeout` and `GetIconInfo` in `com.sun.jna.platform.win32.WinUser` - [@PAX523](https://github.com/PAX523).
* [#419](https://github.com/java-native-access/jna/pull/419): Added `SetupDiOpenDevRegKey` , `SetupDiEnumDeviceInfo` and related constants to `com.sun.jna.platform.win32.SetupApi` - [@ChristianSchwarz](https://github.com/ChristianSchwarz).
* [#421](https://github.com/java-native-access/jna/pull/421): Added `XGrabKeyboard` and `XUngrabKeyboard` to `X11` interface - [@pinaf](https://github.com/pinaf).
* [#422](https://github.com/java-native-access/jna/pull/422): Update libffi to v3.2.1 - [@tdaitx](https://github.com/tdaitx).
* [#425](https://github.com/java-native-access/jna/pull/425): Add support for PPCLE - [@tdaitx](https://github.com/tdaix).
* [#428](https://github.com/java-native-access/jna/pull/428): Added Wincon.h related functions and definitions to `com.sun.jna.platform.win32.Kernel32` - [@lgoldstein](https://github.com/lgoldstein).
* [#430](https://github.com/java-native-access/jna/issues/430): Add android `aarch64`, `x86-64`, `mips`, and `mips64` targets - [@twall](https://github.com/twall).
* [#431](https://github.com/java-native-access/jna/pull/431): Added named pipe API support to `com.sun.jna.platform.win32.Kernel32` - [@lgoldstein](https://github.com/lgoldstein).
* [#432](https://github.com/java-native-access/jna/pull/432): Added `com.sun.jna.platform.win32.Kernel32.SetLocalTime` - [@lgoldstein](https://github.com/lgoldstein).
* [#434](https://github.com/java-native-access/jna/pull/434): Added `com.sun.jna.platform.win32.Kernel32.GetEnvironmentStrings`  - [@lgoldstein](https://github.com/lgoldstein).
* Loosen OSGI OS name matching to accommodate Windows 8 family - Niels Bertram.
* [#436] (https://github.com/java-native-access/jna/pull/469): Added basic Pdh API implementation to 'com.sun.jna.platform.win32' - [@lgoldstein](https://github.com/lgoldstein).
* [#451] (https://github.com/java-native-access/jna/pull/451): Add VARIANT support for CHAR and BYTE  - [@mitkola](https://github.com/mitkola).
* [#478] (https://github.com/java-native-access/jna/issues/451): Ask ldconfig for more places to search for libraries  - [@gohal](https://github.com/gohal).
* [#481] (https://github.com/java-native-access/jna/pull/481): Added volume management functions to `com.sun.jna.platform.win32` - [@lgoldstein](https://github.com/lgoldstein).
* [#483] (https://github.com/java-native-access/jna/pull/483): Found and fixed duplicate method definitions for the same API in `com.sun.jna.platform.win32` - [@lgoldstein](https://github.com/lgoldstein).
* [#485] (https://github.com/java-native-access/jna/pull/485): Implemented `Comparable` interface for many of the base types in `com.sun.jna.platform.win32.WinDef` - [@lgoldstein](https://github.com/lgoldstein).
* [#488] (https://github.com/java-native-access/jna/pull/488): Added `GetRawInputDeviceList` to `com.sun.jna.platform.win32.User32` and `User32Util` - [@lgoldstein](https://github.com/lgoldstein).
* [#490](https://github.com/java-native-access/jna/issues/490): Allow arbitrary calling convention specification, including FFI_MS_CDECL which alters handling of struct return values, and multiple Linux/PowerPC conventions - [@twall](https://github.com/twall).

Bug Fixes
---------
* [#450](https://github.com/java-native-access/jna/pull/450): Libraries extracted to temp directory are now cleaned up in case of library loading errors, as well - [@aschnab](https://github.com/aschnab).
* [#319](https://github.com/java-native-access/jna/pull/319): Fix direct-mapping type-mapped pointer result types - [@marco2357](https://github.com/marco2357).
* [#350](https://github.com/java-native-access/jna/pull/350): Fix `jnacontrib.x11.api.X.Window.getXXXProperty`, returns `null` if the window property is not found - [@rm5248](https://github.com/rm5248).
* Fixed `com.sun.jna.platform.win32.Variant` and `TlbImp` - [@wolftobias](https://github.com/wolftobias).
* Fixed `com.sun.jna.Pointer.getWideStringArray` not respecting the length parameter - [@csoren](https://github.com/csoren).
* Fix missing code in X11 demos - [@twall](https://github.com/twall).
* Fix compiler warnings OSX - [@twall](https://github.com/twall).
* Remove unsupported JAWT from OSX build - [@twall](https://github.com/twall).
* Disable WebStart tests - [@twall](https://github.com/twall).
* Dispose all native resources when JNA's native library is unloaded - Paul Grütter,  [@twall](https://github.com/twall).
* Weakly hold registered direct-mapped classes - [@twall](https://github.com/twall).
* [#382](https://github.com/java-native-access/jna/pull/382): Fixed memory allocation in `com.sun.jna.platform.win32.WTypes.LPWSTR` and `LPSTR` constructors - [@junak-michal](https://github.com/junak-michal).
* Fix publish doc links - [@bhamail](https://github.com/bhamail).
* [#388](https://github.com/java-native-access/jna/issues/388): Ensure native library always opened with provided flags - [@zolyfarkas](https://github.com/zolyfarkas).
* [#403](https://github.com/java-native-access/jna/pull/403): Fix `com.sun.jna.platform.win32.COM.COMUtils.SUCCEEDED` and `FAILED` - [@lwahonen](https://github.com/lwahonen).
* [#404](https://github.com/java-native-access/jna/pull/404): Fix `VARIANT` constructors for `int`, `short`, and `long` - [@lwahonen](https://github.com/lwahonen).
* [#420](https://github.com/java-native-access/jna/pull/420): Fix structure leaving always one element in ThreadLocal set - [@sjappig](https://github.com/sjappig).
* [#467](https://github.com/java-native-access/jna/issues/467): Fix TypeMapper usage with direct-mapped libraries converting primitives to Java objects (specifically enums) - [@twall](https://github.com/twall).
* [#475](https://github.com/java-native-access/jna/issues/475): Avoid modifying native memory in `Structure.equals()/hashCode()`- [@twall](https://github.com/twall).
* [#496](https://github.com/java-native-access/jna/issues/496): Properly handle direct mapping with type mappers which return String/WString - [@twall](https://github.com/twall).

Release 4.1
===========

Features
--------
* Added `com.sun.jna.platform.win32.Advapi32Util.registryCloseKey` - [@falldog](https://github.com/falldog).
* Enabled platform tests to be run w/o building native bits - [@twall](https://github.com/twall).
* Added COM/Typelib java code generator `com.sun.jna.platform.win32.COM.tlb.TlbImp` - [@wolftobias](https://github.com/wolftobias).
* [#226](https://github.com/java-native-access/jna/issues/226): Added OSGI information to jna-platform.jar - [@brettwooldridge](https://github.com/brettwooldridge).
* [#267](https://github.com/java-native-access/jna/pull/267): Added support for Windows RAS32 API, `com.sun.jna.platform.win32.Rasapi32` and `Rasapi32Util` - [@kc7bfi](https://github.com/kc7bfi).
* [#101](https://github.com/java-native-access/jna/issues/101): Modify `com.sun.jna.platform.win32.Advapi32Util.registryGet*` API to support `KEY_WOW64` option - [@falldog](https://github.com/falldog).
* [#271](https://github.com/java-native-access/jna/pull/271): Added `com.sun.jna.platform.win32.Gdi32.ChoosePixelFormat` and `SetPixelFormat` - [@kc7bfi](https://github.com/kc7bfi).
* [#271](https://github.com/java-native-access/jna/pull/271): Added `com.sun.jna.platform.win32.OpenGL32`, `OpenGL32Util` and `WinOpenGL` - [@kc7bfi](https://github.com/kc7bfi).
* [#250](https://github.com/java-native-access/jna/pull/250): Added `com.sun.jna.platform.win32.Kernel32.GetPrivateProfileSection`, `GetPrivateProfileSectionNames` and `WritePrivateProfileSection` and corresponding `Kernel32Util` helpers - [@quipsy-karg](https://github.com/quipsy-karg).
* [#287](https://github.com/java-native-access/jna/pull/287): Added `DBTF_MEDIA` and `DBTF_NET` to `com.sun.jna.platform.win32.DBT` - [@daifei4321](https://github.com/daifei4321).
* [#295](https://github.com/java-native-access/jna/pull/295): Added `com.sun.jna.platform.win32.Kernel32.ResetEvent` - [@manithree](https://github.com/manithree).
* [#301](https://github.com/java-native-access/jna/pull/301): Added `accessCheck` to `com.sun.jna.platform.win32.Advapi32Util`, `MapGenericMask` and `AccessCheck` to `com.sun.jna.platform.win32.Advapi32`, `PRIVILEGE_SET` and `GENERIC_MAPPING` to `com.sun.jna.platform.win32.WinNT` - [@BusyByte](https://github.com/BusyByte).

Bug Fixes
---------
* Fixed inconsistent behavior on `Structure.ByValue` fields within a `Structure` - [@twall](https://github.com/twall).
* [#279](https://github.com/java-native-access/jna/issues/279): Accommodate FreeBSD libc loading - [@sevan](https://github.com/sevan).
* [#287](https://github.com/java-native-access/jna/pull/287): Fixed contrib `win32.Win32WindowDemo`, now showing the added/removed drive letter, and whether the event is about media in drive or physical drive - [@daifei4321](https://github.com/daifei4321).
* [#300](https://github.com/java-native-access/jna/issues/300): Fix stdcall argument alignment - [@twall](https://github.com/twall).

Release 4.0
===========

Features
--------
* Added ASL licensing to facilitate distribution - [@twall](https://github.com/twall).
* [#109](https://github.com/java-native-access/jna/issues/109): Set default Java compatibility level to 1.6 - [@twall](https://github.com/twall).
* [#209](https://github.com/java-native-access/jna/issues/209): Improved default performance saving last error results - [@twall](https://github.com/twall).
* Use predictable names for CPU architecture prefix (namely x86, x86-64); names correspond to OSGI processor values - [@twall](https://github.com/twall).
* Avoid superfluous Structure memory allocation from native - [@twall](https://github.com/twall).
* Added `Library.OPTION_CLASSLOADER`, which enables loading native libraries from any class loader (including JNA's native library). This enables parallel dependencies on JNA (e.g. within a tomcat deployment without having to include JNA in the app server environment) - [@twall](https://github.com/twall).
* Use per-library String encoding settings (see `Native.getDefaultStringEncoding()` and `Structure.getStringEncoding()`) - [@twall](https://github.com/twall).
* Added memory dump for debugging (see `com.sun.jna.Memory`) - [@twall](https://github.com/twall).
* Improved caching of Structure alignment, type mapping, and encoding information - [@twall](https://github.com/twall).
* [#225](https://github.com/java-native-access/jna/pull/225): Added `platform.win32.Kernel32.GetLogicalProcessorInformation` and `platform.win32.Kernel32Util.getLogicalProcessorInformation` - [@trejkaz](https://github.com/trejkaz).
* [#236](https://github.com/java-native-access/jna/issues/236): Auto-strip profiler native method prefix specified by `jna.profiler.prefix`, which defaults to $$YJP$$ - [@twall](https://github.com/twall).
* Added `jna.debug_load` property to diagnose library loading issues - [@twall](https://github.com/twall).
* Throw explicit `IllegalArgumentException` when `Structure.ByReference` is used where it shouldn't be (can result in multiply freed memory or other unexpected behavior) - [@twall](https://github.com/twall).
* [#243](https://github.com/java-native-access/jna/issues/243): Automatically accommodate long library paths on Windows which would otherwise fail - [@twall](https://github.com/twall).
* [#241](https://github.com/java-native-access/jna/issues/241) - Added  `com.sun.jna.platform.win32.Shell32.SHAppBarMessage` - [@bsorrentino](https://github.com/bsorrentino).
* Make `Structure.read/writeField()` protected to facilitate per-field overrides - [@twall](https://github.com/twall).
* Speed up callback lookup where large numbers of native function pointers are in use - [@twall](https://github.com/twall).

Bug Fixes
---------
* [#213](https://github.com/java-native-access/jna/pull/213): Fixed `Structure.toString()` not to dump memory when `jna.dump_memory` is false - [@tomohiron](https://github.com/tomohiron).
* Use dedicated TLS to indicate callback detach state, to avoid any potential conflicts with last error storage - [@twall](https://github.com/twall).
* [#173](https://github.com/java-native-access/jna/issues/173): Fixed OSX 10.8/Xcode 4+ builds, web start path with Oracle 1.7 JDK - [@mkjellman](https://github.com/mkjellman).
* [#215](https://github.com/java-native-access/jna/issues/215): Forced use of XSI `strerror_r` on linux - [LionelCons](https://github.com/LionelCons).
* [#214](https://github.com/java-native-access/jna/issues/214): Don't map library names when an absolute path is provided - [@twall](https://github.com/twall).
* [#218](https://github.com/java-native-access/jna/issues/218): Explicitly handled broken Android `SecurityManager` implementation - [@twall](https://github.com/twall).
* [#223](https://github.com/java-native-access/jna/issues/223): Fixed layout/size derivation for unions - [@twall](https://github.com/twall).
* [#229](https://github.com/java-native-access/jna/issues/229): Added `CreateProcessW` (Unicode version) - [@twall](https://github.com/twall).
* Avoid solaris/x86 JVM bug w/library open flags - [@twall](https://github.com/twall).
* Fixed NPE returning wide string from a direct-mapped function - [@twall](https://github.com/twall).
* [#237](https://github.com/java-native-access/jna/issues/237): Fix LastErrorException/getLastError on AIX - [@skissane](https://github.com/skissane).
* [#228](https://github.com/java-native-access/jna/issues/228): Fix win32/win64 crashes due to LastErrorException buffer overruns (`snprintf` on windows is broken) - [@davidhoyt](https://github.com/davidhoyt).

Release 3.5.2
=============

Features
--------
* Basic [COM support](https://github.com/java-native-access/jna/blob/master/www/PlatformLibrary.md) for w32 - [@wolftobias](https://github.com/wolftobias).
* Avoid superfluous Structure memory allocation by using Structure(Pointer) ctors if available - [@twall](https://github.com/twall).
* [PR#120](https://github.com/java-native-access/jna/pull/120): Provide methods for extracting native libraries from the class path for use by JNA - [@Zlika](https://github.com/Zlika).
* [PR#163](https://github.com/java-native-access/jna/pull/163): The Java `GUID` structure can be used directly as alternative to `Ole32Util.getGUIDFromString()` - [@wolftobias](https://github.com/wolftobias).
* [PR#163](https://github.com/java-native-access/jna/pull/163): Ported Win32 `dbt.h` - [@wolftobias](https://github.com/wolftobias).
* [PR#163](https://github.com/java-native-access/jna/pull/163): Added Win32 `WTSRegisterSessionNotification()` and `WTSUnRegisterSessionNotification()` from `Wtsapi32.dll` - [@wolftobias](https://github.com/wolftobias).
* [PR#163](https://github.com/java-native-access/jna/pull/163): Added Win32 `native_window_msg` that creates windows, registers for USB device and logon/logoff notifications - [@wolftobias](https://github.com/wolftobias).
* [PR#178](https://github.com/java-native-access/jna/pull/178): Added Win32 `USER_INFO_10` structure from `LMAccess.h` - [@davidmc24](https://github.com/davidmc24).
* [PR#192](https://github.com/java-native-access/jna/pull/192): Added Win32 `SHGetSpecialFolderPath()` and initialization file (.ini) API functions from `kernel32.dll` - [@headcrashing](https://github.com/headcrashing).
* [PR#194](https://github.com/java-native-access/jna/pull/194): Added Unit Test for `CLSIDFromProgID()` - [@headcrashing](https://github.com/headcrashing).
* [PR#196](https://github.com/java-native-access/jna/pull/196): Added Win32 `RegisterWindowMessage()` and new wrapper `User32Util` for convenient use of `RegisterWindowMessage`, `CreateWindow` and `CreateWindowEx` - [@headcrashing](https://github.com/headcrashing).
* [PR#187](https://github.com/java-native-access/jna/pull/187): Allow `StructureFieldOrderTest` unit test in platform project to run on Linux. - [@bhamail](https://github.com/bhamail).

Bug Fixes
---------
* [PR#180](https://github.com/java-native-access/jna/pull/180): Fix: added missing fields in `XEvents.getFieldOrder()` - [@xwizard](https://github.com/xwizard).
* [PR#183](https://github.com/java-native-access/jna/pull/183): Fix `LMAccess.GROUP_INFO_3.getFieldOrder()` to return correct fields names - [@bhamail](https://github.com/bhamail).
* [PR#187](https://github.com/java-native-access/jna/pull/187): Fix `getFieldOrder()` to return correct field names for some X11 structures - [@bhamail](https://github.com/bhamail).
* Remove deprecated methods on Memory (getSize,isValid) and Structure (getSize) - [@twall](https://github.com/twall).
* Remove problematic AWT check via `Class.forName("java.awt.Component")` (see [here](https://bugs.eclipse.org/bugs/show_bug.cgi?id=388170)) - [@twall](https://github.com/twall).
* [PR#210](https://github.com/java-native-access/jna/pull/210) Add OSGI processor specs for Mac OS X - [@bertfrees](https://github.com/bertfrees).
* [PR#174](https://github.com/java-native-access/jna/pull/174): Recompile linux-amd64 natives to remove glibc-2.11 dependencies, now requires only 2.2.5 or better - [@twall](https://github.com/twall).
* [PR#183](https://github.com/java-native-access/jna/pull/183): Added `StructureFieldOrderInspector` unit test utility to scan for `Structure` field issues; see: `com.sun.jna.platform.StructureFieldOrderTest.testMethodGetFieldOrder` - [@bhamail](https://github.com/bhamail).
* [PR#187](https://github.com/java-native-access/jna/pull/187): Allow `StructureFieldOrderTest` unit test in platform project to run on Linux - [@bhamail](https://github.com/bhamail).
* [#206](https://github.com/java-native-access/jna/issues/206): Fix `moveToTrash()` on OSX to work with symlinks - [@twall](https://github.com/twall).
* Fix NPE if `Thread.getContextClassLoader()` returns `null`  - [@twall](https://github.com/twall).

Release 3.5.1
=============

Bug Fixes
---------
* Fix bug where string fields sometimes failed to be writtern - [@twall](https://github.com/twall) (roman kisluhin).
* [PR#145](https://github.com/java-native-access/jna/pull/145): Fix `Netapi32Util.getDomainTrusts()` returns "empty" domain object - [@aikidojohn](https://github.com/aikidojohn).
* [PR#145](https://github.com/java-native-access/jna/pull/145): Fix `Netapi32.getDC()` - added missing fields in `DOMAIN_CONTROLLER_INFO` - [@aikidojohn](https://github.com/aikidojohn).
* [PR#151](https://github.com/java-native-access/jna/pull/151): 'platform.jar' in the dist directory was not updated for release 3.5.0. (The 'platform.jar' published to maven central was correct.)

Release 3.5.0
=============

Features
--------
* [#62](https://github.com/java-native-access/jna/issues/62) If a callback is required to reside in a DLL, use [`DLLCallback`](http://twall.github.com/jna/3.5.1/javadoc/com/sun/jna/win32/DLLCallback.html) to tag your Callback object - [@twall](https://github.com/twall).
* `Structure.getFieldOrder()` supersedes `Structure.setFieldOrder()` and is now required - [@twall](https://github.com/twall).
* Search `~/Library/Frameworks` and `/Library/Frameworks` on OSX - [@shaneholloway](https://github.com/shaneholloway).
* Automatic cleanup of native threads (based on suggestions from neil smith) - [@twall](https://github.com/twall).
* Add `android-arm` target - [@ochafik](https://github.com/ochafik), [@twall](https://github.com/twall).
* Add `jna.tmpdir` to override temporary JNA storage location - [@twall](https://github.com/twall).
* Add `EXTRA_MAKE_OPTS` ant property to override make variables - [@twall](https://github.com/twall).
* Add `Library.OPTION_OPEN_FLAGS` to customize dlopen behavior - [@twall](https://github.com/twall).
* [#113](https://github.com/java-native-access/jna/issues/113), [#114](https://github.com/java-native-access/jna/issues/114): Add support for GNU/kFreeBSD and debian multi-arch distros - [@twall](https://github.com/twall).

Bug Fixes
---------
* Fix `Advapi32Util.registryGetValues()` tried to allocate memory for a zero-length `REG_BINARY` value - [@phailwhale22](https://github.com/phailwhale22).
* Fix crash in direct mode callbacks with certain type conversions - [@twall](https://github.com/twall).
* More thoroughly propagate unexpected exceptions generated in jnidispatch - [@twall](https://github.com/twall).
* Cleanup maven poms and publishing to central repo - [@bhamail](https://github.com/bhamail).
* [#129](https://github.com/java-native-access/jna/issues/129): Allow `Memory` field in structure - [@twall](https://github.com/twall).
* Preserve `PointerType` fields on `Structure.read()` if unchanged - [@twall](https://github.com/twall).
* [#128](https://github.com/java-native-access/jna/issues/128): Fix masking extracting DWORD upper and lower WORD values - [@twall](https://github.com/twall).
* [#135](https://github.com/java-native-access/jna/issues/135): Fix for `Advapi32Util.registryGetValues()` when reading zero length values - [@danwi](https://github.com/danwi).

Release 3.4.2
=============

Features
--------
* Add `platform.win32.Kernel32.GetEnvironmentVariable` and `platform.win32.Kernel32Util.getEnvironmentVariable` - [@dblock](https://github.com/dblock).
* Moved `Kernel32.dll` function definitions from `WinNT.java` into `Kernel32.java` - [@dblock](https://github.com/dblock).
* Provide `toPointer()` methods on all `_PTR` types (platform win32) - [@twall](https://github.com/twall).
* Provide `ant -Dskip-native` to skip platform native build - [@twall](https://github.com/twall).
* Provide `ant -Dheadless=true` to run unit tests headless - [@twall](https://github.com/twall).
* Added Windows dev environment instructions - [@twall](https://github.com/twall).

Bug Fixes
---------
* Ensure platform win32 classes use unsigned where appropriate (`ULONG_PTR`, `UINT_PTR`, `ULONGLONG`, `WORD`, `DWORDLONG`) - [@twall](https://github.com/twall).
* [#71](https://github.com/java-native-access/jna/issues/71), [#73](https://github.com/java-native-access/jna/issues/73): Fix OSGI entries in manifest - [@twall](https://github.com/twall).
* [#78](https://github.com/java-native-access/jna/issues/78): Fix NPE in `platform.win32.Netapi32Util.getDomainTrusts` - [@dblock](https://github.com/dblock).
* Fix: auto-sync memory for `struct**` arguments (array of struct pointers) - [@twall](https://github.com/twall).
* Fix: `platform.win32.Secur32.AcquireCredentialsHandle`, `InitializeSecurityContext` and `AcceptSecurityContext` on Win32 64-bit - [@dblock](https://github.com/dblock).
* Fix: avoid overwriting native `char *` or `wchar_t *` fields within structures when unmodified (similar to current operation with pointers) - [@twall](https://github.com/twall).
* Fix: `platform.win32.DsGetDC.DS_DOMAIN_TRUSTS` and `DsEnumerateDomainTrusts` on Win32 64-bit - [@trejkaz](https://github.com/trejkaz).
* Fix: Crash freeing the wrong pointer in `Netapi32Util.getDomainTrusts` - [@trejkaz](https://github.com/trejkaz).
* [#100](https://github.com/java-native-access/jna/issues/100): Fix `platform.win32.W32FileMonitor` - [@dblock](https://github.com/dblock).
* Return INT_PTR from `platform.win32.Shell32.ShellExecute`, since returning
`HINSTANCE` is useless.
* Fix runtime error in some instances where Structure.setFieldOrder is used (never return self when sharing AutoAllocated memory).
* [#107](https://github.com/java-native-access/jna/issues/107): `Structure.clear()` always calls `ensureAllocated()` to avoid NPE.
* Ensure internal memory pointer is *always* allocated when calling `Structure.useMemory()`, even if layout is not yet determined.

Release 3.4.1
=============

Features
--------
* Add 'unsigned' modifier to IntegerType.
* Add to `platform.win32.User32`: `GetLastInputInfo`.
* Add `platform.win32.WinNT.GetFileType` and `platform.win32.Kernel32Util.getFileType`.
* Add to `platform.win32.Kernel32Util`: `getFileType`.

Bug Fixes
---------
* Re-build linux-amd and linux-i386 against older versions of glibc (2.2.5 and
2.1.3 respectively).
* Properly initialize first printer info struct in winspool library.
* Properly support getting and setting zero-array-length `REG_MULTI_SZ` values on Win32.
* Fixed SID in Win32 `USER_INFO_23` and `GROUP_INFO_3`.
* Fixed passing domain name into Win32 `Netapi32Util.getUserInfo`.

Release 3.4.0
=============

Features
--------
* Provide `jna.nosys=true` to avoid loading any system-provided JNA (useful for local build/development).
* Allow override of default jnidispatch library name with `jna.boot.library.name` system property.
* Throw an Error if a system install of JNA is incompatible or if JNA's JNI library does not match.
* Disable automatic jnidispatch unpacking with `jna.nounpack=true`.
* Automatically look up system error messages for LastErrorException.
* Improved callback thread-mapping support; re-use, rename, and group callback
threads.
* Cache structure layout results, improving performance of structure creation.
* linux/arm 32-bit support (hardware provided by Alex Lam).
* linux/ppc 32-bit support (hardware provided by Fritiof Hedman).
* Preliminary linux/ia64, linux/ppc64 support (thanks to Laurent Guerby and the GCC compile farm).
* Windows CE/Mobile support (w32ce-arm) (resources provided by andrea antonello and Hydrologis SRL).
* linux multi-arch support (kohsuke).
* Added REG_QWORD registry type support
* Add to `platform.unix.x11`: `XGrabKey`, `XUngrabKey`, `XSetErrorHandler`.
* Add to `platform.mac.Carbon`: `GetEventDispatcherTarget`, `InstallEventHandler`, `RegisterEventHotKey`, `GetEventParameter`, `RemoveEventHandler`, `UnregisterEventHotKey`.
* Add to `platform.win32.Kernel32`: `CopyFile`, `MoveFile`, `MoveFileEx`, `CreateProcess`, `SetEnvironmentVariables`, `GetFileTime`, `SetFileTime`, `SetFileAttributes`, `DeviceIoControl`, `GetDiskFreeSpaceEx`, `CreateToolhelp32Snapshot`, `Process32First`, `Process32Next`.
* Add to `platform.win32.Msi`: `MsiGetComponentPath`, `MsiLocateComponent`, `MsiGetProductCode`, `MsiEnumComponents`.
* Add to `platform.win32.User32`: `RegisterHotKey`, `UnregisterHotKey`
* Add to `platform.win32.SetupApi`: `SetupDiGetClassDevs`, `SetupDiDestroyDeviceInfoList`, `SetupDiEnumDeviceInterfaces`, `SetupDiGetDeviceInterfaceDetail`, `SetupDiGetDeviceRegistryProperty`.
* Add `platform.win32.Shell32.ShellExecute`.
* Add to `platform.win32.User32`: `SetParent`, `IsWindowVisible`, `MoveWindow`, `SetWindowPos`, `AttachInputThread`, `SetForegroundWindow`, `GetForegroundWindow`, `SetFocus`, `SendInput`, `WaitForInputIdle`, `InvalidateRect`, `RedrawWindow`, `GetWindow`, `UpdateWindow`, `ShowWindow`, `CloseWindow`.
* Add to `platform.win32.Version`: `GetFileVersionInfoSize`, `GetFileVersionInfo`, `VerQueryValue`.
* Add to `platform.win32.Advapi32`: `GetFileSecurity`, `RegQueryValueEx(...Long...)`.
* Add to `platform.win32.Netapi32`: `NetUserGetInfo`.

Bug Fixes
--------
* Revise cleanup of in-use temporary files on win32 (issue 6).
* Fix structure alignment issues on linux/ppc.
* Fix structure alignment issues on linux/arm.
* Account for NIO Buffer position (JIRA issue 185).
* Avoid crash with very long Strings (> 150k in length).
* Fix bug tracking Memory with an associated direct ByteBuffer.
* Fix bug handling structs by value when type mappers are in effect (JIRA issue 188).

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

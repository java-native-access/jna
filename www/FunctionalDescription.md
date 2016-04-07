Functional Overview
===================

JNA's platform-specific functionality is provided by the [libffi
library](https://github.com/atgreen/libffi).   Previous to the integration of
libffi into JNA (largely performed by wmeissner), hand-coded assembly was used
to support linux, sparc, windows, and Mac OSX (intel and PPC targets).   The
libffi library provides an abstraction for calling arbitrary target addresses
with an arbitrary set of typed arguments.

The `ffi_prep_cif()` call describes how the target function wishes to be
called, while `ffi_call()` actually performs the call, provided the CIF
structure returned by `ffi_prep_cif()`, an arguments array, and a buffer for a
return value. 


Interface Mapping
-----------------
When you instantiate a native library interface via `Native.loadLibrary()`,
JNA creates a proxy which routes all method invocations through a single
`invoke` function in `Library.Handler`.  This method looks up an appropriate
`Function` object which represents a function exported by the native library.
The proxy handler may perform some initial name translation to derive the
actual native library function name from the invoked proxy function.

Once the `Function` object is found, its generic `invoke` method is called
with all available arguments.  The proxy function signature is used to figure
out the types of the incoming arguments and the desired return type.

The `Function` object performs any necessary conversion of arguments,
converting `NativeMapped` types into their native representation, or applying
a `TypeMapper` to any incoming types which have registered for `TypeMapper`
conversion.   Similar conversion is performed on function return.  By default,
all `Structure` objects have their Java fields copied into their native memory
before the native function call, and copied back out after the call.

All `Function` invocations are routed through different native methods based
on their return type, but all those native methods are dispatched through the
same `dispatch` call in `native/dispatch.c`.  That function performs any final
conversions of Java objects into native representations before building a
function call description for use by libffi.

The libffi library requires a description of the target function's arguments
and return type in order to perform a platform-specific construction of the
stack suitable for the final native call invocation.  Once libffi has
performed the native call (via `ffi_call()`), it copies the result into a
buffer provided by JNA, which then converts it back into an appropriate Java
object. 

Direct Mapping
--------------
JNI provides for registering a native function to be called directly when a
method marked `native` is called from Java.  JNA constructs code stubs with
libffi for each native method registered via the `Native.register()` call (JNA
uses reflection to identify all methods with the `native` qualifier in the
direct-mapped class).  Each stub dispatches to the function `dispatch_direct`
in `native/dispatch.c`, and has an associated structure allocated which fully
describes the function invocation to avoid any reflection costs at runtime.  

The central `dispatch_direct` function attempts to pass the Java call stack
as-is to the native function (again, using `ffi_call()` from libffi).
The more non-primitive arguments are used, the more the direct dispatch has to
do extra work to convert Java objects into native representations on the
stack.  Ideal performance is achieved by using only primitive or `Pointer`
arguments. 
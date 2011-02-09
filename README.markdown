libffi-ios provides an experimental port of libffi to Apple's iOS.

The libffi closure mechanism has previously required the use of executable data pages, however, iOS sandbox restrictions prevent their use.
The OS does, however, permit remapping of executable pages, allowing one to remap a single page to multiple locations. libffi-ios modifies
ffi_closure_alloc/ffi_closure_free APIs to make use of this functionality to implement dynamically allocated and configured closure
trampolines without requiring the use of executable data.

The implementation works by allocating a writable config page, and then remapping a page of pre-compiled trampolines directly
after the config page. The trampolines use PC-relative addressing to load their context from the config page, and so it is possible
to allocate as many of these trampolines tables as you have memory space.

Note that this implementation is *experimental* and has seen very limited testing.

A build-ios.sh script is provided to configure and build an iOS library; you may need to edit the SDK settings in the script.

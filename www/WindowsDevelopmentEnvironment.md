## Setting up a Windows Development Environment

Windows builds require cygwin.  MSYS will probably work, but is not supported.
When installing cygwin, include ssh, git, make, autotools, and either gcc3 or
mingw64, depending on whether you're targeting win32 or win64, respectively (it's 
possible to build both from the same host).
 
To build on Win64, you'll need mingw64 (available with cygwin or separately),
and optionally the free MS Visual Studio C++ Express compiler.  The MS
compiler is preferred, since it provides structured event handling (SEH),
which allows JNA to trap native faults when run in protected mode.  To build
with the mingw64, uncomment the line assigning CC to $(MINGW) in
native/Makefile and make sure the cross-compiling mingw64 tools are in your
path.  To use the MS compiler, ensure that the 64-bit versions of
cl.exe/ml64.exe/link.exe are in your PATH and that the INCLUDE and LIB
environment variables are set properly (as in VCVARS.BAT). 

Even if compiling with the MS compiler, you must also install mingw64.

Sample configuration, setting up INCLUDE/LIB:
``` shell
export MSVC="/c/Program Files (x86)/Microsoft Visual Studio 10.0/vc"
export WSDK="/c/Program Files (x86)/Microsoft SDKs/Windows/v7.0A"
export WSDK_64="/c/Program Files/Microsoft SDKs/Windows/v7.1"

export INCLUDE="$(cygpath -m "$MSVC")/include;$(cygpath -m "$WSDK")/include"
# for 64-bit target
export LIB="$(cygpath -m "$MSVC")/lib/amd64;$(cygpath -m "$WSDK_64")/lib/x64"
# for 32-bit target
export LIB="$(cygpath -m "$MSVC")/lib;$(cygpath -m "$WSDK")/lib"
```

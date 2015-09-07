## Setting up a Windows Development Environment

#### Java

For a 32-bit build, set `JAVA_HOME` to a 32-bit JDK, eg. `C:\Program Files (x86)\java\jdk1.6.0_24`. 
For a 64-bit build, set `JAVA_HOME` to a 64-bit JDK, eg. `C:\Program Files\java\jdk1.6.0_24`. 

#### Native

MSVC
----

#### Visual Studio

JNA uses the free MS Visual Studio C++ Express compiler to compile
native bits if MSVC is set in the environment. The MS compiler provides
structured event handling (SEH), which allows JNA to trap native faults when
run in protected mode. 

On 64-bit windows, you will still need to install mingw64 in order to
compile a small bit of inline assembly.

To use the MS compiler, ensure that the appropriate 32-bit or 64-bit versions
of cl.exe/ml.exe/ml64.exe/link.exe are in your PATH and that the INCLUDE and
LIB environment variables are set properly (as in VCVARS.BAT). 

Sample configuration setting up INCLUDE/LIB:

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

#### mingw

Install [cygwin](http://www.cygwin.com/).

When installing cygwin, include ssh, git, make, autotools, and mingw{32|64}-g++.
Ensure the mingw compiler (i686-pc-mingw32-gcc.exe or i686-pc-mingw64-gcc.exe) is on your path.

If `cl.exe` is found on your %PATH%, you'll need to invoke `ant native
-DUSE_MSVC=false` in order to avoid using the MS compiler.

### Issues

#### Backslash R Command Not Found

If you get errors such as `'\r': command not found`, run `dos2unix -f [filename]`
for each file that it's complaining about.

### Building

Type `ant` from the top to build the project.

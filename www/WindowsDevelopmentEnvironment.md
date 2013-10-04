## Setting up a Windows Development Environment

32-bit Windows
--------------

#### Java

Set `JAVA_HOME` to a 32-bit JDK, eg. `C:\Program Files (x86)\java\jdk1.6.0_24`. 

#### Cygwin

Install [cygwin](http://www.cygwin.com/) or [msys](http://mingw.org/wiki/msys).

When installing cygwin, include ssh, git, make, autotools, and gcc3.

When installing msys, include gcc packages. 

64-bit Windows
--------------

#### Java

Set `JAVA_HOME` to a 64-bit JDK, eg. `C:\Program Files\java\jdk1.6.0_24`. 

#### Cygwin

Install [cygwin](http://www.cygwin.com/) or [msys](http://mingw.org/wiki/msys).

When installing cygwin, include ssh, git, make, autotools, and mingw64. 

When installing msys, include gcc packages. 

#### MingW64

Install Mingw64 from [here](http://sourceforge.net/projects/mingw-w64/files/Toolchains%20targetting%20Win64/Automated%20Builds/).
Download a package starting with *mingw-w64-bin_i686-mingw*. Extract the files to `c:\MinGW`
or the path where Cygwin is located.

#### Visual Studio

You can optionally use the free MS Visual Studio C++ Express compiler to compile
native bits. The MS compiler provides structured event handling (SEH),
which allows JNA to trap native faults when run in protected mode.

To use the MS compiler, ensure that the 64-bit versions of
cl.exe/ml64.exe/link.exe are in your PATH and that the INCLUDE and LIB
environment variables are set properly (as in VCVARS.BAT). 

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

### Issues

#### Backslash R Command Not Found

If you get errors such as `'\r': command not found`, run `dos2unix -f [filename]`
for each file that it's complaining about.

### Building

Type `ant` from the top to build the project.

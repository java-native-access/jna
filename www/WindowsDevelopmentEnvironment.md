Setting up a Windows Development Environment
============================================

This builds the library based on the Visual C++ compiler, but can be [adjusted to work without](#mingw-only) if needed.

### Background

JNA uses the Microsoft Visual Studio C++ compiler (MSVC) to compile
native bits when MSVC is detected in the environment. The MSVC compiler provides
structured event handling (SEH), which allows JNA to trap native faults when
run in protected mode.   It does this using libffi's `native/libffi/msvcc.sh`
wrapper script, which converts Makefile `gcc` commands to MSVC-compatible
(e.g. `cl.exe`, etc) calls.

For x86, x86_64, you will still need to install mingw64 in order to
compile a small bit of inline assembly.

To use the MSVC compiler, the appropriate x86 or x86_64 versions
of `cl.exe`/`ml(64).exe`/`link.exe` must be in your `PATH`
and that the `INCLUDE` and `LIB` environment variables are set properly.

We'll set these automatically using `VsDevCmd.bat`
<details>
	<summary>Prefer to use <code>bash</code> instead?</summary>
	
```bash
export MSVC="/c/Program Files (x86)/Microsoft Visual Studio 10.0/vc"
export WSDK="/c/Program Files (x86)/Microsoft SDKs/Windows/v7.0A"
export WSDK_64="/c/Program Files/Microsoft SDKs/Windows/v7.1"

export INCLUDE="$(cygpath -m "$MSVC")/include;$(cygpath -m "$WSDK")/include"
# for x86_64 target
export LIB="$(cygpath -m "$MSVC")/lib/amd64;$(cygpath -m "$WSDK_64")/lib/x64"
# for x86 target
export LIB="$(cygpath -m "$MSVC")/lib;$(cygpath -m "$WSDK")/lib"
```

**Warning:** The below steps are for `cmd` only.  If you're choosing to use `bash`, you'll need to adjust each command as needed.

   </details>

### Prerequisites
Starting pont: A clean Windows 10 64-bit Installation with all patches

1. Install "Visual Studio Community 2019" or the "Build Tools for Visual Studio 2019"
   (https://visualstudio.microsoft.com/downloads/)
    * Windows 10 SDK
    * MSVC v142 - VS 2019 C++-x64/x86-Buildtools
    * MSVC v142 - VS 2019 C++-ARM64-Buildtools
    * Windows Universal CRT SDK
2. Install AdoptOpenJDK 8 for the target architecture (https://adoptopenjdk.net/index.html)
3. Install ant (https://ant.apache.org/bindownload.cgi).
3. Install Cygwin 64 Bit (https://cygwin.com/install.html)
    * make
    * automake
    * automake1.15
    * libtool
    * git
    * gcc-g++ (See table)

      | x86_64 | x86 | aarch64 |
      |----------|-------|-----------|
      | `gcc-g++`<br>`mingw64-x86_64-gcc-g++` <br>`mingw64-x86_64-gcc-core` | `gcc-g++`<br>`mingw64-i686-gcc-g++` <br>`mingw64-i686-gcc-core` | `gcc-g++` |

### Steps

_**Note**_: The paths below are samples and depend on the exact versions
installed. For example for "Visual Studio Community 2019" `vcvarsall.bat` can
be found here:
`C:\Program Files (x86)\Microsoft Visual Studio\2019\Community\VC\Auxiliary\Build\vcvarsall.bat`,
while for "Build Tools for Visual Studio 2019" the file is found here:
`C:\Program Files (x86)\Microsoft Visual Studio\2019\BuildTools\VC\Auxiliary\Build\vcvarsall.bat`.

1. Open `cmd` for the following actions
2. Point `JAVA_HOME` to the root of the target JDK:
   #### JAVA_HOME `x86_64`
   ```cmd
   set JAVA_HOME=C:\Program Files\AdoptOpenJDK\jdk-8.0.222.10-hotspot
   ```
   
   #### JAVA_HOME `x86`
   ```cmd
   set JAVA_HOME=C:\Program Files (x86)\AdoptOpenJDK\jdk-8.0.222.10-hotspot
   ```

   #### JAVA_HOME `aarch64`
   Native builds only.  For cross-compiling, use [`x86_64`](#JAVA_HOME-x86_64).
   ```cmd
   set JAVA_HOME=%USERPROFILE%\jdk-16-ea+19-windows-aarch64
   ```

3. Ensure `ant` is accessible from the `PATH`
   ```cmd
   set PATH=%USERPROFILE%\apache-ant-1.9.11\bin;%PATH%
   ```
4. Include 64 Bit Cygwin in the path
   ```cmd
   set PATH=C:\cygwin64\bin\;%PATH%
   ```
5. Setup the Visual Studio build environment using `vcvarsall` in `<host>_<target>` notation:
   #### VsDevCmd `x86_64`
   ```cmd
   "C:\Program Files (x86)\Microsoft Visual Studio\2019\Community\VC\Auxiliary\Build\vcvarsall.bat" x64
   ```

   #### VsDevCmd `x86`
   ```cmd
   "C:\Program Files (x86)\Microsoft Visual Studio\2019\Community\VC\Auxiliary\Build\vcvarsall.bat" x64_x86
   ```

   #### VsDevCmd `aarch64`
   ```cmd
   "C:\Program Files (x86)\Microsoft Visual Studio\2019\Community\VC\Auxiliary\Build\vcvarsall.bat" x64_arm64
   ```

6. Run the build
   ```cmd
   ant
   ```

   ... or if cross-compiling, specify the target architecture and only build the native library, e.g:
   ```cmd
   ant -Dos.prefix=win32-aarch64 native
   ```

### Mingw Only

To build without Visual C++, using only Cygwin, just skip step 5 and skip installing the Visual C++ Build Tools 2019 . (Cygwin currently cannot build `aarch64` binaries, MSVC is needed)

### Troubleshooting

1. For native compiling or linking errors for MSVC builds after `VsDevCmd.bat` was run for a different/wrong architecture:
   - Close and reopen `cmd`
   - Configure `PATH`, `JAVA_HOME` again per target architecture.
   - Run `ant clean`
   - Start the build again.
1. For native compiling or linking errors for MSVC builds, toggle on debug mode in `native/libffi/msvcc.sh`:
   ```diff
   - verbose=
   + verbose=1
   ```
2. Re-run ant with detailed output:
   ```
   ant -DEXTRA_MAKE_OPTS="--debug=v"
   ```

Mac Development Environment
===========================

### Prequisites
* [Xcode](https://developer.apple.com/download/) with Command Line tools
* The following tools: `ant autoconf automake libtool gettext`
   * These are available via [Homebrew](https://brew.sh)
      ```bash
      brew install ant autoconf automake libtool gettext
      ```

   * ... or alternately via [MacPorts](https://www.macports.org/)

### Compiling

1. To compile and run unit tests for the current archecture:
   ```bash
   ant
   ```

2. To compile native components only and skip unit tests:
   ```bash
   ant native
   ```

3. To provide an alternate (older) macOS SDK:
   ```bash
   ant -DSDKROOT=/Applications/Xcode.app/Contents/Developer/Platforms/MacOSX.platform/Developer/SDKs/MacOSX10.7.sdk
   ```

4. To cross-compile for an alternate architecture:
   ```bash
   # ARM64 (>= 11.0)
   ant -Dos.prefix=darwin-aarch64

   # x86 (<= 10.13)
   ant -Dos.prefix=darwin-x86

   # x86_64
   ant -Dos.prefix=darwin-x86-64

   # PPC (<= 10.5)
   ant -Dos.prefix=darwin-ppc

   # PPC64
   ant -Dos.prefix=darwin-ppc64
   ```

### Troubleshooting


* Xcode - If you only have the Xcode command line tools installed, and not the full Xcode UI, you may see build errors like this:

        native:
             [exec] xcode-select: error: tool 'xcodebuild' requires Xcode, but active developer directory '/Library/Developer/CommandLineTools' is a command line tools instance

    This can be fixed by installing the full Xcode from App Store. Verify your installation by launching Xcode once and running `xcodebuild` from the command line.


* Various build tools are also required. You can install these using [brew](http://brew.sh).

        $ brew install autoconf automake libtool gettext

    If you are missing these tools, when you build you are likely to see errors like those below:

        native:
             [exec] Generating configure
             [exec] ./autogen.sh: line 2: exec: autoreconf: not found

    or

        [exec] Can't exec "aclocal": No such file or directory at /usr/local/Cellar/autoconf/2.69/share/autoconf/Autom4te/FileUtils.pm line 326.
        [exec] autoreconf: failed to run aclocal: No such file or directory

    or

        [exec] configure.ac:41: error: possibly undefined macro: AC_PROG_LIBTOOL

    or

        native:
            [exec] Configuring libffi (x86_64)
            [exec] configure: WARNING: unrecognized options: --enable-static, --disable-shared, --with-pic
            [exec] configure: error: cannot find install-sh, install.sh, or shtool in

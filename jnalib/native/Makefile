#
# Makefile for JNA native bits
# Copyright (c) 2007 Timothy Wall All Rights Reserved
#
# You may need to run 'ant javah' at the root before performing a build from 
# this directory.
#
# To build with debug information, use 'make DEBUG=true'
#
# Supported platforms (built and tested):
#
#   Windows 2000/XP/2003/Vista (x86)
#   OS X (i386/ppc)
#   Linux (i386, amd64) 
#   Solaris (i386, sparc, sparcv9)
#   FreeBSD (i386)
#

# Systems which support POSIX signals may be able to support VM crash
# protection simply by defining HAVE_PROTECTION.  This has been enabled
# only for those platforms on which it has been tested successfully.

OS=$(shell uname | sed -e 's/\(CYGWIN\|MINGW32\).*/win32/g' \
                        -e 's/SunOS.*/solaris/g' \
                        -e 's/FreeBSD.*/freebsd/g' \
                        -e 's/Darwin.*/darwin/g' \
                        -e 's/Linux.*/linux/g')

VERSION=$(shell sed -n 's/^.*version. value="\(\([0-9][0-9]*\)\(\.[0-9][0-9]*\(\.[0-9][0-9]*\)\?\)\?\).*".*/\1/p' ../build.xml)
JAVA_INCLUDES=-I"$(JAVA_HOME)/include" \
              -I"$(JAVA_HOME)/include/$(OS)"
BUILD=../build/native
JAVAH=$(BUILD)
INSTALLDIR=../build/$(OS)
JNIDISPATCH_OBJS=$(BUILD)/dispatch.o \
                 $(BUILD)/callback.o 
FFI_SRC=$(shell pwd)/libffi
FFI_BUILD=$(BUILD)/libffi
FFI_LIB=$(FFI_BUILD)/.libs/libffi_convenience.a
FFI_CONFIG=--disable-static --with-pic=yes
LIBRARY=$(BUILD)/$(LIBPFX)jnidispatch$(JNISFX)
TESTLIB=$(BUILD)/$(LIBPFX)testlib$(LIBSFX)

# Reasonable defaults based on GCC
LIBPFX=lib
LIBSFX=.so
JNISFX=$(LIBSFX)
CC=gcc
LD=gcc
# Default to Sun recommendations for JNI compilation
#COPT=-O2 -fomit-frame-pointer
COPT=-fno-omit-frame-pointer -fno-strict-aliasing 
CASM=-S
ifeq ($(DEBUG),true)
CDEBUG=-g
endif
COUT=-o $@
CINCLUDES=$(JAVA_INCLUDES) -I"$(JAVAH)" -I$(FFI_BUILD)/include
CDEFINES=-D_REENTRANT
PCFLAGS=-W -Wall -Wno-unused -Wno-parentheses
CFLAGS=$(PCFLAGS) $(COPT) $(CDEBUG) $(CDEFINES) $(CINCLUDES)
# -static-libgcc avoids gcc library incompatibilities across linux systems
LDFLAGS=-o $@ -shared -Wl,-soname,$@ -static-libgcc
LIBDIR=$(JAVA_HOME)/jre/lib/$(ARCH)
LIBS=-L"$(LIBDIR)" -ljawt 
STRIP=strip -x
# end defaults

ifeq ($(OS),win32)
# Uncomment this line to use MSVC (doesn't yet work with libffi)
#CC=cl
ARCH=i386

ifeq ($(CC),cl)
LD=link
COPT=-O1 -Op
ifeq ($(DEBUG),true)
COPT=-Od
DBG=d
CDEBUG=-D_DEBUG -GZ -Zi
endif
COUT=-Fo$@
CDEFINES=-DWIN32 -D_WINDOWS -D_MBCS -D_USRDLL -DNATIVE_EXPORTS
PCFLAGS=-nologo -MD$(DBG) -W3 -WX -GX -YX -Fd$(@D)/$* -Fp$(@D)/$*
CASM=-FAs

LDFLAGS=/pdb:$(subst .dll,,$@).pdb /implib:$(subst .dll,,$@).lib /debug /machine:i386 /pdbtype:sept /dll /nologo /opt:REF /incremental:no /out:$@ 
LIBDIR=/libpath:"$(JAVA_HOME)/lib" 
LIBS=$(LIBDIR) jawt.lib
STRIP=@echo
else
CDEFINES=-D__int64="long long" -D_JNI_IMPLEMENTATION -DHAVE_PROTECTION
PCFLAGS+=-mno-cygwin
LDFLAGS+=-mno-cygwin -Wl,--add-stdcall-alias -L /lib/mingw 
LIBDIR="$(JAVA_HOME)/lib"
LIBS=$(LIBDIR)/jawt.lib
endif

LIBPFX=
LIBSFX=.dll
endif

ifeq ($(OS),linux)
ARCH=i386
PCFLAGS+=-fPIC
CDEFINES+=-DHAVE_PROTECTION
endif

ifeq ($(OS),freebsd)
ARCH=i386
PCFLAGS+=-fPIC
CINCLUDES+=-I/usr/X11R6/include
LDFLAGS=-o $@ -shared -pthread
CDEFINES+=-DHAVE_PROTECTION
endif

ifeq ($(OS),solaris)
ifeq ($(ARCH),)
ARCH=$(shell uname -n)
endif
PCFLAGS+=-fPIC
LDFLAGS=-o $@ -G
ifeq ($(ARCH), sparcv9)
  # alter CC instead of PCFLAGS, since we need to pass it down to libffi 
  # configure and some of the other settings in PCFLAGS might make the build 
  # choke
  CC += -m64  
  LDFLAGS += -m64 
endif
endif

ifeq ($(OS),darwin)
ARCH=$(shell arch)
ifeq ($(ARCH),ppc)
ALT_ARCH=i386
OSX_FFI_CONFIG=--host=i386-apple-darwin --disable-dependency-tracking
else
ALT_ARCH=ppc
OSX_FFI_CONFIG=--host=powerpc-apple-darwin --disable-dependency-tracking
endif
FFI_ALT_BUILD=$(BUILD)/libffi-$(ALT_ARCH)
LIBSFX=.dylib
JNISFX=.jnilib
ifneq ($(SDKROOT),)
SYSLIBROOT=-Wl,-syslibroot,$(SDKROOT)
ISYSROOT=-isysroot $(SDKROOT)
ARCHFLAGS=-arch ppc -arch i386
endif
PCFLAGS+=$(ISYSROOT) -x objective-c
CDEFINES=-DTARGET_RT_MAC_CFM=0
LDFLAGS=$(ARCHFLAGS) -dynamiclib -o $@ -framework JavaVM \
  -compatibility_version $(shell echo ${VERSION}|sed 's/^\([0-9][0-9]*\).*/\1/g') \
  -current_version $(VERSION) \
  -mmacosx-version-min=10.3 \
  -install_name ${@F} \
  $(SYSLIBROOT)
# JAWT linkage handled by -framework JavaVM
LIBS=
endif

# Unfortunately, we have to use different libffi include files depending on 
# the target, so we can't do a simple universal build on darwin.  Do two
# separate builds, then merge the results.
$(BUILD)/%.o : %.c dispatch.h $(FFI_LIB)
	@mkdir -p $(BUILD)
ifneq ($(SDKROOT),)
	$(CC) -arch $(ARCH) $(CFLAGS) -c $< -o $@.$(ARCH)
	$(CC) -arch $(ALT_ARCH) -I$(FFI_ALT_BUILD)/include $(CFLAGS) -c $< -o $@.$(ALT_ARCH)
	lipo -create -output $@ $@.$(ARCH) $@.$(ALT_ARCH)
else
	$(CC) $(CFLAGS) -c $< $(COUT)
endif

all: $(LIBRARY) $(TESTLIB)

install:
	mkdir $(INSTALLDIR)
	cp $(LIBRARY) $(INSTALLDIR)

$(BUILD)/test_dispatch: test_dispatch.c $(BUILD)/dispatch.o $(BUILD)/dispatch_$(ARCH).o $(BUILD)/callback.o 
	$(CC) $(CFLAGS) $< $(BUILD)/dispatch.o $(BUILD)/dispatch_$(ARCH).o $(BUILD)/callback.o $(COUT)

$(LIBRARY): $(JNIDISPATCH_OBJS) $(FFI_LIB) 
	$(LD) $(LDFLAGS) $(JNIDISPATCH_OBJS) $(FFI_LIB) $(LIBS)

$(TESTLIB): $(BUILD)/testlib.o 
	$(LD) $(LDFLAGS) $< $(TESTDEF) 

$(FFI_LIB):
	@mkdir -p $(FFI_BUILD)
	@if [ ! -f $(FFI_BUILD)/Makefile ]; then \
	  echo "Configuring libffi ($(ARCH))"; \
	  (cd $(FFI_BUILD) \
	    && CC="$(CC) $(CDEBUG)" $(FFI_SRC)/configure $(FFI_CONFIG)); \
	fi
	$(MAKE) -C $(FFI_BUILD)
ifneq ($(SDKROOT),)
	@mkdir -p $(FFI_ALT_BUILD)
	@if [ ! -f $(FFI_ALT_BUILD)/Makefile ]; then \
	  echo "Configuring libffi ($(ALT_ARCH))"; \
	  (cd $(FFI_ALT_BUILD) \
	    && CFLAGS="-arch $(ALT_ARCH) $(ISYSROOT) $(COPT) $(CDEBUG) $(CDEFINES)" \
               LDFLAGS="-arch $(ALT_ARCH)" \
               $(FFI_SRC)/configure $(FFI_CONFIG) $(OSX_FFI_CONFIG)); \
	fi
	$(MAKE) -C $(FFI_ALT_BUILD)
	/usr/bin/libtool -static -o $@.tmp $(FFI_BUILD)/.libs/${@F} $(FFI_ALT_BUILD)/.libs/${@F}
	@mv $@.tmp $@
endif

clean:
	$(RM) -rf $(BUILD) 

version:
	@echo version=$(VERSION)

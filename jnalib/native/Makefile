#
# JNA JNI dispatch and test library
# Copyright (c) 2007 Timothy Wall All Rights Reserved
#
# You may need to run 'ant javah' at the root before performing a build from 
# this directory.
#
# To build the JNI headers, do 'ant javah' from the root
# To build the windows DLL using gcc, do 
#   make CC=gcc
# If you have the OSX 10.4 universal SDK installed, 
#   to target ppc on OSX/i386, do 
#     make ARCH=ppc
#   to target i386 on OSX/PPC, do 
#     make ARCH=i386
OS=$(shell uname | sed -e 's/CYGWIN.*/win32/g' \
                        -e 's/SunOS.*/sparc/g' \
                        -e 's/Darwin.*/darwin/g' \
                        -e 's/Linux.*/linux/g')

JAVA_INCLUDES=-I"$(JAVA_HOME)/include" \
              -I"$(JAVA_HOME)/include/$(OS)"
BUILD=../build/native
JAVAH=$(BUILD)
INSTALLDIR=../build/$(OS)
ASMDISPATCH_OBJS=$(BUILD)/dispatch_$(ARCH).o
JNIDISPATCH_OBJS=$(BUILD)/dispatch.o \
                 $(BUILD)/callback.o \
                 $(ASMDISPATCH_OBJS)
LIBRARY=$(BUILD)/$(LIBPFX)jnidispatch$(JNISFX)
TESTLIB=$(BUILD)/$(LIBPFX)testlib$(LIBSFX)

# Reasonable defaults based on GCC
LIBPFX=lib
LIBSFX=.so
JNISFX=$(LIBSFX)
CC=gcc
LD=gcc
# Default to Sun recommendations for JNI compilation
# Optimization causes errors in FP callback return values on linux/x86,
# crashes osx/x86, and other errors on ppc
# Turning it off eliminates those failures
#COPT=-O2 -fomit-frame-pointer
COPT=-fno-omit-frame-pointer -fno-strict-aliasing 
CASM=-S
ifeq ($(DEBUG),true)
CDEBUG=-g
endif
COUT=-o $@
CINCLUDES=$(JAVA_INCLUDES) -I"$(JAVAH)"
CDEFINES=-D_REENTRANT
PCFLAGS=-W -Wall -Wno-unused -Wno-parentheses -Werror
CFLAGS=$(PCFLAGS) $(COPT) $(CDEBUG) $(CDEFINES) $(CINCLUDES)
LDFLAGS=-o $@ -shared -Wl,-soname,$@ 
LIBDIR=$(JAVA_HOME)/jre/lib/$(ARCH)
LIBS=-L"$(LIBDIR)" -ljawt 
STRIP=strip -x
# end defaults

ifeq ($(OS),win32)
CC=cl
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
TESTDEF=/def:$(subst .dll,,$(@F)).def
STRIP=@echo
else
CDEFINES=-D__int64="long long" -D_JNI_IMPLEMENTATION
PCFLAGS=-mno-cygwin
# NOTE: have to recreate specs or dllcrt2.o isn't found
SPECS=$(BUILD)/specs.new
LDFLAGS+=-mno-cygwin -Wl,--add-stdcall-alias -specs $(BUILD)/specs.new -L /lib/mingw 
LIBDIR="$(JAVA_HOME)/lib"
LIBS=$(LIBDIR)/jawt.lib
endif

LIBPFX=
LIBSFX=.dll
endif

ifeq ($(OS),linux)
ARCH=i386
# Avoid 1.5/1.6 bug linking directly to -ljawt
CDEFINES+=-DNEED_JAWT_HACK
LIBS=
endif

# FIXME untested
ifeq ($(OS),sparc)
ARCH=sparc
# Avoid 1.5/1.6 bug linking directly to -ljawt
CDEFINES+=-DNEED_JAWT_HACK
LIBS=
endif

ifeq ($(OS),darwin)
ARCH=$(shell arch)
LIBSFX=.dylib
JNISFX=.jnilib
# Use universal binary when updated to work with gcc 4
ifneq ($(SDKROOT),)
ifneq ($(UNIVERSAL),)
ARCHFLAGS=-arch ppc -arch i386 -isysroot $(SDKROOT)
ASMDISPATCH_OBJS=$(BUILD)/dispatch_i386.o $(BUILD)/dispatch_ppc.o
else
ARCHFLAGS=-arch $(ARCH) -isysroot $(SDKROOT)
endif
endif
PCFLAGS+=$(ARCHFLAGS)
CDEFINES=-DTARGET_RT_MAC_CFM=0
LDFLAGS=$(ARCHFLAGS) -dynamiclib -o $@ -framework JavaVM 
LIBDIR=$(SDKROOT)/System/Library/Frameworks/JavaVM.framework/Versions/CurrentJDK/Libraries
endif

$(BUILD)/%.o : %.c
	@mkdir -p $(BUILD)
	$(CC) $(CFLAGS) -c $< $(COUT)

$(BUILD)/%.o: %.s
	@mkdir -p $(BUILD)
	$(AS) $(ASFLAGS) $< -o $@

# Generate assembly listing 
$(BUILD)/%.asm: %.c
	@mkdir -p $(BUILD)
	$(CC) $(CFLAGS) $(CASM) -c $< 
	@if [ -f $*.asm ]; then mv $*.asm $@; fi
	@rm -f $*.obj ${@D}/$*.o

all: $(LIBRARY) $(TESTLIB)

install:
	mkdir $(INSTALLDIR)
	cp $(LIBRARY) $(INSTALLDIR)

$(BUILD)/test_dispatch: test_dispatch.c $(BUILD)/dispatch.o $(BUILD)/dispatch_$(ARCH).o $(BUILD)/callback.o 
	$(CC) $(CFLAGS) $< $(BUILD)/dispatch.o $(BUILD)/dispatch_$(ARCH).o $(BUILD)/callback.o $(COUT)

$(BUILD)/specs.new: 
	gcc -dumpspecs | sed s%dllcrt2%/lib/mingw/dllcrt2%g > $@

$(LIBRARY): $(JNIDISPATCH_OBJS) $(SPECS)
	$(LD) $(LDFLAGS) $(JNIDISPATCH_OBJS) $(LIBS)

$(TESTLIB): $(BUILD)/testlib.o $(SPECS)
	$(LD) $(LDFLAGS) $< $(TESTDEF) 

clean:
	$(RM) -rf $(BUILD)

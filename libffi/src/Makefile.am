## Process this with automake to create Makefile.in

AUTOMAKE_OPTIONS = foreign

SUFFIXES = .c .lo .o .s .S

.deps/%.P: $(srcdir)/%.s
	@echo "Computing dependencies for $<..."
	@o='o';				\
	test -n "$o" && o='$$o';	\
	$(MKDEP) $< | sed "s,^\(.*\)\.o:,\1.$$o \1.l$$o $@:," > $@

.deps/%.P: $(srcdir)/%.S
	@echo "Computing dependencies for $<..."
	@o='o';				\
	test -n "$o" && o='$$o';	\
	$(MKDEP) $< | sed "s,^\(.*\)\.o:,\1.$$o \1.l$$o $@:," > $@

lib_LTLIBRARIES = libffi.la
noinst_PROGRAMS = ffitest

TARGET_SRC_MIPS_GCC = mips/ffi.c mips/o32.S mips/n32.S
TARGET_SRC_MIPS_SGI = mips/ffi.c mips/o32.s mips/n32.s
TARGET_SRC_X86 = x86/ffi.c x86/sysv.S
TARGET_SRC_SPARC = sparc/ffi.c sparc/v8.S
TARGET_SRC_ALPHA = alpha/ffi.c alpha/osf.S
TARGET_SRC_M68K = m68k/ffi.c m68k/sysv.S
TARGET_SRC_POWERPC = powerpc/ffi.c powerpc/sysv.S
TARGET_SRC_ARM =  arm/sysv.S arm/ffi.c

##libffi_la_SOURCES = debug.c prep_cif.c types.c $(TARGET_SRC_@TARGET@)
## Work around automake deficiency
libffi_la_common_SOURCES = debug.c prep_cif.c types.c
if MIPS_GCC
libffi_la_SOURCES = $(libffi_la_common_SOURCES) $(TARGET_SRC_MIPS_GCC)
endif
if MIPS_SGI
libffi_la_SOURCES = $(libffi_la_common_SOURCES) $(TARGET_SRC_MIPS_SGI)
endif
if X86
libffi_la_SOURCES = $(libffi_la_common_SOURCES) $(TARGET_SRC_X86)
endif
if SPARC
libffi_la_SOURCES = $(libffi_la_common_SOURCES) $(TARGET_SRC_SPARC)
endif
if ALPHA
libffi_la_SOURCES = $(libffi_la_common_SOURCES) $(TARGET_SRC_ALPHA)
endif
if M68K
libffi_la_SOURCES = $(libffi_la_common_SOURCES) $(TARGET_SRC_M68K)
endif
if POWERPC
libffi_la_SOURCES = $(libffi_la_common_SOURCES) $(TARGET_SRC_POWERPC)
endif
if ARM
libffi_la_SOURCES = $(libffi_la_common_SOURCES) $(TARGET_SRC_ARM)
endif

libffi_la_LDFLAGS = -version-info 2:3:1
ffitest_SOURCES = ffitest.c
ffitest_LDADD = libffi.la

EXTRA_DIST = mips/ffi.c mips/n32.S mips/n32.s mips/o32.S mips/o32.s \
sparc/ffi.c sparc/v8.S \
x86/ffi.c x86/sysv.S \
alpha/ffi.c alpha/osf.S \
m68k/ffi.c m68k/sysv.S \
powerpc/ffi.c powerpc/sysv.S powerpc/asm.h \
arm/ffi.c arm/sysv.S

VPATH = @srcdir@:@srcdir@/@TARGETDIR@

if MIPS_SGI

%.o: %.s
	$(COMPILE) -c $<

# This rule should only be used for compiling with the SGI assembler.
%.lo: %.s
	$(LTCOMPILE) -c $<

else

# This is the general rule.
%.o: %.S
	$(COMPILE) -c $<

# This is the general rule.
%.lo: %.S
	$(LTCOMPILE) -c $<

endif

INCLUDES = -I$(top_srcdir)/include -I../include

test:	ffitest
	./ffitest

lint:
	$(LINT) $(INCLUDES) $(srcdir)/*.c $(srcdir)/@TARGETDIR@/*.c

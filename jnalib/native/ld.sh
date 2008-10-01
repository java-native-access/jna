#!/bin/sh
#
# ld-compatible wrapper for link.exe
#
#args="/pdbtype:sept"
MSVC="/c/Program Files (x86)/Microsoft Visual Studio 9.0/vc/bin"
args="/nologo /opt:REF /incremental:no /subsystem:console /nodefaultlib:msvcrtd"
link="$MSVC/link"
while [ $# -gt 0 ]
do
  case $1
  in
    -m32)
      link="$MSVC/link"
      args="$args /machine:X86"
      shift 1
    ;;
    -m64)
      link="$MSVC/x86_amd64/link"
      args="$args /machine:X64"
      shift 1
    ;;
    -g)
      args="$args /debug"
      shift 1
    ;;
    -o)
      dir="$(dirname $2)"
      base="$(basename $2|sed 's/\.[^.]*//g')"
      args="$args /out:\"$2\" /pdb:$dir/$base.pdb /implib:$dir/$base.lib"
      shift 2
    ;;
    -shared)
      args="$args /DLL"
      shift 1
    ;;
    -static-libgcc)
      shift 1
    ;;
    *.dll)
      args="$args $(echo $1|sed -e 's/.dll/.lib/g')"
      shift 1
    ;;
    *.o|*.lib|*.a)
      args="$args $(echo $1|sed -e 's%\\%/%g')"
      shift 1
    ;;
    *)
      echo "Unsupported argument '$1'"
      exit 1
    ;;
  esac
done

echo "\"$link\" $args"
eval "\"$link\" $args"
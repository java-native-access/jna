#!/bin/sh
#
# ld-compatible wrapper for link.exe
#
#args="/pdbtype:sept"
args="/nologo /opt:REF /incremental:no /subsystem:console /nodefaultlib:msvcrtd"

if [ -z "$LIB" -a "$Lib" ]; then
    exit "LIB must be set for LINK.EXE to function properly"
fi

link=link
while [ $# -gt 0 ]
do
  case $1
  in
    -m32)
      if type link | grep amd64; then
          echo "Wrong LINK.EXE in path; use 32-bit version"
          exit 1
      fi
      if echo $LIB | grep amd64; then
          echo "Wrong paths in LIB; use 32-bit version"
          exit 1
      fi
      shift 1
    ;;
    -m64)
      if ! type link | grep amd64; then
          echo "Wrong LINK.EXE in path; use 64-bit version"
          exit 1
      fi
      if ! echo $LIB | grep amd64; then
          echo "Wrong paths in LIB; use 64-bit version"
          exit 1
      fi
      shift 1
    ;;
    -g)
      args="$args /debug"
      shift 1
    ;;
    -o)
      file=$(cygpath -m "$2")
      dir=$(dirname "$file")
      base=$(basename "$file"|sed 's/\.[^.]*//g')
      args="$args /out:\"$file\" /pdb:$dir/$base.pdb /implib:$dir/$base.lib"
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
      file=$(cygpath -m "$1")
      args="$args $(echo $file|sed -e 's/.dll/.lib/g')"
      shift 1
    ;;
    *.o|*.lib|*.a)
      file=$(cygpath -m "$1")
      args="$args $(echo $file|sed -e 's%\\%/%g')"
      shift 1
    ;;
    *)
      echo "Unsupported argument '$1'"
      exit 1
    ;;
  esac
done

echo "\"$link\" $args (LIB=$LIB)"
eval "\"$link\" $args"

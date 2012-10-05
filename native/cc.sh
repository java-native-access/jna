#!/bin/sh
#
# GCC-compatible wrapper for cl.exe
#
# Assumes CL.EXE and ML.EXE are in PATH and INCLUDE/LIB appropriately set
#

nowarn="/wd4127 /wd4820 /wd4706 /wd4100 /wd4255 /wd4668"
args="/nologo /EHac /W3 $nowarn" # /WX
#md="/MD"

cl="cl"
ml="ml"

if [ -z "$INCLUDE" -a -z "$Include" -o -z "$LIB" -a -z "$Lib" ]; then
    exit "INCLUDE and LIB must be set for CL.EXE to function properly"
fi

output=
while [ $# -gt 0 ]
do
  case $1
  in
    -fexceptions)
      shift 1
    ;;
    -fno-omit-frame-pointer)
      # TODO: does this have an equivalent?
      shift 1
    ;;
    -fno-strict-aliasing)
      # TODO: does this have an equivalent?
      shift 1
    ;;
    -mno-cygwin) 
      shift 1
    ;;
    -m32)
      if echo $PATH | grep amd64 >& /dev/null; then
          echo "Wrong CL.EXE in path; use 32-bit version"
          exit 1
      fi
      if echo $LIB | grep amd64 >& /dev/null; then
          echo "Wrong paths in LIB; use 32-bit version"
          exit 1
      fi
      ml=ml
      shift 1
    ;;
    -m64)
      if ! echo $PATH | grep amd64 >& /dev/null; then
          echo "Wrong CL.EXE in path; use 64-bit version"
          exit 1
      fi
      if ! echo $LIB | grep amd64 >& /dev/null; then
          echo "Wrong paths in LIB; use 64-bit version"
          exit 1
      fi
      ml=ml64
      shift 1
    ;;
    -O*)
      args="$args $i"
      shift 1
    ;;
    -g)
      # using /RTC1 instead of /GZ
      args="$args /Od /D_DEBUG /RTC1 /Zi"
#      md=/MDd
      shift 1
    ;;
    -c)
      args="$args /c"
      args="$(echo $args | sed 's%/Fe%/Fo%g')"
      single=/c
      shift 1
    ;;
    -shared)
      args="$args /LD"
      shift 1
    ;;
    -D*=*)
      name="$(echo $1|sed 's/-D\([^=][^=]*\)=.*/\1/g')"
      value="$(echo $1|sed 's/-D[^=][^=]*=//g')"
      args="$args -D${name}='$value'"
      defines="$defines -D${name}='$value'"
      shift 1
    ;;
    -D*)
      args="$args $1"
      defines="$defines $1"
      shift 1
    ;;
    -E)
      args="$args /E"
      shift 1
    ;;
    -I)
      file=$(cygpath -m "$2")
      args="$args /I\"$file\""
      includes="$includes /I\"$file\""
      shift 2
    ;;
    -I*)
      file="$(echo $1|sed -e 's/-I//g')"
      file=$(cygpath -m "$file")
      args="$args /I\"$file\""
      includes="$includes /I\"$file\""
      shift 1
    ;;
    -W|-Wextra)
      # TODO map extra warnings
      shift 1
    ;;
    -Wall)
      args="$args /Wall"
      shift 1
    ;;
    -Werror)
      args="$args /WX"
      shift 1
    ;;
    -W*)
      # TODO map specific warnings
      shift 1
    ;;
    -S)
      args="$args /FAs"
      shift 1
    ;;
    -o)
      file=$(cygpath -m "$2")
      outdir=$(dirname "$file")
      base=$(basename "$file"|sed 's/\.[^.]*//g')
      if [ -n "$assembly" ]; then
        target="$file"
      fi
      if [ -n "$single" ]; then 
        output="/Fo$file"
      else
        output="/Fe$file"
      fi
      if [ -n "$assembly" ]; then
        args="$args $output"
      else
        args="$args $output \"/Fd$outdir/$base\" \"/Fp$outdir/$base\" \"/Fa$outdir/$base\""
      fi
      shift 2
    ;;
    *.S)
      file=$(cygpath -m "$1")
      src=$(echo $file|sed -e 's/.S$/.asm/g' -e 's%\\%/%g')
      echo "$cl /nologo /EP $includes $defines \"$file\" > \"$src\""
      eval "$cl /nologo /EP $includes $defines \"$file\"" > "$src" || exit $?
      md=""
      cl="$ml"
      output=$(echo $output | sed 's%/F[dpa][^ ]*%%g')
      args="/nologo $single \"$src\" $output"
      assembly="true"
      shift 1
    ;;
    *.c)
      file=$(cygpath -m "$1")
      args="$args \"$(echo $file|sed -e 's%\\%/%g')\""
      shift 1
    ;;
    -print-multi-os-directory)
      # Ignore this when called by accident
      echo ""
      exit 0
    ;;
    *)
      echo "Unsupported argument '$1'"
      exit 1
    ;;
  esac
done

args="$md $args"
echo "$cl $args"
eval "$cl $args"
result=$?
# @#!%@!# ml64 broken output
if [ -n "$assembly" ]; then
    mv $src $outdir
    mv *.obj $target
fi
exit $result

#!/bin/sh
#
# GCC-compatible wrapper for cl.exe
#
MSVC="/c/Program Files (x86)/Microsoft Visual Studio 9.0/vc/bin"
nowarn="/wd4127 /wd4820 /wd4706 /wd4100 /wd4255 /wd4668"
args="/nologo /EHac /W3 /LD $nowarn" # /WX
# FIXME is this equivalent to --static-libgcc? links to msvcrt.lib
# I've forgotten why it was originally added
# /MD causes link problems
#md=/MD
cl="$MSVC/cl"
ml="$MSVC/ml"
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
      cl="$MSVC/cl"
      ml="$MSVC/ml"
      shift 1
    ;;
    -m64)
      cl="$MSVC/x86_amd64/cl"
      ml="$MSVC/x86_amd64/ml64"
      shift 1
    ;;
    -O*)
      args="$args $i"
      shift 1
    ;;
    -g)
      # using /RTC1 instead of /GZ
      args="$args /Od /D_DEBUG /RTC1 /Zi"
      md=/MDd
      shift 1
    ;;
    -c)
      args="$args /c"
      args="$(echo $args | sed 's%/Fe%/Fo%g')"
      single="/c"
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
    -I)
      args="$args /I\"$2\""
      includes="$includes /I\"$2\""
      shift 2
    ;;
    -I*)
      args="$args /I\"$(echo $1|sed -e 's/-I//g')\""
      includes="$includes /I\"$(echo $1|sed -e 's/-I//g')\""
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
      outdir="$(dirname $2)"
      base="$(basename $2|sed 's/\.[^.]*//g')"
      if [ -n "$single" ]; then 
        output="/Fo$2"
      else
        output="/Fe$2"
      fi
      if [ -n "$assembly" ]; then
        args="$args $output"
      else
        args="$args $output /Fd$outdir/$base /Fp$outdir/$base /Fa$outdir/$base"
      fi
      shift 2
    ;;
    *.S)
      src="$(echo $1|sed -e 's/.S$/.asm/g' -e 's%\\%/%g')"
      echo "$cl /EP $includes $defines $1 > $src"
      "$cl" /nologo /EP $includes $defines $1 > $src || exit $?
      md=""
      cl="$ml"
      output="$(echo $output | sed 's%/F[dpa][^ ]*%%g')"
      args="/nologo $single $src $output"
      assembly="true"
      shift 1
    ;;
    *.c)
      args="$args $(echo $1|sed -e 's%\\%/%g')"
      shift 1
    ;;
    *)
      echo "Unsupported argument '$1'"
      exit 1
    ;;
  esac
done

args="$md $args"
echo "$cl $args"
eval "\"$cl\" $args"
result=$?
# @#!%@!# ml64 broken output
if [ -n "$assembly" ]; then
    mv $src $outdir
    mv *.obj $outdir
fi
exit $result
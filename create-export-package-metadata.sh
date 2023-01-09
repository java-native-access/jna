#!/usr/bin/env sh
set -e

extract_export_package_value_for_buildxml() {
  sed -z -E 's:\r?\n ::g' "$1" \
    | grep '^Export-Package' \
    | sed 's/^Export-Package: //' \
    | sed 's/",/",\n/g' \
    | sed 's/1\.0\.0/${osgi.version}/g' \
    | sed 's/"/\&quot;/g'
}

rm -rf tmp

mkdir tmp

cp -r src tmp

mvn \
-f create-export-package-metadata-pom.xml \
-DsourceDirectory=tmp/src \
-DoutputDirectory=tmp/target \
-DexportedPackages=com.sun.jna,com.sun.jna.ptr,com.sun.jna.win32 \
clean package

cp -r contrib/platform/src tmp

mvn \
-f create-export-package-metadata-pom.xml \
-DsourceDirectory=tmp/src \
-DoutputDirectory=tmp/target-platform \
-DexportedPackages=\
com.sun.jna.platform,\
com.sun.jna.platform.dnd,\
com.sun.jna.platform.linux,\
com.sun.jna.platform.mac,\
com.sun.jna.platform.unix,\
com.sun.jna.platform.unix.aix,\
com.sun.jna.platform.unix.solaris,\
com.sun.jna.platform.win32,\
com.sun.jna.platform.win32.COM,\
com.sun.jna.platform.win32.COM.tlb,\
com.sun.jna.platform.win32.COM.tlb.imp,\
com.sun.jna.platform.win32.COM.util,\
com.sun.jna.platform.win32.COM.util.annotation,\
com.sun.jna.platform.wince \
-DimportedPackages=com.sun.jna,com.sun.jna.ptr,com.sun.jna.win32 \
clean package

echo 'build.xml: Export-Package:'
echo
extract_export_package_value_for_buildxml tmp/target/META-INF/MANIFEST.MF
echo
echo

echo 'contrib/platform/build.xml: Export-Package:'
echo
extract_export_package_value_for_buildxml tmp/target-platform/META-INF/MANIFEST.MF
echo
echo

rm -r tmp

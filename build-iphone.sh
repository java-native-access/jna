#!/bin/sh

PLATFORM_IOS=/Developer/Platforms/iPhoneOS.platform/
PLATFORM_IOS_SIM=/Developer/Platforms/iPhoneSimulator.platform/
SDK_IOS_VERSION="4.1"

mkdir -p "build-ios"
pushd "build-ios"
export CC="${PLATFORM_IOS}"/Developer/usr/bin/gcc-4.2
export CFLAGS="-arch armv6 -isysroot ${PLATFORM_IOS}/Developer/SDKs/iPhoneOS${SDK_IOS_VERSION}.sdk/"
../configure --host=armv6-apple-darwin && make
popd

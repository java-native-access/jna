#!/bin/sh

PLATFORM_IOS=/Developer/Platforms/iPhoneOS.platform/
PLATFORM_IOS_SIM=/Developer/Platforms/iPhoneSimulator.platform/
SDK_IOS_VERSION="4.1"
MIN_IOS_VERSION="3.0"

mkdir -p "build-ios"
pushd "build-ios"
export CC="${PLATFORM_IOS}"/Developer/usr/bin/gcc-4.2
export CFLAGS="-arch armv6 -isysroot ${PLATFORM_IOS}/Developer/SDKs/iPhoneOS${SDK_IOS_VERSION}.sdk/ -miphoneos-version-min=${MIN_IOS_VERSION}"
../configure --host=arm-apple-darwin10 && make
popd

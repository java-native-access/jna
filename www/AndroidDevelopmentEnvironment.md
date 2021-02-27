Android Development Environment
===============================

* Add Android SDK/NDK tools into `PATH` (used by `native/Makefile`).
* Set environment variable `NDK_PLATFORM` (used by `native/Makefile`).
* Build using: `ant -Dos.prefix=android-arm dist`.
* Tests must be run on the target platform, not the build platform.
* Add `dist/jna.jar` and/or `dist/platform.jar` to your application, as needed.
* If you're using android-maven-plugin, `jna.jar` can be used as-is (native libraries will be automatically copied into your project if it directly expresses the jna dependency, and also uses android-maven-plugin).
* If you're using Google's Eclipse plugin then you must manually remove libjnidispatch.so from jna.jar/lib/armeabi and add it into your project's libs/armeabi directory.
* See http://code.google.com/p/android/issues/detail?id=17861 and http://developer.android.com/guide/practices/jni.html for more information.
* The NDK can be downloaded from https://developer.android.com/ndk/index.html

Sample build
------------

```bash
export NDK_PLATFORM=/home/matthias/bin/android-ndk-r12b/platforms/android-21
export PATH=$NDK_PLATFORM/../../toolchains/aarch64-linux-android-4.9/prebuilt/linux-x86_64/bin/:$PATH
export PATH=$NDK_PLATFORM/../../toolchains/arm-linux-androideabi-4.9/prebuilt/linux-x86_64/bin/:$PATH
export PATH=$NDK_PLATFORM/../../toolchains/mips64el-linux-android-4.9/prebuilt/linux-x86_64/bin/:$PATH
export PATH=$NDK_PLATFORM/../../toolchains/mipsel-linux-android-4.9/prebuilt/linux-x86_64/bin/:$PATH
export PATH=$NDK_PLATFORM/../../toolchains/x86-4.9/prebuilt/linux-x86_64/bin/:$PATH
export PATH=$NDK_PLATFORM/../../toolchains/x86_64-4.9/prebuilt/linux-x86_64/bin/:$PATH
ant -Dos.prefix=android-aarch64
ant -Dos.prefix=android-armv7
ant -Dos.prefix=android-arm
ant -Dos.prefix=android-mips64
ant -Dos.prefix=android-mips
ant -Dos.prefix=android-x86-64
ant -Dos.prefix=android-x86
```

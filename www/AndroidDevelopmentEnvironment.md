Android Development Environment
===============================

* Add Android SDK/NDK tools into `PATH` (used by `native/Makefile`).
* Set environment variables `NDK` and `NDK_PLATFORM` (used by `native/Makefile`).
* Build using: `ant -Dos.prefix=android-arm dist`.
* Tests must be run on the target platform, not the build platform.
* Add `dist/jna.jar` and/or `dist/platform.jar` to your application, as needed.
* If you're using android-maven-plugin, `jna.jar` can be used as-is (native libraries will be automatically copied into your project).
* If you're using Google's Eclipse plugin then you must manually remove libjnidispatch.so from jna.jar/lib/armeabi and add it into your project's libs/armeabi directory.
* See http://code.google.com/p/android/issues/detail?id=17861 and http://developer.android.com/guide/practices/jni.html for more information.
* The NDK can be downloaded from https://developer.android.com/ndk/index.html

Sample build
------------

NDKs with version >= 18

```bash
export NDK=/home/matthias/Android/Sdk/ndk/30.0.16248370
export NDK_PLATFORM=21
export PATH=$NDK/toolchains/llvm/prebuilt/linux-x86_64/bin/:$PATH
ant -Dos.prefix=android-aarch64
ant -Dos.prefix=android-armv7
ant -Dos.prefix=android-x86-64
ant -Dos.prefix=android-x86
```

NDKs with version < 18 (<= 16 for armv5 and mips targets)

```bash
export NDK=/home/matthias/bin/android-ndk-r12b
export NDK_PLATFORM=21
export PATH=$NDK/toolchains/aarch64-linux-android-4.9/prebuilt/linux-x86_64/bin/:$PATH
export PATH=$NDK/toolchains/arm-linux-androideabi-4.9/prebuilt/linux-x86_64/bin/:$PATH
export PATH=$NDK/toolchains/mips64el-linux-android-4.9/prebuilt/linux-x86_64/bin/:$PATH
export PATH=$NDK/toolchains/mipsel-linux-android-4.9/prebuilt/linux-x86_64/bin/:$PATH
export PATH=$NDK/toolchains/x86-4.9/prebuilt/linux-x86_64/bin/:$PATH
export PATH=$NDK/toolchains/x86_64-4.9/prebuilt/linux-x86_64/bin/:$PATH
ant -Dos.prefix=android-aarch64
ant -Dos.prefix=android-armv7
ant -Dos.prefix=android-arm
ant -Dos.prefix=android-mips64
ant -Dos.prefix=android-mips
ant -Dos.prefix=android-x86-64
ant -Dos.prefix=android-x86
```

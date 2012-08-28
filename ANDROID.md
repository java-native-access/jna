* Add the path of an Android NDK toolchain into PATH (e.g. export PATH=$PATH:$ANDROID_NDK_HOME/toolchains/arm-linux-androideabi-4.4.3/prebuilt/linux-x86/bin)
* Set ANDROID_HOME in native/Makefile to the full path of the NDK platform to use (e.g. /home/gili/android-ndk-r8/platforms/android-8/arch-arm). WARNING: The home directory alias ("~/") may not be used in ANDROID_HOME.
* Tests must be run on the target platform, not the build platform
* Build using: ant -Dos.prefix=android-arm dist
* Add dist/jna.jar and/or dist/platform.jar to your application, as needed.
* If you're using android-maven-plugin, jna.jar can be used as-is (native libraries will be automatically copied into your project).
* If you're using Google's Eclipse plugin then you must manually remove libjnidispatch.so from jna.jar/lib/armeabi and add it into your project's "libs/armeabi" directory.
* See http://code.google.com/p/android/issues/detail?id=17861 and http://developer.android.com/guide/practices/jni.html for more information.
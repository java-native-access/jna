Android Development Environment
===============================

* Add Android SDK/NDK tools into `PATH` (used by `native/Makefile`).
* Set environment variable `NDK_PLATFORM` (used by `native/Makefile`).
* Build using: `ant -Dos.prefix=android-arm dist`.
* Tests must be run on the target platform, not the build platform.
* Add `dist/jna.jar` and/or `dist/platform.jar` to your application, as needed.
* If you're using android-maven-plugin, `jna.jar` can be used as-is (native libraries will be automatically copied into your project).
* If you're using Google's Eclipse plugin then you must manually remove libjnidispatch.so from jna.jar/lib/armeabi and add it into your project's libs/armeabi directory.
* See [http://code.google.com/p/android/issues/detail?id=17861](http://code.google.com/p/android/issues/detail?id=17861) and [http://developer.android.com/guide/practices/jni.html](http://developer.android.com/guide/practices/jni.html) for more information.




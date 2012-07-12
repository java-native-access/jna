* Add Android SDK/NDK tools into PATH (used by native/Makefile)
* Set ANDROID_HOME (used by native/Makefile)
* Tests must be run on the target platform, not the build platform
* libjnidispatch.so must be preinstalled; you cannot rely on JNA to unpack it
  from its jar file for you.  Follow the Android NDK directions.


* Add Android SDK/NDK tools into PATH
* Set ANDROID_HOME
* Tests must be run on the target platform
* Before running, if you don't pre-install libjnidispatch.so where JNA can
  load it from the system path, you need to adjust where JNA unpacks it:
  PackageManager pm = context.getPackageManager();
  String dataDir = pm.getApplicationInfo(context.getPackageName(), 0).dataDir;
  System.setProperty("jna.unpack.path", dataDir);

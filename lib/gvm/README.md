# JNA for Native Image

This directory specifies sources for running JNA under [GraalVM Native Image](https://www.graalvm.org/latest/reference-manual/native-image/).

Native Image targets run under the [Substrate Virtual Machine (SVM)](https://docs.oracle.com/en/graalvm/enterprise/20/docs/reference-manual/native-image/SubstrateVM/), rather than a traditional JVM.

JNA is usable out of the box on JVM but requires some configuration for SVM:

- [JNI registration](https://www.graalvm.org/latest/reference-manual/native-image/dynamic-features/JNI/): Lookups and calls over JNI must be registered in a configuration file.

- [Proxy registration](https://www.graalvm.org/latest/reference-manual/native-image/dynamic-features/DynamicProxy/): Classes that extend JNA's `Library` interface must also be registered as dynamic proxies.

**There are several ways to properly configure SVM: manually or via the built-in GraalVM [`Feature`](https://www.graalvm.org/sdk/javadoc/org/graalvm/nativeimage/hosted/Feature.html) support.**

## Automatic Configuration

To automatically configure `native-image` for JNA, **add the `jna-graalvm.jar` to your classpath:**

**Maven (`pom.xml`):**
```xml
    <dependency>
      <groupId>net.java.dev.jna</groupId>
      <artifactId>jna-graalvm</artifactId>
      <version>... (5.15.0 or greater) ...</version>
    </dependency>
```

**Gradle Groovy DSL (`build.gradle`):**
```groovy
    // check maven central for the latest version
    implementation 'net.java.dev.jna:jna-graalvm:5.15.0'
```

**Gradle Kotlin DSL (`build.gradle.kts`):**
```kotlin
    // check maven central for the latest version
    implementation("net.java.dev.jna:jna-graalvm:5.15.0")
```

That's it! You should see `com.sun.jna.JavaNativeAccess` show up during your native image build:

```
   ========================================================================================================================
   GraalVM Native Image: Generating 'graalvm-native-static-jna' (executable)...
   ========================================================================================================================
   For detailed information and explanations on the build output, visit:
   https://github.com/oracle/graal/blob/master/docs/reference-manual/native-image/BuildOutput.md
   ------------------------------------------------------------------------------------------------------------------------
   [1/8] Initializing...                                                                                    (2.7s @ 0.18GB)
    Java version: 22.0.1+8, vendor version: Oracle GraalVM 22.0.1+8.1
    Graal compiler: optimization level: 2, target machine: armv8-a, PGO: off
    C compiler: cc (apple, arm64, 15.0.0)
    Garbage collector: Serial GC (max heap size: 80% of RAM)
    3 user-specific feature(s):
    - com.oracle.svm.thirdparty.gson.GsonFeature
→   - com.sun.jna.JavaNativeAccess: Enables access to JNA at runtime on SubstrateVM
    ...
```

## Manual Configuration

The configuration produced by the automatic route is generally identical to the configuration needed to run your app, but you can also set it up manually instead.

**Omit the `jna-graalvm.jar` dependency**

If you include it on your Native Image classpath at build-time, the `JavaNativeAccess` feature is added automatically. If you want to manually configure JNI for JNA, don't include it on your classpath.

> [!WARNING]
> These configurations are provided on a best-effort basis and may be different for your app.

### Specify JNI configurations

If you don't already have one, create a [`jni-config.json`](https://www.graalvm.org/latest/reference-manual/native-image/dynamic-features/JNI/#reflection-metadata) file within your Java app, and configure Native Image to find it.

Then, add the configurations below to your `jni-config.json`.

<details>
<summary>Click to show JSON configuration entries</summary>
<pre>
[
  {
  "name":"com.sun.jna.Callback",
  "allDeclaredMethods":true,
  "allPublicMethods":true,
  "allDeclaredConstructors":true,
  "allPublicConstructors":true
},
{
  "name":"com.sun.jna.CallbackReference",
  "allDeclaredMethods":true,
  "allPublicMethods":true,
  "allDeclaredConstructors":true,
  "allPublicConstructors":true,
  "methods":[{"name":"getCallback","parameterTypes":["java.lang.Class","com.sun.jna.Pointer","boolean"] }, {"name":"getFunctionPointer","parameterTypes":["com.sun.jna.Callback","boolean"] }, {"name":"getNativeString","parameterTypes":["java.lang.Object","boolean"] }, {"name":"initializeThread","parameterTypes":["com.sun.jna.Callback","com.sun.jna.CallbackReference$AttachOptions"] }]
},
{
  "name":"com.sun.jna.CallbackReference$AttachOptions",
  "allDeclaredMethods":true,
  "allPublicMethods":true,
  "allDeclaredConstructors":true,
  "allPublicConstructors":true
},
{
  "name":"com.sun.jna.FromNativeConverter",
  "allDeclaredMethods":true,
  "allPublicMethods":true,
  "allDeclaredConstructors":true,
  "allPublicConstructors":true,
  "methods":[{"name":"nativeType","parameterTypes":[] }]
},
{
  "name":"com.sun.jna.IntegerType",
  "allDeclaredMethods":true,
  "allPublicMethods":true,
  "allDeclaredConstructors":true,
  "allPublicConstructors":true,
  "fields":[{"name":"value", "allowWrite":true}]
},
{
  "name":"com.sun.jna.JNIEnv",
  "allDeclaredMethods":true,
  "allPublicMethods":true,
  "allDeclaredConstructors":true,
  "allPublicConstructors":true
},
{
  "name":"com.sun.jna.Native",
  "allDeclaredMethods":true,
  "allPublicMethods":true,
  "allDeclaredConstructors":true,
  "allPublicConstructors":true,
  "methods":[
    {"name":"dispose","parameterTypes":[] },
    {"name":"fromNative","parameterTypes":["com.sun.jna.FromNativeConverter","java.lang.Object","java.lang.reflect.Method"] },
    {"name":"fromNative","parameterTypes":["java.lang.Class","java.lang.Object"] },
    {"name":"fromNative","parameterTypes":["java.lang.reflect.Method","java.lang.Object"] },
    {"name":"nativeType","parameterTypes":["java.lang.Class"] },
    {"name":"toNative","parameterTypes":["com.sun.jna.ToNativeConverter","java.lang.Object"]},
    {"name":"open","parameterTypes":["java.lang.String","java.lang.Integer"]},
    {"name":"close","parameterTypes":["java.lang.Long"]},
    {"name":"findSymbol","parameterTypes":["java.lang.Long","java.lang.String"]}
  ]
},
{
  "name":"com.sun.jna.Native$ffi_callback",
  "allDeclaredMethods":true,
  "allPublicMethods":true,
  "allDeclaredConstructors":true,
  "allPublicConstructors":true,
  "methods":[{"name":"invoke","parameterTypes":["long","long","long"] }]
},
{
  "name":"com.sun.jna.NativeLong",
  "allDeclaredMethods":true,
  "allPublicMethods":true,
  "allDeclaredConstructors":true,
  "allPublicConstructors":true
},
{
  "name":"com.sun.jna.NativeMapped",
  "allDeclaredMethods":true,
  "allPublicMethods":true,
  "allDeclaredConstructors":true,
  "allPublicConstructors":true,
  "methods":[{"name":"toNative","parameterTypes":[] }]
},
{
  "name":"com.sun.jna.Pointer",
  "allDeclaredMethods":true,
  "allPublicMethods":true,
  "allDeclaredConstructors":true,
  "allPublicConstructors":true,
  "fields":[{"name":"peer", "allowWrite":true}],
  "methods":[{"name":"<init>","parameterTypes":["long"] }]
},
{
  "name":"com.sun.jna.PointerType",
  "allDeclaredMethods":true,
  "allPublicMethods":true,
  "allDeclaredConstructors":true,
  "allPublicConstructors":true,
  "fields":[{"name":"pointer", "allowWrite":true}]
},
{
  "name":"com.sun.jna.Structure",
  "allDeclaredMethods":true,
  "allPublicMethods":true,
  "allDeclaredConstructors":true,
  "allPublicConstructors":true,
  "fields":[{"name":"memory", "allowWrite":true}, {"name":"typeInfo", "allowWrite":true}],
  "methods":[{"name":"autoRead","parameterTypes":[] }, {"name":"autoWrite","parameterTypes":[] }, {"name":"getTypeInfo","parameterTypes":[] }, {"name":"getTypeInfo","parameterTypes":["java.lang.Object"] }, {"name":"newInstance","parameterTypes":["java.lang.Class"] }, {"name":"newInstance","parameterTypes":["java.lang.Class","long"] }, {"name":"newInstance","parameterTypes":["java.lang.Class","com.sun.jna.Pointer"] }]
},
{
  "name":"com.sun.jna.Structure$ByValue",
  "allDeclaredMethods":true,
  "allPublicMethods":true,
  "allDeclaredConstructors":true,
  "allPublicConstructors":true
},
{
  "name":"com.sun.jna.Structure$FFIType",
  "allDeclaredMethods":true,
  "allPublicMethods":true,
  "allDeclaredConstructors":true,
  "allPublicConstructors":true
},
{
  "name":"com.sun.jna.Structure$FFIType$FFITypes",
  "allDeclaredMethods":true,
  "allPublicMethods":true,
  "allDeclaredConstructors":true,
  "allPublicConstructors":true,
  "fields":[{"name":"ffi_type_double", "allowWrite":true}, {"name":"ffi_type_float", "allowWrite":true}, {"name":"ffi_type_longdouble", "allowWrite":true}, {"name":"ffi_type_pointer", "allowWrite":true}, {"name":"ffi_type_sint16", "allowWrite":true}, {"name":"ffi_type_sint32", "allowWrite":true}, {"name":"ffi_type_sint64", "allowWrite":true}, {"name":"ffi_type_sint8", "allowWrite":true}, {"name":"ffi_type_uint16", "allowWrite":true}, {"name":"ffi_type_uint32", "allowWrite":true}, {"name":"ffi_type_uint64", "allowWrite":true}, {"name":"ffi_type_uint8", "allowWrite":true}, {"name":"ffi_type_void", "allowWrite":true}]
},
{
  "name":"com.sun.jna.WString",
  "allDeclaredMethods":true,
  "allPublicMethods":true,
  "allDeclaredConstructors":true,
  "allPublicConstructors":true,
  "methods":[{"name":"<init>","parameterTypes":["java.lang.String"] }]
},
{
  "name":"com.sun.jna.ptr.PointerByReference",
  "allDeclaredMethods":true,
  "allPublicMethods":true,
  "allDeclaredConstructors":true,
  "allPublicConstructors":true
},
{
  "name":"com.sun.jna.platform.mac.CoreFoundation$CFStringRef",
  "allDeclaredMethods":true,
  "allPublicMethods":true,
  "allDeclaredConstructors":true,
  "allPublicConstructors":true
}
]
</pre>
</details>

### Specify proxy configurations

If you don't already have one, create a [`proxy-config.json`](https://www.graalvm.org/latest/reference-manual/native-image/metadata/#dynamic-proxy) file within your Java app, and configure Native Image to find it.

Then, add the configurations below to your `proxy-config.json`.

<details>
<summary>Click to show JSON configuration entries</summary>
<pre>
[
  {
    "interfaces":["com.sun.jna.Callback"]
  },
  {
    "interfaces":["com.sun.jna.Library"]
  },
  {
    "interfaces":["com.sun.jna.platform.unix.LibC"]
  },
  {
    "interfaces":["com.sun.jna.platform.linux.Udev"]
  },
  {
    "interfaces":["com.sun.jna.platform.linux.LibRT"]
  },
  {
    "interfaces":["com.sun.jna.platform.mac.SystemB"]
  },
  {
    "interfaces":["com.sun.jna.platform.mac.IOKit"]
  },
  {
    "interfaces":["com.sun.jna.platform.mac.CoreFoundation"]
  },
  {
    "interfaces":["com.sun.jna.win32.StdCallLibrary"]
  }
]
</pre>
</details>

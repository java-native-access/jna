plugins {
  java
  application
  alias(libs.plugins.graalvm)
}

application {
  mainClass = "com.example.JnaNative"
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(22)
        vendor = JvmVendorSpec.GRAAL_VM
    }
}

dependencies {
  implementation(libs.bundles.jna)
  implementation(libs.bundles.graalvm.api)
  nativeImageClasspath(libs.jna.graalvm)
}

graalvmNative {
  testSupport = true
  toolchainDetection = false

  binaries {
    named("main") {
      buildArgs.addAll(listOf(
        "-H:+UnlockExperimentalVMOptions",
        "-H:+ReportExceptionStackTraces",
        "-H:+JNIEnhancedErrorCodes",
      ))
    }
  }
}

// Allow the outer Ant build to override the version of JNA or GraalVM.
// These properties are used in JNA's CI and don't need to be in projects that use JNA.

val jnaVersion: String by properties
val graalvmVersion: String by properties
val overrides = jnaVersion.isNotBlank() || graalvmVersion.isNotBlank()

if (overrides) configurations.all {
  resolutionStrategy.eachDependency {
    if (requested.group == "net.java.dev.jna") {
      useVersion(jnaVersion)
      because("overridden by ant build")
    }
    if (requested.group == "org.graalvm") {
      useVersion(graalvmVersion)
      because("overridden by ant build")
    }
  }
}

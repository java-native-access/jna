/* Copyright (c) 2015 Adam Marcionek, All Rights Reserved
 *
 * The contents of this file is dual-licensed under 2
 * alternative Open Source/Free licenses: LGPL 2.1 or later and
 * Apache License 2.0. (starting with JNA version 4.0.0).
 *
 * You can freely decide which license you want to apply to
 * the project.
 *
 * You may obtain a copy of the LGPL License at:
 *
 * http://www.gnu.org/licenses/licenses.html
 *
 * A copy is also included in the downloadable source code package
 * containing JNA, in file "LGPL2.1".
 *
 * You may obtain a copy of the Apache License at:
 *
 * http://www.apache.org/licenses/
 *
 * A copy is also included in the downloadable source code package
 * containing JNA, in file "AL2.0".
 */
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

val nativeImageDebug: String by properties

graalvmNative {
  testSupport = true
  toolchainDetection = false

  binaries {
    named("main") {
      buildArgs.addAll(listOf(
        "-H:+UnlockExperimentalVMOptions",
        "-H:+ReportExceptionStackTraces",
        "--features=com.sun.jna.SubstrateStaticJNA",
      ).plus(if (nativeImageDebug != "true") emptyList() else listOf(
        "--verbose",
        "--debug-attach",
        "-H:+JNIEnhancedErrorCodes",
      )))
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

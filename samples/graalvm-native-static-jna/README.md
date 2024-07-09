# JNA Sample: GraalVM Native Image (Static)

This directory contains a sample Gradle project which uses JNA with [GraalVM](https://graalvm.org/). The project builds a
[native image](https://www.graalvm.org/latest/reference-manual/native-image/) which uses JNA features, powered by JNA's integration library for Substrate.

This sample leverages [Static JNI](https://www.blog.akhil.cc/static-jni) to build JNA and JNA-related user code
directly into the native image.

Using this technique can optimize startup time and other performance factors, because no dynamic library unpack-and-load
step is required to use JNA.

/* Copyright (c) 2017 Matthias Bläsing, All Rights Reserved
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
package com.sun.jna;

import java.io.*;
import org.junit.AfterClass;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;


public class ELFAnalyserTest {

    private static final File TEST_RESOURCES = new File("build/test-resources");
    private static final File WIN32_LIB = new File(TEST_RESOURCES, "win32-x86-64.dll");
    private static final File LINUX_ARMEL_LIB = new File(TEST_RESOURCES, "linux-armel.so");
    private static final File LINUX_ARMEL_NOFLAG_LIG = new File(TEST_RESOURCES, "linux-armel-noflag.so");
    private static final File LINUX_ARMHF_NOFLAG_LIG = new File(TEST_RESOURCES, "linux-armhf-noflag.so");
    private static final File LINUX_ARMHF_LIB = new File(TEST_RESOURCES, "linux-armhf.so");
    private static final File LINUX_AMD64_LIB = new File(TEST_RESOURCES, "linux-amd64.so");

    @BeforeClass
    public static void initClass() throws IOException {
        TEST_RESOURCES.mkdirs();

        extractTestFile(WIN32_LIB);
        extractTestFile(LINUX_ARMEL_LIB);
        extractTestFile(LINUX_ARMHF_LIB);
        extractTestFile(LINUX_AMD64_LIB);
        makeLinuxArmNoflagLib(LINUX_ARMEL_LIB, LINUX_ARMEL_NOFLAG_LIG);
        makeLinuxArmNoflagLib(LINUX_ARMHF_LIB, LINUX_ARMHF_NOFLAG_LIG);
    }

    @Test
    public void testNonELF() throws IOException {
        ELFAnalyser ahfd = ELFAnalyser.analyse(WIN32_LIB.getAbsolutePath());
        assertFalse(ahfd.isELF());
    }

    @Test
    public void testNonArm() throws IOException {
        ELFAnalyser ahfd = ELFAnalyser.analyse(LINUX_AMD64_LIB.getAbsolutePath());
        assertTrue(ahfd.isELF());
        assertFalse(ahfd.isArm());
        assertTrue(ahfd.is64Bit());
    }

    @Test
    public void testArmhf() throws IOException {
        ELFAnalyser ahfd = ELFAnalyser.analyse(LINUX_ARMHF_LIB.getAbsolutePath());
        assertTrue(ahfd.isELF());
        assertTrue(ahfd.isArm());
        assertFalse(ahfd.is64Bit());
        assertFalse(ahfd.isArmSoftFloatFlag());
        assertTrue(ahfd.isArmHardFloatFlag());
        assertTrue(ahfd.isArmEabiAapcsVfp());
        assertTrue(ahfd.isArmHardFloat());
    }

    @Test
    public void testArmel() throws IOException {
        ELFAnalyser ahfd = ELFAnalyser.analyse(LINUX_ARMEL_LIB.getAbsolutePath());
        assertTrue(ahfd.isELF());
        assertTrue(ahfd.isArm());
        assertFalse(ahfd.is64Bit());
        assertTrue(ahfd.isArmSoftFloatFlag());
        assertFalse(ahfd.isArmHardFloatFlag());
        assertFalse(ahfd.isArmEabiAapcsVfp());
        assertFalse(ahfd.isArmHardFloat());
    }

    @Test
    public void testArmelNoflag() throws IOException {
        ELFAnalyser ahfd = ELFAnalyser.analyse(LINUX_ARMEL_NOFLAG_LIG.getAbsolutePath());
        assertTrue(ahfd.isELF());
        assertTrue(ahfd.isArm());
        assertFalse(ahfd.is64Bit());
        assertFalse(ahfd.isArmSoftFloatFlag());
        assertFalse(ahfd.isArmHardFloatFlag());
        assertFalse(ahfd.isArmEabiAapcsVfp());
        assertFalse(ahfd.isArmHardFloat());
    }

    @Test
    public void testArmhfNoflag() throws IOException {
        ELFAnalyser ahfd = ELFAnalyser.analyse(LINUX_ARMHF_NOFLAG_LIG.getAbsolutePath());
        assertTrue(ahfd.isELF());
        assertTrue(ahfd.isArm());
        assertFalse(ahfd.is64Bit());
        assertFalse(ahfd.isArmSoftFloatFlag());
        assertFalse(ahfd.isArmHardFloatFlag());
        assertTrue(ahfd.isArmEabiAapcsVfp());
        assertTrue(ahfd.isArmHardFloat());
    }

    @AfterClass
    public static void afterClass() throws IOException {
        LINUX_AMD64_LIB.delete();
        LINUX_ARMHF_LIB.delete();
        LINUX_ARMEL_LIB.delete();
        WIN32_LIB.delete();
        LINUX_ARMEL_NOFLAG_LIG.delete();
        LINUX_ARMHF_NOFLAG_LIG.delete();
        TEST_RESOURCES.delete();
    }

    private static void extractTestFile(File outputFile) throws IOException {
        String inputPath = "/com/sun/jna/data/" + outputFile.getName();
        InputStream is = ELFAnalyserTest.class.getResourceAsStream(inputPath);
        try {
            OutputStream os = new FileOutputStream(outputFile);
            try {
                copyStream(is, os);
            } finally {
                os.close();
            }
        } finally {
            is.close();
        }
    }

    // The e_flags for elf arm binaries begin at an offset of 0x24 bytes.
    // The procedure call standard is coded on the second byte.
    private static void makeLinuxArmNoflagLib(File sourceFile, File outputFile) throws IOException {
        final int POS_ABI_FLOAT_BIT = (byte) 0x25;
        copyFile(sourceFile, outputFile);

        RandomAccessFile out = new RandomAccessFile(outputFile, "rw");

        out.seek(POS_ABI_FLOAT_BIT);
        out.write(0);

        out.close();
    }

    private static void copyFile(File sourceFile, File outputFile) throws IOException {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = new FileInputStream(sourceFile);
            outputStream = new FileOutputStream(outputFile);
            copyStream(inputStream, outputStream);
        } finally {
            closeSilently(inputStream);
            closeSilently(outputStream);
        }
    }

    private static void copyStream(InputStream is, OutputStream os) throws IOException {
        int read;
        byte[] buffer = new byte[1024 * 1024];
        while ((read = is.read(buffer)) > 0) {
            os.write(buffer, 0, read);
        }
    }

    private static void closeSilently(Closeable closeable) {
        if(closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (IOException ex) {}
    }
}


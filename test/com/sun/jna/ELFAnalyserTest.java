
package com.sun.jna;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
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
    private static final File LINUX_ARMHF_LIB = new File(TEST_RESOURCES, "linux-armhf.so");
    private static final File LINUX_AMD64_LIB = new File(TEST_RESOURCES, "linux-amd64.so");
    
    @BeforeClass
    public static void initClass() throws IOException {
        File win32Zip = new File("dist/win32-x86-64.jar");
        File linuxArmelZip = new File("dist/linux-armel.jar");
        File linuxArmhfZip = new File("dist/linux-arm.jar");
        File linuxAmd64Zip = new File("dist/linux-x86-64.jar");
        
        TEST_RESOURCES.mkdirs();
        
        extractFileFromZip(win32Zip, "jnidispatch.dll", WIN32_LIB);
        extractFileFromZip(linuxArmelZip, "libjnidispatch.so", LINUX_ARMEL_LIB);
        extractFileFromZip(linuxArmhfZip, "libjnidispatch.so", LINUX_ARMHF_LIB);
        extractFileFromZip(linuxAmd64Zip, "libjnidispatch.so", LINUX_AMD64_LIB);
        makeLinuxArmelNoflagLib(LINUX_ARMEL_LIB, LINUX_ARMEL_NOFLAG_LIG);
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
        assertFalse(ahfd.isArmSoftFloat());
        assertTrue(ahfd.isArmHardFloat());
    }
    
    @Test
    public void testArmel() throws IOException {
        ELFAnalyser ahfd = ELFAnalyser.analyse(LINUX_ARMEL_LIB.getAbsolutePath());
        assertTrue(ahfd.isELF());
        assertTrue(ahfd.isArm());
        assertFalse(ahfd.is64Bit());
        assertTrue(ahfd.isArmSoftFloat());
        assertFalse(ahfd.isArmHardFloat());
    }

    @Test
    public void testArmelNoflag() throws IOException {
        ELFAnalyser ahfd = ELFAnalyser.analyse(LINUX_ARMEL_NOFLAG_LIG.getAbsolutePath());
        assertTrue(ahfd.isELF());
        assertTrue(ahfd.isArm());
        assertFalse(ahfd.is64Bit());
        assertTrue(ahfd.isArmSoftFloat());
        assertFalse(ahfd.isArmHardFloat());
    }
    
    @AfterClass
    public static void afterClass() throws IOException {
        LINUX_AMD64_LIB.delete();
        LINUX_ARMHF_LIB.delete();
        LINUX_ARMEL_LIB.delete();
        WIN32_LIB.delete();
        LINUX_ARMEL_NOFLAG_LIG.delete();
        TEST_RESOURCES.delete();
    }
    
    private static void extractFileFromZip(File zipTarget, String zipEntryName, File outputFile) throws IOException {
        ZipFile zip = new ZipFile(zipTarget);
        try {
            ZipEntry entry = zip.getEntry(zipEntryName);
            if(entry == null)  {
                throw new IOException("ZipEntry for name " + zipEntryName + " not found in " + zipTarget.getAbsolutePath());
            }
            InputStream is = zip.getInputStream(entry); // Implicitly closed by closing ZipFile
            OutputStream os = new FileOutputStream(outputFile);
            try {
                copyStream(is, os);
            } finally {
                os.close();
            }
        } finally {
            zip.close();
        }
    }

    // The e_flags for elf arm binaries begin at an offset of 0x24 bytes.
    // The procedure call standard is coded on the second byte.
    private static void makeLinuxArmelNoflagLib(File sourceFile, File outputFile) throws IOException {
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


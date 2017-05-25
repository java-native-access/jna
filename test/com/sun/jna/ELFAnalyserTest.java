
package com.sun.jna;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.junit.AfterClass;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;


public class ELFAnalyserTest {

    
    private static File testResources = new File("build/test-resources");
    private static File win32Lib = new File(testResources, "win32-x86-64.dll");
    private static File linuxArmelLib = new File(testResources, "linux-armel.so");
    private static File linuxArmhfLib = new File(testResources, "linux-armhf.so");
    private static File linuxAmd64Lib = new File(testResources, "linux-amd64.so");
    
    @BeforeClass
    public static void initClass() throws IOException {
        File win32Zip = new File("dist/win32-x86-64.jar");
        File linuxArmelZip = new File("dist/linux-armel.jar");
        File linuxArmhfZip = new File("dist/linux-arm.jar");
        File linuxAmd64Zip = new File("dist/linux-x86-64.jar");
        
        testResources.mkdirs();
        
        extractFileFromZip(win32Zip, "jnidispatch.dll", win32Lib);
        extractFileFromZip(linuxArmelZip, "libjnidispatch.so", linuxArmelLib);
        extractFileFromZip(linuxArmhfZip, "libjnidispatch.so", linuxArmhfLib);
        extractFileFromZip(linuxAmd64Zip, "libjnidispatch.so", linuxAmd64Lib);
    }
    
    @Test
    public void testNonELF() throws IOException {
        ELFAnalyser ahfd = ELFAnalyser.analyse(win32Lib.getAbsolutePath());
        assertFalse(ahfd.isELF());
    }
    
    @Test
    public void testNonArm() throws IOException {
        ELFAnalyser ahfd = ELFAnalyser.analyse(linuxAmd64Lib.getAbsolutePath());
        assertTrue(ahfd.isELF());
        assertFalse(ahfd.isArm());
        assertTrue(ahfd.is64Bit());
    }
    
    @Test
    public void testArmhf() throws IOException {
        ELFAnalyser ahfd = ELFAnalyser.analyse(linuxArmhfLib.getAbsolutePath());
        assertTrue(ahfd.isELF());
        assertTrue(ahfd.isArm());
        assertFalse(ahfd.is64Bit());
        assertFalse(ahfd.isArmSoftFloat());
        assertTrue(ahfd.isArmHardFloat());
    }
    
    @Test
    public void testArmel() throws IOException {
        ELFAnalyser ahfd = ELFAnalyser.analyse(linuxArmelLib.getAbsolutePath());
        assertTrue(ahfd.isELF());
        assertTrue(ahfd.isArm());
        assertFalse(ahfd.is64Bit());
        assertTrue(ahfd.isArmSoftFloat());
        assertFalse(ahfd.isArmHardFloat());
    }
    
    @AfterClass
    public static void afterClass() throws IOException {
        linuxAmd64Lib.delete();
        linuxArmhfLib.delete();
        linuxArmelLib.delete();
        win32Lib.delete();
        testResources.delete();
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
                int read;
                byte[] buffer = new byte[1024 * 1024];
                while((read = is.read(buffer)) > 0) {
                    os.write(buffer, 0, read);
                }
            } finally {
                os.close();
            }
        } finally {
            zip.close();
        }
    }
}


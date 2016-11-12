/*
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

package com.sun.jna.platform.win32.COM;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

public class Helper {
    public static final File tempDir = new File(System.getProperty("java.io.tmpdir"));
    
    /**
     * Sleep for specified seconds.
     * 
     * @param seconds 
     */
    public static void sleep(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException ex) {
            // Ignore
        }
    }
    
    /**
     * Extract data contained in classpath into a system accessible target file.
     * 
     * @param localPath
     * @param target
     * @throws IOException 
     */
    public static void extractClasspathFileToReal(String localPath, File target) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = Helper.class.getResourceAsStream(localPath);
            os =  new FileOutputStream(target);
            
            int read;
            byte[] buffer = new byte[20480];
            
            while((read = is.read(buffer)) > 0) {
                os.write(buffer, 0, read);
            }
            
        } finally {
            if(is != null) {
                try {
                    is.close();
                } catch(Exception ex) {}
            }
            if(os != null) {
                try {
                    os.close();
                } catch(Exception ex) {}
            }
        }
    }
    
    /**
     * Create a temporary file, that does not exist.
     * 
     * @param prefix
     * @param suffix
     * @return
     * @throws IOException 
     */
    public static File createNotExistingFile(String prefix, String suffix) throws IOException {
        File tempFile = Files.createTempFile(prefix, suffix).toFile();
        tempFile.delete();
        return tempFile;
    }
}

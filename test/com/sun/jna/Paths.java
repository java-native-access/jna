/* Copyright (c) 2013 Timothy Wall, All Rights Reserved
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package com.sun.jna;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public interface Paths {
    boolean USING_CLOVER = System.getProperty("java.class.path").indexOf("clover") != -1;
    /** Use this as a parent class loader to ensure clover can be loaded. */
    class CloverLoader extends URLClassLoader {
        public CloverLoader() throws MalformedURLException{
            this(null);
        }
        public CloverLoader(ClassLoader parent) throws MalformedURLException{
            super(new URL[] {
                    new File(USING_CLOVER ? "lib/clover.jar" : "/dev/null").toURI().toURL()
                  }, parent);
        }
    }
    String BUILDDIR = Platform.isWindowsCE() 
        ? "/Storage Card"
        : System.getProperty("jna.builddir",
                             USING_CLOVER
                             ? "build.clover" : "build");
    String CLASSES = BUILDDIR + (Platform.isWindowsCE() ? "" : "/classes");
    String JNAJAR = BUILDDIR + "/jna.jar";
    
    String TESTPATH = Platform.isWindowsCE()
        ? "/Storage Card/"
        : System.getProperty("jna.nativedir",
                             BUILDDIR + "/native-" + Platform.RESOURCE_PREFIX + "/");
    String TESTJAR = BUILDDIR + "/jna-test.jar";
}

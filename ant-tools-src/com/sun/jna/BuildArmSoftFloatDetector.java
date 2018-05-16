/* Copyright (c) 2016 Matthias Bläsing, All Rights Reserved
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

import java.io.File;
import java.io.IOException;
import org.apache.tools.ant.Project;

/**
 * Ant task to expose the arm soft-/hardfloat detection routines of the JNA core
 * for the build script.
 */
public class BuildArmSoftFloatDetector {

    private String targetProperty;
    private Project project;

    public void setProject(Project proj) {
        project = proj;
    }

    /**
     * targetProperty receives the name of the property, that should take the
     * new property
     * 
     * @param targetProperty 
     */
    public void setTargetProperty(String targetProperty) {
        this.targetProperty = targetProperty;
    }
    
    public void execute() throws IOException {
        boolean result = false;
        // On linux /proc/self/exe is a symblink to the currently executing
        // binary (the JVM)
        File self = new File("/proc/self/exe");
        try {
            // The self.getCanonicalPath() resolves the symblink to the backing
            // realfile and passes that to the detection routines
            ELFAnalyser ahfd = ELFAnalyser.analyse(self.getCanonicalPath());
            result = ! ahfd.isArmHardFloat();
        } catch (IOException ex) {
            result = false;
        }
        project.setNewProperty(targetProperty, Boolean.toString(result));
    }
}

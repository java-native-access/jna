
package com.sun.jna;

import java.io.File;
import java.io.IOException;
import org.apache.tools.ant.Project;

/**
 * Ant task to expose the arm soft-/hardfloat detection routines of the JNA core
 * for the build script.
 * 
 * <p>The build script is expected to build a minimal set of classes that are 
 * required to execute this. At the time of writing these are:</p>
 * 
 * <ul>
 * <li>com.sun.jna.ELFAnalyser</li>
 * </ul>
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
            result = ahfd.isArmSoftFloat();
        } catch (IOException ex) {
            result = false;
        }
        project.setNewProperty(targetProperty, Boolean.toString(result));
    }
}

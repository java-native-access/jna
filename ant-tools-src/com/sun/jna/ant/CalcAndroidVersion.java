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

package com.sun.jna.ant;

import java.io.IOException;
import org.apache.tools.ant.Project;

/**
 * Calculate the androidVersion code based on the major, minor, patchlevel.
 *
 * <p>This is inspired by the AndroidGradleExample implemented here:
 * <a href="https://github.com/jayway/AndroidGradleExample/blob/master/versioning.gradle">
 * https://github.com/jayway/AndroidGradleExample/blob/master/versioning.gradle
 * </a>
 * </p>
 *
 * <p>The androidVersion is calculated from the major/minor/revision/buildNumber
 * quarted as:</p>
 *
 * <pre>
 * androidVersion = 1000000 * major
 *                +   10000 * minor
 *                +     100 * revision
 *                + (isRelease) ? 99 : 0
 * </pre>
 */
public class CalcAndroidVersion {

    private String targetProperty;
    private Integer major;
    private Integer minor;
    private Integer revision;
    private String releaseProperty;
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

    public void setMajor(Integer major) {
        this.major = major;
    }

    public void setMinor(Integer minor) {
        this.minor = minor;
    }

    public void setRevision(Integer revision) {
        this.revision = revision;
    }

    /**
     * Name of property, that is set, if a release is done
     *
     * @param releaseProperty
     */
    public void setReleaseProperty(String releaseProperty) {
        this.releaseProperty = releaseProperty;
    }

    public void execute() throws IOException {
        int androidVersion = 1000000 * major
                           +   10000 * minor
                           +     100 * revision;
        if(project.getProperties().containsKey(releaseProperty)) {
            androidVersion += 99;
        }
        project.setNewProperty(targetProperty, Integer.toString(androidVersion));
    }
}

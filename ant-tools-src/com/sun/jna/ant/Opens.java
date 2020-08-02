/* Copyright (c) 2020 Matthias Bläsing, All Rights Reserved
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

import java.util.HashSet;
import java.util.Set;

public class Opens {
    private String packageName;
    private String to;

    public String getPackage() {
        return packageName;
    }

    public void setPackage(String packageName) {
        this.packageName = packageName;
    }

    public String getBinaryPackageName() {
        return packageName.replace(".", "/");
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String[] getToList() {
        if(to == null) {
            return null;
        } else {
            Set<String> toList = new HashSet<String>();
            for(String s: to.split(",")) {
                String entry = s.trim();
                if(! entry.isEmpty()) {
                    toList.add(entry);
                }
            }
            if(toList.isEmpty()) {
                return null;
            } else {
                return toList.toArray(new String[0]);
            }
        }
    }

    @Override
    public String toString() {
        return "Opens{" + "packageName=" + packageName + ", to=" + to + '}';
    }
}

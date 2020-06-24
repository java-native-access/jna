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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.ModuleVisitor;
import static org.objectweb.asm.Opcodes.*;

public class ModuleGenerator {
    private String name;
    private String version;
    private boolean open;
    private String mainClass;
    private File targetFile;
    private List<Exports> exports = new ArrayList<Exports>();
    private List<Opens> opens = new ArrayList<Opens>();
    private List<Requires> requires = new ArrayList<Requires>();
    private List<Package> packages = new ArrayList<Package>();

    public File getTargetFile() {
        return targetFile;
    }

    public void setTargetFile(File targetFile) {
        this.targetFile = targetFile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public String getMainClass() {
        return mainClass;
    }

    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }

    public String getMainClassBinary() {
        if(mainClass != null) {
            return mainClass.replace(".", "/");
        } else {
            return mainClass;
        }
    }

    public Exports createExports() {
        Exports export = new Exports();
        this.exports.add(export);
        return export;
    }

    public List<Exports> getExports() {
        return exports;
    }

    public void setExports(List<Exports> exports) {
        this.exports = exports;
    }

    public Requires createRequires() {
        Requires require = new Requires();
        this.requires.add(require);
        return require;
    }

    public List<Requires> getRequires() {
        return requires;
    }

    public void setRequires(List<Requires> requires) {
        this.requires = requires;
    }

    public Opens createOpens() {
        Opens openEntry = new Opens();
        this.opens.add(openEntry);
        return openEntry;
    }

    public List<Opens> getOpens() {
        return opens;
    }

    public void setOpens(List<Opens> opens) {
        this.opens = opens;
    }

    public Package createPackage() {
        Package packageEntry = new Package();
        this.packages.add(packageEntry);
        return packageEntry;
    }

    public List<Package> getPackages() {
        return packages;
    }

    public void setPackages(List<Package> packageEntry) {
        this.packages = packageEntry;
    }

    public void execute() throws IOException {
        System.out.println(this);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        cw.visit(V9, ACC_MODULE, "module-info", null, null, null);
        ModuleVisitor mv = cw.visitModule(name, (open ? ACC_OPEN : 0), version);
        mv.visitRequire("java.base", ACC_MANDATED, null);
        for(Requires require: requires) {
            mv.visitRequire(
                require.getModule(),
                (require.isStatic() ? ACC_STATIC : 0)|( require.isTransitive() ? ACC_TRANSITIVE : 0),
                null);
        }
        for(Exports export: exports) {
            mv.visitExport(
                export.getBinaryPackageName(),
                0,
                export.getToList()
            );
        }
        for(Opens openEntry: opens) {
            mv.visitOpen(
                openEntry.getBinaryPackageName(),
                0,
                openEntry.getToList());
        }
        for(Package packageEntry: packages) {
            mv.visitPackage(packageEntry.getName());
        }
        if(getMainClassBinary() != null) {
            mv.visitMainClass(getMainClassBinary());
        }
        mv.visitEnd();
        cw.visitEnd();
        try (FileOutputStream fos = new FileOutputStream(targetFile)) {
            fos.write(cw.toByteArray());
        }
    }

    @Override
    public String toString() {
        return "ModuleGenerator{" + "name=" + name + ", version=" + version + ", open=" + open + ", mainClass=" + mainClass + ", targetFile=" + targetFile + ", exports=" + exports + ", opens=" + opens + ", requires=" + requires + ", packages=" + packages + '}';
    }
}

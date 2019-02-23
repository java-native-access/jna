/* Copyright (c) 2007 Timothy Wall, All Rights Reserved
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

import java.lang.reflect.Field;

/**
 * Provide native to Java type conversion context for a {@link Structure} field
 * read.
 */
public class StructureReadContext extends FromNativeContext {

    private Structure structure;
    private Field field;
    StructureReadContext(Structure struct, Field field) {
        super(field.getType());
        this.structure = struct;
        this.field = field;
    }
    /** Get the {@link Structure} the field is a member of. */
    public Structure getStructure() { return structure; }
    /** Get the {@link Field} being read from native memory. */
    public Field getField() { return field; }
}

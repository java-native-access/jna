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
package com.sun.jna;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * Test utility class for inspecting {@link com.sun.jna.Structure#getFieldOrder()} methods.
 *
 * @author Dan Rollo
 * Date: 1/17/13
 * Time: 4:29 PM
 */
public class StructureFieldOrderInspectorTest extends TestCase {

    private String origPropJNANoSys;

    @Override
    protected void setUp() {
        origPropJNANoSys = System.getProperty("jna.nosys");
        System.setProperty("jna.nosys", "true"); // would be set by ant script, set here for IDE usage
    }

    @Override
    protected void tearDown() {
        if (origPropJNANoSys == null) {
            Properties props = (Properties)System.getProperties().clone();
            props.remove("jna.nosys");
            System.setProperties(props);
        } else {
            System.setProperty("jna.nosys", origPropJNANoSys);
        }
    }


    public void testFindStructureSubClasses() {

        final Set<Class<? extends Structure>> classes = StructureFieldOrderInspector.findSubTypesOfStructure(Platform.class);

        assertTrue("Found no Structure sub types.", classes.size() > 0);

        for (final Class<? extends Structure> structureSubType : classes) {
            assertTrue(structureSubType.getName(), Structure.class.isAssignableFrom(structureSubType));
        }
    }

    public void testCheckMethodGetFieldOrderExisting() {
        StructureFieldOrderInspector.checkMethodGetFieldOrder(StructureByValueTest.TestNativeMappedInStructure.class, null);
    }

    public void testCheckMethodGetFieldOrderTagInterface() {
        StructureFieldOrderInspector.checkMethodGetFieldOrder(StructureByValueTest.TestNativeMappedInStructure.ByValue.class, null);
    }


    private static final class MyStructMissingField extends Structure {
        public String missingDeclaredField;

        @Override
        protected List<String> getFieldOrder() {
            //noinspection unchecked
            return Collections.<String>emptyList();
        }
    }
    public void testCheckMethodGetFieldOrderMissingField() throws Exception {
        try {
            StructureFieldOrderInspector.checkMethodGetFieldOrder(MyStructMissingField.class, null);
            fail("Expected Error: Structure.getFieldOrder()...");
        } catch (RuntimeException e) {
            assertTrue(e.getCause().getCause().getMessage().contains("not match declared field names"));
        }
    }

    private static final class MyStructExtraField extends Structure {
        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("extraField");
        }
    }
    public void testCheckMethodGetFieldOrderExtraField() throws Exception {
        try {
            StructureFieldOrderInspector.checkMethodGetFieldOrder(MyStructExtraField.class, null);
            fail("Expected Error: Structure.getFieldOrder()...");
        } catch (RuntimeException e) {
            assertTrue(e.getCause().getCause().getMessage().contains("not match declared field names"));
        }
    }


    private static final class MyStructStaticField extends Structure {
        public long instanceField;
        public static long myStaticField = -1;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("instanceField");
        }
    }
    public void testCheckMethodGetFieldOrderStaticField() throws Exception {
        StructureFieldOrderInspector.checkMethodGetFieldOrder(MyStructStaticField.class, null);
    }


    private static class MyStructSuper extends Structure {
        public long instanceField;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("instanceField");
        }
    }
    private static final class MyStructChildEmpty extends MyStructSuper {
    }
    public void testCheckMethodGetFieldOrderSuperImplOnly() throws Exception {
        StructureFieldOrderInspector.checkMethodGetFieldOrder(MyStructChildEmpty.class, null);
    }


    public void testCheckMethodGetFieldOrderWithAbstractSubtype() throws Exception {
        StructureFieldOrderInspector.checkMethodGetFieldOrder(Union.class, null);
    }

    public void testCheckMethodGetFieldOrderWithIgnoreCtorError() throws Exception {
        final List<String> ignoreConstructorError = new ArrayList<String>();
        ignoreConstructorError.add(StructureFieldOrderInspectorTest.class.getName());
        StructureFieldOrderInspector.checkMethodGetFieldOrder(MyStructExtraField.class, ignoreConstructorError);
    }

    public void testCheckStructureGetFieldOrder() throws Exception {
        StructureFieldOrderInspector.checkStructureGetFieldOrder(Platform.class, null);
    }

    public void testBatchCheckStructureGetFieldOrder() throws Exception {
        try {
            StructureFieldOrderInspector.batchCheckStructureGetFieldOrder(StructureTest.class, null);
            fail("Expected structure failures");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().startsWith("Some Structure sub types"));
        }
    }
}

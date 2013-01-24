package com.sun.jna;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    protected void setUp() {
        origPropJNANoSys = System.getProperty("jna.nosys");
        System.setProperty("jna.nosys", "true"); // would be set by ant script, set here for IDE usage
    }

    protected void tearDown() {
        if (origPropJNANoSys == null) {
            System.getProperties().remove("jna.nosys");
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
        @SuppressWarnings("UnusedDeclaration")
        public String missingDeclaredField;

        @Override
        protected List getFieldOrder() {
            //noinspection unchecked
            return Arrays.asList();
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
        protected List getFieldOrder() {
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

    private static final class MyStructStaticFieldOnlyStatic extends Structure {
        public static long myStaticField = -1;

        @Override
        protected List getFieldOrder() {
            return Arrays.asList();
        }
    }
    /**
     * //@todo Seems this may be a bug. Error below occurs if Structure has no instance field (and only a static field), like: MyStructStaticFieldOnlyStatic.
    java.lang.RuntimeException: Could not instantiate Structure sub type: com.sun.jna.StructureFieldOrderInspectorTest$MyStructStaticFieldOnlyStatic
        at com.sun.jna.StructureFieldOrderInspector.checkMethodGetFieldOrder(StructureFieldOrderInspector.java:146)
        at com.sun.jna.StructureFieldOrderInspectorTest.testCheckMethodGetFieldOrderStaticFieldOnlyStatic(StructureFieldOrderInspectorTest.java:111)
        at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
        at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:57)
        at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
        at com.intellij.junit3.JUnit3IdeaTestRunner.doRun(JUnit3IdeaTestRunner.java:139)
        at com.intellij.junit3.JUnit3IdeaTestRunner.startRunnerWithArgs(JUnit3IdeaTestRunner.java:52)
        at com.intellij.rt.execution.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:195)
        at com.intellij.rt.execution.junit.JUnitStarter.main(JUnitStarter.java:63)
        at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
        at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:57)
        at com.intellij.rt.execution.application.AppMain.main(AppMain.java:120)
    Caused by: java.lang.reflect.InvocationTargetException
        at sun.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method)
        at sun.reflect.NativeConstructorAccessorImpl.newInstance(NativeConstructorAccessorImpl.java:57)
        at sun.reflect.DelegatingConstructorAccessorImpl.newInstance(DelegatingConstructorAccessorImpl.java:45)
        at java.lang.reflect.Constructor.newInstance(Constructor.java:532)
        at com.sun.jna.StructureFieldOrderInspector.checkMethodGetFieldOrder(StructureFieldOrderInspector.java:131)
        ... 22 more
    Caused by: java.lang.IllegalArgumentException: Structure class com.sun.jna.StructureFieldOrderInspectorTest$MyStructStaticFieldOnlyStatic has unknown size (ensure all fields are public)
        at com.sun.jna.Structure.deriveLayout(Structure.java:1108)
        at com.sun.jna.Structure.calculateSize(Structure.java:908)
        at com.sun.jna.Structure.calculateSize(Structure.java:896)
        at com.sun.jna.Structure.allocateMemory(Structure.java:357)
        at com.sun.jna.Structure.<init>(Structure.java:191)
        at com.sun.jna.Structure.<init>(Structure.java:180)
        at com.sun.jna.Structure.<init>(Structure.java:167)
        at com.sun.jna.Structure.<init>(Structure.java:159)
        at com.sun.jna.StructureFieldOrderInspectorTest$MyStructStaticFieldOnlyStatic.<init>(StructureFieldOrderInspectorTest.java:89)
        ... 27 more
     */
/*
    public void testCheckMethodGetFieldOrderStaticFieldOnlyStatic() throws Exception {
        StructureFieldOrderInspector.checkMethodGetFieldOrder(MyStructStaticFieldOnlyStatic.class, null);
    }
//*/

    private static final class MyStructStaticField extends Structure {
        public long instanceField;  // @todo Why error if at least one instance field does not exist? see above:MyStructStaticFieldOnlyStatic
        public static long myStaticField = -1;

        @Override
        protected List getFieldOrder() {
            //return Arrays.asList();
            return Arrays.asList("instanceField");
        }
    }
    public void testCheckMethodGetFieldOrderStaticField() throws Exception {
        StructureFieldOrderInspector.checkMethodGetFieldOrder(MyStructStaticField.class, null);
    }


    private static class MyStructSuper extends Structure {
        public long instanceField;

        @Override
        protected List getFieldOrder() {
            //return Arrays.asList();
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

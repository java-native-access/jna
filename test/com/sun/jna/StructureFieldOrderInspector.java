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

import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Utility class for detecting missing {@link com.sun.jna.Structure#getFieldOrder()} methods.
 *
 * This class could be moved to the unit test tree, but then reusing it in the 'platform' project would require
 * publishing this test tree.
 *
 * @author Dan Rollo
 * Date: 1/17/13
 * Time: 4:08 PM
 */
public final class StructureFieldOrderInspector {

    private StructureFieldOrderInspector(){}

    /**
     * Search for Structure sub types in the source tree of the given class, and validate the getFieldOrder() method,
     * and collects all errors into one exception.
     *
     * @param classDeclaredInSourceTreeToSearch a class who's source tree will be searched for Structure sub types.
     * @param ignoreConstructorError list of classname prefixes for which to ignore construction errors.
     */
    public static void batchCheckStructureGetFieldOrder(final Class<?> classDeclaredInSourceTreeToSearch,
                                                   final List<String> ignoreConstructorError) {
        batchCheckStructureGetFieldOrder(classDeclaredInSourceTreeToSearch, ignoreConstructorError, false);
    }

    /**
     * Search for Structure sub types in the source tree of the given class, and validate the getFieldOrder() method,
     * and collects all errors into one exception.
     *
     * @param classDeclaredInSourceTreeToSearch a class who's source tree will be searched for Structure sub types.
     * @param ignoreConstructorError list of classname prefixes for which to ignore construction errors.
     * @param onlyInnerClasses limit scan to inner classes of the supplied class
     */
    public static void batchCheckStructureGetFieldOrder(final Class<?> classDeclaredInSourceTreeToSearch,
                                                   final List<String> ignoreConstructorError,
                                                   final boolean onlyInnerClasses) {
        final Set<Class<? extends Structure>> classes = StructureFieldOrderInspector.findSubTypesOfStructure(classDeclaredInSourceTreeToSearch, onlyInnerClasses);

        final List<Throwable> problems = new ArrayList<Throwable>();

        for (final Class<? extends Structure> structureSubType : classes) {
            try {
                StructureFieldOrderInspector.checkMethodGetFieldOrder(structureSubType, ignoreConstructorError);
            } catch (Throwable t) {
                problems.add(t);
            }
        }

        if (problems.size() > 0) {
            String msg = "";
            for (final Throwable t : problems) {
                msg += t.getMessage() + "; \n";
            }

            throw new RuntimeException("Some Structure sub types (" + problems.size() + ") have problems with getFieldOrder(): \n" + msg);
        }
    }

    /**
     * Search for Structure sub types in the source tree of the given class, and validate the getFieldOrder() method.
     *
     * @param classDeclaredInSourceTreeToSearch a class who's source tree will be searched for Structure sub types.
     * @param ignoreConstructorError list of classname prefixes for which to ignore construction errors.
     */
    public static void checkStructureGetFieldOrder(final Class<?> classDeclaredInSourceTreeToSearch,
                                                   final List<String> ignoreConstructorError) {
        final Set<Class<? extends Structure>> classes = StructureFieldOrderInspector.findSubTypesOfStructure(classDeclaredInSourceTreeToSearch);

        for (final Class<? extends Structure> structureSubType : classes) {
            StructureFieldOrderInspector.checkMethodGetFieldOrder(structureSubType, ignoreConstructorError);
        }
    }

    /**
     * Find all classes that extend {@link Structure}.
     */
    public static Set<Class<? extends Structure>> findSubTypesOfStructure(final Class<?> classDeclaredInSourceTreeToSearch, boolean onlyInnerClasses) {

        // use: http://code.google.com/p/reflections/

        final Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setScanners(new SubTypesScanner(false /* don't exclude Object.class */), new ResourcesScanner())
                .setUrls(ClasspathHelper.forClass(classDeclaredInSourceTreeToSearch))
        );

        Set<Class<? extends Structure>> types = new HashSet<Class<? extends Structure>>(reflections.getSubTypesOf(Structure.class));
        if(onlyInnerClasses) {
            Iterator<Class<? extends Structure>> it = types.iterator();
            while(it.hasNext()) {
                if(! (it.next().getEnclosingClass() == classDeclaredInSourceTreeToSearch)) {
                    it.remove();
                }
            }
        }
        return types;
    }

    /**
     * Find all classes that extend {@link Structure}.
     */
    public static Set<Class<? extends Structure>> findSubTypesOfStructure(final Class<?> classDeclaredInSourceTreeToSearch) {
        return findSubTypesOfStructure(classDeclaredInSourceTreeToSearch, false);
    }

    public static void checkMethodGetFieldOrder(final Class<? extends Structure> structureSubType,
                                                final List<String> ignoreConstructorError) {

        if (Structure.ByValue.class.isAssignableFrom(structureSubType)
                || Structure.ByReference.class.isAssignableFrom(structureSubType)) {

            // ignore tagging interfaces
            return;
        }

        if (Modifier.isAbstract(structureSubType.getModifiers())) {
            // do not try to construct abstract Structure sub types
            return;
        }
        final Constructor<? extends Structure> structConstructor;
        try {
            structConstructor = structureSubType.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            if (structureSubType == Structure.FFIType.class) {
                // ignore this case
                // @todo Allow user to pass in list of classes for which to skip construction?
                return;
            }
            throw new RuntimeException("Parameterless constructor failed on Structure sub type: " + structureSubType.getName());
        }

        if (!structConstructor.isAccessible()) {
            structConstructor.setAccessible(true);
        }
        final Structure structure;
        try {
            structure= structConstructor.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException("Could not instantiate Structure sub type: " + structureSubType.getName(), e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Could not instantiate Structure sub type: " + structureSubType.getName(), e);
        } catch (InvocationTargetException e) {
            // this is triggered by checks in Structure.getFields(), and static loadlibrary() failures
            if (ignoreConstructorError != null) {
                final String structSubtypeName = structureSubType.getName();
                for (final String classPrefix : ignoreConstructorError) {
                    if (structSubtypeName.startsWith(classPrefix)) {
                        return;
                    }
                }
            }
            throw new RuntimeException("Could not instantiate Structure sub type: " + structureSubType.getName(), e);
        }

        final List<String> methodCallFieldOrder = structure.getFieldOrder();

        final List<Field> actualFields = structure.getFieldList();
        final List<String> actualFieldNames = new ArrayList<String>(actualFields.size());
        for (final Field field : actualFields) {
            // ignore static fields
            if (!Modifier.isStatic(field.getModifiers())) {
                final String actualFieldName = field.getName();
                if (!methodCallFieldOrder.contains(actualFieldName)) {
                    throw new IllegalArgumentException(structureSubType.getName() + ".getFieldOrder() [" + methodCallFieldOrder
                            + "] does not include declared field: " + actualFieldName);
                }
                actualFieldNames.add(actualFieldName);
            }
        }

        for (final String methodCallField : methodCallFieldOrder) {
            if (!actualFieldNames.contains(methodCallField)) {
                throw new IllegalArgumentException(structureSubType.getName() + ".getFieldOrder() [" + methodCallFieldOrder
                        + "] includes undeclared field: " + methodCallField);
            }
        }
    }
}

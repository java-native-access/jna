package com.sun.jna;

import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Utility class for detecting missing {@link com.sun.jna.Structure#getFieldOrder()} methods.
 *
 * This class could be moved to the unit test tree, but them reusing it in the 'platform' project would require
 * publishing this test tree.
 *
 * @author Dan Rollo
 * Date: 1/17/13
 * Time: 4:08 PM
 */
public final class StructureFieldOrderInspector {

    private StructureFieldOrderInspector(){}

    /**
     * Find all classes that extend {@link Structure}.
     */
    public static Set<Class<Structure>> findStructureSubClasses(final ClassLoader classLoader) {

        // @todo use: http://code.google.com/p/reflections/

        List<ClassLoader> classLoadersList = new LinkedList<ClassLoader>();
        classLoadersList.add(ClasspathHelper.contextClassLoader());
        classLoadersList.add(ClasspathHelper.staticClassLoader());

        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setScanners(new SubTypesScanner(false /* don't exclude Object.class */), new ResourcesScanner())
                .setUrls(ClasspathHelper.forClassLoader(classLoadersList.toArray(new ClassLoader[0])))
                .filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix("org.your.package"))));

        //Set<Class<?>> classes = reflections.getSubTypesOf(Object.class);
        Set<Class<Structure>> classes = (Set<Class<Structure>>) reflections.getSubTypesOf(Structure.class);

        return classes;
    }

}

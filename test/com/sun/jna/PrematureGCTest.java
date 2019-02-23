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

import org.junit.Test;

/**
 * Reported in https://github.com/java-native-access/jna/issues/664
 *
 * For the
 */
public class PrematureGCTest {
    @Test
    public void testGC() {
        Native.setProtected(false);

        // This code below is reduced from a different test.  (I forget which.)
        new Memory(4);

        // The code below comes from NativeTest.testLongStringGeneration.
        StringBuilder buf = new StringBuilder();
        final int MAX = 200000;
        for (int i = 0; i < MAX; i++) {
            buf.append("aaaaaaaaaa");
        }
        String s1 = buf.toString();
        Memory m = new Memory(( MAX * 10 + 1) * Native.WCHAR_SIZE);
        m.setWideString(0, s1);
        m.getWideString(0);
    }
}

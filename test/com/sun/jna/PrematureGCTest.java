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

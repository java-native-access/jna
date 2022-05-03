/* Copyright (c) 2021 Dmytro Sheyko, All Rights Reserved
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
package com.sun.jna.platform.win32;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import com.sun.jna.Memory;
import java.lang.ref.Reference;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;

/**
 * https://github.com/java-native-access/jna/issues/1362
 */
public class Crypt32UtilClearSecuritySensitiveDataTest {
    @Rule public ErrorCollector errors = new ErrorCollector();
    Field allocatedMemory;

    @Before
    public void setUp() throws NoSuchFieldException, ClassNotFoundException {
        allocatedMemory = Memory.class.getDeclaredField("allocatedMemory");
        allocatedMemory.setAccessible(true);
    }

    boolean stillHover(byte[] sample) throws IllegalAccessException {
        for(Reference<Memory> memRef: ((Map<Long, Reference<Memory>>) allocatedMemory.get(null)).values()) {
            Memory memory = memRef.get();
            byte[] array = memory.getByteArray(0, (int) memory.size());
            if (Arrays.equals(array, sample)) {
                return true;
            }
        }
        return false;
    }

    @After
    public void tearDown() {
        Memory.disposeAll();
    }

    @Test
    public void testEncryption() throws IllegalAccessException {
        byte[] original = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, };
        Crypt32Util.cryptProtectData(original, null, WinCrypt.CRYPTPROTECT_UI_FORBIDDEN, "", null);

        errors.checkThat("original is still hover", false, is(stillHover(original)));
    }

    @Test
    public void testDecryption() throws IllegalAccessException {
        byte[] original = { 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, };
        byte[] encrypted = Crypt32Util.cryptProtectData(original, null, WinCrypt.CRYPTPROTECT_UI_FORBIDDEN, "", null);
        Crypt32Util.cryptUnprotectData(encrypted, null, WinCrypt.CRYPTPROTECT_UI_FORBIDDEN, null);

        errors.checkThat("original is still hover", false, is(stillHover(original)));
        errors.checkThat("encrypted is still hover", false, is(stillHover(encrypted)));
    }

    @Test
    public void testEncryptionWithEntropy() throws IllegalAccessException {
        byte[] original = { 25, 26, 27, 28, 29, 30, };
        byte[] entropy = { 21, 20, 19, 18, 17, 16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, };
        Crypt32Util.cryptProtectData(original, entropy, WinCrypt.CRYPTPROTECT_UI_FORBIDDEN, "", null);

        errors.checkThat("original is still hover", false, is(stillHover(original)));
        errors.checkThat("entropy is still hover", false, is(stillHover(entropy)));
    }

    @Test
    public void testDecryptionWithEntropy() throws IllegalAccessException {
        byte[] original = { 31, 32, 33, 34, };
        byte[] entropy = { 41, 40, 39, 38, 37, 36, 35, 34, 33, 32, 31, 30, 29, 28, 27, 26, 25, 24, 23, 22, };
        byte[] encrypted = Crypt32Util.cryptProtectData(original, entropy, WinCrypt.CRYPTPROTECT_UI_FORBIDDEN, "", null);
        Crypt32Util.cryptUnprotectData(encrypted, entropy, WinCrypt.CRYPTPROTECT_UI_FORBIDDEN, null);

        errors.checkThat("original is still hover", false, is(stillHover(original)));
        errors.checkThat("entropy is still hover", false, is(stillHover(entropy)));
        errors.checkThat("encrypted is still hover", false, is(stillHover(encrypted)));
    }

    @Test
    public void testUnsuccessfulDecryption() throws IllegalAccessException {
        byte[] original = { 35, 36, 37, 38, 39, 40, 41, };
        try {
            Crypt32Util.cryptUnprotectData(original, null, WinCrypt.CRYPTPROTECT_UI_FORBIDDEN, null);
            errors.addError(new AssertionError("Win32Exception is expected"));
        } catch (Win32Exception e) {
            // ok, expected
        }

        errors.checkThat("original is still hover", false, is(stillHover(original)));
    }

    @Test
    public void testUnsuccessfulDecryptionBadEntropy() throws IllegalAccessException {
        byte[] original = { 42, 43, };
        byte[] entropy0 = { 44, 45, 46, };
        byte[] entropy1 = { 47, 48, 49, 50, };
        byte[] encrypted = Crypt32Util.cryptProtectData(original, entropy0, WinCrypt.CRYPTPROTECT_UI_FORBIDDEN, "", null);
        try {
            Crypt32Util.cryptUnprotectData(encrypted, entropy1, WinCrypt.CRYPTPROTECT_UI_FORBIDDEN, null);
            errors.addError(new AssertionError("Win32Exception is expected"));
        } catch (Win32Exception e) {
            // ok, expected
        }

        errors.checkThat("original is still hover", false, is(stillHover(original)));
        errors.checkThat("entropy0 is still hover", false, is(stillHover(entropy0)));
        errors.checkThat("entropy1 is still hover", false, is(stillHover(entropy1)));
        errors.checkThat("encrypted is still hover", false, is(stillHover(encrypted)));
    }
}

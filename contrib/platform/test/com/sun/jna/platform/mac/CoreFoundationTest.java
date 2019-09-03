/*
 * Copyright (c) 2019 Daniel Widdis
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
package com.sun.jna.platform.mac;

import static com.sun.jna.platform.mac.CoreFoundationUtil.release;
import static com.sun.jna.platform.mac.CoreFoundationUtil.releaseAll;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.mac.CoreFoundation.CFAllocatorRef;
import com.sun.jna.platform.mac.CoreFoundation.CFArrayRef;
import com.sun.jna.platform.mac.CoreFoundation.CFDataRef;
import com.sun.jna.platform.mac.CoreFoundation.CFMutableDictionaryRef;
import com.sun.jna.platform.mac.CoreFoundation.CFNumberRef;
import com.sun.jna.platform.mac.CoreFoundation.CFNumberType;
import com.sun.jna.platform.mac.CoreFoundation.CFStringRef;
import com.sun.jna.platform.mac.CoreFoundation.CFTypeRef;
import com.sun.jna.ptr.DoubleByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;

public class CoreFoundationTest {

    private static final CoreFoundation CF = CoreFoundation.INSTANCE;

    @Test
    public void testCFStringRef() {
        String awesome = "ǝɯosǝʍɐ sı ∀Nſ"; // Unicode
        CFStringRef cfAwesome = CFStringRef.toCFString(awesome);
        assertEquals(awesome.length(), CF.CFStringGetLength(cfAwesome));
        assertEquals(awesome, CoreFoundationUtil.cfPointerToString(cfAwesome));

        Memory mem = new Memory(awesome.getBytes().length + 1);
        mem.clear();
        assertTrue(CF.CFStringGetCString(cfAwesome, mem, mem.size(), CoreFoundation.kCFStringEncodingUTF8));
        byte[] awesomeBytes = mem.getByteArray(0, (int) mem.size() - 1);
        byte[] awesomeArr = awesome.getBytes();
        for (int i = 0; i < awesomeArr.length; i++) {
            assertEquals(awesomeArr[i], awesomeBytes[i]);
        }
        // Essentially a toString, can't rely on format but should contain the string
        CFStringRef desc = CF.CFCopyDescription(cfAwesome);
        assertTrue(CoreFoundationUtil.cfPointerToString(desc).contains(awesome));

        release(desc);
        release(cfAwesome);
    }

    @Test
    public void testCFNumberRef() {
        LongByReference max = new LongByReference(Long.MAX_VALUE);
        CFNumberRef cfMax = CF.CFNumberCreate(null, CFNumberType.kCFNumberLongLongType.ordinal(), max);
        assertEquals(Long.MAX_VALUE, CoreFoundationUtil.cfPointerToLong(cfMax));
        release(cfMax);

        IntByReference zero = new IntByReference(0);
        IntByReference one = new IntByReference(1);
        CFNumberRef cfZero = CF.CFNumberCreate(null, CFNumberType.kCFNumberIntType.ordinal(), zero);
        CFNumberRef cfOne = CF.CFNumberCreate(null, CFNumberType.kCFNumberIntType.ordinal(), one);

        assertEquals(0, CoreFoundationUtil.cfPointerToInt(cfZero));
        assertEquals(1, CoreFoundationUtil.cfPointerToInt(cfOne));

        release(cfZero);
        release(cfOne);
    }

    @Test
    public void testCFRetainCount() {
        DoubleByReference pi = new DoubleByReference(Math.PI);
        DoubleByReference e = new DoubleByReference(Math.E);
        CFNumberRef cfE = CF.CFNumberCreate(null, CFNumberType.kCFNumberDoubleType.ordinal(), e);
        CFNumberRef cfPi = CF.CFNumberCreate(null, CFNumberType.kCFNumberDoubleType.ordinal(), pi);
        assertEquals(1, CF.CFGetRetainCount(cfE));
        assertEquals(1, CF.CFGetRetainCount(cfPi));
        CF.CFRetain(cfE);
        CF.CFRetain(cfPi);
        CF.CFRetain(cfPi);
        assertEquals(2, CF.CFGetRetainCount(cfE));
        assertEquals(3, CF.CFGetRetainCount(cfPi));

        List<CFTypeRef> irrationalReferences = new ArrayList<>();
        irrationalReferences.add(cfE);
        irrationalReferences.add(cfPi);
        releaseAll(irrationalReferences);

        assertEquals(1, CF.CFGetRetainCount(cfE));
        assertEquals(2, CF.CFGetRetainCount(cfPi));
        release(cfPi);
        assertEquals(1, CF.CFGetRetainCount(cfPi));
        release(cfE);
        release(cfPi);
    }

    @Test
    public void testCFArray() {
        CFNumberRef[] refArray = new CFNumberRef[3];
        int size = Native.getNativeSize(CFNumberRef.class);
        Memory contiguousArray = new Memory(size * refArray.length);
        for (int i = 0; i < refArray.length; i++) {
            refArray[i] = CF.CFNumberCreate(null, CoreFoundation.CFNumberType.kCFNumberIntType.ordinal(),
                    new IntByReference(i));
            contiguousArray.setPointer(i * size, refArray[i].getPointer());
        }
        CFArrayRef cfPtrArray = CF.CFArrayCreate(null, contiguousArray, refArray.length, null);

        assertEquals(refArray.length, CF.CFArrayGetCount(cfPtrArray));
        for (int i = 0; i < refArray.length; i++) {
            CFTypeRef result = CF.CFArrayGetValueAtIndex(cfPtrArray, i);
            CFNumberRef numRef = new CFNumberRef(result.getPointer());
            assertEquals(i, CoreFoundationUtil.cfPointerToInt(numRef));
        }

        for (int i = 0; i < refArray.length; i++) {
            release(refArray[i]);
        }
        release(cfPtrArray);
    }

    @Test
    public void testCFData() {
        String deadBug = "The only good bug is a dead bug.";
        Memory bugBytes = new Memory(deadBug.length() + 1);
        bugBytes.clear();
        bugBytes.setString(0, deadBug);

        CFDataRef cfBug = CF.CFDataCreate(null, bugBytes, bugBytes.size());
        assertEquals(bugBytes.size(), CF.CFDataGetLength(cfBug));

        PointerByReference bytes = CF.CFDataGetBytePtr(cfBug);
        assertEquals(deadBug, bytes.getPointer().getString(0));

        release(cfBug);
    }

    @Test
    public void testCFDictionary() {
        CFAllocatorRef alloc = CF.CFAllocatorGetDefault();
        CFMutableDictionaryRef dict = CF.CFDictionaryCreateMutable(alloc, 2, null, null);
        CFStringRef oneStr = CFStringRef.toCFString("one");

        // Key does not exist, returns null
        assertFalse(CF.CFDictionaryGetValueIfPresent(dict, oneStr, null));
        Pointer cfNull = CF.CFDictionaryGetValue(dict, oneStr);
        assertNull(cfNull);

        // Store and retrieve null value
        CF.CFDictionarySetValue(dict, oneStr, null);
        assertTrue(CF.CFDictionaryGetValueIfPresent(dict, oneStr, null));
        Pointer cfNullValue = CF.CFDictionaryGetValue(dict, oneStr);
        assertNull(cfNullValue);

        // Store (replace the null) and retrieve integer value
        IntByReference one = new IntByReference(1);
        CFNumberRef cfOne = CF.CFNumberCreate(null, CFNumberType.kCFNumberIntType.ordinal(), one);
        CF.CFDictionarySetValue(dict, oneStr, cfOne);

        assertTrue(CF.CFDictionaryGetValueIfPresent(dict, oneStr, null));
        Pointer result = CF.CFDictionaryGetValue(dict, oneStr);
        CFNumberRef numRef = new CFNumberRef(result);
        assertEquals(1, CoreFoundationUtil.cfPointerToInt(numRef));

        PointerByReference resultPtr = new PointerByReference();
        assertTrue(CF.CFDictionaryGetValueIfPresent(dict, oneStr, resultPtr));
        numRef = new CFNumberRef(resultPtr.getValue());
        assertEquals(1, CoreFoundationUtil.cfPointerToInt(numRef));

        // Test non-CF type as key
        IntByReference onePtr = new IntByReference(1);
        CF.CFDictionarySetValue(dict, onePtr, oneStr);
        result = CF.CFDictionaryGetValue(dict, onePtr);
        CFStringRef strRef = new CFStringRef(result);
        assertEquals("one", CoreFoundationUtil.cfPointerToString(strRef));

        release(oneStr);
        release(cfOne);
        release(dict);
    }
}

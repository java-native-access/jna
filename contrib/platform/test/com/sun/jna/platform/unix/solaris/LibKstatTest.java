/* Copyright (c) 2017 Daniel Widdis, All Rights Reserved
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
package com.sun.jna.platform.unix.solaris;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.platform.unix.solaris.LibKstat.Kstat;
import com.sun.jna.platform.unix.solaris.LibKstat.KstatCtl;
import com.sun.jna.platform.unix.solaris.LibKstat.KstatNamed;

import junit.framework.TestCase;

/**
 * Exercise the {@link Kstat} class.
 *
 * @author widdis@gmail.com
 */
public class LibKstatTest extends TestCase {

    public void testKstatLookupString() {
        if (Platform.isSolaris()) {
            KstatCtl kc = LibKstat.INSTANCE.kstat_open();

            // Test reading string
            Kstat ksp = kstatLookup(kc, "cpu_info", -1, null);
            assertNotNull(ksp);
            assertTrue(kstatRead(kc, ksp));
            assertNotNull(kstatDataLookupString(ksp, "vendor_id"));
            assertNotNull(kstatDataLookupString(ksp, "brand"));
            assertNotNull(kstatDataLookupString(ksp, "stepping"));
            assertNotNull(kstatDataLookupString(ksp, "model"));
            assertNotNull(kstatDataLookupString(ksp, "family"));

            LibKstat.INSTANCE.kstat_close(kc);
        }
    }

    public void testKstatLookupLong() {
        if (Platform.isSolaris()) {
            KstatCtl kc = LibKstat.INSTANCE.kstat_open();

            // Test reading long
            Kstat ksp = kstatLookup(kc, null, -1, "file_cache");
            assertNotNull(ksp);
            assertTrue(kstatRead(kc, ksp));
            assertTrue(kstatDataLookupLong(ksp, "buf_max") > 0);

            LibKstat.INSTANCE.kstat_close(kc);
        }
    }

    public void testKstatLookupAll() {
        if (Platform.isSolaris()) {
            KstatCtl kc = LibKstat.INSTANCE.kstat_open();

            for (Kstat ksp : kstatLookupAll(kc, "cpu", -1, "sys")) {
                if (kstatRead(kc, ksp)) {
                    assertTrue(kstatDataLookupLong(ksp, "cpu_ticks_idle") >= 0);
                    assertTrue(kstatDataLookupLong(ksp, "cpu_ticks_kernel") >= 0);
                    assertTrue(kstatDataLookupLong(ksp, "cpu_ticks_user") >= 0);
                }
            }

            LibKstat.INSTANCE.kstat_close(kc);
        }
    }

    /**
     * Convenience method for kstat_data_lookup() with String return values.
     * Searches the kstat's data section for the record with the specified name.
     * This operation is valid only for kstat types which have named data
     * records. Currently, only the KSTAT_TYPE_NAMED and KSTAT_TYPE_TIMER kstats
     * have named data records.
     *
     * @param ksp
     *            The kstat to search
     * @param name
     *            The key for the name-value pair, or name of the timer as
     *            applicable
     * @return The value as a String.
     */
    private static String kstatDataLookupString(Kstat ksp, String name) {
        if (ksp.ks_type != LibKstat.KSTAT_TYPE_NAMED && ksp.ks_type != LibKstat.KSTAT_TYPE_TIMER) {
            throw new IllegalArgumentException("Not a kstat_named or kstat_timer kstat.");
        }
        Pointer p = LibKstat.INSTANCE.kstat_data_lookup(ksp, name);
        if (p == null) {
            fail(String.format("Failed lo lookup kstat value for key %s", name));
            return "";
        }
        KstatNamed data = new KstatNamed(p);
        switch (data.data_type) {
            case LibKstat.KSTAT_DATA_CHAR:
                return new String(data.value.charc).trim();
            case LibKstat.KSTAT_DATA_INT32:
                return Integer.toString(data.value.i32);
            case LibKstat.KSTAT_DATA_UINT32:
                if (data.value.ui32 > 0) {
                    return Integer.toString(data.value.ui32);
                }
                return Long.toString(data.value.ui32 & 0x00000000ffffffffL);
            case LibKstat.KSTAT_DATA_INT64:
                return Long.toString(data.value.i64);
            case LibKstat.KSTAT_DATA_UINT64:
                if (data.value.ui64 > 0) {
                    return Long.toString(data.value.ui64);
                }
                return BigInteger.valueOf(data.value.ui64).add(BigInteger.ONE.shiftLeft(64)).toString();
            case LibKstat.KSTAT_DATA_STRING:
                return data.value.str.addr.getString(0);
            default:
                fail(String.format("Unimplemented kstat data type %d", data.data_type));
                return "";
        }
    }

    /**
     * Convenience method for kstat_data_lookup() with numeric return values.
     * Searches the kstat's data section for the record with the specified name.
     * This operation is valid only for kstat types which have named data
     * records. Currently, only the KSTAT_TYPE_NAMED and KSTAT_TYPE_TIMER kstats
     * have named data records.
     *
     * @param ksp
     *            The kstat to search
     * @param name
     *            The key for the name-value pair, or name of the timer as
     *            applicable
     * @return The value as a long. If the data type is a character or string
     *         type, returns 0 and logs an error.
     */
    private static long kstatDataLookupLong(Kstat ksp, String name) {
        if (ksp.ks_type != LibKstat.KSTAT_TYPE_NAMED && ksp.ks_type != LibKstat.KSTAT_TYPE_TIMER) {
            throw new IllegalArgumentException("Not a kstat_named or kstat_timer kstat.");
        }
        Pointer p = LibKstat.INSTANCE.kstat_data_lookup(ksp, name);
        if (p == null) {
            fail(String.format("Failed lo lookup kstat value on %s:%d:%s for key %s", new String(ksp.ks_module).trim(),
                    ksp.ks_instance, new String(ksp.ks_name).trim(), name));
            return 0L;
        }
        KstatNamed data = new KstatNamed(p);
        switch (data.data_type) {
            case LibKstat.KSTAT_DATA_INT32:
                return (long) data.value.i32;
            case LibKstat.KSTAT_DATA_UINT32:
                return data.value.ui32 & 0x00000000ffffffffL;
            case LibKstat.KSTAT_DATA_INT64:
                return data.value.i64;
            case LibKstat.KSTAT_DATA_UINT64:
                // Doesn't actually return unsigned; caller must interpret
                return data.value.ui64;
            default:
                fail(String.format("Unimplemented or non-numeric kstat data type %d", data.data_type));
                return 0L;
        }
    }

    /**
     * Convenience method for kstat_read() which gets data from the kernel for
     * the kstat pointed to by ksp. ksp.ks_data is automatically allocated (or
     * reallocated) to be large enough to hold all of the data. ksp.ks_ndata is
     * set to the number of data fields, ksp.ks_data_size is set to the total
     * size of the data, and ksp.ks_snaptime is set to the high-resolution time
     * at which the data snapshot was taken.
     *
     * @param ksp
     *            The kstat from which to retrieve data
     * @return True if successful; false otherwise
     */
    private static boolean kstatRead(KstatCtl kc, Kstat ksp) {
        int retry = 0;
        while (0 > LibKstat.INSTANCE.kstat_read(kc, ksp, null)) {
            if (LibKstat.EAGAIN != Native.getLastError() || 5 <= ++retry) {
                fail(String.format("Failed to read kstat %s:%d:%s", new String(ksp.ks_module).trim(), ksp.ks_instance,
                        new String(ksp.ks_name).trim()));
                return false;
            }
            try {
                Thread.sleep(8 << retry);
            } catch (InterruptedException e) {
                fail(e.getMessage());
                return false;
            }
        }
        return true;
    }

    /**
     * Convenience method for kstat_lookup(). Traverses the kstat chain,
     * searching for a kstat with the same ks_module, ks_instance, and ks_name
     * fields; this triplet uniquely identifies a kstat. If ks_module is NULL,
     * ks_instance is -1, or ks_name is NULL, then those fields will be ignored
     * in the search.
     *
     * @param module
     *            The module, or null to ignore
     * @param instance
     *            The instance, or -1 to ignore
     * @param name
     *            The name, or null to ignore
     * @return The first match of the requested Kstat structure if found, or
     *         null
     */
    private static Kstat kstatLookup(KstatCtl kc, String module, int instance, String name) {
        int ret = LibKstat.INSTANCE.kstat_chain_update(kc);
        if (ret < 0) {
            fail(String.format("Failed to update kstat chain"));
            return null;
        }
        return LibKstat.INSTANCE.kstat_lookup(kc, module, instance, name);
    }

    /**
     * Convenience method for kstat_lookup(). Traverses the kstat chain,
     * searching for all kstats with the same ks_module, ks_instance, and
     * ks_name fields; this triplet uniquely identifies a kstat. If ks_module is
     * NULL, ks_instance is -1, or ks_name is NULL, then those fields will be
     * ignored in the search.
     *
     * @param module
     *            The module, or null to ignore
     * @param instance
     *            The instance, or -1 to ignore
     * @param name
     *            The name, or null to ignore
     * @return All matches of the requested Kstat structure if found, or an
     *         empty list otherwise
     */
    private static List<Kstat> kstatLookupAll(KstatCtl kc, String module, int instance, String name) {
        List<Kstat> kstats = new ArrayList<>();
        int ret = LibKstat.INSTANCE.kstat_chain_update(kc);
        if (ret < 0) {
            fail(String.format("Failed to update kstat chain"));
            return kstats;
        }
        for (Kstat ksp = LibKstat.INSTANCE.kstat_lookup(kc, module, instance, name); ksp != null; ksp = ksp.next()) {
            if ((module == null || module.equals(new String(ksp.ks_module).trim()))
                    && (instance < 0 || instance == ksp.ks_instance)
                    && (name == null || name.equals(new String(ksp.ks_name).trim()))) {
                kstats.add(ksp);
            }
        }
        return kstats;
    }
}

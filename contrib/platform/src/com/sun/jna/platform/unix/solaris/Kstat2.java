/* Copyright (c) 2022 Daniel Widdis, All Rights Reserved
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

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;
import com.sun.jna.Union;
import com.sun.jna.platform.unix.solaris.Kstat2.Kstat2NV.UNION.IntegersArr;
import com.sun.jna.platform.unix.solaris.Kstat2.Kstat2NV.UNION.StringsArr;
import com.sun.jna.ptr.PointerByReference;

/**
 * Kstat2 library. The kstat2 facility is a general-purpose mechanism for
 * providing kernel statistics to users.
 * <p>
 * Kstat2 is available in Solaris 11.4 and later.
 */
public interface Kstat2 extends Library {

    /**
     * Requires Solaris 11.4. Users should test for UnsatisfiedLinkError
     */
    Kstat2 INSTANCE = Native.load("kstat2", Kstat2.class);

    // enum kstat2_status -- return values and error codes
    int KSTAT2_S_OK = 0; // Request was successful
    int KSTAT2_S_NO_PERM = 1; // Insufficient permissions for request
    int KSTAT2_S_NO_MEM = 2; // Insufficient memory available
    int KSTAT2_S_NO_SPACE = 3; // No space available for operation
    int KSTAT2_S_INVAL_ARG = 4; // Invalid argument supplied
    int KSTAT2_S_INVAL_STATE = 5; // Invalid state for this request
    int KSTAT2_S_INVAL_TYPE = 6; // Invalid data type found
    int KSTAT2_S_NOT_FOUND = 7; // Resource not found
    int KSTAT2_S_CONC_MOD = 8; // Concurrent modification of map detected
    int KSTAT2_S_DEL_MAP = 9; // Referenced map has been deleted
    int KSTAT2_S_SYS_FAIL = 10; // System call has failed, see errno

    // enum kstat2_match_type
    int KSTAT2_M_STRING = 0; // String
    int KSTAT2_M_GLOB = 1; // Glob with ?, *, or [...]
    int KSTAT2_M_RE = 2; // PCRE Regex

    /*
     * Values in Kstat2NV structure
     */
    // enum kstat2_nv_type
    byte KSTAT2_NVVT_MAP = 0; // Nested Name/Value map
    byte KSTAT2_NVVT_INT = 1; // 64-bit unsigned integer
    byte KSTAT2_NVVT_INTS = 2; // Array of 64-bit unsigned integers
    byte KSTAT2_NVVT_STR = 3; // Null-terminated C string
    byte KSTAT2_NVVT_STRS = 4; // Array of null-terminated C strings

    // enum kstat2_nv_kind
    byte KSTAT2_NVK_SYS = 0x01; // System kstat value type
    byte KSTAT2_NVK_USR = 0x02; // User-supplied value type
    byte KSTAT2_NVK_MAP = 0x04; // Sub-map value type
    byte KSTAT2_NVK_ALL = 0x07; // All value types (only for iteration)

    // enum kstat2_nv_flag
    short KSTAT2_NVF_NONE = 0x00; // No flags present
    short KSTAT2_NVF_INVAL = 0x01; // Value is invalid

    /**
     * Opaque kstat handle.
     */
    class Kstat2Handle extends PointerType {
        private PointerByReference ref = new PointerByReference();

        /**
         * Instantiates and opens a new Kstat2Handle with no filtering. All of the
         * system's kstats will be available. Convenience method for
         * {@link Kstat2#kstat2_open(PointerByReference, Kstat2MatcherList)} with a null
         * matcher list.
         */
        public Kstat2Handle() {
            this(null);
        }

        /**
         * Instantiates and opens a new Kstat2Handle filtered with the provided matcher.
         * Convenience method for
         * {@link Kstat2#kstat2_open(PointerByReference, Kstat2MatcherList)}.
         *
         * @param matchers
         *            Only kstats that match one or more of the provided matchers will
         *            be available. If a NULL or empty matcher list is provided, all of
         *            the system's kstats will be available. Restricting the number of
         *            kstats available will improve performance and reduce the memory
         *            footprint.
         */
        public Kstat2Handle(Kstat2MatcherList matchers) {
            super();
            int ks = INSTANCE.kstat2_open(ref, matchers);
            if (ks != KSTAT2_S_OK) {
                throw new Kstat2StatusException(ks);
            }
            this.setPointer(ref.getValue());
        }

        /**
         * Convenience method for {@link Kstat2#kstat2_update(Kstat2Handle)} that
         * synchronises the user's view with that of the kernel. The kernel may at any
         * point add or remove kstats, causing the user's view of the available kstats
         * to become out of date.
         *
         * @return Upon successful completion, returns a int value of
         *         {@link Kstat2#KSTAT2_S_OK}. If an error occurs a value other than
         *         KSTAT2_S_OK is returned.
         */
        public int update() {
            return INSTANCE.kstat2_update(this);
        }

        /**
         * Convenience method for
         * {@link Kstat2#kstat2_lookup_map(Kstat2Handle, String, PointerByReference)}
         * that obtains a reference to a kstat2 map given the URI of the map.
         *
         * @param uri
         *            The URI of the map to return.
         * @return A reference to the map.
         */
        public Kstat2Map lookupMap(String uri) {
            PointerByReference pMap = new PointerByReference();
            int ks = INSTANCE.kstat2_lookup_map(this, uri, pMap);
            if (ks != KSTAT2_S_OK) {
                throw new Kstat2StatusException(ks);
            }
            return new Kstat2Map(pMap.getValue());
        }

        /**
         * Convenience method for {@link Kstat2#kstat2_close(PointerByReference)}. After
         * use, the kstat handle should be closed to reclaim the handles and memory that
         * it allocated on open.
         *
         * @return Upon successful completion, returns a int value of
         *         {@link Kstat2#KSTAT2_S_OK}. If an error occurs a value other than
         *         KSTAT2_S_OK is returned.
         */
        public int close() {
            return INSTANCE.kstat2_close(ref);
        }
    }

    /**
     * Opaque kstat match list.
     */
    class Kstat2MatcherList extends PointerType {
        private PointerByReference ref = new PointerByReference();

        /**
         * Instantiates a new Kstat2MatcherList, allocating the necessary resources.
         * Convenience method for
         * {@link Kstat2#kstat2_alloc_matcher_list(PointerByReference)}.
         * <p>
         * It is the caller's responsibility to free this matcher list by calling
         * {@link #free()}.
         */
        public Kstat2MatcherList() {
            super();
            int ks = INSTANCE.kstat2_alloc_matcher_list(ref);
            if (ks != KSTAT2_S_OK) {
                throw new Kstat2StatusException(ks);
            }
            this.setPointer(ref.getValue());
        }

        /**
         * Convenience method for
         * {@link Kstat2#kstat2_add_matcher(int, String, Kstat2MatcherList)} that adds
         * matchers to the provided matcher list. Each call appends the new matcher to
         * the provided matcher list. Matches are on kstat URI, with the following match
         * types supported: {@link Kstat2#KSTAT2_M_STRING} which performs a direct
         * {@code strcmp} with the kstat URI, {@link Kstat2#KSTAT2_M_GLOB} which
         * performs a glob pattern match using {@code gmatch}, and
         * {@link Kstat2#KSTAT2_M_RE} which performs a Perl Compatible Regular
         * Expression (PCRE) match using {@code pcre_exec}.
         *
         * @param type
         *            The type of matcher, from the {@code kstat2_match_type_t}
         *            enumeration.
         * @param match
         *            The string to match.
         * @return Upon successful completion, returns a int value of
         *         {@link Kstat2#KSTAT2_S_OK}. If an error occurs a value other than
         *         KSTAT2_S_OK is returned.
         */
        public int addMatcher(int type, String match) {
            return INSTANCE.kstat2_add_matcher(type, match, this);
        }

        /**
         * Convenience method for
         * {@link Kstat2#kstat2_free_matcher_list(PointerByReference)} that frees the
         * resources associated with the matcher list.
         *
         * @return Upon successful completion, returns a int value of
         *         {@link Kstat2#KSTAT2_S_OK}. If an error occurs a value other than
         *         KSTAT2_S_OK is returned.
         */
        public int free() {
            return INSTANCE.kstat2_free_matcher_list(ref);
        }
    }

    /**
     * Opaque kstat map handle.
     */
    class Kstat2Map extends PointerType {

        public Kstat2Map() {
            super();
        }

        public Kstat2Map(Pointer p) {
            super(p);
        }

        /**
         * Convenience method for
         * {@link Kstat2#kstat2_map_get(Kstat2Map, String, PointerByReference)} that
         * retrieves the name/value (nv) pair identified by the supplied name.
         *
         * @param name
         *            The uri of the data to retrieve.
         * @return The name/value data.
         */
        public Kstat2NV mapGet(String name) {
            PointerByReference pbr = new PointerByReference();
            int ks = INSTANCE.kstat2_map_get(this, name, pbr);
            if (ks != KSTAT2_S_OK) {
                throw new Kstat2StatusException(ks);
            }
            return new Kstat2NV(pbr.getValue());
        }

        /**
         * Convenience method for
         * {@link Kstat2#kstat2_map_get(Kstat2Map, String, PointerByReference)} that
         * retrieves the name/value (nv) pair identified by the supplied name and
         * returns the value as an object.
         *
         * @param name
         *            The name of the data to retrieve.
         * @return If the value is of type {@link Kstat2#KSTAT2_NVVT_MAP}, a
         *         {@link Kstat2Map} is returned.
         *         <p>
         *         If the value is of type {@link Kstat2#KSTAT2_NVVT_INT}, a
         *         {@code long} is returned.
         *         <p>
         *         If the value is of type {@link Kstat2#KSTAT2_NVVT_INTS}, an array of
         *         {@code long} is returned.
         *         <p>
         *         If the value is of type {@link Kstat2#KSTAT2_NVVT_STR}, a
         *         {@link String} is returned.
         *         <p>
         *         If the value is of type {@link Kstat2#KSTAT2_NVVT_STRS}, an array of
         *         {@link String} is returned.
         *         <p>
         *         If no value exists for this property (error or invalid data
         *         {@link Kstat2#KSTAT2_NVF_INVAL}), returns {@code null}.
         */
        public Object getValue(String name) {
            try {
                Kstat2NV nv = mapGet(name);
                if (nv.flags == KSTAT2_NVF_INVAL) {
                    return null;
                }
                switch (nv.type) {
                    case KSTAT2_NVVT_MAP:
                        return nv.data.map;
                    case KSTAT2_NVVT_INT:
                        return nv.data.integerVal;
                    case KSTAT2_NVVT_INTS:
                        return nv.data.integers.addr.getLongArray(0, nv.data.integers.len);
                    case KSTAT2_NVVT_STR:
                        return nv.data.strings.addr.getString(0);
                    case KSTAT2_NVVT_STRS:
                        return nv.data.strings.addr.getStringArray(0, nv.data.strings.len);
                    default:
                        return null;
                }
            } catch (Kstat2StatusException e) {
                return null;
            }
        }
    }

    /**
     * Immutable Name/Value pair.
     */
    @FieldOrder({ "name", "type", "kind", "flags", "data" })
    class Kstat2NV extends Structure {
        public String name; // Name of the pair
        public byte type; // Value type of the pair
        public byte kind; // Kind of the pair
        public short flags; // Flags of the pair
        public UNION data; // Data value

        public static class UNION extends Union {
            public Kstat2Map map;
            public long integerVal;
            public IntegersArr integers;
            public StringsArr strings;

            @FieldOrder({ "addr", "len" })
            public static class IntegersArr extends Structure {
                public Pointer addr;
                public int len; // length of array
            }

            @FieldOrder({ "addr", "len" })
            public static class StringsArr extends Structure {
                public Pointer addr;
                public int len; // length of array
            }
        }

        public Kstat2NV() {
            super();
        }

        public Kstat2NV(Pointer p) {
            super(p);
            read();
        }

        @Override
        public void read() {
            super.read();
            switch (type) {
                case KSTAT2_NVVT_MAP:
                    data.setType(Kstat2Map.class);
                    break;
                case KSTAT2_NVVT_INT:
                    data.setType(long.class);
                    break;
                case KSTAT2_NVVT_INTS:
                    data.setType(IntegersArr.class);
                    break;
                case KSTAT2_NVVT_STR:
                case KSTAT2_NVVT_STRS:
                    data.setType(StringsArr.class);
                    break;
                default:
                    break;
            }
            data.read();
        }
    }

    /**
     * Initializes an opaque kstat2 handle that provides access to a specific view
     * of the kernel statistics.
     *
     * @param handle
     *            A pointer to the handle to be initialized.
     * @param matchers
     *            Only kstats that match one or more of the provided matchers will
     *            be available. If a NULL or empty matcher list is provided, all of
     *            the system's kstats will be available, which is equivalent to
     *            calling the kstat2_open() function. Restricting the number of
     *            kstats available will improve performance and reduce the memory
     *            footprint.
     * @return Upon successful completion, returns a int value of
     *         {@link Kstat2#KSTAT2_S_OK}. If an error occurs a value other than
     *         KSTAT2_S_OK is returned.
     */
    int kstat2_open(PointerByReference /* Kstat2Handle */ handle, Kstat2MatcherList matchers);

    /**
     * Synchronises the user's view with that of the kernel. The kernel may at any
     * point add or remove kstats, causing the user's view of the available kstats
     * to become out of date. The kstat2_update() function should be called
     * periodically to resynchronise the two views.
     *
     * @param handle
     *            The handle to be updated.
     * @return Upon successful completion, returns a int value of
     *         {@link Kstat2#KSTAT2_S_OK}. If an error occurs a value other than
     *         KSTAT2_S_OK is returned.
     */
    int kstat2_update(Kstat2Handle handle);

    /**
     * The kstat2_close() function frees all resources that are associated with the
     * handle. It is the caller's responsibility to free any allocated matcher list
     * by calling the kstat2_free_matcher_list() function.
     *
     * @param handle
     *            A reference to the handle to close.
     * @return Upon successful completion, returns a int value of
     *         {@link Kstat2#KSTAT2_S_OK}. If an error occurs a value other than
     *         KSTAT2_S_OK is returned.
     */
    int kstat2_close(PointerByReference /* Kstat2Handle */ handle);

    /**
     * Allocates a new matcher list to allow matchers to be provided to the
     * {@link Kstat2#kstat2_open(PointerByReference, Kstat2MatcherList)} function.
     *
     * @param matchers
     *            Receives a pointer to the allocated matcher list.
     * @return Upon successful completion, returns a int value of
     *         {@link Kstat2#KSTAT2_S_OK}. If an error occurs a value other than
     *         KSTAT2_S_OK is returned.
     */
    int kstat2_alloc_matcher_list(PointerByReference /* Kstat2MatcherList */ matchers);

    /**
     * Frees the resources associated with the matcher list.
     *
     * @param matchers
     *            A pointer to the {@link Kstat2MatcherList} to be freed.
     * @return Upon successful completion, returns a int value of
     *         {@link Kstat2#KSTAT2_S_OK}. If an error occurs a value other than
     *         KSTAT2_S_OK is returned.
     */
    int kstat2_free_matcher_list(PointerByReference /* Kstat2MatcherList */ matchers);

    /**
     * Adds matchers to the provided matcher list. Each call appends the new matcher
     * to the provided matcher list. Matches are on kstat URI, with the following
     * match types supported: {@link Kstat2#KSTAT2_M_STRING} which performs a direct
     * {@code strcmp} with the kstat URI, {@link Kstat2#KSTAT2_M_GLOB} which
     * performs a glob pattern match using {@code gmatch}, and
     * {@link Kstat2#KSTAT2_M_RE} which performs a Perl Compatible Regular
     * Expression (PCRE) match using {@code pcre_exec}.
     *
     * @param type
     *            The type of matcher, from the {@code kstat2_match_type_t}
     *            enumeration.
     * @param match
     *            The string to match.
     * @param matchers
     *            The list to which to append the matcher.
     * @return Upon successful completion, returns a int value of
     *         {@link Kstat2#KSTAT2_S_OK}. If an error occurs a value other than
     *         KSTAT2_S_OK is returned.
     */
    int kstat2_add_matcher(int type, String match, Kstat2MatcherList matchers);

    /**
     * Obtains a reference to a kstat2 map given the URI of the map.
     *
     * @param handle
     *            The handle to obtain the map from.
     * @param uri
     *            The string to match.
     * @param map
     *            Receives a reference to the relevant map.
     * @return Upon successful completion, the function will set the map parameter
     *         to reference the relevant map and {@link Kstat2#KSTAT2_S_OK} will be
     *         returned. If the requested item cannot be found, the map reference
     *         will be set to {@code NULL} and an error will be returned.
     */
    int kstat2_lookup_map(Kstat2Handle handle, String uri, PointerByReference /* Kstat2Map */ map);

    /**
     * Retrieves the name/value (nv) pair identified by the supplied name. The
     * reference returned is managed by the library and must not be passed to
     * free().
     *
     * @param map
     *            The map from which to retrieve the data.
     * @param name
     *            The uri of the data to retrieve.
     * @param nv
     *            Receives a reference to the name/value data.
     * @return Upon successful completion, returns {@link Kstat2#KSTAT2_S_OK}. If
     *         there is an error performing the requested operation, an error code
     *         will be returned.
     */
    int kstat2_map_get(Kstat2Map map, String name, PointerByReference /* Kstat2NV */ nv);

    /**
     * Gives a descriptive error message for the supplied status value.
     *
     * @param status
     *            A value in the {@code kstat2_status} enumeration.
     * @return A descriptive string for the supplied status code.
     */
    String kstat2_status_string(int status);
}

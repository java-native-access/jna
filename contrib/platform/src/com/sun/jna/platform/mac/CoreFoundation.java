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

import com.sun.jna.Library;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.ptr.ByReference;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.DoubleByReference;
import com.sun.jna.ptr.FloatByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.ptr.ShortByReference;

/**
 * Core Foundation is a framework that provides fundamental software services
 * useful to application services, application environments, and to applications
 * themselves. Core Foundation also provides abstractions for common data types.
 * <p>
 * Core Foundation functions have names that indicate when you own a returned
 * object: Object-creation functions have “Create” embedded in the name, and
 * Object-duplication functions that have “Copy” embedded in the name. If you
 * own an object, it is your responsibility to relinquish ownership (using
 * {@link #CFRelease}) when you have finished with it.
 * <p>
 * If you receive an object from any Core Foundation function other than a
 * creation or copy function—such as a Get function—you do not own it and cannot
 * be certain of the object’s life span. If you want to ensure that such an
 * object is not disposed of while you are using it, you must claim ownership
 * (with the {@link #CFRetain} function).
 */
public interface CoreFoundation extends Library {

    CoreFoundation INSTANCE = Native.load("CoreFoundation", CoreFoundation.class);

    int kCFNotFound = -1;

    int kCFStringEncodingASCII = 0x0600;
    int kCFStringEncodingUTF8 = 0x08000100;

    CFTypeID ARRAY_TYPE_ID = INSTANCE.CFArrayGetTypeID();
    CFTypeID BOOLEAN_TYPE_ID = INSTANCE.CFBooleanGetTypeID();
    CFTypeID DATA_TYPE_ID = INSTANCE.CFDataGetTypeID();
    CFTypeID DATE_TYPE_ID = INSTANCE.CFDateGetTypeID();
    CFTypeID DICTIONARY_TYPE_ID = INSTANCE.CFDictionaryGetTypeID();
    CFTypeID NUMBER_TYPE_ID = INSTANCE.CFNumberGetTypeID();
    CFTypeID STRING_TYPE_ID = INSTANCE.CFStringGetTypeID();

    /**
     * The {@code CFTypeRef} type is the base type defined in Core Foundation. It is
     * used as the type and return value in several polymorphic functions. It is a
     * generic object reference that acts as a placeholder for other true Core
     * Foundation objects.
     */
    class CFTypeRef extends PointerType {
        public CFTypeRef() {
            super();
        }

        public CFTypeRef(Pointer p) {
            super(p);
        }

        /**
         * Convenience method for {@link CoreFoundation#CFGetTypeID} on this object.
         *
         * @return The {@link CFTypeID}
         */
        public CFTypeID getTypeID() {
            if (this.getPointer() == null) {
                return new CFTypeID(0);
            }
            return INSTANCE.CFGetTypeID(this);
        }

        /**
         * Test whether this object has the specified ID
         *
         * @param typeID
         *            The {@link CFTypeID} for the class to test
         * @return true if this object has the same ID as {@code typeID}
         */
        public boolean isTypeID(CFTypeID typeID) {
            return getTypeID().equals(typeID);
        }

        /**
         * Convenience method for {@link CoreFoundation#CFRetain} on this object.
         */
        public void retain() {
            INSTANCE.CFRetain(this);
        }

        /**
         * Convenience method for {@link CoreFoundation#CFRelease} on this object.
         */
        public void release() {
            INSTANCE.CFRelease(this);
        }
    }

    /**
     * A reference type used in many Core Foundation parameters and function
     * results. It refers to a {@code CFAllocator} object, which allocates,
     * reallocates, and deallocates memory for Core Foundation objects.
     */
    class CFAllocatorRef extends CFTypeRef {
    }

    /**
     * A reference to a {@code CFNumber} object.
     */
    class CFNumberRef extends CFTypeRef {
        public CFNumberRef() {
            super();
        }

        public CFNumberRef(Pointer p) {
            super(p);
            if (!isTypeID(NUMBER_TYPE_ID)) {
                throw new ClassCastException("Unable to cast to CFNumber. Type ID: " + getTypeID());
            }
        }

        /**
         * Convert this {@code CFNumber} to a {@code long}.
         * <p>
         * This method assumes a 64-bit integer is stored and does not do type checking.
         * Users should use {@link #CFNumberGetType} to determine the appropriate type
         * conversion. If this object's type differs from the return type, and the
         * conversion is lossy or the return value is out of range, then this method
         * returns an approximate value.
         *
         * @return The corresponding {@code long}
         */
        public long longValue() {
            LongByReference lbr = new LongByReference();
            INSTANCE.CFNumberGetValue(this, CFNumberType.kCFNumberLongLongType.typeIndex(), lbr);
            return lbr.getValue();
        }

        /**
         * Convert this {@code CFNumber} to an {@code int}.
         * <p>
         * This method assumes a 32-bit integer is stored and does not do type checking.
         * Users should use {@link #CFNumberGetType} to determine the appropriate type
         * conversion. If this object's type differs from the return type, and the
         * conversion is lossy or the return value is out of range, then this method
         * returns an approximate value.
         *
         * @return The corresponding {@code int}
         */
        public int intValue() {
            IntByReference ibr = new IntByReference();
            INSTANCE.CFNumberGetValue(this, CFNumberType.kCFNumberIntType.typeIndex(), ibr);
            return ibr.getValue();
        }

        /**
         * Convert this {@code CFNumber} to a {@code short}.
         * <p>
         * This method assumes a 16-bit integer is stored and does not do type checking.
         * Users should use {@link #CFNumberGetType} to determine the appropriate type
         * conversion. If this object's type differs from the return type, and the
         * conversion is lossy or the return value is out of range, then this method
         * returns an approximate value.
         *
         * @return The corresponding {@code short}
         */
        public short shortValue() {
            ShortByReference sbr = new ShortByReference();
            INSTANCE.CFNumberGetValue(this, CFNumberType.kCFNumberShortType.typeIndex(), sbr);
            return sbr.getValue();
        }

        /**
         * Convert this {@code CFNumber} to a {@code byte}.
         * <p>
         * This method assumes an 8-bit integer is stored and does not do type checking.
         * Users should use {@link #CFNumberGetType} to determine the appropriate type
         * conversion. If this object's type differs from the return type, and the
         * conversion is lossy or the return value is out of range, then this method
         * returns an approximate value.
         *
         * @return The corresponding {@code byte}
         */
        public byte byteValue() {
            ByteByReference bbr = new ByteByReference();
            INSTANCE.CFNumberGetValue(this, CFNumberType.kCFNumberCharType.typeIndex(), bbr);
            return bbr.getValue();
        }

        /**
         * Convert this {@code CFNumber} to a {@code double}.
         * <p>
         * This method assumes a 64-bit floating point value is stored and does not do
         * type checking. Users should use {@link #CFNumberGetType} to determine the
         * appropriate type conversion. If this object's type differs from the return
         * type, and the conversion is lossy or the return value is out of range, then
         * this method returns an approximate value.
         *
         * @return The corresponding {@code double}
         */
        public double doubleValue() {
            DoubleByReference dbr = new DoubleByReference();
            INSTANCE.CFNumberGetValue(this, CFNumberType.kCFNumberDoubleType.typeIndex(), dbr);
            return dbr.getValue();
        }

        /**
         * Convert this {@code CFNumber} to a {@code float}.
         * <p>
         * This method assumes a 32-bit floating point value is stored and does not do
         * type checking. Users should use {@link #CFNumberGetType} to determine the
         * appropriate type conversion. If this object's type differs from the return
         * type, and the conversion is lossy or the return value is out of range, then
         * this method returns an approximate value.
         *
         * @return The corresponding {@code float}
         */
        public float floatValue() {
            FloatByReference fbr = new FloatByReference();
            INSTANCE.CFNumberGetValue(this, CFNumberType.kCFNumberFloatType.typeIndex(), fbr);
            return fbr.getValue();
        }
    }

    /**
     * Enum of values used for {@link CFNumberType} in {@link #CFNumberGetValue} and
     * {@link #CFNumberGetType}. Use {@link CFNumberType#typeIndex} for the expected
     * integer value corresponding to the C-style enum.
     */
    enum CFNumberType {
        unusedZero, kCFNumberSInt8Type, kCFNumberSInt16Type, kCFNumberSInt32Type, kCFNumberSInt64Type,
        kCFNumberFloat32Type, kCFNumberFloat64Type, kCFNumberCharType, kCFNumberShortType, kCFNumberIntType,
        kCFNumberLongType, kCFNumberLongLongType, kCFNumberFloatType, kCFNumberDoubleType, kCFNumberCFIndexType,
        kCFNumberNSIntegerType, kCFNumberCGFloatType, kCFNumberMaxType;

        /**
         * Index for the type of {@link CFNumberRef} stored.
         *
         * @return a {@link CFIndex} representing the enum ordinal.
         */
        public CFIndex typeIndex() {
            return new CFIndex(this.ordinal());
        }
    }

    /**
     * A reference to a {@code CFBoolean} object.
     */
    class CFBooleanRef extends CFTypeRef {
        public CFBooleanRef() {
            super();
        }

        public CFBooleanRef(Pointer p) {
            super(p);
            if (!isTypeID(BOOLEAN_TYPE_ID)) {
                throw new ClassCastException("Unable to cast to CFBoolean. Type ID: " + getTypeID());
            }
        }

        /**
         * Convert a reference to a Core Foundations Boolean into its {@code boolean}
         *
         * @return The corresponding {@code boolean}
         */
        public boolean booleanValue() {
            return 0 != INSTANCE.CFBooleanGetValue(this);
        }
    }

    /**
     * A reference to an immutable {@code CFArray} object.
     * <p>
     * CFArray is “toll-free bridged” with its Cocoa Foundation counterpart,
     * {@code NSArray}. Therefore, in a method where you see an {@code NSArray *}
     * parameter, you can pass in a {@link #CFArrayRef}.
     */
    class CFArrayRef extends CFTypeRef {
        public CFArrayRef() {
            super();
        }

        public CFArrayRef(Pointer p) {
            super(p);
            if (!isTypeID(ARRAY_TYPE_ID)) {
                throw new ClassCastException("Unable to cast to CFArray. Type ID: " + getTypeID());
            }
        }

        /**
         * Convenience method for {@link #CFArrayGetCount} on this object
         *
         * @return The number of values in this array.
         */
        public int getCount() {
            return INSTANCE.CFArrayGetCount(this).intValue();
        }

        /**
         * Convenience method for {@link #CFArrayGetValueAtIndex} on this object
         *
         * @param idx
         *            The index of the value to retrieve.
         * @return The value at the {@code idx} index.
         */
        public Pointer getValueAtIndex(int idx) {
            return INSTANCE.CFArrayGetValueAtIndex(this, new CFIndex(idx));
        }
    }

    /**
     * A reference to an immutable {@code CFData} object.
     */
    class CFDataRef extends CFTypeRef {
        public CFDataRef() {
            super();
        }

        public CFDataRef(Pointer p) {
            super(p);
            if (!isTypeID(DATA_TYPE_ID)) {
                throw new ClassCastException("Unable to cast to CFData. Type ID: " + getTypeID());
            }
        }

        /**
         * Convenience method for {@link #CFDataGetLength} on this object
         *
         * @return An index that specifies the number of bytes associated with this
         *         object.
         */
        public int getLength() {
            return INSTANCE.CFDataGetLength(this).intValue();
        }

        /**
         * Convenience method for {@link #CFDataGetBytePtr} on this object
         *
         * @return A read-only pointer to the bytes associated with this object.
         */
        public Pointer getBytePtr() {
            return INSTANCE.CFDataGetBytePtr(this);
        }
    }

    /**
     * A reference to an immutable {@code CFDictionary} object.
     */
    class CFDictionaryRef extends CFTypeRef {
        public CFDictionaryRef() {
            super();
        }

        public CFDictionaryRef(Pointer p) {
            super(p);
            if (!isTypeID(DICTIONARY_TYPE_ID)) {
                throw new ClassCastException("Unable to cast to CFDictionary. Type ID: " + getTypeID());
            }
        }

        /**
         * Convenience method for {@link CoreFoundation#CFDictionaryGetValue} on this
         * object.
         *
         * @param key
         *            The key for which to find a match.
         * @return The value associated with key, or {@code null} if no key-value pair
         *         matching key exists.
         */
        public Pointer getValue(PointerType key) {
            return INSTANCE.CFDictionaryGetValue(this, key);
        }

        /**
         * Convenience method for
         * {@link CoreFoundation#CFDictionaryGetValueIfPresent} on this object.
         *
         * @param key
         *            The key for which to find a match.
         * @param value
         *            A pointer to memory which, on return, is filled with the
         *            pointer-sized value if a matching key is found.
         * @return {@code true} if a matching key was found, otherwise
         *         {@code false}
         */
        public boolean getValueIfPresent(PointerType key, PointerByReference value) {
            return INSTANCE.CFDictionaryGetValueIfPresent(this, key, value) > 0;
        }
    }

    /**
     * A reference to a mutable {@code CFDictionary} object.
     */
    class CFMutableDictionaryRef extends CFDictionaryRef {
        public CFMutableDictionaryRef() {
            super();
        }

        public CFMutableDictionaryRef(Pointer p) {
            super(p);
        }

        /**
         * Convenience method for {@link CoreFoundation#CFDictionarySetValue} on this
         * object.
         *
         * @param key
         *            The key of the value to set.
         * @param value
         *            The value to add to or replace .
         */
        public void setValue(PointerType key, PointerType value) {
            INSTANCE.CFDictionarySetValue(this, key, value);
        }
    }

    /**
     * A reference to a {@code CFString} object, which “encapsulates” a Unicode
     * string along with its length. {@code CFString} is an opaque type that defines
     * the characteristics and behavior of {@code CFString} objects.
     */
    class CFStringRef extends CFTypeRef {
        public CFStringRef() {
            super();
        }

        public CFStringRef(Pointer p) {
            super(p);
            if (!isTypeID(STRING_TYPE_ID)) {
                throw new ClassCastException("Unable to cast to CFString. Type ID: " + getTypeID());
            }
        }

        /**
         * Convenience function which calls {@link #CFStringCreateWithCharacters} to
         * create a new {@link CFStringRef} from the given Java {@link java.lang.String}
         * and returns its reference pointer.
         * <p>
         * This reference must be released with {@link #CFRelease} to avoid leaking
         * references.
         *
         * @param s
         *            A {@link java.lang.String}.
         * @return An immutable string containing {@code s}, or {@code null} if there
         *         was a problem creating the object.
         */
        public static CFStringRef createCFString(String s) {
            final char[] chars = s.toCharArray();
            return INSTANCE.CFStringCreateWithCharacters(null, chars, new CFIndex(chars.length));
        }

        /**
         * Convert a reference to a Core Foundations String into its
         * {@link java.lang.String}
         *
         * @return The corresponding {@link java.lang.String}, or null if the conversion
         *         failed.
         */
        public String stringValue() {
            CFIndex length = INSTANCE.CFStringGetLength(this);
            if (length.longValue() == 0) {
                return "";
            }
            CFIndex maxSize = INSTANCE.CFStringGetMaximumSizeForEncoding(length, kCFStringEncodingUTF8);
            if (maxSize.intValue() == kCFNotFound) {
                throw new StringIndexOutOfBoundsException("CFString maximum number of bytes exceeds LONG_MAX.");
            }
            Memory buf = new Memory(maxSize.longValue());
            if (0 != INSTANCE.CFStringGetCString(this, buf, maxSize, kCFStringEncodingUTF8)) {
                return buf.getString(0, "UTF8");
            }
            throw new IllegalArgumentException("CFString conversion fails or the provided buffer is too small.");
        }
    }

    /**
     * A wrapper for the {@link NativeLong} type, used for {@link CFNumberRef}
     * types, {@link CFStringRef} lengths, and {@link CFArrayRef} sizes and indices.
     */
    class CFIndex extends NativeLong {
        private static final long serialVersionUID = 1L;

        public CFIndex() {
            super();
        }

        public CFIndex(long value) {
            super(value);
        }
    }

    /**
     * A type for unique, constant integer values that identify particular Core
     * Foundation opaque types.
     * <p>
     * Because the value for a type ID can change from release to release, your code
     * should not rely on stored or hard-coded type IDs nor should it hard-code any
     * observed properties of a type ID (such as, for example, it being a small
     * integer).
     */
    class CFTypeID extends NativeLong {
        private static final long serialVersionUID = 1L;

        public CFTypeID() {
            super();
        }

        public CFTypeID(long value) {
            super(value);
        }

        @Override
        public String toString() {
            if (this.equals(ARRAY_TYPE_ID)) {
                return "CFArray";
            } else if (this.equals(BOOLEAN_TYPE_ID)) {
                return "CFBoolean";
            } else if (this.equals(DATA_TYPE_ID)) {
                return "CFData";
            } else if (this.equals(DATE_TYPE_ID)) {
                return "CFDate";
            } else if (this.equals(DICTIONARY_TYPE_ID)) {
                return "CFDictionary";
            } else if (this.equals(NUMBER_TYPE_ID)) {
                return "CFNumber";
            } else if (this.equals(STRING_TYPE_ID)) {
                return "CFString";
            } else {
                return super.toString();
            }
        }
    }

    /**
     * Creates a string from a buffer of Unicode characters.
     * <p>
     * This reference must be released with {@link #CFRelease} to avoid leaking
     * references.
     *
     * @param alloc
     *            The allocator to use to allocate memory for the new string. Pass
     *            {@code null} or {@code kCFAllocatorDefault} to use the current
     *            default allocator.
     * @param chars
     *            The buffer of Unicode characters to copy into the new string.
     * @param length
     *            The number of characters in the buffer pointed to by chars. Only
     *            this number of characters will be copied to internal storage.
     * @return An immutable string containing {@code chars}, or {@code null} if
     *         there was a problem creating the object.
     */
    CFStringRef CFStringCreateWithCharacters(CFAllocatorRef alloc, char[] chars, CFIndex length);

    /**
     * Creates a {@code CFNumber} object using a specified value.
     * <p>
     * This reference must be released with {@link #CFRelease} to avoid leaking
     * references.
     *
     * @param alloc
     *            The allocator to use to allocate memory for the new object. Pass
     *            {@code null} or {@code kCFAllocatorDefault} to use the current
     *            default allocator.
     * @param theType
     *            A constant that specifies the data type of the value to convert.
     *            The ordinal value of the enum.
     *            <p>
     *            The {@code theType} parameter is not necessarily preserved when
     *            creating a new {@code CFNumber} object. The {@code CFNumber}
     *            object will be created using whatever internal storage type the
     *            creation function deems appropriate. Use the function
     *            {@link #CFNumberGetType} to find out what type the
     *            {@code CFNumber} object used to store your value.
     * @param valuePtr
     *            A pointer to the value for the returned number object.
     * @return A new number with the value specified by {@code valuePtr}.
     */
    CFNumberRef CFNumberCreate(CFAllocatorRef alloc, CFIndex theType, ByReference valuePtr);

    /**
     * Creates a new immutable array with the given values.
     * <p>
     * This reference must be released with {@link #CFRelease} to avoid leaking
     * references.
     *
     * @param alloc
     *            The allocator to use to allocate memory for the new array and its
     *            storage for values. Pass {@code null} or
     *            {@code kCFAllocatorDefault} to use the current default allocator.
     * @param values
     *            A C array of the pointer-sized values to be in the new array. The
     *            values in the new array are ordered in the same order in which
     *            they appear in this C array. This value may be {@code null} if
     *            {@code numValues} is 0. This C array is not changed or freed by
     *            this function. If {@code values} is not a valid pointer to a C
     *            array of at least {@code numValues} elements, the behavior is
     *            undefined.
     * @param numValues
     *            The number of values to copy from the {@code values} C array into
     *            the new array. This number will be the count of the new array—it
     *            must not be negative or greater than the number of elements in
     *            values.
     * @param callBacks
     *            A pointer to a {@code CFArrayCallBacks} structure initialized with
     *            the callbacks for the array to use on each value in the
     *            collection. The retain callback is used within this function, for
     *            example, to retain all of the new values from the {@code values} C
     *            array. A copy of the contents of the callbacks structure is made,
     *            so that a pointer to a structure on the stack can be passed in or
     *            can be reused for multiple collection creations.
     *            <p>
     *            This value may be {@code null}, which is treated as if a valid
     *            structure of version 0 with all fields {@code null} had been
     *            passed in.
     * @return A new immutable array containing {@code numValues} from
     *         {@code values}, or {@code null} if there was a problem creating the
     *         object.
     */
    CFArrayRef CFArrayCreate(CFAllocatorRef alloc, Pointer values, CFIndex numValues, Pointer callBacks);

    /**
     * Creates an immutable {@code CFData} object using data copied from a specified
     * byte buffer.
     * <p>
     * This reference must be released with {@link #CFRelease} to avoid leaking
     * references.
     *
     * @param alloc
     *            The allocator to use to allocate memory for the new object. Pass
     *            {@code null} or {@code kCFAllocatorDefault} to use the current
     *            default allocator.
     * @param bytes
     *            A pointer to the byte buffer that contains the raw data to be
     *            copied into the Data.
     * @param length
     *            The number of bytes in the buffer ({@code bytes}).
     * @return A new {@code CFData} object, or {@code null} if there was a problem
     *         creating the object.
     */
    CFDataRef CFDataCreate(CFAllocatorRef alloc, Pointer bytes, CFIndex length);

    /**
     * Creates a new mutable dictionary.
     * <p>
     * This reference must be released with {@link #CFRelease} to avoid leaking
     * references.
     *
     * @param alloc
     *            The allocator to use to allocate memory for the new string. Pass
     *            {@code null} or {@code kCFAllocatorDefault} to use the current
     *            default allocator.
     * @param capacity
     *            The maximum number of key-value pairs that can be contained by the
     *            new dictionary. The dictionary starts empty and can grow to this
     *            number of key-value pairs (and it can have less).
     *            <p>
     *            Pass 0 to specify that the maximum capacity is not limited. The
     *            value must not be negative.
     * @param keyCallBacks
     *            A pointer to a {@code CFDictionaryKeyCallBacks} structure
     *            initialized with the callbacks to use to retain, release,
     *            describe, and compare keys in the dictionary. A copy of the
     *            contents of the callbacks structure is made, so that a pointer to
     *            a structure on the stack can be passed in or can be reused for
     *            multiple collection creations.
     *            <p>
     *            This value may be {@code null}, which is treated as a valid
     *            structure of version 0 with all fields {@code null}.
     * @param valueCallBacks
     *            A pointer to a {@code CFDictionaryValueCallBacks} structure
     *            initialized with the callbacks to use to retain, release,
     *            describe, and compare values in the dictionary. A copy of the
     *            contents of the callbacks structure is made, so that a pointer to
     *            a structure on the stack can be passed in or can be reused for
     *            multiple collection creations.
     *            <p>
     *            This value may be {@code null}, which is treated as a valid
     *            structure of version 0 with all fields {@code null}.
     * @return A new dictionary, or {@code null} if there was a problem creating the
     *         object.
     */
    CFMutableDictionaryRef CFDictionaryCreateMutable(CFAllocatorRef alloc, CFIndex capacity, Pointer keyCallBacks,
            Pointer valueCallBacks);

    /**
     * Returns a textual description of a Core Foundation object.
     * <p>
     * The nature of the description differs by object. For example, a description
     * of a CFArray object would include descriptions of each of the elements in the
     * collection.
     * <p>
     * You can use this function for debugging Core Foundation objects in your code.
     * Note, however, that the description for a given object may be different in
     * different releases of the operating system. Do not create dependencies in
     * your code on the content or format of the information returned by this
     * function.
     *
     * @param cf
     *            The {@code CFType} object (a generic reference of type
     *            {@code CFTypeRef}) from which to derive a description.
     * @return A string that contains a description of {@code cf}.
     */
    CFStringRef CFCopyDescription(CFTypeRef cf);

    /**
     * Releases a Core Foundation object.
     * <p>
     * If the retain count of {@code cf} becomes zero the memory allocated to the
     * object is deallocated and the object is destroyed. If you create, copy, or
     * explicitly retain (see the {@link #CFRetain} function) a Core Foundation
     * object, you are responsible for releasing it when you no longer need it.
     *
     * @param cf
     *            A {@code CFType} object to release. This value must not be
     *            {@code null}.
     */
    void CFRelease(CFTypeRef cf);

    /**
     * Retains a Core Foundation object. You should retain a Core Foundation object
     * when you receive it from elsewhere (that is, you did not create or copy it)
     * and you want it to persist.
     * <p>
     * If you retain a Core Foundation object you are responsible for releasing it
     * with {@link #CFRelease}.
     *
     * @param cf
     *            The {@code CFType} object to retain. This value must not be
     *            {@code null}.
     * @return The input value, {code cf}.
     */
    CFTypeRef CFRetain(CFTypeRef cf);

    /**
     * Returns the reference count of a Core Foundation object.
     *
     * @param cf
     *            The {@code CFType} object to examine.
     * @return A number representing the reference count of {code cf}.
     */
    CFIndex CFGetRetainCount(CFTypeRef cf);

    /**
     * Returns the value associated with a given key.
     *
     * @param theDict
     *            The dictionary to examine.
     * @param key
     *            The key for which to find a match in {@code theDict}. The key hash
     *            and equal callbacks provided when the dictionary was created are
     *            used to compare. If the hash callback was {@code null}, the key is
     *            treated as a pointer and converted to an integer. If the equal
     *            callback was {@code null}, pointer equality (in C, ==) is used. If
     *            {@code key}, or any of the keys in {@code theDict}, is not
     *            understood by the equal callback, the behavior is undefined.
     * @return The value associated with key in {@code theDict}, or {@code null} if
     *         no key-value pair matching key exists. Since {@code null} is also a
     *         valid value in some dictionaries, use
     *         {@link #CFDictionaryGetValueIfPresent} to distinguish between a value
     *         that is not found, and a {@code null} value.
     */
    Pointer CFDictionaryGetValue(CFDictionaryRef theDict, PointerType key);

    /**
     * Returns a boolean value that indicates whether a given value for a given key
     * is in a dictionary, and returns that value indirectly if it exists.
     *
     * @param theDict
     *            The dictionary to examine.
     * @param key
     *            The key for which to find a match in {@code theDict}. The key hash
     *            and equal callbacks provided when the dictionary was created are
     *            used to compare. If the hash callback was {@code null}, the key is
     *            treated as a pointer and converted to an integer. If the equal
     *            callback was {@code null}, pointer equality (in C, ==) is used. If
     *            {@code key}, or any of the keys in {@code theDict}, is not
     *            understood by the equal callback, the behavior is undefined.
     * @param value
     *            A pointer to memory which, on return, is filled with the
     *            pointer-sized value if a matching key is found. If no key match is
     *            found, the contents of the storage pointed to by this parameter
     *            are undefined. This value may be {@code null}, in which case the
     *            value from the dictionary is not returned (but the return value of
     *            this function still indicates whether or not the key-value pair
     *            was present).
     * @return 1 if a matching key was found, otherwise 0.
     */
    byte CFDictionaryGetValueIfPresent(CFDictionaryRef theDict, PointerType key, PointerByReference value);

    /**
     * Sets the value corresponding to a given key.
     *
     * @param theDict
     *            The dictionary to modify. If this parameter is a fixed-capacity
     *            dictionary and it is full before this operation, and the key does
     *            not exist in the dictionary, the behavior is undefined.
     * @param key
     *            The key of the value to set in {@code theDict}. If a key which
     *            matches {@code key} is already present in the dictionary, only the
     *            value for the key is changed ("add if absent, replace if
     *            present"). If no key matches {@code key}, the key-value pair is
     *            added to the dictionary.
     *            <p>
     *            If a key-value pair is added, both key and value are retained by
     *            the dictionary, using the retain callback provided when
     *            {@code theDict} was created. {@code key} must be of the type
     *            expected by the key retain callback.
     * @param value
     *            The value to add to or replace in {@code theDict}. {@code value}
     *            is retained using the value retain callback provided when
     *            {@code theDict} was created, and the previous value if any is
     *            released. {@code value} must be of the type expected by the retain
     *            and release callbacks.
     */
    void CFDictionarySetValue(CFMutableDictionaryRef theDict, PointerType key, PointerType value);

    /**
     * Copies the character contents of a string to a local C string buffer after
     * converting the characters to a given encoding.
     *
     * @param theString
     *            The string whose contents you wish to access.
     * @param bufferToFill
     *            The C string buffer into which to copy the string. On return, the
     *            buffer contains the converted characters. If there is an error in
     *            conversion, the buffer contains only partial results.
     *            <p>
     *            The buffer must be large enough to contain the converted
     *            characters and a NUL terminator.
     * @param bufferSize
     *            The length of {@code buffer} in bytes.
     * @param encoding
     *            The string encoding to which the character contents of
     *            {@code theString} should be converted. The encoding must specify
     *            an 8-bit encoding.
     * @return 1 upon success or 0 if the conversion fails or the provided buffer is
     *         too small.
     */
    byte CFStringGetCString(CFStringRef theString, Pointer bufferToFill, CFIndex bufferSize, int encoding);

    /**
     * Returns the value of a {@code CFBoolean} object.
     *
     * @param bool
     *            The boolean to examine.
     * @return 1 if the value of {@code bool} is {@code true}, 0 otherwise.
     */
    byte CFBooleanGetValue(CFBooleanRef bool);

    /**
     * Returns the number of values currently in an array.
     *
     * @param theArray
     *            a {@link CFArrayRef} object.
     * @return The number of values in {@code array}.
     */
    CFIndex CFArrayGetCount(CFArrayRef theArray);

    /**
     * Retrieves a value at a given index.
     *
     * @param theArray
     *            The array to examine.
     * @param idx
     *            The index of the value to retrieve. If the index is outside the
     *            index space of {@code theArray} (0 to N-1 inclusive (where N is
     *            the count of {@code theArray})), the behavior is undefined.
     * @return The value at the {@code idx} index in {@code theArray}).
     */
    Pointer CFArrayGetValueAtIndex(CFArrayRef theArray, CFIndex idx);

    /**
     * Returns the type used by a {@code CFNumber} object to store its value.
     *
     * @param number
     *            The {@code CFNumber} object to examine.
     * @return A constant that indicates the data type of the value contained in
     *         number. See {@link CFNumberType} for a list of possible values.
     */
    CFIndex CFNumberGetType(CFNumberRef number);

    /**
     * Obtains the value of a {@code CFNumber} object cast to a specified type.
     *
     * @param number
     *            The {@code CFNumber} object to examine.
     * @param theType
     *            A constant that specifies the data type to return. See
     *            {@link CFNumberType} for a list of possible values.
     * @param valuePtr
     *            On return, contains the value of {@code number}.
     * @return 1 if the operation was successful, otherwise 0.
     */
    byte CFNumberGetValue(CFNumberRef number, CFIndex theType, ByReference valuePtr);

    /**
     * Returns the number (in terms of UTF-16 code pairs) of Unicode characters in a
     * string.
     *
     * @param theString
     *            The string to examine.
     * @return The number (in terms of UTF-16 code pairs) of characters stored in
     *         {@code theString}.
     */
    CFIndex CFStringGetLength(CFStringRef theString);

    /**
     * Returns the maximum number of bytes a string of a specified length (in
     * Unicode characters) will take up if encoded in a specified encoding.
     *
     * @param length
     *            The number of Unicode characters to evaluate.
     * @param encoding
     *            The string encoding for the number of characters specified by
     *            length.
     * @return The maximum number of bytes that could be needed to represent length
     *         number of Unicode characters with the string encoding encoding, or
     *         {@link #kCFNotFound} if the number exceeds {@link Long#MAX_VALUE}.
     */
    CFIndex CFStringGetMaximumSizeForEncoding(CFIndex length, int encoding);

    /**
     * Gets the default allocator object for the current thread.
     *
     * @return A reference to the default allocator for the current thread. If none
     *         has been explicitly set, returns the generic system allocator.
     *         <p>
     *         The default allocator can never be released, so it is not necessary
     *         to {@link #CFRetain} this reference.
     */
    CFAllocatorRef CFAllocatorGetDefault();

    /**
     * Returns the number of bytes contained by a {@code CFData} object.
     *
     * @param theData
     *            The {@code CFData} object to examine.
     * @return An index that specifies the number of bytes in {@code theData}.
     */
    CFIndex CFDataGetLength(CFDataRef theData);

    /**
     * Returns a read-only pointer to the bytes of a {@code CFData} object.
     *
     * @param theData
     *            The {@code CFData} object to examine.
     * @return A read-only pointer to the bytes associated with {@code theData}.
     */
    Pointer CFDataGetBytePtr(CFDataRef theData);

    /**
     * Returns the type of a {@code CFType} object.
     *
     * @param theObject
     *            The {@code CFData} object to examine.
     * @return A value of type {@link CFTypeID} that identifies the opaque type of
     *         {@code cf}.
     */
    CFTypeID CFGetTypeID(CFTypeRef theObject);

    /**
     * @return The type identifier for the {@code CFArray} opaque type.
     */
    CFTypeID CFArrayGetTypeID();

    /**
     * @return The type identifier for the {@code CFBoolean} opaque type.
     */
    CFTypeID CFBooleanGetTypeID();

    /**
     * @return The type identifier for the {@code CFDate} opaque type.
     */
    CFTypeID CFDateGetTypeID();

    /**
     * @return The type identifier for the {@code CFData} opaque type.
     *         <p>
     *         {@code CFMutableData} objects have the same type identifier as
     *         {@code CFData} objects.
     */
    CFTypeID CFDataGetTypeID();

    /**
     * @return The type identifier for the {@code CFDictionary} opaque type.
     *         <p>
     *         {@code CFMutableDictionary} objects have the same type identifier as
     *         {@code CFDictionary} objects.
     */
    CFTypeID CFDictionaryGetTypeID();

    /**
     * @return The type identifier for the {@code CFNumber} opaque type.
     */
    CFTypeID CFNumberGetTypeID();

    /**
     * @return The type identifier for the {@code CFString} opaque type.
     */
    CFTypeID CFStringGetTypeID();
}

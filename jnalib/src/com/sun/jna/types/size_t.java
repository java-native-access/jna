package com.sun.jna.types;

import com.sun.jna.IntegerType;
import com.sun.jna.Pointer;

/** Standard POSIX size_t type. */
public class size_t extends IntegerType {
    public size_t() { this(0); }
    public size_t(long value) { super(Pointer.SIZE, value); }
}

package com.sun.jna.types;

import com.sun.jna.IntegerType;
import com.sun.jna.Pointer;

/** Standard POSIX off_t type. */
public class off_t extends IntegerType {
    public off_t() { this(0); }
    public off_t(long value) { super(Pointer.SIZE, value); }
}

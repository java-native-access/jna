Using ByReference Arguments
===========================

When a function accepts a pointer-to-type argument you can use one of the `ByReference` types to capture the returned value, or subclass your own. For example:
 
    // Original C declaration
    void allocate_buffer(char **bufp, int* lenp);

    // Equivalent JNA mapping
    void allocate_buffer(PointerByReference bufp, IntByReference lenp);

    // Usage
    PointerByReference pref = new PointerByReference();
    IntByReference iref = new IntByReference();
    lib.allocate_buffer(pref, iref);
    Pointer p = pref.getValue();
    byte[] buffer = p.getByteArray(0, iref.getValue());

Alternatively, you could use a Java array with a single element of the desired type, but the `ByReference` convention better conveys the intent of the code. The `Pointer` class provides a number of accessor methods in addition to `getByteArray()` which effectively function as a typecast onto the memory.

Type-safe pointers may be declared by deriving from the `PointerType` class.


Using Pointers and Arrays
=========================

Primitive array arguments (including structs) are represented by their corresponding Java types. For example:
 
    // Original C declarations
    void fill_buffer(int *buf, int len);
    void fill_buffer(int buf[], int len); // same thing with array syntax

    // Equivalent JNA mapping
    void fill_buffer(int[] buf, int len);

NOTE: If the parameter is to be used by the native function outside the scope of the function call, you must use Memory or an NIO Buffer. The memory provided by a Java primitive array will only be valid for use by the native code for the duration of the function call.

Arrays of C strings (the `char* argv[]` to the C `main`, for example), may be represented by `String[]` in Java code. JNA will automatically pass an equivalent array with a `NULL` final element.


Using Structures And Unions
===========================

When a function requires a pointer to a struct, a Java Structure should be used. If the struct is passed or returned by value, you need only make minor modifications to the parameter or return type class declaration.

Typically you define a public static class derived from `Structure` within your library interface definition. This allows the structure to share any options (like custom type mapping) defined for the library interface.

If a function requires an array of struct (allocated contiguously in memory), a Java `Structure[]` may be used. When passing in an array of `Structure`, it is not necessary to initialize the array elements (the function call will allocate, zero memory, and assign the elements for you). If you do need to initialize the array, you should use the `Structure.toArray` method to obtain an array of Structure elements contiguous in memory, which you can then initialize as needed.

Unions are generally interchangeable with Structures, but require that you indicate which union field is active with the `setType` method before it can be properly passed to a function call.



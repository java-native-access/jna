Customized Mapping from Java to Native
======================================

The `TypeMapper` class and related interfaces provide for converting any Java type used as an argument, return value, or structure member to be converted to or from a native type. The example Win32 API interfaces use a type mapper to convert Java boolean into the Win32 BOOL type. A TypeMapper instance is passed as the value for the `TYPE_MAPPER` key in the options map passed to `Native.loadLibrary`.

Alternatively, user-defined types may implement the `NativeMapped` interface, which determines conversion to and from native types on a class-by-class basis.

You may also customize the mapping of Java method names to the corresponding native function name. The `StdCallFunctionMapper` is one implementation which automatically generates stdcall-decorated function names from a Java interface method signature. The mapper should be passed as the value for the `OPTION_FUNCTION_MAPPER` key in the options map passed to the `Native.loadLibrary` call.


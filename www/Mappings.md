Default Type Mappings
=====================

Java primitive types (and their object equivalents) map directly to the native C type of the same size.

<table>
<thead><td>Native Type</td><td>Size</td><td>Java Type</td><td>Common Windows Types</td></thead>
<tr><td>char</td><td>8-bit integer</td><td>byte</td><td>BYTE, TCHAR</td></tr>
<tr><td>short</td><td>16-bit integer</td><td>short</td><td>WORD</td></tr>
<tr><td>wchar_t</td><td>16/32-bit character</td><td>char</td><td>TCHAR</td></tr>
<tr><td>int</td><td>32-bit integer</td><td>int</td><td>DWORD</td></tr>
<tr><td>int</td><td>boolean value</td><td>boolean</td><td>BOOL</td></tr>
<tr><td>long</td><td>32/64-bit integer</td><td>NativeLong</td><td>LONG</td></tr>
<tr><td>long long</td><td>64-bit integer</td><td>long</td><td>__int64</td></tr>
<tr><td>float</td><td>32-bit FP</td><td>float</td><td></td></tr>
<tr><td>double</td><td>64-bit FP</td><td>double</td><td></td></tr>
<tr><td>char*</td><td>C string</td><td>String</td><td>LPTCSTR</td></tr>
<tr><td>void*</td><td>pointer</td><td>Pointer</td><td>LPVOID, HANDLE, LP<i>XXX</i></td></tr>
</table>

Unsigned types use the same mappings as signed types. C enums are usually interchangeable with "int".


Default Type Mappings
=====================

Java primitive types (and their object equivalents) map directly to the native C type of the same size.

Native Type|Size|Java Type|Common Windows Types
char|8-bit integer|byte|BYTE, TCHAR
short|16-bit integer|short|WORD
wchar_t|16/32-bit character|char|TCHAR
int|32-bit integer|int|DWORD
int|boolean value|boolean|BOOL
long|32/64-bit integer|NativeLong|LONG
long long|64-bit integer|long|__int64
float|32-bit FP|float|
double|64-bit FP|double|
char*|C string|String|LPTCSTR
void*|pointer|Pointer|LPVOID, HANDLE, LPXXX

Unsigned types use the same mappings as signed types. C enums are usually interchangeable with "int".


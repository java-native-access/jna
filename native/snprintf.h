#ifndef _SNPRINTF_H
#define _SNPRINTF_H
// snprintf on windows is broken; always nul-terminate manually
// DO NOT rely on the return value...
static int snprintf(char * str, size_t size, const char * format, ...) {
  int retval;
  va_list ap;
  va_start(ap, format);
  retval = _vsnprintf_s(str, size, _TRUNCATE, format, ap);
  va_end(ap);
  return retval;
}
#endif /* _SNPRINTF_H */

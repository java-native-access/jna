#include <stdarg.h>
#include <stdio.h>
#include <string.h>
#include <limits.h>
#include <stdlib.h>

#include <tchar.h>

#define BUFSIZE_0 1024

#ifdef _UNICODE
#define __VSNTPRINTF__ vsnwprintf
#define __SNTPRINTF__ snwprintf
#else
#define __VSNTPRINTF__ vsnprintf
#define __SNTPRINTF__ snprintf
#endif

int
__VSNTPRINTF__ (_TCHAR* s,  size_t n,  const _TCHAR *format, va_list ap)
{ 
  int res;
  _TCHAR*  tmpbuf;
  size_t bufsize = n;

  /* If the format string is empty, nothing to do.  */
  if (__builtin_expect ((strlen (format) == 0), 0))
    return 0;
 
   /* The supplied count may be big enough. Try to use the library
     _vsntprintf, fixing up the case where the library function
     neglects to terminate with '/0'.  */ 	
  if (n > 0)
    {
      /* A NULL destination will cause a segfault with _vsnprintf.
         if n > 0.  Nor do we want to copy our tmpbuf to NULL later. */ 
      if (!s)
	return -1;
      res = _vsntprintf (s, n, format, ap);
      if (res > 0)
        { 
          if ((unsigned) res == n)  
            s[res - 1] = 0;
	  return res;
	}
       /* If n is already larger than INT_MAX, increasing it won't
          help.  */
       if (n > INT_MAX)
          return -1;

       /* Try a larger buffer.  */ 
       bufsize *= 2;
    }

  if (bufsize < BUFSIZE_0)
    bufsize = BUFSIZE_0;
  tmpbuf  = (_TCHAR *) malloc (bufsize * sizeof (_TCHAR));
  if (!tmpbuf)
    return -1;

  res = _vsntprintf (tmpbuf, bufsize, format, ap);

  /* The test for bufsize limit is probably not necesary
     with 2GB address space iimit, since, in practice, malloc will
     fail well before INT_MAX.  */
  while (res < 0 && bufsize <= INT_MAX)
  {
    _TCHAR * newbuf;
    bufsize *= 2;
    newbuf = (_TCHAR*) realloc (tmpbuf, bufsize * sizeof (_TCHAR));
    if (!newbuf)
      break;
    tmpbuf = newbuf;
    res = _vsntprintf (tmpbuf, bufsize, format,ap);
  }

  if (res > 0 && n > 0)
    {
      if (n > (unsigned) res)
	memcpy (s, tmpbuf, (res + 1) * sizeof (_TCHAR));
      else
        {
	  memcpy (s, tmpbuf, (n - 1) * sizeof (_TCHAR));
	  s[n - 1] = 0;
	}
    }

  free (tmpbuf);
  return res;
}

int __SNTPRINTF__(_TCHAR* s, size_t n, const _TCHAR* format, ...)
{
  int res;
  va_list ap;
         
  va_start (ap, format);
  res = __VSNTPRINTF__ (s, n, format, ap);
  va_end (ap);
  return res;
}

#ifdef TEST
int main(void)
{
char string[10];
char string2[10];
char string3[12];
char bigstring [1024 * 8];
int rv;

rv = snprintf(string, sizeof(string), "%s", "longer than ten");
printf("[%s] (%d)\n", string, rv);

rv = snprintf(string2, sizeof(string2), "%s", "shorter");
printf("[%s] (%d)\n", string2, rv);

rv = snprintf(string3, sizeof(string3), "%s%d%s", "longer", 7777, "than ten");
printf("[%s] (%d)\n", string3, rv);

rv = snprintf(string, 0, "%s%d%s", "longer", 7777, "than ten");
printf("[%s] (%d)\n", string, rv);

memset (bigstring, 'x', sizeof (bigstring)); 
bigstring [sizeof (bigstring) - 1] = 0;
rv = snprintf(NULL, 0, "%s", bigstring);
printf("[%s] (%d)\n", string, rv);

return 0;
}
#endif

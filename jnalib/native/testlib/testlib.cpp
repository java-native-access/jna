// testlib.cpp : Defines the entry point for the DLL application.

#define WIN32_LEAN_AND_MEAN		// Exclude rarely-used stuff from Windows headers

#define TESTLIB_API extern "C" __declspec(dllexport)

#include <windows.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include <stdio.h>




////////////////////////////////////////////////////////////////////////////////
// Declarations
////////////////////////////////////////////////////////////////////////////////

struct MATHOP
{
	int value1;
	int value2;
	char* message;
};




////////////////////////////////////////////////////////////////////////////////
// Functions
////////////////////////////////////////////////////////////////////////////////

BOOL APIENTRY DllMain( HANDLE hModule, 
                       DWORD  ul_reason_for_call, 
                       LPVOID lpReserved)
{
    switch (ul_reason_for_call)
	{
		case DLL_PROCESS_ATTACH:
		case DLL_THREAD_ATTACH:
		case DLL_THREAD_DETACH:
		case DLL_PROCESS_DETACH:
			break;
    }
    return TRUE;
}

TESTLIB_API void doNothing()
{
	return;
}

TESTLIB_API void doNothingLoop()
{
	int i;
	for (i=0; i<1000000; i++)
	{
	}
}

TESTLIB_API void callClock()
{
	clock();
}

TESTLIB_API void loopClock()
{
	int i;
	for (i=0; i<1000000; i++)
	{
		clock();
	}
}

TESTLIB_API BOOL returnTrue()
{
	return TRUE;
}

TESTLIB_API BOOL returnFalse()
{
	return FALSE;
}

TESTLIB_API int returnInt()
{
	return 42;
}

TESTLIB_API double returnDouble()
{
	return 42.0;
}

TESTLIB_API char* returnString()
{
	return "An adventurer is you!";
}


TESTLIB_API BOOL not(BOOL value)
{
	return value==TRUE ? FALSE : TRUE;
}

TESTLIB_API int add(int value1, int value2)
{
	return value1+value2;
}

TESTLIB_API void print(char* value)
{
	printf("\n<output from printf(): %s>\n",value);
}

TESTLIB_API void addAndPrint(MATHOP* op)
{
	printf("Before addition\n");
	int result=op->value1 + op->value2;
	printf("\nNative code sum: %i\n",result);
	printf(op->message,result);
}


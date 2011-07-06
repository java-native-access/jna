Callbacks and Closures
======================

Callback declarations consist of a simple interface that extends the Callback interface and implements a callback method (or defines a single method of arbitrary name). Callbacks are implemented by wrapping a Java object method in a little bit of C glue code. The simplest usage resembles using anonymous inner classes to register event listeners. Following is an example of callback usage:

    // Original C declarations
    typedef void (*sig_t) (int);
    sig_t signal(int sig, sig_t func);
    int SIGUSR1 = 30;

    // Equivalent JNA mappings
    public interface CLibrary extends Library {
        int SIGUSR1 = 30;
        interface sig_t extends Callback {
            void invoke(int signal);
        }
        sig_t signal(int sig, sig_t fn);
        int raise(int sig);
    }
    ...
    CLibrary lib = (CLibrary)Native.loadLibrary("c", CLibrary.class);
    // WARNING: you must keep a reference to the callback object
    // until you deregister the callback; if the callback object
    // is garbage-collected, the native callback invocation will
    // probably crash.
    CLibrary.sig_t fn = new CLibrary.sig_t() {
        public void invoke(int sig) {
            System.out.println("signal " + sig + " was raised");
        }
    };
    CLibrary.sig_t old_handler = lib.signal(CLibrary.SIGUSR1, fn);
    lib.raise(CLibrary.SIGUSR1);
    ...

Here is a more involved example, using the Win32 APIs to enumerate all native windows:

    // Original C declarations
    typedef int (__stdcall *WNDENUMPROC)(void*,void*);
    int __stdcall EnumWindows(WNDENUMPROC,void*);

    // Equivalent JNA mappings
    public interface User32 extends StdCallLibrary {
        interface WNDENUMPROC extends StdCallCallback {
            /** Return whether to continue enumeration. */
            boolean callback(Pointer hWnd, Pointer arg);
        }
        boolean EnumWindows(WNDENUMPROC lpEnumFunc, Pointer arg);
    }
    ...
    User32 user32 = User32.INSTANCE;

    user32.EnumWindows(new WNDENUMPROC() {
        int count;
        public boolean callback(Pointer hWnd, Pointer userData) {
            System.out.println("Found window " + hWnd + ", total " + ++count);
            return true;
        }
    }, null);

If your callback needs to live beyond the method invocation where it is used, make sure you keep a reference to it or the native code will call back to an empty stub after the callback object is garbage collected.

Proxy wrappers are automatically generated for function pointers found within structs initialized by native code. This facilitates calling those functions from Java.


Direct Mapping
==============

JNA supports a direct mapping method which can improve performance substantially, approaching that of custom JNI. Method signatures are the same as they would be in a JNA interface mapping, but they can be any static or object methods. You only need register them within the static initializer of the defining class, as in the example below. The `Native.register()` method takes the name of your native library, the same as Native.loadLibrary() would.

    import com.sun.jna.*;

    public class HelloWorld {

        static {
            Native.register(Platform.isWindows() ? "msvcrt" : "m");
        }

        public static native double cos(double);
        public static native double sin(double);

        public static void main(String[] args) {
            System.out.println("cos(0)=" + cos(0));
            System.out.println("sin(0)=" + sin(0));
        }
    }

Direct mapping supports the same type mappings as interface mapping, except for arrays of Pointer/String/WString/NativeMapped as function arguments. You can easily convert from interface mapping to direct mapping by creating a direct mapping class which implements your library interface, with all methods defined as native methods. Then your library instance variable can be assigned an instance of this new class instead of the object returned by `Native.loadLibrary()`.


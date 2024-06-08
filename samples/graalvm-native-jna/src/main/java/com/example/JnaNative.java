package com.example;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;

public final class JnaNative {
    public interface CLibrary extends Library {
        CLibrary INSTANCE = (CLibrary)
            Native.load((Platform.isWindows() ? "msvcrt" : "c"),
                                CLibrary.class);

        void printf(String format, Object... args);
    }

    public static void main(String[] args) {
        System.out.println("Hello, JNA!");
        for (int i=0;i < args.length;i++) {
            CLibrary.INSTANCE.printf("Argument %d: %s\n", i, args[i]);
        }
    }
}

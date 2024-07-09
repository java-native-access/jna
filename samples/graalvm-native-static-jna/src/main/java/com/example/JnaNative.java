/* Copyright (c) 2007-2015 Timothy Wall, All Rights Reserved
 *
 * The contents of this file is dual-licensed under 2
 * alternative Open Source/Free licenses: LGPL 2.1 or later and
 * Apache License 2.0. (starting with JNA version 4.0.0).
 *
 * You can freely decide which license you want to apply to
 * the project.
 *
 * You may obtain a copy of the LGPL License at:
 *
 * http://www.gnu.org/licenses/licenses.html
 *
 * A copy is also included in the downloadable source code package
 * containing JNA, in file "LGPL2.1".
 *
 * You may obtain a copy of the Apache License at:
 *
 * http://www.apache.org/licenses/
 *
 * A copy is also included in the downloadable source code package
 * containing JNA, in file "AL2.0".
 */
package com.example;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;

public final class JnaNative {
    static {
        System.loadLibrary("jnidispatch");
    }

    public interface CLibrary extends Library {
        CLibrary INSTANCE = (CLibrary)
            Native.load((Platform.isWindows() ? "msvcrt" : "c"),
                                CLibrary.class);

        void puts(String value);
    }

    public static void main(String[] args) {
        System.out.println("Hello, JNA!");
        CLibrary.INSTANCE.puts("Hello from C!");
    }
}

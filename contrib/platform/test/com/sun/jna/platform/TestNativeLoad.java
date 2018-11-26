
package com.sun.jna.platform;

import com.sun.jna.NativeLibrary;

public class TestNativeLoad {
    public static void main(String[] args) {
        System.setProperty("jna.debug_load", "true");
        NativeLibrary.addSearchPath("test", "/home/matthias/src/jnalib/");
        NativeLibrary.getInstance("test");
    }
}

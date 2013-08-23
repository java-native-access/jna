package com.sun.jna.platform.win32.COM;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface DISP_ID {
    int value();
}

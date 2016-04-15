package com.sun.jna.platform.win32.COM.util.office.excel;

import com.sun.jna.platform.win32.COM.util.annotation.ComInterface;
import com.sun.jna.platform.win32.COM.util.annotation.ComMethod;
import com.sun.jna.platform.win32.COM.util.annotation.ComProperty;

@ComInterface
public interface Shape {

    @ComProperty
    int getTop();

    @ComProperty
    void setTop(int value);

    @ComProperty
    int getLeft();

    @ComProperty
    void setLeft(int value);
}

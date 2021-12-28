/* Copyright (c) 2021 Mo Beigi, All Rights Reserved
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
package com.sun.jna.platform.win32;

import com.sun.jna.Memory;
import com.sun.jna.Native;

/**
 * {@link Oleacc} utility API
 *
 * @author Mo Beigi, me@mobeigi.org
 */
public abstract class OleaccUtil
{
    /**
     * Retrieves the localized string that describes the object's role for the specified role value.
     * This method will allocate the correct memory based on if you are using the ANSI or UNICODE win32 API.
     *
     * @see <a href="https://docs.microsoft.com/en-us/windows/win32/api/oleacc/nf-oleacc-getroletexta">GetRoleTextA function (oleacc.h)</a>
     * @see <a href="https://docs.microsoft.com/en-us/windows/win32/api/oleacc/nf-oleacc-getroletextw">GetRoleTextW function (oleacc.h)</a>
     *
     * @param iRole One of the object role constants.
     * @return The role text string.
     * @throws Win32Exception Error populated from GetLastError
     */
    public static String GetRoleText(int iRole) throws Win32Exception {
        int result = Oleacc.INSTANCE.GetRoleText(iRole, null, 0);
        if (result == 0) {
            throw new Win32Exception(Native.getLastError());
        }
        int charToBytes = Boolean.getBoolean("w32.ascii") ? 1 : Native.WCHAR_SIZE;
        Memory memory = new Memory((long) (result + 1) * charToBytes); // plus 1 for null terminator
        int result2 = Oleacc.INSTANCE.GetRoleText(iRole, memory, result + 1);
        if (result2 == 0) {
            throw new Win32Exception(Native.getLastError());
        }

        if (Boolean.getBoolean("w32.ascii")) {
            return memory.getString(0);
        } else {
            return memory.getWideString(0);
        }
    }
}

/* Copyright (c) 2015 Goldstein Lyor, All Rights Reserved
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package com.sun.jna.platform.unix;

/**
 * Contains definitions related to the {@code reboot} API
 * @author Lyor Goldstein
 */
public interface Reboot {
    /** Perform a hard reset now.  */
    int RB_AUTOBOOT = 0x01234567;
    /* Halt the system.  */
    int RB_HALT_SYSTEM = 0xcdef0123;
    /** Enable reboot using Ctrl-Alt-Delete keystroke.  */
    int RB_ENABLE_CAD = 0x89abcdef;
    /** Disable reboot using Ctrl-Alt-Delete keystroke.  */
    int RB_DISABLE_CAD = 0;
    /** Stop system and switch power off if possible.  */
    int RB_POWER_OFF = 0x4321fedc;
    /** Suspend system using software suspend.  */
    int RB_SW_SUSPEND = 0xd000fce2;
    /** Reboot system into new kernel.  */
    int RB_KEXEC = 0x45584543;

    /**
     * Stops/Reboots the machine
     * @param cmd The command
     * @return If successful, this call never returns.  Otherwise, a -1
     * is returned and an error is returned in the global variable {@code errno}.
     * @see <A HREF="http://www.unix.com/man-page/freebsd/2/reboot/">man 2 reboot</A>
     */
    int reboot(int cmd);
}

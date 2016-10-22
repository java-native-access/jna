/* Copyright (c) 2015 Goldstein Lyor, All Rights Reserved
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

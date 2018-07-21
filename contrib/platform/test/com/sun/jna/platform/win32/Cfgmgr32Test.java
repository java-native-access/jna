/* Copyright (c) 2018 Daniel Widdis, All Rights Reserved
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;

import org.junit.Test;

import com.sun.jna.ptr.IntByReference;

/**
 * Tests methods in Cfgmgr32
 * 
 * @author widdis[at]gmail[dot]com
 */
public class Cfgmgr32Test {
    /**
     * Tests CM_Locate_DevNode, CM_Get_Parent, CM_Get_Child, CM_Get_Sibling
     */
    @Test
    public void testDevNode() {
        // Fetch the root node
        IntByReference outputNode = new IntByReference();
        assertEquals(Cfgmgr32.CR_SUCCESS,
                Cfgmgr32.INSTANCE.CM_Locate_DevNode(outputNode, null, Cfgmgr32.CM_LOCATE_DEVNODE_NORMAL));
        // Get first child
        int rootNode = outputNode.getValue();
        int inputNode = rootNode;
        assertEquals(Cfgmgr32.CR_SUCCESS, Cfgmgr32.INSTANCE.CM_Get_Child(outputNode, inputNode, 0));
        // Iterate this child and its siblings
        do {
            inputNode = outputNode.getValue();
            // Get parent, confirm it matches root
            assertEquals(Cfgmgr32.CR_SUCCESS, Cfgmgr32.INSTANCE.CM_Get_Parent(outputNode, inputNode, 0));
            assertEquals(rootNode, outputNode.getValue());
        } while (Cfgmgr32.CR_SUCCESS == Cfgmgr32.INSTANCE.CM_Get_Sibling(outputNode, inputNode, 0));
    }

    /**
     * Tests CM_Locate_DevNode, CM_Get_Device_ID_Size, CM_Get_Device_ID
     * 
     * @throws UnsupportedEncodingException
     */
    @Test
    public void testDeviceID() {
        // Fetch the root node
        IntByReference outputNode = new IntByReference();
        assertEquals(Cfgmgr32.CR_SUCCESS,
                Cfgmgr32.INSTANCE.CM_Locate_DevNode(outputNode, null, Cfgmgr32.CM_LOCATE_DEVNODE_NORMAL));
        int rootNode = outputNode.getValue();

        // Get Device ID character count
        IntByReference pulLen = new IntByReference();
        Cfgmgr32.INSTANCE.CM_Get_Device_ID_Size(pulLen, rootNode, 0);
        assertTrue(pulLen.getValue() > 0);

        // Get Device ID from util
        String deviceId = Cfgmgr32Util.CM_Get_Device_ID(rootNode);
        assertEquals(pulLen.getValue(), deviceId.length());

        // Look up node from device ID
        assertEquals(Cfgmgr32.CR_SUCCESS,
                Cfgmgr32.INSTANCE.CM_Locate_DevNode(outputNode, deviceId, Cfgmgr32.CM_LOCATE_DEVNODE_NORMAL));
        assertEquals(rootNode, outputNode.getValue());
    }
}

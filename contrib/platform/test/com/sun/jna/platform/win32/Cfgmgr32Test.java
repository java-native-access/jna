/* Copyright (c) 2018, 2021 Daniel Widdis, All Rights Reserved
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

import static com.sun.jna.platform.win32.Cfgmgr32.CM_DRP_CONFIGFLAGS;
import static com.sun.jna.platform.win32.Cfgmgr32.CM_DRP_DEVICEDESC;
import static com.sun.jna.platform.win32.Cfgmgr32.CM_DRP_DEVICE_POWER_DATA;
import static com.sun.jna.platform.win32.Cfgmgr32.CM_DRP_HARDWAREID;
import static com.sun.jna.platform.win32.Cfgmgr32.CM_LOCATE_DEVNODE_NORMAL;
import static com.sun.jna.platform.win32.Cfgmgr32.CR_SUCCESS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.util.ArrayDeque;
import java.util.Queue;

import org.junit.Test;

import com.sun.jna.ptr.IntByReference;

import static com.sun.jna.platform.win32.Cfgmgr32.CR_INVALID_DEVNODE;
import static com.sun.jna.platform.win32.Cfgmgr32.CR_INVALID_PROPERTY;

/**
 * Tests methods in Cfgmgr32
 */
public class Cfgmgr32Test {
    private static final Cfgmgr32 CFG = Cfgmgr32.INSTANCE;

    /**
     * Tests CM_Locate_DevNode, CM_Get_Parent, CM_Get_Child, CM_Get_Sibling
     */
    @Test
    public void testDevNode() {
        // Fetch the root node
        IntByReference outputNode = new IntByReference();
        assertEquals(CR_SUCCESS, CFG.CM_Locate_DevNode(outputNode, null, CM_LOCATE_DEVNODE_NORMAL));
        // Get first child
        int rootNode = outputNode.getValue();
        int inputNode = rootNode;
        assertEquals(CR_SUCCESS, CFG.CM_Get_Child(outputNode, inputNode, 0));
        // Iterate this child and its siblings
        do {
            inputNode = outputNode.getValue();
            // Get parent, confirm it matches root
            assertEquals(CR_SUCCESS, CFG.CM_Get_Parent(outputNode, inputNode, 0));
            assertEquals(rootNode, outputNode.getValue());
        } while (CR_SUCCESS == CFG.CM_Get_Sibling(outputNode, inputNode, 0));
    }

    /**
     * Tests CM_Locate_DevNode, CM_Get_Device_ID_Size, CM_Get_Device_ID
     *
     * @throws UnsupportedEncodingException
     */
    @Test
    public void testDeviceId() {
        // Fetch the root node
        IntByReference outputNode = new IntByReference();
        assertEquals(CR_SUCCESS, CFG.CM_Locate_DevNode(outputNode, null, CM_LOCATE_DEVNODE_NORMAL));
        int rootNode = outputNode.getValue();

        // Get Device ID character count
        IntByReference pulLen = new IntByReference();
        CFG.CM_Get_Device_ID_Size(pulLen, rootNode, 0);
        assertTrue(pulLen.getValue() > 0);

        // Get Device ID from util
        String deviceId = Cfgmgr32Util.CM_Get_Device_ID(rootNode);
        assertEquals(pulLen.getValue(), deviceId.length());

        // Look up node from device ID
        assertEquals(CR_SUCCESS, CFG.CM_Locate_DevNode(outputNode, deviceId, CM_LOCATE_DEVNODE_NORMAL));
        assertEquals(rootNode, outputNode.getValue());
    }

    /**
     * Tests CM_Get_DevNode_Registry_Property util
     */
    @Test
    public void testDeviceProperties() {
        Object props;

        // Test an invalid node
        try {
            props = Cfgmgr32Util.CM_Get_DevNode_Registry_Property(-1, CM_DRP_DEVICEDESC);
            assertTrue("Should not be reached - method is expected to raise a Cfgmgr32Exception", false);
        } catch (Cfgmgr32Util.Cfgmgr32Exception ex) {
            assertEquals(CR_INVALID_DEVNODE, ex.getErrorCode());
        }

        // Not all devices have all properties and will fail with CR_NO_SUCH_VALUE.
        // So do BFS of device tree and run tests on all devices until we've tested each
        boolean descTested = false;
        boolean hwidTested = false;
        boolean flagsTested = false;
        boolean powerTested = false;

        // Fetch the root node
        IntByReference outputNode = new IntByReference();
        assertEquals(CR_SUCCESS, CFG.CM_Locate_DevNode(outputNode, null, CM_LOCATE_DEVNODE_NORMAL));
        int node = outputNode.getValue();

        // Navigate the device tree using BFS
        Queue<Integer> deviceQueue = new ArrayDeque<>();
        IntByReference child = new IntByReference();
        IntByReference sibling = new IntByReference();
        // Initialize queue with root node
        deviceQueue.add(node);
        while (!deviceQueue.isEmpty()) {
            // Process the next device in the queue
            node = deviceQueue.poll();

            // Run tests
            props = Cfgmgr32Util.CM_Get_DevNode_Registry_Property(node, CM_DRP_DEVICEDESC);
            if (props != null) {
                assertTrue(props instanceof String);
                descTested = true;
            }
            props = Cfgmgr32Util.CM_Get_DevNode_Registry_Property(node, CM_DRP_HARDWAREID);
            if (props != null) {
                assertTrue(props instanceof String[]);
                hwidTested = true;
            }
            props = Cfgmgr32Util.CM_Get_DevNode_Registry_Property(node, CM_DRP_CONFIGFLAGS);
            if (props != null) {
                assertTrue(props instanceof Integer);
                flagsTested = true;
            }
            props = Cfgmgr32Util.CM_Get_DevNode_Registry_Property(node, CM_DRP_DEVICE_POWER_DATA);
            if (props != null) {
                assertTrue(props instanceof byte[]);
                powerTested = true;
            }
            // Test an invalid type
            try {
                props = Cfgmgr32Util.CM_Get_DevNode_Registry_Property(node, 0);
                assertTrue("Should not be reached - method is expected to raise a Cfgmgr32Exception", false);
            } catch (Cfgmgr32Util.Cfgmgr32Exception ex) {
                assertEquals(CR_INVALID_PROPERTY, ex.getErrorCode());
            }

            // If we've done all tests we can exit the loop
            if (descTested && hwidTested && flagsTested && powerTested) {
                break;
            }

            // If not done, add any children to the queue
            if (CR_SUCCESS == CFG.CM_Get_Child(child, node, 0)) {
                deviceQueue.add(child.getValue());
                while (CR_SUCCESS == CFG.CM_Get_Sibling(sibling, child.getValue(), 0)) {
                    deviceQueue.add(sibling.getValue());
                    child.setValue(sibling.getValue());
                }
            }
        }
        assertTrue(descTested);
        assertTrue(hwidTested);
        assertTrue(flagsTested);
        assertTrue(powerTested);
    }
}

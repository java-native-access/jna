/* Copyright (c) 2018,2020,2021 Daniel Widdis, All Rights Reserved
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

import static com.sun.jna.platform.win32.IPHlpAPI.AF_INET;
import static com.sun.jna.platform.win32.IPHlpAPI.AF_INET6;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.regex.Pattern;

import org.junit.Test;

import com.sun.jna.Memory;
import com.sun.jna.platform.win32.IPHlpAPI.FIXED_INFO;
import com.sun.jna.platform.win32.IPHlpAPI.MIB_IFROW;
import com.sun.jna.platform.win32.IPHlpAPI.MIB_IF_ROW2;
import com.sun.jna.platform.win32.IPHlpAPI.MIB_TCP6ROW_OWNER_PID;
import com.sun.jna.platform.win32.IPHlpAPI.MIB_TCP6TABLE_OWNER_PID;
import com.sun.jna.platform.win32.IPHlpAPI.MIB_TCPROW_OWNER_PID;
import com.sun.jna.platform.win32.IPHlpAPI.MIB_TCPSTATS;
import com.sun.jna.platform.win32.IPHlpAPI.MIB_TCPTABLE_OWNER_PID;
import com.sun.jna.platform.win32.IPHlpAPI.MIB_TCP_STATE;
import com.sun.jna.platform.win32.IPHlpAPI.MIB_UDP6TABLE_OWNER_PID;
import com.sun.jna.platform.win32.IPHlpAPI.MIB_UDPSTATS;
import com.sun.jna.platform.win32.IPHlpAPI.MIB_UDPTABLE_OWNER_PID;
import com.sun.jna.platform.win32.IPHlpAPI.TCP_TABLE_CLASS;
import com.sun.jna.platform.win32.IPHlpAPI.UDP_TABLE_CLASS;
import com.sun.jna.ptr.IntByReference;

public class IPHlpAPITest {
    private static final IPHlpAPI IPHLP = IPHlpAPI.INSTANCE;

    @Test
    public void testGetIfEntry() throws SocketException {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        for (NetworkInterface netint : Collections.list(interfaces)) {
            if (!netint.isLoopback() && netint.getHardwareAddress() != null) {
                // Create new MIB_IFROW, set index to this interface index
                MIB_IFROW ifRow = new MIB_IFROW();
                ifRow.dwIndex = netint.getIndex();
                assertEquals(WinError.NO_ERROR, IPHLP.GetIfEntry(ifRow));
                // These originate from unsigned ints. Use standard Java
                // widening conversion to long which does sign-extension,
                // then drop any copies of the sign bit, to prevent the value
                // being considered a negative one by Java if it is set
                long bytesSent = ifRow.dwOutOctets & 0xffffffffL;
                long packetsSent = ifRow.dwOutUcastPkts & 0xffffffffL;
                long bytesRecv = ifRow.dwInOctets & 0xffffffffL;
                long packetsRecv = ifRow.dwInUcastPkts & 0xffffffffL;
                // Bytes should match or exceed minimum packet size of 20.
                // It is possible to have bytes not part of a packet but not vice versa.
                assertTrue(bytesSent >= packetsSent * 20L);
                assertTrue(bytesRecv >= packetsRecv * 20L);
            }
        }

    }

    @Test
    public void testGetIfEntry2() throws SocketException {
        byte majorVersion = Kernel32.INSTANCE.GetVersion().getLow().byteValue();
        if (majorVersion >= 6) {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface netint : Collections.list(interfaces)) {
                if (!netint.isLoopback() && netint.getHardwareAddress() != null) {
                    // Create new MIB_IF_ROW2, set index to this interface index
                    MIB_IF_ROW2 ifRow = new MIB_IF_ROW2();
                    ifRow.InterfaceIndex = netint.getIndex();
                    assertEquals(WinError.NO_ERROR, IPHlpAPI.INSTANCE.GetIfEntry2(ifRow));
                    // Bytes should exceed packets
                    // These originate from unsigned longs.
                    BigInteger bytesSent = new BigInteger(Long.toHexString(ifRow.OutOctets), 16);
                    BigInteger packetsSent = new BigInteger(Long.toHexString(ifRow.OutUcastPkts), 16);
                    BigInteger bytesRecv = new BigInteger(Long.toHexString(ifRow.InOctets), 16);
                    BigInteger packetsRecv = new BigInteger(Long.toHexString(ifRow.InUcastPkts), 16);
                    BigInteger minPacketSize = BigInteger.valueOf(20);
                    assertNotEquals(-1, bytesSent.compareTo(packetsSent.multiply(minPacketSize)));
                    assertNotEquals(-1, bytesRecv.compareTo(packetsRecv.multiply(minPacketSize)));
                }
            }
        } else {
            System.err.println("testGetIfEntery2 test can only be run on Vista or later.");
        }
    }

    @Test
    public void testGetNetworkParams() {
        Pattern ValidIP = Pattern.compile("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

        IntByReference bufferSize = new IntByReference();
        assertEquals(WinError.ERROR_BUFFER_OVERFLOW, IPHlpAPI.INSTANCE.GetNetworkParams(null, bufferSize));
        Memory buffer = new Memory(bufferSize.getValue());
        assertEquals(WinError.ERROR_SUCCESS, IPHlpAPI.INSTANCE.GetNetworkParams(buffer, bufferSize));
        FIXED_INFO fixedInfo = new FIXED_INFO(buffer);

        // Check all DNS servers are valid IPs
        IPHlpAPI.IP_ADDR_STRING dns = fixedInfo.DnsServerList;
        while (dns != null) {
            // Start with 16-char byte array
            String addr = new String(dns.IpAddress.String);
            // addr String will have length 16; trim off trailing null(s)
            int nullPos = addr.indexOf(0);
            if (nullPos != -1) {
                addr = addr.substring(0, nullPos);
            }
            // addr is now a dotted-notation IP string. Test valid
            assertTrue(ValidIP.matcher(addr).matches());
            dns = dns.Next;
        }
    }

    @Test
    public void testGetTcpStatistics() {
        MIB_TCPSTATS stats = new MIB_TCPSTATS();
        int err = IPHlpAPI.INSTANCE.GetTcpStatistics(stats);
        assertEquals(String.format("Error %d calling GetTcpStatistics.", err), WinError.NO_ERROR, err);
        assertTrue("RTO algorithm must be between 1 and 4.", stats.dwRtoAlgorithm >= 1);
        assertTrue("RTO algorithm must be between 1 and 4.", stats.dwRtoAlgorithm <= 4);

        // Above should roughly match IPv4 stats with Ex version
        MIB_TCPSTATS stats4 = new MIB_TCPSTATS();
        err = IPHlpAPI.INSTANCE.GetTcpStatisticsEx(stats4, IPHlpAPI.AF_INET);
        assertEquals(String.format("Error %d calling GetTcpStatisticsEx.", err), WinError.NO_ERROR, err);
        assertEquals("RTO algorithm from GetTcpStatistics should match GetTcpStatisticsEx", stats.dwRtoAlgorithm,
                stats4.dwRtoAlgorithm);
        assertTrue("Reset connections should not decrease between calls to GetTcpStatistics and GetTcpStatisticsEx",
                stats.dwEstabResets <= stats4.dwEstabResets);
        assertTrue(
                "Active connections opened should not decrease between calls to GetTcpStatistics and GetTcpStatisticsEx",
                stats.dwActiveOpens <= stats4.dwActiveOpens);
        assertTrue(
                "Passive connections opened should not decrease between calls to GetTcpStatistics and GetTcpStatisticsEx",
                stats.dwPassiveOpens <= stats4.dwPassiveOpens);
    }

    @Test
    public void testGetUdpStatistics() {
        // The Math.min constructs when checking dwNoPorts + dwInErrors in
        // comparison to dwInDatagramsis is used, because at least on
        // appveyor inconsistent numbers were observed, rendering harder
        // constaints useless.
        // Sample:
        // Datagrams received with errors (332) or no port (2) should be less than inbound datagrams (97).

        MIB_UDPSTATS stats = new MIB_UDPSTATS();
        int err = IPHlpAPI.INSTANCE.GetUdpStatistics(stats);
        assertEquals(String.format("Error %d calling GetUdpStatistics.", err), WinError.NO_ERROR, err);
        assertTrue(
                String.format("Datagrams received with errors (%d) or no port (%d) should be less than inbound datagrams (%d).",
                    stats.dwNoPorts, stats.dwInErrors, stats.dwInDatagrams
                ),
                Math.min(1, stats.dwNoPorts + stats.dwInErrors) <= stats.dwInDatagrams
        );

        // Above should roughly match IPv4 stats with Ex version
        MIB_UDPSTATS stats4 = new MIB_UDPSTATS();
        err = IPHlpAPI.INSTANCE.GetUdpStatisticsEx(stats4, IPHlpAPI.AF_INET);
        assertEquals(String.format("Error %d calling GetUdpStatistics.", err), WinError.NO_ERROR, err);
        assertTrue(
                String.format(
                    "Datagrams received with no port should not decrease between calls to GetUdpStatistics (%d) and GetUdpStatisticsEx (%d)",
                    stats.dwNoPorts,stats4.dwNoPorts
                ),
                stats.dwNoPorts <= stats4.dwNoPorts
        );
        assertTrue(
                String.format(
                    "Datagrams received with errors should not decrease between calls to GetUdpStatistics (%d) and GetUdpStatisticsEx (%d)",
                    stats.dwInErrors, stats4.dwInErrors
                ),
                stats.dwInErrors <= stats4.dwInErrors);
        assertTrue(
                String.format(
                    "Datagrams received should not decrease between calls to GetUdpStatistics (%d) and GetUdpStatisticsEx (%d)",
                    stats.dwInDatagrams, stats4.dwInDatagrams
                ),
                stats.dwInDatagrams <= stats4.dwInDatagrams);
        assertTrue(
                String.format("Datagrams received with errors (%d) or no port (%d) should be less than inbound datagrams (%d). (Ex-Version)",
                    stats4.dwNoPorts, stats4.dwInErrors, stats4.dwInDatagrams
                ),
                Math.min(1, stats4.dwNoPorts + stats4.dwInErrors) <= stats4.dwInDatagrams);
    }

    @Test
    public void testTCPv4Connections() {
        // Get size needed
        IntByReference sizePtr = new IntByReference();
        assertEquals(WinError.ERROR_INSUFFICIENT_BUFFER,
                IPHLP.GetExtendedTcpTable(null, sizePtr, false, AF_INET, TCP_TABLE_CLASS.TCP_TABLE_OWNER_PID_ALL, 0));
        // Get buffer and populate table
        int size = sizePtr.getValue();
        // Even if array is empty size will have room for dwNumEntries
        assertTrue(size > 0);
        Memory buf;
        do {
            size = sizePtr.getValue();
            buf = new Memory(size);
            int ret = IPHLP.GetExtendedTcpTable(buf, sizePtr, false, AF_INET, TCP_TABLE_CLASS.TCP_TABLE_OWNER_PID_ALL,
                    0);
            if (size < sizePtr.getValue()) {
                assertEquals(WinError.ERROR_INSUFFICIENT_BUFFER, ret);
            } else {
                assertEquals(WinError.NO_ERROR, ret);
            }
            // In case size changes and buffer was too small, repeat
        } while (size < sizePtr.getValue());
        MIB_TCPTABLE_OWNER_PID tcpTable = new MIB_TCPTABLE_OWNER_PID(buf);
        for (int i = 0; i < tcpTable.dwNumEntries; i++) {
            MIB_TCPROW_OWNER_PID row = tcpTable.table[i];
            assertTrue(row.dwState >= MIB_TCP_STATE.MIB_TCP_STATE_CLOSED);
            assertTrue(row.dwState <= MIB_TCP_STATE.MIB_TCP_STATE_DELETE_TCB);
        }
    }

    @Test
    public void testTCPv6Connections() {
        // Get size needed
        IntByReference sizePtr = new IntByReference();
        assertEquals(WinError.ERROR_INSUFFICIENT_BUFFER,
                IPHLP.GetExtendedTcpTable(null, sizePtr, false, AF_INET6, TCP_TABLE_CLASS.TCP_TABLE_OWNER_PID_ALL, 0));
        // Get buffer and populate table
        int size = sizePtr.getValue();
        // Even if array is empty size will have room for dwNumEntries
        assertTrue(size > 0);
        Memory buf;
        do {
            size = sizePtr.getValue();
            buf = new Memory(size);
            int ret = IPHLP.GetExtendedTcpTable(buf, sizePtr, false, AF_INET6, TCP_TABLE_CLASS.TCP_TABLE_OWNER_PID_ALL,
                    0);
            if (size < sizePtr.getValue()) {
                assertEquals(WinError.ERROR_INSUFFICIENT_BUFFER, ret);
            } else {
                assertEquals(WinError.NO_ERROR, ret);
            }
            // In case size changes and buffer was too small, repeat
        } while (size < sizePtr.getValue());
        MIB_TCP6TABLE_OWNER_PID tcpTable = new MIB_TCP6TABLE_OWNER_PID(buf);
        for (int i = 0; i < tcpTable.dwNumEntries; i++) {
            MIB_TCP6ROW_OWNER_PID row = tcpTable.table[i];
            assertTrue(row.State >= MIB_TCP_STATE.MIB_TCP_STATE_CLOSED);
            assertTrue(row.State <= MIB_TCP_STATE.MIB_TCP_STATE_DELETE_TCB);
        }
    }

    @Test
    public void testUDPv4Connections() {
        // Get size needed
        IntByReference sizePtr = new IntByReference();
        assertEquals(WinError.ERROR_INSUFFICIENT_BUFFER,
                IPHLP.GetExtendedUdpTable(null, sizePtr, false, AF_INET, UDP_TABLE_CLASS.UDP_TABLE_OWNER_PID, 0));
        // Get buffer and populate table
        int size = sizePtr.getValue();
        // Even if array is empty size will have room for dwNumEntries
        assertTrue(size > 0);
        Memory buf;
        do {
            size = sizePtr.getValue();
            buf = new Memory(size);
            int ret = IPHLP.GetExtendedUdpTable(buf, sizePtr, false, AF_INET, UDP_TABLE_CLASS.UDP_TABLE_OWNER_PID, 0);
            if (size < sizePtr.getValue()) {
                assertEquals(WinError.ERROR_INSUFFICIENT_BUFFER, ret);
            } else {
                assertEquals(WinError.NO_ERROR, ret);
            }
            // In case size changes and buffer was too small, repeat
        } while (size < sizePtr.getValue());
        MIB_UDPTABLE_OWNER_PID udpTable = new MIB_UDPTABLE_OWNER_PID(buf);
        if (udpTable.dwNumEntries > 0) {
            assertTrue(udpTable.dwNumEntries * udpTable.table[0].size() <= size);
        }
    }

    @Test
    public void testUDPv6Connections() {
        // Get size needed
        IntByReference sizePtr = new IntByReference();
        assertEquals(WinError.ERROR_INSUFFICIENT_BUFFER,
                IPHLP.GetExtendedUdpTable(null, sizePtr, false, AF_INET6, UDP_TABLE_CLASS.UDP_TABLE_OWNER_PID, 0));
        // Get buffer and populate table
        int size = sizePtr.getValue();
        // Even if array is empty size will have room for dwNumEntries
        assertTrue(size > 0);
        Memory buf;
        do {
            size = sizePtr.getValue();
            buf = new Memory(size);
            int ret = IPHLP.GetExtendedUdpTable(buf, sizePtr, false, AF_INET6, UDP_TABLE_CLASS.UDP_TABLE_OWNER_PID, 0);
            if (size < sizePtr.getValue()) {
                assertEquals(WinError.ERROR_INSUFFICIENT_BUFFER, ret);
            } else {
                assertEquals(WinError.NO_ERROR, ret);
            }
            // In case size changes and buffer was too small, repeat
        } while (size < sizePtr.getValue());
        MIB_UDP6TABLE_OWNER_PID udpTable = new MIB_UDP6TABLE_OWNER_PID(buf);
        if (udpTable.dwNumEntries > 0) {
            assertTrue(udpTable.dwNumEntries * udpTable.table[0].size() <= size);
        }
    }
}

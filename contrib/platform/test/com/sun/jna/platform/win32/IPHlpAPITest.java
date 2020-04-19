/* Copyright (c) 2018,2020 Daniel Widdis, All Rights Reserved
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
import com.sun.jna.platform.win32.IPHlpAPI.MIB_TCPSTATS;
import com.sun.jna.platform.win32.IPHlpAPI.MIB_UDPSTATS;
import com.sun.jna.ptr.IntByReference;

public class IPHlpAPITest {

    @Test
    public void testGetIfEntry() throws SocketException {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        for (NetworkInterface netint : Collections.list(interfaces)) {
            if (!netint.isLoopback() && netint.getHardwareAddress() != null) {
                // Create new MIB_IFROW, set index to this interface index
                MIB_IFROW ifRow = new MIB_IFROW();
                ifRow.dwIndex = netint.getIndex();
                assertEquals(WinError.NO_ERROR, IPHlpAPI.INSTANCE.GetIfEntry(ifRow));
                // Bytes should exceed packets
                // These originate from unsigned ints. Use standard Java
                // widening conversion to long which does sign-extension,
                // then drop any copies of the sign bit, to prevent the value
                // being considered a negative one by Java if it is set
                long bytesSent = (ifRow.dwOutOctets) & 0xffffffffL;
                long packetsSent = (ifRow.dwOutUcastPkts) & 0xffffffffL;
                if (packetsSent > 0) {
                    assertTrue(bytesSent > packetsSent);
                } else {
                    assertEquals(0, bytesSent);
                }
                long bytesRecv = (ifRow.dwInOctets) & 0xffffffffL;
                long packetsRecv = (ifRow.dwInUcastPkts) & 0xffffffffL;
                if (packetsRecv > 0) {
                    assertTrue(bytesRecv > packetsRecv);
                } else {
                    assertEquals(0, bytesRecv);
                }
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
                    if (packetsSent.longValue() > 0) {
                        assertEquals(1, bytesSent.compareTo(packetsSent));
                    } else {
                        assertEquals(0, bytesSent.compareTo(packetsSent));
                    }
                    BigInteger bytesRecv = new BigInteger(Long.toHexString(ifRow.InOctets), 16);
                    BigInteger packetsRecv = new BigInteger(Long.toHexString(ifRow.InUcastPkts), 16);
                    if (packetsRecv.longValue() > 0) {
                        assertEquals(1, bytesRecv.compareTo(packetsRecv));
                    } else {
                        assertEquals(0, bytesRecv.compareTo(packetsRecv));
                    }
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
        assertEquals(WinError.NO_ERROR, IPHlpAPI.INSTANCE.GetTcpStatistics(stats));
        assertTrue(stats.dwRtoAlgorithm >= 1);
        assertTrue(stats.dwRtoAlgorithm <= 4);
        assertTrue(stats.dwEstabResets <= stats.dwCurrEstab);

        // Above should roughly match IPv4 stats with Ex version
        MIB_TCPSTATS stats4 = new MIB_TCPSTATS();
        assertEquals(WinError.NO_ERROR, IPHlpAPI.INSTANCE.GetTcpStatisticsEx(stats4, IPHlpAPI.AF_INET));
        assertEquals(stats.dwRtoAlgorithm, stats4.dwRtoAlgorithm);
        assertTrue(stats4.dwEstabResets <= stats4.dwCurrEstab);
        assertTrue(stats.dwActiveOpens <= stats4.dwActiveOpens);
        assertTrue(stats.dwPassiveOpens <= stats4.dwPassiveOpens);
    }

    @Test
    public void testGetUdpStatistics() {
        MIB_UDPSTATS stats = new MIB_UDPSTATS();
        assertEquals(WinError.NO_ERROR, IPHlpAPI.INSTANCE.GetUdpStatistics(stats));
        assertTrue(stats.dwNoPorts + stats.dwInErrors <= stats.dwInDatagrams);

        // Above should roughly match IPv4 stats with Ex version
        MIB_UDPSTATS stats4 = new MIB_UDPSTATS();
        assertEquals(WinError.NO_ERROR, IPHlpAPI.INSTANCE.GetUdpStatisticsEx(stats4, IPHlpAPI.AF_INET));
        assertTrue(stats.dwNoPorts <= stats4.dwNoPorts);
        assertTrue(stats.dwInErrors <= stats4.dwInErrors);
        assertTrue(stats.dwInDatagrams <= stats4.dwInDatagrams);
        assertTrue(stats4.dwNoPorts + stats4.dwInErrors <= stats4.dwInDatagrams);
    }
}

package com.sun.jna.platform.win32;

import junit.framework.TestCase;

import com.sun.jna.platform.win32.Guid.GUID;

// TODO: Auto-generated Javadoc
/**
 * The Class GuidTest.
 */
public class GuidTest extends TestCase {

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		junit.textui.TestRunner.run(GuidTest.class);
	}

	/**
	 * Instantiates a new guid test.
	 */
	public GuidTest() {
	}

	/**
	 * Loads a GUID from string and verify that the guid returned has the
	 * expected values in each byte.
	 */
	public void testGUIDFromString() {
		String sourceGuidStr = "{A5DCBF10-6530-11D2-901F-00C04FB951ED}";
		// test loading via static method
		GUID targetGuid = GUID.fromString(sourceGuidStr);

		assertEquals(targetGuid.toGuidString(), sourceGuidStr);
	}

	/**
	 * Loads a GUID from string via the constructor and verify that the guid
	 * returned has the expected values in each byte.
	 */
	public void testGUIDFromString2() {
		String sourceGuidStr = "{A5DCBF10-6530-11D2-901F-00C04FB951ED}";
		// test loading via constructor
		GUID targetGuid = new GUID(sourceGuidStr);

		assertEquals(targetGuid.toGuidString(), sourceGuidStr);
	}

	/**
	 * Loads a GUID from a byte array and verify that the guid returned has the
	 * expected values in each byte.
	 */
	public void testGUIDFromBinary() {
		byte[] sourceGuidBArr = new byte[] { (byte) 0xA5, (byte) 0xDC,
				(byte) 0xBF, (byte) 0x10, (byte) 0x65, (byte) 0x30,
				(byte) 0x11, (byte) 0xD2, (byte) 0x90, (byte) 0x1F,
				(byte) 0x00, (byte) 0xC0, (byte) 0x4F, (byte) 0xB9,
				(byte) 0x51, (byte) 0xED };

		// test loading via static method
		GUID targetGuid = GUID.fromBinary(sourceGuidBArr);
		byte[] targetGuidBArr = targetGuid.toByteArray();

		for (int i = 0; i < sourceGuidBArr.length; i++) {
			assertEquals(targetGuidBArr[i], sourceGuidBArr[i]);
		}
	}

	/**
	 * Loads a GUID from a byte array via the constructor and verify that the
	 * guid returned has the expected values in each byte.
	 */
	public void testGUIDFromBinary2() {
		byte[] sourceGuidBArr = new byte[] { (byte) 0xA5, (byte) 0xDC,
				(byte) 0xBF, (byte) 0x10, (byte) 0x65, (byte) 0x30,
				(byte) 0x11, (byte) 0xD2, (byte) 0x90, (byte) 0x1F,
				(byte) 0x00, (byte) 0xC0, (byte) 0x4F, (byte) 0xB9,
				(byte) 0x51, (byte) 0xED };

		// test loading via constructor
		GUID targetGuid = new GUID(sourceGuidBArr);
		byte[] targetGuidBArr = targetGuid.toByteArray();

		for (int i = 0; i < sourceGuidBArr.length; i++) {
			assertEquals(targetGuidBArr[i], sourceGuidBArr[i]);
		}
	}

	/**
	 * Instantiates two guids, one with windows build-in function and one via
	 * jna and compares it.
	 */
	public void testBehaviourWithOle32() {
		GUID ole32Guid = Ole32Util.getGUIDFromString("{A5DCBF10-6530-11D2-901F-00C04FB951ED}");
		GUID jnaGuid = new GUID("{A5DCBF10-6530-11D2-901F-00C04FB951ED}");

		assertEquals(ole32Guid, jnaGuid);
	}

	/**
	 * Test with the windows build-in function, compares the result of the
	 * methods.
	 */
	public void testBehaviourWithOle32_2() {
		GUID ole32Guid = Ole32Util
				.getGUIDFromString("{A5DCBF10-6530-11D2-901F-00C04FB951ED}");
		GUID jnaGuid = new GUID("{A5DCBF10-6530-11D2-901F-00C04FB951ED}");

		String ole32Guidstr = Ole32Util.getStringFromGUID(ole32Guid);
		String jnaGuidStr = jnaGuid.toGuidString();

		assertEquals(ole32Guidstr, jnaGuidStr);
	}

	/**
	 * Tests the new guid with the build-in function coming with windows.
	 */
	public void testNewGuid() {
		GUID newGuid = GUID.newGuid();
		String guidString = newGuid.toGuidString();
		GUID guidFromString = Ole32Util.getGUIDFromString(guidString);

		assertEquals(guidFromString.toGuidString(), guidString);
	}
	
	/**
	 * Tests the GUID.ByValue.
	 */
	public void testGuidByValue() {
		GUID newGuid = GUID.newGuid();
		String guidString = newGuid.toGuidString();
		
		GUID.ByValue bv = new GUID.ByValue(newGuid);
		
		String guidBV = bv.toGuidString();

		assertEquals(guidBV, guidString);
	}
}

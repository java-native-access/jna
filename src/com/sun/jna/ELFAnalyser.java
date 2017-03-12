
package com.sun.jna;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

/**
 * Analyse an ELF file for platform specific attributes.
 * 
 * <p>Primary use-case: Detect whether the java binary is arm hardfloat or softfloat.</p>
 */
class ELFAnalyser {
    /**
     * Generic ELF header
     */
    private static final byte[] ELF_MAGIC = new byte[]{(byte) 0x7F, (byte) 'E', (byte) 'L', (byte) 'F'};
    /**
     * e_flags mask if executable file conforms to hardware floating-point
     * procedure-call standard (arm ABI version 5)
     */
    private static final int EF_ARM_ABI_FLOAT_HARD = 0x00000400;
    /**
     * e_flags mask if executable file conforms to software floating-point
     * procedure-call standard (arm ABI version 5)
     */
    private static final int EF_ARM_ABI_FLOAT_SOFT = 0x00000200;
    private static final int EI_DATA_BIG_ENDIAN = 2;
    private static final int E_MACHINE_ARM = 0x28;
    private static final int EI_CLASS_64BIT = 2;
    
    public static ELFAnalyser analyse(String filename) throws IOException {
        ELFAnalyser res = new ELFAnalyser(filename);
        res.runDetection();
        return res;
    }
    
    private final String filename;
    private boolean ELF = false;
    private boolean _64Bit = false;
    private boolean bigEndian = false;
    private boolean armHardFloat = false;
    private boolean armSoftFloat = false;
    private boolean arm = false;

    /**
     * @return true if the parsed file was detected to be an ELF file
     */
    public boolean isELF() {
        return ELF;
    }

    /**
     * @return true if the parsed file was detected to be for a 64bit architecture
     * and pointers are expected to be 8byte wide
     */
    public boolean is64Bit() {
        return _64Bit;
    }

    /**
     * @return true if the parsed file is detected to be big endian, false if 
     * the file is little endian
     */
    public boolean isBigEndian() {
        return bigEndian;
    }

    /**
     * @return filename of the parsed file
     */
    public String getFilename() {
        return filename;
    }

    /**
     * @return true if file was detected to conform to the hardware floating-point
     * procedure-call standard
     */
    public boolean isArmHardFloat() {
        return armHardFloat;
    }

    /**
     * @return true if file was detected to conform to the software floating-point
     * procedure-call standard
     */
    public boolean isArmSoftFloat() {
        return armSoftFloat;
    }

    /**
     * @return true if the parsed file was detected to be build for the arm
     * architecture
     */
    public boolean isArm() {
        return arm;
    }

    private ELFAnalyser(String filename) {
        this.filename = filename;
    }

    private void runDetection() throws IOException {
        // run precheck - only of if the file at least hold an ELF header parsing
        // runs further.
        RandomAccessFile raf = new RandomAccessFile(filename, "r");
        if (raf.length() > 4) {
            byte[] magic = new byte[4];
            raf.seek(0);
            raf.read(magic);
            if (Arrays.equals(magic, ELF_MAGIC)) {
                ELF = true;
            }
        }
        if (!ELF) {
            return;
        }
        raf.seek(4);
        // The total header size depends on the pointer size of the platform
        // so before the header is loaded the pointer size has to be determined
        byte sizeIndicator = raf.readByte();
        _64Bit = sizeIndicator == EI_CLASS_64BIT;
        raf.seek(0);
        ByteBuffer headerData = ByteBuffer.allocate(_64Bit ? 64 : 52);
        raf.getChannel().read(headerData, 0);
        bigEndian = headerData.get(5) == EI_DATA_BIG_ENDIAN;
        headerData.order(bigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);

        arm = headerData.get(0x12) == E_MACHINE_ARM;
        
        if(arm) {
            int flags = headerData.getInt(_64Bit ? 0x30 : 0x24);
            armSoftFloat = (flags & EF_ARM_ABI_FLOAT_SOFT) == EF_ARM_ABI_FLOAT_SOFT;
            armHardFloat = (flags & EF_ARM_ABI_FLOAT_HARD) == EF_ARM_ABI_FLOAT_HARD;
        }
    }
}

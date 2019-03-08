/* Copyright (c) 2017 Matthias Bläsing, All Rights Reserved
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
package com.sun.jna;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
    private boolean armHardFloatFlag = false;
    private boolean armSoftFloatFlag = false;
    private boolean armEabiAapcsVfp = false;
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

    public boolean isArmHardFloat() {
        return isArmEabiAapcsVfp() || isArmHardFloatFlag();
    }

    /**
     * @return true if file was detected to specify, that FP parameters/result
     *         passing conforms to AAPCS, VFP variant (hardfloat)
     */
    public boolean isArmEabiAapcsVfp() {
        return armEabiAapcsVfp;
    }

    /**
     * @return true if file was detected to conform to the hardware floating-point
     * procedure-call standard via ELF flags
     */
    public boolean isArmHardFloatFlag() {
        return armHardFloatFlag;
    }

    /**
     * @return true if file was detected to conform to the software floating-point
     * procedure-call standard via ELF flags
     */
    public boolean isArmSoftFloatFlag() {
        return armSoftFloatFlag;
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
        RandomAccessFile raf = new RandomAccessFile(filename, "r");
        try {
            // run precheck - only of if the file at least hold an ELF header parsing
            // runs further.
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
            byte endianessIndicator = raf.readByte();
            _64Bit = sizeIndicator == EI_CLASS_64BIT;
            bigEndian = endianessIndicator == EI_DATA_BIG_ENDIAN;
            raf.seek(0);
            // header length
            ByteBuffer headerData = ByteBuffer.allocate(_64Bit ? 64 : 52);
            raf.getChannel().read(headerData, 0);

            headerData.order(bigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);

            // e_machine
            arm = headerData.get(0x12) == E_MACHINE_ARM;

            if(arm) {
                // e_flags
                int flags = headerData.getInt(_64Bit ? 0x30 : 0x24);
                armHardFloatFlag = (flags & EF_ARM_ABI_FLOAT_HARD) == EF_ARM_ABI_FLOAT_HARD;
                armSoftFloatFlag = (flags & EF_ARM_ABI_FLOAT_SOFT) == EF_ARM_ABI_FLOAT_SOFT;

                parseEabiAapcsVfp(headerData, raf);
            }
        } finally {
            try {
                raf.close();
            } catch (IOException ex) {
                // Swallow - closing
            }
        }
    }

    private void parseEabiAapcsVfp(ByteBuffer headerData, RandomAccessFile raf) throws IOException {
        ELFSectionHeaders sectionHeaders = new ELFSectionHeaders(_64Bit, bigEndian, headerData, raf);

        for (ELFSectionHeaderEntry eshe : sectionHeaders.getEntries()) {
            if(".ARM.attributes".equals(eshe.getName())) {
                ByteBuffer armAttributesBuffer = ByteBuffer.allocate(eshe.getSize());
                armAttributesBuffer.order(bigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
                raf.getChannel().read(armAttributesBuffer, eshe.getOffset());
                armAttributesBuffer.rewind();
                Map<Integer, Map<ArmAeabiAttributesTag, Object>> armAttributes = parseArmAttributes(armAttributesBuffer);
                Map<ArmAeabiAttributesTag, Object> fileAttributes = armAttributes.get(1);
                if(fileAttributes == null)  {
                    continue;
                }
                /**
                 * Tag_ABI_VFP_args, (=28), uleb128
                 *  0 The user intended FP parameter/result passing to conform to AAPCS, base variant
                 *  1 The user intended FP parameter/result passing to conform to AAPCS, VFP variant
                 *  2 The user intended FP parameter/result passing to conform to tool chain-specific conventions
                 *  3 Code is compatible with both the base and VFP variants; the non-variadic functions to pass FP parameters/results
                 */
                Object abiVFPargValue = fileAttributes.get(ArmAeabiAttributesTag.ABI_VFP_args);
                if(abiVFPargValue instanceof Integer && ((Integer) abiVFPargValue).equals(1)) {
                    armEabiAapcsVfp = true;
                } else if (abiVFPargValue instanceof BigInteger && ((BigInteger) abiVFPargValue).intValue() == 1) {
                    armEabiAapcsVfp = true;
                }
            }
        }
    }

    static class ELFSectionHeaders {
        private final List<ELFSectionHeaderEntry> entries = new ArrayList<ELFSectionHeaderEntry>();

        public ELFSectionHeaders(boolean _64bit, boolean bigEndian, ByteBuffer headerData, RandomAccessFile raf) throws IOException {
            long shoff;
            int shentsize;
            int shnum;
            short shstrndx;
            if (_64bit) {
                shoff = headerData.getLong(0x28);
                shentsize = headerData.getShort(0x3A);
                shnum = headerData.getShort(0x3C);
                shstrndx = headerData.getShort(0x3E);
            } else {
                shoff = headerData.getInt(0x20);
                shentsize = headerData.getShort(0x2E);
                shnum = headerData.getShort(0x30);
                shstrndx = headerData.getShort(0x32);
            }

            int tableLength = shnum * shentsize;

            ByteBuffer data = ByteBuffer.allocate(tableLength);
            data.order(bigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
            raf.getChannel().read(data, shoff);

            for(int i = 0; i < shnum; i++) {
                data.position(i * shentsize);
                ByteBuffer header = data.slice();
                header.order(data.order());
                header.limit(shentsize);
                entries.add(new ELFSectionHeaderEntry(_64bit, header));
            }

            ELFSectionHeaderEntry stringTable = entries.get(shstrndx);
            ByteBuffer stringBuffer = ByteBuffer.allocate(stringTable.getSize());
            stringBuffer.order(bigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
            raf.getChannel().read(stringBuffer, stringTable.getOffset());
            stringBuffer.rewind();

            ByteArrayOutputStream baos = new ByteArrayOutputStream(20);
            for (ELFSectionHeaderEntry eshe : entries) {
                baos.reset();

                ((Buffer) stringBuffer).position(eshe.getNameOffset());

                while(stringBuffer.position() < stringBuffer.limit()) {
                    byte b = stringBuffer.get();
                    if(b == 0) {
                        break;
                    } else {
                        baos.write(b);
                    }
                }

                eshe.setName(baos.toString("ASCII"));
            }
        }

        public List<ELFSectionHeaderEntry> getEntries() {
            return entries;
        }
    }

    static class ELFSectionHeaderEntry {
        private final int nameOffset;
        private String name;
        private final int type;
        private final int flags;
        private final int offset;
        private final int size;

        public ELFSectionHeaderEntry(boolean _64bit, ByteBuffer sectionHeaderData) {
            this.nameOffset = sectionHeaderData.getInt(0x0);
            this.type = sectionHeaderData.getInt(0x4);
            this.flags = (int) (_64bit ? sectionHeaderData.getLong(0x8) : sectionHeaderData.getInt(0x8));
            this.offset = (int) (_64bit ? sectionHeaderData.getLong(0x18) : sectionHeaderData.getInt(0x10));
            this.size = (int) (_64bit ? sectionHeaderData.getLong(0x20) : sectionHeaderData.getInt(0x14));
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getNameOffset() {
            return nameOffset;
        }

        public int getType() {
            return type;
        }

        public int getFlags() {
            return flags;
        }

        public int getOffset() {
            return offset;
        }

        public int getSize() {
            return size;
        }

        @Override
        public String toString() {
            return "ELFSectionHeaderEntry{" + "nameIdx=" + nameOffset + ", name=" + name + ", type=" + type + ", flags=" + flags + ", offset=" + offset + ", size=" + size + '}';
        }
    }

    static class ArmAeabiAttributesTag {

        public enum ParameterType {
            UINT32, NTBS, ULEB128
        }

        private final int value;
        private final String name;
        private final ParameterType parameterType;

        public ArmAeabiAttributesTag(int value, String name, ParameterType parameterType) {
            this.value = value;
            this.name = name;
            this.parameterType = parameterType;
        }

        public int getValue() {
            return value;
        }

        public String getName() {
            return name;
        }

        public ParameterType getParameterType() {
            return parameterType;
        }

        @Override
        public String toString() {
            return name + " (" + value + ")";
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 67 * hash + this.value;
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final ArmAeabiAttributesTag other = (ArmAeabiAttributesTag) obj;
            if (this.value != other.value) {
                return false;
            }
            return true;
        }

        private static final List<ArmAeabiAttributesTag> tags = new LinkedList<ArmAeabiAttributesTag>();
        private static final Map<Integer, ArmAeabiAttributesTag> valueMap = new HashMap<Integer, ArmAeabiAttributesTag>();
        private static final Map<String, ArmAeabiAttributesTag> nameMap = new HashMap<String, ArmAeabiAttributesTag>();

        // Enumerated from ARM IHI 0045E, 2.5 Attributes summary and history
        public static final ArmAeabiAttributesTag File = addTag(1, "File", ParameterType.UINT32);
        public static final ArmAeabiAttributesTag Section = addTag(2, "Section", ParameterType.UINT32);
        public static final ArmAeabiAttributesTag Symbol = addTag(3, "Symbol", ParameterType.UINT32);
        public static final ArmAeabiAttributesTag CPU_raw_name = addTag(4, "CPU_raw_name", ParameterType.NTBS);
        public static final ArmAeabiAttributesTag CPU_name = addTag(5, "CPU_name", ParameterType.NTBS);
        public static final ArmAeabiAttributesTag CPU_arch = addTag(6, "CPU_arch", ParameterType.ULEB128);
        public static final ArmAeabiAttributesTag CPU_arch_profile = addTag(7, "CPU_arch_profile", ParameterType.ULEB128);
        public static final ArmAeabiAttributesTag ARM_ISA_use = addTag(8, "ARM_ISA_use", ParameterType.ULEB128);
        public static final ArmAeabiAttributesTag THUMB_ISA_use = addTag(9, "THUMB_ISA_use", ParameterType.ULEB128);
        public static final ArmAeabiAttributesTag FP_arch = addTag(10, "FP_arch", ParameterType.ULEB128);
        public static final ArmAeabiAttributesTag WMMX_arch = addTag(11, "WMMX_arch", ParameterType.ULEB128);
        public static final ArmAeabiAttributesTag Advanced_SIMD_arch = addTag(12, "Advanced_SIMD_arch", ParameterType.ULEB128);
        public static final ArmAeabiAttributesTag PCS_config = addTag(13, "PCS_config", ParameterType.ULEB128);
        public static final ArmAeabiAttributesTag ABI_PCS_R9_use = addTag(14, "ABI_PCS_R9_use", ParameterType.ULEB128);
        public static final ArmAeabiAttributesTag ABI_PCS_RW_data = addTag(15, "ABI_PCS_RW_data", ParameterType.ULEB128);
        public static final ArmAeabiAttributesTag ABI_PCS_RO_data = addTag(16, "ABI_PCS_RO_data", ParameterType.ULEB128);
        public static final ArmAeabiAttributesTag ABI_PCS_GOT_use = addTag(17, "ABI_PCS_GOT_use", ParameterType.ULEB128);
        public static final ArmAeabiAttributesTag ABI_PCS_wchar_t = addTag(18, "ABI_PCS_wchar_t", ParameterType.ULEB128);
        public static final ArmAeabiAttributesTag ABI_FP_rounding = addTag(19, "ABI_FP_rounding", ParameterType.ULEB128);
        public static final ArmAeabiAttributesTag ABI_FP_denormal = addTag(20, "ABI_FP_denormal", ParameterType.ULEB128);
        public static final ArmAeabiAttributesTag ABI_FP_exceptions = addTag(21, "ABI_FP_exceptions", ParameterType.ULEB128);
        public static final ArmAeabiAttributesTag ABI_FP_user_exceptions = addTag(22, "ABI_FP_user_exceptions", ParameterType.ULEB128);
        public static final ArmAeabiAttributesTag ABI_FP_number_model = addTag(23, "ABI_FP_number_model", ParameterType.ULEB128);
        public static final ArmAeabiAttributesTag ABI_align_needed = addTag(24, "ABI_align_needed", ParameterType.ULEB128);
        public static final ArmAeabiAttributesTag ABI_align8_preserved = addTag(25, "ABI_align8_preserved", ParameterType.ULEB128);
        public static final ArmAeabiAttributesTag ABI_enum_size = addTag(26, "ABI_enum_size", ParameterType.ULEB128);
        public static final ArmAeabiAttributesTag ABI_HardFP_use = addTag(27, "ABI_HardFP_use", ParameterType.ULEB128);
        public static final ArmAeabiAttributesTag ABI_VFP_args = addTag(28, "ABI_VFP_args", ParameterType.ULEB128);
        public static final ArmAeabiAttributesTag ABI_WMMX_args = addTag(29, "ABI_WMMX_args", ParameterType.ULEB128);
        public static final ArmAeabiAttributesTag ABI_optimization_goals = addTag(30, "ABI_optimization_goals", ParameterType.ULEB128);
        public static final ArmAeabiAttributesTag ABI_FP_optimization_goals = addTag(31, "ABI_FP_optimization_goals", ParameterType.ULEB128);
        public static final ArmAeabiAttributesTag compatibility = addTag(32, "compatibility", ParameterType.NTBS);
        public static final ArmAeabiAttributesTag CPU_unaligned_access = addTag(34, "CPU_unaligned_access", ParameterType.ULEB128);
        public static final ArmAeabiAttributesTag FP_HP_extension = addTag(36, "FP_HP_extension", ParameterType.ULEB128);
        public static final ArmAeabiAttributesTag ABI_FP_16bit_format = addTag(38, "ABI_FP_16bit_format", ParameterType.ULEB128);
        public static final ArmAeabiAttributesTag MPextension_use = addTag(42, "MPextension_use", ParameterType.ULEB128);
        public static final ArmAeabiAttributesTag DIV_use = addTag(44, "DIV_use", ParameterType.ULEB128);
        public static final ArmAeabiAttributesTag nodefaults = addTag(64, "nodefaults", ParameterType.ULEB128);
        public static final ArmAeabiAttributesTag also_compatible_with = addTag(65, "also_compatible_with", ParameterType.NTBS);
        public static final ArmAeabiAttributesTag conformance = addTag(67, "conformance", ParameterType.NTBS);
        public static final ArmAeabiAttributesTag T2EE_use = addTag(66, "T2EE_use", ParameterType.ULEB128);
        public static final ArmAeabiAttributesTag Virtualization_use = addTag(68, "Virtualization_use", ParameterType.ULEB128);
        public static final ArmAeabiAttributesTag MPextension_use2 = addTag(70, "MPextension_use", ParameterType.ULEB128);

        private static ArmAeabiAttributesTag addTag(int value, String name, ArmAeabiAttributesTag.ParameterType type) {
            ArmAeabiAttributesTag tag = new ArmAeabiAttributesTag(value, name, type);

            if (!valueMap.containsKey(tag.getValue())) {
                valueMap.put(tag.getValue(), tag);
            }
            if (!nameMap.containsKey(tag.getName())) {
                nameMap.put(tag.getName(), tag);
            }
            tags.add(tag);
            return tag;
        }

        public static List<ArmAeabiAttributesTag> getTags() {
            return Collections.unmodifiableList(tags);
        }

        public static ArmAeabiAttributesTag getByName(String name) {
            return nameMap.get(name);
        }

        public static ArmAeabiAttributesTag getByValue(int value) {
            if (valueMap.containsKey(value)) {
                return valueMap.get(value);
            } else {
                ArmAeabiAttributesTag pseudoTag = new ArmAeabiAttributesTag(value, "Unknown " + value, getParameterType(value));
                return pseudoTag;
            }
        }

        private static ArmAeabiAttributesTag.ParameterType getParameterType(int value) {
            // ARM IHI 0045E, 2.2.6 Coding extensibility and compatibility
            ArmAeabiAttributesTag tag = getByValue(value);
            if (tag == null) {
                if ((value % 2) == 0) {
                    return ArmAeabiAttributesTag.ParameterType.ULEB128;
                } else {
                    return ArmAeabiAttributesTag.ParameterType.NTBS;
                }
            } else {
                return tag.getParameterType();
            }
        }
    }


    private static Map<Integer, Map<ArmAeabiAttributesTag, Object>> parseArmAttributes(ByteBuffer bb) {
        byte format = bb.get();
        if (format != 0x41) {
            // Version A
            // Not supported
            return Collections.EMPTY_MAP;
        }
        while (bb.position() < bb.limit()) {
            int posSectionStart = bb.position();
            int sectionLength = bb.getInt();
            if (sectionLength <= 0) {
                // Fail!
                break;
            }
            String vendorName = readNTBS(bb, null);
            if ("aeabi".equals(vendorName)) {
                return parseAEABI(bb);
            }
            ((Buffer) bb).position(posSectionStart + sectionLength);
        }
        return Collections.EMPTY_MAP;
    }

    private static Map<Integer, Map<ArmAeabiAttributesTag, Object>> parseAEABI(ByteBuffer buffer) {
        Map<Integer, Map<ArmAeabiAttributesTag, Object>> data = new HashMap<Integer, Map<ArmAeabiAttributesTag, Object>>();
        while (buffer.position() < buffer.limit()) {
            int pos = buffer.position();
            int subsectionTag = readULEB128(buffer).intValue();
            int length = buffer.getInt();
            if (subsectionTag == (byte) 1) {
                data.put(subsectionTag, parseFileAttribute(buffer));
            }
            ((Buffer) buffer).position(pos + length);
        }
        return data;
    }

    private static Map<ArmAeabiAttributesTag, Object> parseFileAttribute(ByteBuffer bb) {
        Map<ArmAeabiAttributesTag, Object> result = new HashMap<ArmAeabiAttributesTag, Object>();
        while (bb.position() < bb.limit()) {
            int tagValue = readULEB128(bb).intValue();
            ArmAeabiAttributesTag tag = ArmAeabiAttributesTag.getByValue(tagValue);
            switch (tag.getParameterType()) {
                case UINT32:
                    result.put(tag, bb.getInt());
                    break;
                case NTBS:
                    result.put(tag, readNTBS(bb, null));
                    break;
                case ULEB128:
                    result.put(tag, readULEB128(bb));
                    break;
            }
        }
        return result;
    }

    private static String readNTBS(ByteBuffer buffer, Integer position) {
        if (position != null) {
            ((Buffer) buffer).position(position);
        }
        int startingPos = buffer.position();
        byte currentByte;
        do {
            currentByte = buffer.get();
        } while (currentByte != '\0' && buffer.position() <= buffer.limit());
        int terminatingPosition = buffer.position();
        byte[] data = new byte[terminatingPosition - startingPos - 1];
        ((Buffer) buffer).position(startingPos);
        buffer.get(data);
        ((Buffer) buffer).position(buffer.position() + 1);
        try {
            return new String(data, "ASCII");
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static BigInteger readULEB128(ByteBuffer buffer) {
        BigInteger result = BigInteger.ZERO;
        int shift = 0;
        while (true) {
            byte b = buffer.get();
            result = result.or(BigInteger.valueOf(b & 127).shiftLeft(shift));
            if ((b & 128) == 0) {
                break;
            }
            shift += 7;
        }
        return result;
    }
}

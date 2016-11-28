/* Copyright (c) 2016 Adam Marcionek, All Rights Reserved
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

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Union;
import com.sun.jna.win32.W32APITypeMapper;

/**
 * Ported from Ntifs.h
 * Microsoft Windows WDK 10
 * @author amarcionek[at]gmail.com
 */
public interface Ntifs extends WinDef, BaseTSD {

    // Defined in winnt.h
    public int MAXIMUM_REPARSE_DATA_BUFFER_SIZE = 16 * 1024;

    // 1 long and 2 shorts aligned on longlong
    public int REPARSE_BUFFER_HEADER_SIZE = 8;

    public int SYMLINK_FLAG_RELATIVE = 1;

    public static class SymbolicLinkReparseBuffer extends Structure {

        public static class ByReference extends SymbolicLinkReparseBuffer implements Structure.ByReference {
            public ByReference() {
            }

            public ByReference(Pointer memory) {
                super(memory);
            }
        }

        /**
         * Offset, in bytes, of the substitute name string in the PathBuffer array.
         * Note that this offset must be divided by sizeof(WCHAR) to get the array index.
         */
        public short SubstituteNameOffset = 0;

        /**
         * Length, in bytes, of the substitute name string. If this string is NULL-terminated,
         * SubstituteNameLength does not include space for the UNICODE_NULL character.
         */
        public short SubstituteNameLength = 0;

        /**
         * Offset, in bytes, of the print name string in the PathBuffer array.
         * Note that this offset must be divided by sizeof(WCHAR) to get the array index.
         */
        public short PrintNameOffset = 0;

        /**
         * Length, in bytes, of the print name string. If this string is NULL-terminated,
         * PrintNameLength does not include space for the UNICODE_NULL character.
         */
        public short PrintNameLength = 0;

        /**
         * Used to indicate if the given symbolic link is an absolute or relative symbolic link.
         * If Flags contains SYMLINK_FLAG_RELATIVE, the symbolic link contained in the PathBuffer
         * array (at offset SubstitueNameOffset) is processed as a relative symbolic link; otherwise, 
         * it is processed as an absolute symbolic link.
         */
        public int Flags = 0;

        /**
         * First character of the path string. This is followed in memory by the remainder of the string.
         * The path string contains the substitute name string and print name string. The substitute name
         * and print name strings can appear in any order in the PathBuffer. (To locate the substitute
         * name and print name strings in the PathBuffer, use the SubstituteNameOffset, SubstituteNameLength,
         * PrintNameOffset, and PrintNameLength members.)
         * <b>NOTE: MAXIMUM_REPARSE_DATA_BUFFER_SIZE is chosen here based on documentation. Because chars are two
         * bytes, the actual array size needs to be divided by 2</b>
         */
        public char[] PathBuffer = new char[MAXIMUM_REPARSE_DATA_BUFFER_SIZE / 2];

        public static int sizeOf() {
            return Native.getNativeSize(MountPointReparseBuffer.class, null);
        }

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[] { "SubstituteNameOffset", "SubstituteNameLength", "PrintNameOffset", "PrintNameLength", "Flags", "PathBuffer" });
        }

        public SymbolicLinkReparseBuffer() {
            super(W32APITypeMapper.UNICODE);
        }

        public SymbolicLinkReparseBuffer(Pointer memory) {
            super(memory, Structure.ALIGN_DEFAULT, W32APITypeMapper.UNICODE);
            read();
        }

        public SymbolicLinkReparseBuffer(String substituteName, String printName, int Flags) {
            super();
            String bothNames = substituteName + printName;
            PathBuffer = bothNames.toCharArray();
            this.SubstituteNameOffset = 0;
            this.SubstituteNameLength = (short) (substituteName.length() * 2);
            this.PrintNameOffset = (short) (substituteName.length() * 2);
            this.PrintNameLength = (short) (printName.length() * 2);
            this.Flags = Flags;
            write();
        }

        public SymbolicLinkReparseBuffer(short SubstituteNameOffset, short SubstituteNameLength, short PrintNameOffset, short PrintNameLength, int Flags, String PathBuffer) {
            super();
            this.SubstituteNameOffset = SubstituteNameOffset;
            this.SubstituteNameLength = SubstituteNameLength;
            this.PrintNameOffset = PrintNameOffset;
            this.PrintNameLength = PrintNameLength;
            this.Flags = Flags;
            this.PathBuffer = PathBuffer.toCharArray();
            write();
        }

        /**
         * @return the print name in a String
         */
        public String getPrintName() {
            return String.copyValueOf(PathBuffer, PrintNameOffset / 2, PrintNameLength / 2);
        }

        /**
         * @return the substitute name in a String
         */
        public String getSubstituteName() {
            return String.copyValueOf(PathBuffer, SubstituteNameOffset / 2, SubstituteNameLength / 2);
        }
    }

    public static class MountPointReparseBuffer extends Structure {

        public static class ByReference extends MountPointReparseBuffer implements Structure.ByReference {
            public ByReference() {
            }

            public ByReference(Pointer memory) {
                super(memory);
            }
        }

        /**
         * Offset, in bytes, of the substitute name string in the PathBuffer array.
         * Note that this offset must be divided by sizeof(WCHAR) to get the array index.
         */
        public short SubstituteNameOffset = 0;

        /**
         * Length, in bytes, of the substitute name string. If this string is NULL-terminated,
         * SubstituteNameLength does not include space for the UNICODE_NULL character.
         */
        public short SubstituteNameLength = 0;

        /**
         * Offset, in bytes, of the print name string in the PathBuffer array.
         * Note that this offset must be divided by sizeof(WCHAR) to get the array index.
         */
        public short PrintNameOffset = 0;

        /**
         * Length, in bytes, of the print name string. If this string is NULL-terminated,
         * PrintNameLength does not include space for the UNICODE_NULL character.
         */
        public short PrintNameLength = 0;

        /**
         * First character of the path string. This is followed in memory by the remainder of the string.
         * The path string contains the substitute name string and print name string. The substitute name
         * and print name strings can appear in any order in the PathBuffer. (To locate the substitute
         * name and print name strings in the PathBuffer, use the SubstituteNameOffset, SubstituteNameLength,
         * PrintNameOffset, and PrintNameLength members.)
         * <b>NOTE: MAXIMUM_REPARSE_DATA_BUFFER_SIZE is chosen here based on documentation. Because chars are two
         * bytes, the actual array size needs to be divided by 2</b>
         */
        public char[] PathBuffer = new char[MAXIMUM_REPARSE_DATA_BUFFER_SIZE / 2];

        public static int sizeOf() {
            return Native.getNativeSize(MountPointReparseBuffer.class, null);
        }

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[] { "SubstituteNameOffset", "SubstituteNameLength", "PrintNameOffset", "PrintNameLength", "PathBuffer" });
        }

        public MountPointReparseBuffer() {
            super(W32APITypeMapper.UNICODE);
        }

        public MountPointReparseBuffer(Pointer memory) {
            super(memory, Structure.ALIGN_DEFAULT, W32APITypeMapper.UNICODE);
            read();
        }

        public MountPointReparseBuffer(String substituteName, String printName) {
            super();
            String bothNames = substituteName + printName;
            PathBuffer = bothNames.toCharArray();
            this.SubstituteNameOffset = 0;
            this.SubstituteNameLength = (short) substituteName.length();
            this.PrintNameOffset = (short) (substituteName.length() * 2);
            this.PrintNameLength = (short) (printName.length() * 2);
            write();
        }

        public MountPointReparseBuffer(short SubstituteNameOffset, short SubstituteNameLength, short PrintNameOffset, short PrintNameLength, String PathBuffer) {
            super();
            this.SubstituteNameOffset = SubstituteNameOffset;
            this.SubstituteNameLength = SubstituteNameLength;
            this.PrintNameOffset = PrintNameOffset;
            this.PrintNameLength = PrintNameLength;
            this.PathBuffer = PathBuffer.toCharArray();
            write();
        }
    }

    public static class GenericReparseBuffer extends Structure {

        public static class ByReference extends GenericReparseBuffer implements Structure.ByReference {
            public ByReference() {
            }

            public ByReference(Pointer memory) {
                super(memory);
            }
        }

        /**
         * Microsoft-defined data for the reparse point.
         * <b>NOTE: MAXIMUM_REPARSE_DATA_BUFFER_SIZE is chosen based on documentation</b>
         */
        public byte[] DataBuffer = new byte[MAXIMUM_REPARSE_DATA_BUFFER_SIZE];

        public static int sizeOf() {
            return Native.getNativeSize(GenericReparseBuffer.class, null);
        }

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[] { "DataBuffer" });
        }

        public GenericReparseBuffer() {
            super();
        }

        public GenericReparseBuffer(Pointer memory) {
            super(memory);
            read();
        }

        public GenericReparseBuffer(String DataBuffer) {
            super();
            this.DataBuffer = DataBuffer.getBytes();
            write();
        }
    }

    /**
     * The REPARSE_DATA_BUFFER structure contains reparse point data for a Microsoft reparse point.
     * (Third-party reparse point owners must use the REPARSE_GUID_DATA_BUFFER structure instead.)
     */
    public static class REPARSE_DATA_BUFFER extends Structure {

        public static class ByReference extends REPARSE_DATA_BUFFER implements Structure.ByReference {
            public ByReference() {
            }

            public ByReference(Pointer memory) {
                super(memory);
            }
        }

        /**
         * Reparse point tag. Must be a Microsoft reparse point tag.
         */
        public int ReparseTag = 0;

        /**
         * Size, in bytes, of the reparse data in the DataBuffer member.
         */
        public short ReparseDataLength = 0;

        /**
         * Length, in bytes, of the unparsed portion of the file name pointed to by the FileName member of the associated file object.
         * For more information about the FileName member, see FILE_OBJECT. This member is only valid for create operations when the
         * I/O fails with STATUS_REPARSE. For all other purposes, such as setting or querying a reparse point for the reparse data,
         * this member is treated as reserved.
         */
        public short Reserved = 0;

        public static class REPARSE_UNION extends Union {
            public static class ByReference extends REPARSE_UNION  implements Structure.ByReference {

            }

            public REPARSE_UNION() {
                super();
            }

            public REPARSE_UNION(Pointer memory) {
                super(memory);
            }

            public SymbolicLinkReparseBuffer symLinkReparseBuffer;
            public MountPointReparseBuffer mountPointReparseBuffer;
            public GenericReparseBuffer genericReparseBuffer;
        }

        public REPARSE_UNION u;

        public static int sizeOf() {
            return Native.getNativeSize(REPARSE_DATA_BUFFER.class, null);
        }

        /**
         * @return size of the structure considering the ReparseDataLength size
         */
        public int getSize() {
            return REPARSE_BUFFER_HEADER_SIZE + ReparseDataLength;
        }

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[] { "ReparseTag", "ReparseDataLength", "Reserved", "u" });
        }

        public REPARSE_DATA_BUFFER() {
            super();
        }

        public REPARSE_DATA_BUFFER(int ReparseTag, short Reserved) {
            super();
            this.ReparseTag = ReparseTag;
            this.Reserved = Reserved;
            this.ReparseDataLength = 0;
            write();
        }

        public REPARSE_DATA_BUFFER(int ReparseTag, short Reserved, SymbolicLinkReparseBuffer symLinkReparseBuffer) {
            super();
            this.ReparseTag = ReparseTag;
            this.Reserved = Reserved;
            this.ReparseDataLength = (short) symLinkReparseBuffer.size();
            this.u.setType(SymbolicLinkReparseBuffer.class);
            this.u.symLinkReparseBuffer = symLinkReparseBuffer;
            write();
        }

        public REPARSE_DATA_BUFFER(Pointer memory) {
            super(memory);
            read();
        }

        @Override
        public void read() {
            super.read();
            // Set structure value based on ReparseTag and then re-read the union.
            switch(ReparseTag) {
                default:
                    u.setType(GenericReparseBuffer.class);
                    break;
                case WinNT.IO_REPARSE_TAG_MOUNT_POINT:
                    u.setType(MountPointReparseBuffer.class);
                    break;
                case WinNT.IO_REPARSE_TAG_SYMLINK:
                    u.setType(SymbolicLinkReparseBuffer.class);
                    break;
            }
            u.read();
        }
    }
}

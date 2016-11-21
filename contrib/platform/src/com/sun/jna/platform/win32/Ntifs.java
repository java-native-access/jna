/* Copyright (c) 2016 Adam Marcionek All Rights Reserved
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
package com.sun.jna.platform.win32;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Union;

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
        public USHORT SubstituteNameOffset = new USHORT(0);

        /**
         * Length, in bytes, of the substitute name string. If this string is NULL-terminated,
         * SubstituteNameLength does not include space for the UNICODE_NULL character.
         */
        public USHORT SubstituteNameLength = new USHORT(0);

        /**
         * Offset, in bytes, of the print name string in the PathBuffer array.
         * Note that this offset must be divided by sizeof(WCHAR) to get the array index.
         */
        public USHORT PrintNameOffset = new USHORT(0);

        /**
         * Length, in bytes, of the print name string. If this string is NULL-terminated,
         * PrintNameLength does not include space for the UNICODE_NULL character.
         */
        public USHORT PrintNameLength = new USHORT(0);

        /**
         * Used to indicate if the given symbolic link is an absolute or relative symbolic link.
         * If Flags contains SYMLINK_FLAG_RELATIVE, the symbolic link contained in the PathBuffer
         * array (at offset SubstitueNameOffset) is processed as a relative symbolic link; otherwise, 
         * it is processed as an absolute symbolic link.
         */
        public ULONG Flags = new ULONG(0);

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
            super();
        }

        public SymbolicLinkReparseBuffer(Pointer memory) {
            super(memory);
            read();
        }

        public SymbolicLinkReparseBuffer(String substituteName, String printName, int Flags) {
            super();
            String bothNames = substituteName + printName;
            PathBuffer = bothNames.toCharArray();
            this.SubstituteNameOffset = new USHORT(0);
            this.SubstituteNameLength = new USHORT(substituteName.length() * 2);
            this.PrintNameOffset = new USHORT((substituteName.length()) * 2);
            this.PrintNameLength = new USHORT(printName.length() * 2);
            this.Flags = new ULONG(Flags);
            write();
        }

        public SymbolicLinkReparseBuffer(USHORT SubstituteNameOffset, USHORT SubstituteNameLength, USHORT PrintNameOffset, USHORT PrintNameLength, ULONG Flags, String PathBuffer) {
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
            return String.copyValueOf(PathBuffer, this.PrintNameOffset.intValue() / 2, this.PrintNameLength.intValue() / 2);
        }

        /**
         * @return the substitute name in a String
         */
        public String getSubstituteName() {
            return String.copyValueOf(PathBuffer, this.SubstituteNameOffset.intValue() / 2, this.SubstituteNameLength.intValue() / 2);
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
        public USHORT SubstituteNameOffset;

        /**
         * Length, in bytes, of the substitute name string. If this string is NULL-terminated,
         * SubstituteNameLength does not include space for the UNICODE_NULL character.
         */
        public USHORT SubstituteNameLength;

        /**
         * Offset, in bytes, of the print name string in the PathBuffer array.
         * Note that this offset must be divided by sizeof(WCHAR) to get the array index.
         */
        public USHORT PrintNameOffset;

        /**
         * Length, in bytes, of the print name string. If this string is NULL-terminated,
         * PrintNameLength does not include space for the UNICODE_NULL character.
         */
        public USHORT PrintNameLength;

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
            super();
        }

        public MountPointReparseBuffer(Pointer memory) {
            super(memory);
            read();
        }

        public MountPointReparseBuffer(String substituteName, String printName) {
            super();
            String bothNames = substituteName + printName;
            PathBuffer = bothNames.toCharArray();
            this.SubstituteNameOffset = new USHORT(0);
            this.SubstituteNameLength = new USHORT(substituteName.length());
            this.PrintNameOffset = new USHORT((substituteName.length()) * 2);
            this.PrintNameLength = new USHORT(printName.length() * 2);
            write();
        }

        public MountPointReparseBuffer(USHORT SubstituteNameOffset, USHORT SubstituteNameLength, USHORT PrintNameOffset, USHORT PrintNameLength, String PathBuffer) {
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
        public ULONG ReparseTag;

        /**
         * Size, in bytes, of the reparse data in the DataBuffer member.
         */
        public USHORT ReparseDataLength = new USHORT(0);

        /**
         * Length, in bytes, of the unparsed portion of the file name pointed to by the FileName member of the associated file object.
         * For more information about the FileName member, see FILE_OBJECT. This member is only valid for create operations when the
         * I/O fails with STATUS_REPARSE. For all other purposes, such as setting or querying a reparse point for the reparse data,
         * this member is treated as reserved.
         */
        public USHORT Reserved;

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
            return REPARSE_BUFFER_HEADER_SIZE + this.ReparseDataLength.intValue();
        }

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[] { "ReparseTag", "ReparseDataLength", "Reserved", "u" });
        }

        public REPARSE_DATA_BUFFER() {
            super();
        }

        public REPARSE_DATA_BUFFER(int ReparseTag, int Reserved) {
            super();
            this.ReparseTag = new ULONG(ReparseTag);
            this.Reserved = new USHORT(Reserved);
            this.ReparseDataLength = new USHORT(0);
            write();
        }

        public REPARSE_DATA_BUFFER(int ReparseTag, int Reserved, SymbolicLinkReparseBuffer symLinkReparseBuffer) {
            super();
            this.ReparseTag = new ULONG(ReparseTag);
            this.Reserved = new USHORT(Reserved);
            this.ReparseDataLength = new USHORT(symLinkReparseBuffer.size());
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
            switch(ReparseTag.intValue()) {
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

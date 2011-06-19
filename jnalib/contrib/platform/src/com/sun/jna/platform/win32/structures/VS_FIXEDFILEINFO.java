package com.sun.jna.platform.win32.structures;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public class VS_FIXEDFILEINFO extends Structure {
    
    public static class ByReference extends VS_FIXEDFILEINFO implements Structure.ByReference {
        public ByReference() {
        }

        public ByReference(Pointer memory) {
            super(memory);
        }
    }

    public VS_FIXEDFILEINFO() {
    }

    public VS_FIXEDFILEINFO(Pointer memory) {
        useMemory(memory);
        read();
    }
	
	public int Signature;
	public int StrucVersion;
	public int FileVersionMS;
	public int FileVersionLS;
	public int ProductVersionMS;
	public int ProductVersionLS;
	public int FileFlagsMask;
	public int FileFlags;
	public int FileOS;
	public int FileType;
	public int FileSubtype;
	public int FileDateMS;
	public int FileDateLS;
}

package com.sun.jna.platform.win32.COM.tlb.imp;

public class TlbVariable {

	String name;
	
	String value;
	
	public TlbVariable(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}

package com.sun.jna.platform.win32.COM.tlb.imp;

import java.util.ArrayList;

public class TlbVariables {
	
	private ArrayList<TlbVariable> tlbVariables = new ArrayList<TlbVariable>();
	
	public TlbVariables() {
	}
	
	public int size() {
		return this.tlbVariables.size();
	}
	
	public void addVariable(TlbVariable variable) {
		this.tlbVariables.add(variable);
	}
	
}

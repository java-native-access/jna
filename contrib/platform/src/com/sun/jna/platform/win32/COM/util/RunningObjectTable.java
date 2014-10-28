package com.sun.jna.platform.win32.COM.util;

import java.util.ArrayList;
import java.util.List;

import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.COM.COMException;
import com.sun.jna.platform.win32.COM.COMUtils;
import com.sun.jna.ptr.PointerByReference;

public class RunningObjectTable implements IRunningObjectTable {

	protected RunningObjectTable(com.sun.jna.platform.win32.COM.RunningObjectTable raw) {
		this.raw = raw;
	}

	com.sun.jna.platform.win32.COM.RunningObjectTable raw;
	
	@Override
	public Iterable<IDispatch> enumRunning() {
		PointerByReference ppenumMoniker = new PointerByReference();
		WinNT.HRESULT hr = this.raw.EnumRunning(ppenumMoniker);
		COMUtils.checkRC(hr);
		
		com.sun.jna.platform.win32.COM.EnumMoniker raw = new com.sun.jna.platform.win32.COM.EnumMoniker(ppenumMoniker.getValue());
		return new EnumMoniker(raw, this.raw);
	}

	@Override
	public <T> List<T> getActiveObjectsByInterface(Class<T> comInterface) {
		List<T> result = new ArrayList<T>();

		for(IDispatch obj: this.enumRunning()) {
			try {
				T dobj = obj.queryInterface(comInterface);
				
				result.add(dobj);
			} catch(COMException ex) {
				
			}
		}

		return result;
	}
}

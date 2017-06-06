package com.sun.jna.platform.win32.COM.util.office.word;

import com.sun.jna.platform.win32.COM.util.annotation.ComProperty;

public interface ComIOptions {

	@ComProperty
	boolean getShowReadabilityStatistics();

	@ComProperty
	void setShowReadabilityStatistics(boolean b);
}

package com.sun.jna.win32;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public interface W32APIOptions extends StdCallLibrary {
    /** Standard options to use the unicode version of a w32 API. */
    Map<String, Object> UNICODE_OPTIONS = Collections.unmodifiableMap(new HashMap<String, Object>() {
        private static final long serialVersionUID = 1L;    // we're not serializing it

        {
            put(OPTION_TYPE_MAPPER, W32APITypeMapper.UNICODE);
            put(OPTION_FUNCTION_MAPPER, W32APIFunctionMapper.UNICODE);
        }
    });

    /** Standard options to use the ASCII/MBCS version of a w32 API. */
    Map<String, Object> ASCII_OPTIONS = Collections.unmodifiableMap(new HashMap<String, Object>() {
        private static final long serialVersionUID = 1L;    // we're not serializing it
        {
            put(OPTION_TYPE_MAPPER, W32APITypeMapper.ASCII);
            put(OPTION_FUNCTION_MAPPER, W32APIFunctionMapper.ASCII);
        }
    });

    /** Default options to use - depends on the value of {@code w32.ascii} system property */
    Map<String, Object> DEFAULT_OPTIONS = Boolean.getBoolean("w32.ascii") ? ASCII_OPTIONS : UNICODE_OPTIONS;
}

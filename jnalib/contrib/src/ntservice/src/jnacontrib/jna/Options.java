/*
 * Options.java
 *
 * Created on 8. August 2007, 17:07
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jnacontrib.jna;

import static com.sun.jna.Library.OPTION_FUNCTION_MAPPER;
import static com.sun.jna.Library.OPTION_TYPE_MAPPER;

import java.util.HashMap;
import java.util.Map;

import com.sun.jna.win32.W32APIFunctionMapper;
import com.sun.jna.win32.W32APITypeMapper;

/**
 *
 * @author TB
 */
public interface Options {
  Map<String, Object> UNICODE_OPTIONS = new HashMap<String, Object>() {
    {
      put(OPTION_TYPE_MAPPER, W32APITypeMapper.UNICODE);
      put(OPTION_FUNCTION_MAPPER, W32APIFunctionMapper.UNICODE);
    }
	private static final long serialVersionUID = 1L;
  };
}

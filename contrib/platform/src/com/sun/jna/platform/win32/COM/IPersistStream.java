/* Copyright (c) 2014 Dr David H. Akehurst (itemis), All Rights Reserved
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
package com.sun.jna.platform.win32.COM;


/**
 * Enables the saving and loading of objects that use a simple serial stream for their storage needs.
 * 
 * @see <a href="http://msdn.microsoft.com/en-us/library/windows/desktop/ms690091%28v=vs.85%29.aspx">MSDN</a>
 *
 */
public interface IPersistStream extends IPersist {

	/**
	 * Determines whether an object has changed since it was last saved to its
	 * stream.
	 * 
	 * (Unimplemented)
	 * 
	 */
	boolean IsDirty();

	/**
	 * Initializes an object from the stream where it was saved previously
	 * 
	 * (Unimplemented)
	 * 
	 */

	void Load(IStream stm);

	/**
	 * Saves an object to the specified stream.
	 * 
	 * (Unimplemented)
	 * 
	 */
	void Save(IStream stm);

	/**
	 * Retrieves the size of the stream needed to save the object.
	 * 
	 * (Unimplemented)
	 * 
	 */
	void GetSizeMax();
}

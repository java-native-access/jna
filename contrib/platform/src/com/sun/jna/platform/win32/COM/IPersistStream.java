/* Copyright (c) 2014 Dr David H. Akehurst (itemis), All Rights Reserved
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

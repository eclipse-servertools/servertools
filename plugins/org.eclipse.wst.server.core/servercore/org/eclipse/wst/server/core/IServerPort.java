/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core;
/**
 * An abstract port on a server.
 */
public interface IServerPort {
	/**
	 * Return an optional internal id used to identify this port.
	 * 
	 * @return java.lang.String
	 */
	public String getId();

	/**
	 * Return the name of the port.
	 * @return java.lang.String
	 */
	public String getName();

	/**
	 * Return the actual port number.
	 * @return int
	 */
	public int getPort();

	/**
	 * Returns the protocol, e.g. HTTP of this port. Returns null
	 * if the protocol is unknown.
	 * 
	 * @return java.lang.String
	 */
	public String getProtocol();

	/**
	 * Returns the content types that this port would normally serve, or null
	 * if the content is unknown.
	 * 
	 * @return java.lang.String[]
	 */
	public String[] getContentTypes();
	
	/**
	 * Returns true if this port is an "advanced" port and should not be shown
	 * to novice users.
	 * 
	 * @return boolean
	 */
	public boolean isAdvanced();
}
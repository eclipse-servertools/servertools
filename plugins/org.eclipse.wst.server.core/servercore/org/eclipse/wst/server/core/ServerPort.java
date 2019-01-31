/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core;
/**
 * This class represents a port on a server.
 * 
 * @since 1.0
 */
public class ServerPort {
	private String id;
	private String name;
	private int port;
	private String protocol;
	private String[] contentTypes;
	private boolean advanced;
	
	/**
	 * Create a new server port.
	 * 
	 * @param id a server-specific port id
	 * @param name a human-readable name for the port
	 * @param port the number number
	 * @param protocol the port's protocol
	 */
	public ServerPort(String id, String name, int port, String protocol) {
		this(id, name, port, protocol, null, true);
	}

	/**
	 * Create a new server port.
	 * 
	 * @param id a server-specific port id
	 * @param name a human-readable name for the port
	 * @param port the number number
	 * @param protocol the port's protocol
	 * @param contentTypes the content types that the port supports
	 * @param advanced <code>true</code> if the port is "advanced" and should only
	 *    be shown to advanced users, <code>false</code> otherwise
	 */
	public ServerPort(String id, String name, int port, String protocol, String[] contentTypes, boolean advanced) {
		super();
		this.id = id;
		this.name = name;
		this.port = port;
		this.protocol = protocol;
		this.contentTypes = contentTypes;
		this.advanced = advanced;
	}

	/**
	 * Return an optional internal id used to identify this port.
	 * 
	 * @return an internal id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Return the name of the port.
	 * 
	 * @return the name of the port
	 */
	public String getName() {
		return name;
	}

	/**
	 * Return the actual port number.
	 * 
	 * @return the port number
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Returns the protocol, e.g. HTTP of this port. Returns null
	 * if the protocol is unknown.
	 * 
	 * @return the procotol
	 */
	public String getProtocol() {
		return protocol;
	}

	/**
	 * Returns the content types that this port would normally serve, or null
	 * if the content is unknown.
	 * 
	 * @return a possibly empty array of content types
	 */
	public String[] getContentTypes() {
		return contentTypes;
	}

	/**
	 * Returns true if this port is an "advanced" port and should not be shown
	 * to novice users.
	 * 
	 * @return <code>true</code> if the port is advanced, or <code>false</code>
	 *    otherwise
	 */
	public boolean isAdvanced() {
		return advanced;
	}
	
	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if (!(obj instanceof ServerPort))
			return false;
		ServerPort sp = (ServerPort) obj;
		return (sp.port == port);
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() {
		return port;
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return "ServerPort [" + getName() + ", " + getId() + ", " + getPort() + ", " + getProtocol() + "]";
	}
}
/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.util;

import org.eclipse.wst.server.core.IServerPort;
/**
 * A default implementation of the IServerPort interface.
 * 
 * @since 1.0
 */
public class ServerPort implements IServerPort {
	private String id;
	private String name;
	private int port;
	private String protocol;
	private String[] contentTypes;
	private boolean advanced;
	
	/**
	 * ServerPort constructor comment.
	 */
	public ServerPort(String id, String name, int port, String protocol) {
		this(id, name, port, protocol, null, true);
	}
	
	public ServerPort(String id, String name, int port, String protocol, boolean advanced) {
		this(id, name, port, protocol, null, advanced);
	}

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
	 * ServerPort constructor comment.
	 */
	public ServerPort(String name, int port, String protocol) {
		this(null, name, port, protocol);
	}
	
	/**
	 * Return an optional internal id used to identify this port.
	 * 
	 * @return java.lang.String
	 */
	public String getId() {
		return id;
	}

	/**
	 * Return the name of the port.
	 * 
	 * @return java.lang.String
	 */
	public String getName() {
		return name;
	}

	/**
	 * Return the actual port number.
	 * @return int
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Returns the protocol, e.g. HTTP of this port. Returns null
	 * if the protocol is unknown.
	 * 
	 * @return java.lang.String
	 */
	public String getProtocol() {
		return protocol;
	}

	/**
	 * Returns the content that this port would normally serve, or null
	 * if the content is unknown.
	 * 
	 * @return java.lang.String[]
	 */
	public String[] getContentTypes() {
		return contentTypes;
	}

	/**
	 * Returns true if this port is an "advanced" port and should not be shown
	 * to novice users.
	 * 
	 * @return boolean
	 */
	public boolean isAdvanced() {
		return advanced;
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof ServerPort))
			return false;
		ServerPort sp = (ServerPort) obj;
		return (sp.port == port);
	}
	
	public int hashCode() {
		return port;
	}
	
	public String toString() {
		return "ServerPort [" + getName() + ", " + getId() + ", " + getPort() + ", " + getProtocol() + "]";
	}
}
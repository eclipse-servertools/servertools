/*******************************************************************************
 * Copyright (c) 2015 IBM Corporation and others.
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
package org.eclipse.wst.server.discovery;

/**
 * @since 1.1
 * 
 */
public class ServerProxy {
	private String serverId;
	private String serverName;
	private String serverDescription;
	private RuntimeProxy runtimeType;
	private String extension;
	private String uri;
	private String proxyServerId;

	/**
	 * ServerType constructor comment.
	 * 
	 * @param element a configuration element
	 */
	public ServerProxy(String id, String name, String description, RuntimeProxy runtimeType, String extension, String uri, String proxyServerId) {
		super();
		this.serverId = id;
		this.serverName = name;
		this.serverDescription = description;
		this.runtimeType = runtimeType;
		this.extension = extension;
		this.uri = uri;
		this.proxyServerId = proxyServerId;
	}

	/**
	 * Returns the id of this factory.
	 *
	 * @return java.lang.String
	 */
	public String getId() {
		return serverId;
	}

	public String getName() {
		return serverName;
	}

	public boolean startBeforePublish() {
		return true;
	}

	public boolean synchronousStart() {
		return true;
	}
	
	public String getDescription() {
		return serverDescription;
	}

	public RuntimeProxy getRuntimeType() {
		return runtimeType;
	}

	public String getExtension() {
		return extension;
	}
	
	public String getURI() {
		return uri;
	}
	
	public String getProxyServerId() {
		return proxyServerId;
	}
	/**
	 * Return a string representation of this object.
	 * 
	 * @return java.lang.String
	 */
	public String toString() {
		return "ServerType[" + getId() + "]";
	}
}
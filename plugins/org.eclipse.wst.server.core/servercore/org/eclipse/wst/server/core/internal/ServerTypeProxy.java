/*******************************************************************************
 * Copyright (c) 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.internal;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.discovery.ServerProxy;
/**
 * @since 1.7
 * 
 */
public class ServerTypeProxy  implements IServerType{
	private String serverId;
	private String serverName;
	private String serverDescription;
	private IRuntimeType runtimeType;
	private String extension;
	private String uri;
	private String proxyServerId;


	/**
	 * ServerType constructor comment.
	 * 
	 * @param element a configuration element
	 */
	public ServerTypeProxy(String id, String name, String description, String hostName, IRuntimeType runtimeType) {
		super();
		this.serverId = id;
		this.serverName = name;
		this.serverDescription = description;
		this.runtimeType = runtimeType;
	}
	public ServerTypeProxy(ServerProxy serverProxy) {
		super();
		this.serverId = serverProxy.getId();
		this.serverName = serverProxy.getName();
		this.serverDescription = serverProxy.getDescription();
		this.runtimeType = new RuntimeTypeProxy(serverProxy.getRuntimeType());
		this.extension = serverProxy.getExtension();
		this.uri = serverProxy.getURI();
		this.proxyServerId = serverProxy.getProxyServerId();
	
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

	public String getDescription() {
		return serverDescription;
	}

	public IRuntimeType getRuntimeType() {
		return runtimeType;
	}

	public boolean hasRuntime() {
		return false;
	}

	public void dispose() {
		runtimeType = null;
	}

	public String getExtension(){
		return extension;
	}
	/**
	 * Return a string representation of this object.
	 * 
	 * @return java.lang.String
	 */
	public String toString() {
		return "ServerType[" + getId() + "]";
	}
	
	public boolean supportsLaunchMode(String launchMode) {
		return false;
	}
	
	public boolean hasServerConfiguration() {
		return false;
	}
	
	public boolean supportsRemoteHosts() {
		return true;
	}
	
	public IServerWorkingCopy createServer(String id, IFile file, IRuntime runtime, IProgressMonitor monitor)
			throws CoreException {
		return null;
	}
	
	public IServerWorkingCopy createServer(String id, IFile file, IProgressMonitor monitor) throws CoreException {
		return null;
	}

	public String getURI() {
		return uri;
	}
	
	public String getProxyServerId() {
		return proxyServerId;
	}

}
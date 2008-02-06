/*******************************************************************************
 * Copyright (c) 2003, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.wizard.page;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServerType;
import org.eclipse.wst.server.core.IServerWorkingCopy;
/**
 * A helper class used to cache the creation of servers.
 */
public class ServerCreationCache {
	protected Map<String, IServerWorkingCopy> cache;

	/**
	 * ServerCreationCache constructor comment.
	 */
	public ServerCreationCache() {
		super();
		cache = new HashMap<String, IServerWorkingCopy>();
	}

	/**
	 * Return the key to use for the given server type.
	 * 
	 * @param type the server type
	 * @param isLocalhost true if the server is local
	 * @return the key
	 */
	private String getKey(IServerType type, boolean isLocalhost) {
		return type.getId() + "|" + isLocalhost + "|";
	}

	/**
	 * Create a new server and put it in the cache.
	 *
	 * @param type the server type
	 * @param runtime a runtime, or null
	 * @param isLocalhost true if the server is local
	 * @param monitor a progress monitor
	 * @return a server working copy
	 * @throws CoreException if anything goes wrong
	 */
	public IServerWorkingCopy createServer(IServerType type, IRuntime runtime, boolean isLocalhost, IProgressMonitor monitor) throws CoreException {
		IServerWorkingCopy server = type.createServer(null, null, runtime, monitor);
		cache.put(getKey(type, isLocalhost), server);
		return server;
	}

	/**
	 * Returns a previously cached server, if one exists
	 * 
	 * @param type the server type
	 * @param isLocalhost true if the server is local
	 * @return a working copy
	 */
	public IServerWorkingCopy getCachedServer(IServerType type, boolean isLocalhost) {
		try {
			IServerWorkingCopy server = cache.get(getKey(type, isLocalhost));
			if (server != null)
				return server;
		} catch (Exception e) {
			// ignore
		}
		
		return null;
	}
}
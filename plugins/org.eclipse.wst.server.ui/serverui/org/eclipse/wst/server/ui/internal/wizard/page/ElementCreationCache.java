/**********************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui.internal.wizard.page;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServerType;
import org.eclipse.wst.server.core.IServerWorkingCopy;
/**
 * A helper class used to cache the creation of server elements.
 */
public class ElementCreationCache {
	protected Map elementCache;
	protected Map taskCache;

	/**
	 * ElementCreationCache constructor comment.
	 */
	public ElementCreationCache() {
		super();
		elementCache = new HashMap();
		taskCache = new HashMap();
	}
	
	/**
	 * Return the key to use for the given factory.
	 *
	 * @param type the server type
	 * @param isLocalhost true if the server is local
	 * @return the key
	 */
	protected String getKey(IServerType type, boolean isLocalhost) {
		return type.getId() + "|" + isLocalhost + "|";
	}

	/**
	 * Returns a server. 
	 *
	 * @param type
	 * @param isLocalhost true if the server is local
	 * @param monitor a progress monitor
	 * @return a server working copy
	 * @throws CoreException if anything goes wrong
	 */
	public IServerWorkingCopy getServer(IServerType type, boolean isLocalhost, IProgressMonitor monitor) throws CoreException {
		try {
			IServerWorkingCopy server = getCachedServer(type, isLocalhost);
			if (server != null)
				return server;
		} catch (Exception e) {
			// ignore
		}
	
		try {
			IFile file = null;
			//if (ServerPreferences.getInstance().isCreateResourcesInWorkspace())
			//	file = ServerUtil.getUnusedServerFile(WizardUtil.getServerProject(), type);
			
			IServerWorkingCopy server = type.createServer(null, file, (IRuntime)null, monitor);
			elementCache.put(getKey(type, isLocalhost), server);
			return server;
		} catch (CoreException ce) {
			throw ce;
		}
	}

	/**
	 * Returns a cached server resource. 
	 *
	 * @param type the server type
	 * @param isLocalhost true if the server is local
	 * @return a working copy
	 */
	public IServerWorkingCopy getCachedServer(IServerType type, boolean isLocalhost) {
		try {
			IServerWorkingCopy server = (IServerWorkingCopy) elementCache.get(getKey(type, isLocalhost));
			if (server != null)
				return server;
		} catch (Exception e) {
			// ignore
		}

		return null;
	}

	/**
	 * Clears a cached server resource. 
	 *
	 * @param type the server type
	 * @param isLocalhost true if the server is local
	 */
	public void clearCachedServer(IServerType type, boolean isLocalhost) {
		try {
			elementCache.remove(getKey(type, isLocalhost));
		} catch (Exception e) {
			// ignore
		}
	}
}
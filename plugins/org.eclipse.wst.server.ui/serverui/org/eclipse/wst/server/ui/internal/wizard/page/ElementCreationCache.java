package org.eclipse.wst.server.ui.internal.wizard.page;
/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementations
 **********************************************************************/
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServerConfigurationType;
import org.eclipse.wst.server.core.IServerConfigurationWorkingCopy;
import org.eclipse.wst.server.core.IServerType;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.ServerUtil;
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
	 * @param factory
	 * @return
	 */
	protected String getKey(IServerType type, String host) {
		return type.getId() + "|" + host + "|";
	}

	protected String getKey(IServerConfigurationType type) {
		return type.getId() + "|";
	}

	/**
	 * Returns a server configuration. 
	 *
	 * @param type org.eclipse.wst.server.core.IServerConfigurationTpye
	 * @return org.eclipse.wst.server.core.IServerConfigurationWorkingCopy
	 */
	public IServerConfigurationWorkingCopy getServerConfiguration(IServerConfigurationType type, IRuntime runtime, IProgressMonitor monitor) throws CoreException {
		try {
			IServerConfigurationWorkingCopy configuration = getCachedServerConfiguration(type);
			if (configuration != null)
				return configuration;
		} catch (Exception e) { }

		try {
			IFile file = null;
			if (ServerCore.getServerPreferences().isCreateResourcesInWorkspace())
				file = ServerUtil.getUnusedServerConfigurationFile(WizardUtil.getServerProject(), type);
			
			IServerConfigurationWorkingCopy configuration = type.createServerConfiguration(null, file, monitor);
			if (runtime != null)
				elementCache.put(getKey(type) + runtime.getId(), configuration);
			return configuration;
		} catch (CoreException ce) {
			throw ce;
		}
	}

	/**
	 * Returns a server. 
	 *
	 * @param type org.eclipse.wst.server.core.IServerType
	 * @return org.eclipse.wst.server.core.IServerWorkingCopy
	 */
	public IServerWorkingCopy getServer(IServerType type, String host, IProgressMonitor monitor) throws CoreException {
		try {
			IServerWorkingCopy server = getCachedServer(type, host);
			if (server != null)
				return server;
		} catch (Exception e) { }
	
		try {
			IFile file = null;
			if (ServerCore.getServerPreferences().isCreateResourcesInWorkspace())
				file = ServerUtil.getUnusedServerFile(WizardUtil.getServerProject(), type);
			
			IServerWorkingCopy server = type.createServer(null, file, (IRuntime)null, monitor);
			elementCache.put(getKey(type, host), server);
			return server;
		} catch (CoreException ce) {
			throw ce;
		}
	}

	/**
	 * Returns a cached server resource. 
	 *
	 * @param factory org.eclipse.wst.server.core.model.IServerFactory
	 * @return org.eclipse.wst.server.core.model.IServerResource
	 */
	public IServerWorkingCopy getCachedServer(IServerType type, String host) {
		try {
			IServerWorkingCopy server = (IServerWorkingCopy) elementCache.get(getKey(type, host));
			if (server != null)
				return server;
		} catch (Exception e) { }

		return null;
	}
	
	/**
	 * Returns a cached server resource. 
	 *
	 * @param factory org.eclipse.wst.server.core.model.IServerFactory
	 * @return org.eclipse.wst.server.core.model.IServerResource
	 */
	public IServerConfigurationWorkingCopy getCachedServerConfiguration(IServerConfigurationType type) {
		try {
			IServerConfigurationWorkingCopy wc = (IServerConfigurationWorkingCopy) elementCache.get(getKey(type));
			if (wc != null)
				return wc;
		} catch (Exception e) { }

		return null;
	}
}

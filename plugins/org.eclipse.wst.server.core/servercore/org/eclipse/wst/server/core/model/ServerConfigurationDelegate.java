/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.model;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServerConfiguration;
import org.eclipse.wst.server.core.IServerConfigurationWorkingCopy;
import org.eclipse.wst.server.core.internal.ServerConfiguration;
import org.eclipse.wst.server.core.internal.ServerConfigurationWorkingCopy;
/**
 * A server configuration. Server configurations usually contain
 * directories (the resources to be run on the server) and configuration
 * information. (i.e. mime types, data sources, etc.)
 * <p>
 * This abstract class is intended to be extended only by clients
 * to extend the <code>serverConfigurationTypes</code> extension point.
 * </p>
 */
public abstract class ServerConfigurationDelegate {
	private ServerConfiguration configuration;
	private ServerConfigurationWorkingCopy configurationWC;

	/**
	 * Called when the server is loaded as a model object.
	 */
	public final void initialize(ServerConfiguration configuration2) {
		configuration = configuration2;
		initialize();
	}
	
	public final void initialize(ServerConfigurationWorkingCopy configuration2) {
		configuration = configuration2;
		configurationWC = configuration2;
		initialize();
	}
	
	public void initialize() {
		// do nothing
	}
	
	public IServerConfiguration getServerConfiguration() {
		return configuration;
	}
	
	public IServerConfigurationWorkingCopy getServerConfigurationWC() {
		return configurationWC;
	}

	/**
	 * Called when this server resource has become invalid or no longer
	 * required and is being deregistered or dicarded. This method can
	 * be used to remove listeners, etc.
	 */
	public void dispose() {
		configuration = null;
	}

	public void load(IPath path, IProgressMonitor monitor) throws CoreException {
		throw new CoreException(null);
	}

	public void load(IFolder folder, IProgressMonitor monitor) throws CoreException {
		throw new CoreException(null);
	}
	
	public abstract void save(IPath path, IProgressMonitor monitor) throws CoreException;
	
	public abstract void save(IFolder folder, IProgressMonitor monitor) throws CoreException;

	/**
	 * Initializes this server configuration with default values. This method is called when
	 * a new configuration is created so that the configuration can be initialized with
	 * meaningful values.
	 */
	public void setDefaults() {
		// do nothing;
	}

	public abstract void importFromPath(IPath path, IProgressMonitor monitor) throws CoreException;

	public abstract void importFromRuntime(IRuntime runtime, IProgressMonitor monitor) throws CoreException;
}
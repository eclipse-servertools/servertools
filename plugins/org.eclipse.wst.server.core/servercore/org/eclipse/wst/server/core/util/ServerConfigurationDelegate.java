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
package org.eclipse.wst.server.core.util;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.IServerConfiguration;
import org.eclipse.wst.server.core.model.*;
/**
 * Abstract implementation of IServerConfiguration to implement
 * methods that are not required often.
 */
public abstract class ServerConfigurationDelegate implements IServerConfigurationDelegate {
	protected IServerConfiguration configuration;

	/**
	 * Called when the server is loaded as a model object.
	 */
	public void initialize(IServerConfiguration configuration2) {
		this.configuration = configuration2;
	}
	
	public IServerConfiguration getServerConfiguration() {
		return configuration;
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
}
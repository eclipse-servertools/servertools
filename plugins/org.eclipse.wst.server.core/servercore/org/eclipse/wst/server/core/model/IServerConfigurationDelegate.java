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
import org.eclipse.wst.server.core.IServerConfiguration;
/*
 * A server configuration. Server configurations usually contain
 * directories (the resources to be run on the server) and configuration
 * information. (i.e. mime types, data sources, etc.)
 */
public interface IServerConfigurationDelegate {
	/**
	 * Called when the server is loaded as a model object.
	 */
	public void initialize(IServerConfiguration configuration);

	/**
	 * Called when this server resource has become invalid or no longer
	 * required and is being deregistered or dicarded. This method can
	 * be used to remove listeners, etc.
	 */
	public void dispose();
	
	public void load(IPath path, IProgressMonitor monitor) throws CoreException;

	public void load(IFolder folder, IProgressMonitor monitor) throws CoreException;
	
	public void save(IPath path, IProgressMonitor monitor) throws CoreException;
	
	public void save(IFolder folder, IProgressMonitor monitor) throws CoreException;
}
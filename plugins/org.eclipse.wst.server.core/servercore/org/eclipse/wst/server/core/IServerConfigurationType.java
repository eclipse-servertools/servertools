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
package org.eclipse.wst.server.core;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
/**
 * A server configuration. Server configurations usually contain
 * directories (the resources to be run on the server) and configuration
 * information. (i.e. mime types, data sources, etc.)
 * 
 * <p>This interface is not intended to be implemented by clients.</p>
 */
public interface IServerConfigurationType extends IOrdered {
	/**
	 * 
	 * @return
	 */
	public String getId();

	/**
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * 
	 * @return
	 */
	public String getDescription();

	/**
	 * Returns the extensions to filter when importing the server
	 * resource. If these extensions are given, the resource is
	 * assumed to be a file. If null is returned, the import will
	 * look for folders instead.
	 *
	 * @return java.lang.String[]
	 */
	public String[] getImportFilterExtensions();

	public boolean isFolder();

	public IServerConfigurationWorkingCopy createServerConfiguration(String id, IFile file, IProgressMonitor monitor) throws CoreException;
	
	public IServerConfigurationWorkingCopy importFromPath(String id, IFile file, IPath path, IProgressMonitor monitor) throws CoreException;
	
	public IServerConfigurationWorkingCopy importFromRuntime(String id, IFile file, IRuntime runtime, IProgressMonitor monitor) throws CoreException;
}
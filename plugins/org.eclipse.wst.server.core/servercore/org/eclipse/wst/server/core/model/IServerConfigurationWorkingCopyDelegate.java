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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServerConfigurationWorkingCopy;
/**
 * 
 */
public interface IServerConfigurationWorkingCopyDelegate extends IServerConfigurationDelegate {
	public static final byte PRE_SAVE = 0;
	public static final byte POST_SAVE = 1;

	/**
	 * Called when the server is loaded as a model object.
	 */
	public void initialize(IServerConfigurationWorkingCopy configuration);
	
	public void setDefaults();
	
	public void importFromPath(IPath path, IProgressMonitor monitor) throws CoreException;

	public void importFromRuntime(IRuntime runtime, IProgressMonitor monitor) throws CoreException;
	
	/**
	 * Handle a save of this server configuration. This method is called when the server
	 * configuration working copy save() method is invoked and can be used to resolve
	 * calculated fields or perform other operations related to the changes
	 * that are being made.
	 * 
	 * @param id
	 * @param monitor
	 */
	public void handleSave(byte id, IProgressMonitor monitor);
}
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
import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.wst.server.core.IServerWorkingCopy;
/**
 *
 */
public interface IServerWorkingCopyDelegate extends IServerDelegate {
	public static final byte PRE_SAVE = 0;
	public static final byte POST_SAVE = 1;

	/**
	 * Called when the server is loaded as a model object.
	 */
	public void initialize(IServerWorkingCopy workingCopy);

	public void setDefaults();

	/**
	 * Add the given module to this configuration. The
	 * module must exist, should not already be deployed
	 * within the configuration, and canAddProject()
	 * should have returned true. The configuration must assume
	 * any default settings and add the module without any UI.
	 *
	 * @param add org.eclipse.wst.server.core.model.IModule[]
	 * @param remove org.eclipse.wst.server.core.model.IModule[] 
	 * @param monitor org.eclipse.core.runtime.IProgressMonitor
	 */
	public void modifyModules(IModule[] add, IModule[] remove, IProgressMonitor monitor) throws CoreException;

	/**
	 * Handle a save of this server. This method is called when the server
	 * working copy save() method is invoked and can be used to resolve
	 * calculated fields or perform other operations related to the changes
	 * that are being made.
	 * 
	 * @param id
	 * @param monitor
	 */
	public void handleSave(byte id, IProgressMonitor monitor);
}
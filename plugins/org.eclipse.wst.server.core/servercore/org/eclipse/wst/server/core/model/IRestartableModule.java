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
import org.eclipse.core.runtime.IStatus;
/**
 * An interface for a server that allows individual modules
 * to be restarted while the server is running. This interface
 * can be implemented by a server to allow it's
 * individual modules to be restarted one at a time.
 */
public interface IRestartableModule extends IServerDelegate {
	/**
	 * Returns true if the given module can be restarted.
	 *
	 * @param module org.eclipse.wst.server.core.model.IModule
	 * @return boolean
	 */
	public boolean canRestartModule(IModule module);

	/**
	 * Check if the given module is in sync on the server. It should
	 * return true if the module should be restarted (is out of
	 * sync) or false if the module does not need to be restarted.
	 *
	 * @param module org.eclipse.wst.server.core.model.IModule
	 * @return boolean
	 */
	public boolean isModuleRestartNeeded(IModule module);

	/**
	 * Restart the user module on the server. This method should
	 * update the module sync state and fire an event for the
	 * module.
	 *
	 * @param module org.eclipse.wst.server.core.model.IModule
	 * @param monitor org.eclipse.core.runtime.IProgressMonitor
	 */
	public IStatus restartModule(IModule module, IProgressMonitor monitor) throws CoreException;
}
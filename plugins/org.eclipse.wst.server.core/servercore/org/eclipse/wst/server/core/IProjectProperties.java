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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.model.IProjectPropertiesListener;
/**
 * This interface holds information on the properties of a given project.
 *
 * <p>This interface is not intended to be implemented by clients.</p>
 */
public interface IProjectProperties {
	/**
	 * Returns the preferred runtime server for the project. This method
	 * returns null if the server was never chosen or does not currently exist. (if the
	 * server is recreated or was in a closed project, etc. this method will return
	 * the original value if it becomes available again)
	 *
	 * @return org.eclipse.wst.server.core.IServer
	 */
	public IServer getDefaultServer();

	/**
	 * Sets the preferred runtime server for the project. Set the server to
	 * null to clear the setting. If there is a problem saving the file, a CoreException
	 * will be thrown.
	 *
	 * @param server org.eclipse.wst.server.model.IServer
	 * @param monitor org.eclipse.core.runtime.IProgressMonitor
	 * @throws org.eclipse.core.runtime.CoreException
	 */
	public void setDefaultServer(IServer server, IProgressMonitor monitor) throws CoreException;

	/**
	 * Returns the current runtime target for this project.
	 * 
	 * @return
	 */
	public IRuntime getRuntimeTarget();

	/**
	 * Sets the runtime target for the project.
	 * 
	 * @param target
	 * @param monitor
	 */
	public void setRuntimeTarget(IRuntime runtime, IProgressMonitor monitor) throws CoreException;

	/**
	 * 
	 * @param listener
	 */
	public void addProjectPropertiesListener(IProjectPropertiesListener listener);

	/**
	 * 
	 * @param listener
	 */
	public void removeProjectPropertiesListener(IProjectPropertiesListener listener);
}
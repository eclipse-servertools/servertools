/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
/**
 * This interface holds information on the properties of a given project.
 *
 * <p>This interface is not intended to be implemented by clients.</p>
 * 
 * @see IProjectPropertiesListener
 * @since 1.0
 */
public interface IProjectProperties {
	/**
	 * Returns the preferred runtime server for the project. This method
	 * returns null if the server was never chosen or does not currently exist. (if the
	 * server is recreated or was in a closed project, etc. this method will return
	 * the original value if it becomes available again)
	 *
	 * @return the current default server, or <code>null</code> if there is no
	 *    default server
	 */
	public IServer getDefaultServer();

	/**
	 * Sets the preferred runtime server for the project. Set the server to
	 * null to clear the setting. If there is a problem saving the file, a CoreException
	 * will be thrown.
	 *
	 * @param server the server to set the default server, or <code>null</code>
	 *    to unset the default
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @throws CoreException if there is a problem setting the default server
	 */
	public void setDefaultServer(IServer server, IProgressMonitor monitor) throws CoreException;

	/**
	 * Returns the current runtime target for this project.
	 * 
	 * @return the current runtime target, or <code>null</code> if the project has
	 *    no runtime target 
	 */
	public IRuntime getRuntimeTarget();

	/**
	 * Sets the runtime target for the project.
	 * 
	 * @param runtime the runtime to use as the target, or <code>null</code> to
	 *    unset the target 
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @throws CoreException if there is a problem setting the runtime target
	 */
	public void setRuntimeTarget(IRuntime runtime, IProgressMonitor monitor) throws CoreException;
}
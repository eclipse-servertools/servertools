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
package org.eclipse.wst.server.core.model;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeTargetHandler;
/**
 * 
 * @since 1.0
 */
public abstract class RuntimeTargetHandlerDelegate {
	private IRuntimeTargetHandler handler;

	/**
	 * Initializes the runtime target handler.
	 * 
	 * @param newHandler the new handler
	 */
	public final void initialize(IRuntimeTargetHandler newHandler) {
		handler = newHandler;
	}

	/**
	 * Returns the runtime target handler that this delegate is associated with.
	 * 
	 * @return the runtime target handler
	 */
	public IRuntimeTargetHandler getRuntimeTargetHandler() {
		return handler;
	}

	/**
	 * Set the runtime target on the given project.
	 * 
	 * @param project the project to set the runtime on
	 * @param runtime the target runtime
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @throws CoreException thrown if there is a problem setting the runtime
	 */
	public abstract void setRuntimeTarget(IProject project, IRuntime runtime, IProgressMonitor monitor) throws CoreException;

	/**
	 * Remove the runtime target from the given project.
	 * 
	 * @param project the project to remove the runtime from
	 * @param runtime the target runtime
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @throws CoreException thrown if there is a problem removing the runtime
	 */
	public abstract void removeRuntimeTarget(IProject project, IRuntime runtime, IProgressMonitor monitor) throws CoreException;
}
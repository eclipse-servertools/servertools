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
 * A runtime target handler is used when associating a runtime with
 * a particular project. It has the ability to make any changes it requires
 * on the projects - adding or removing files, setting up the classpath, etc.
 * <p>
 * This abstract class is intended to be extended only by clients
 * to extend the <code>runtimeTargetHandlers</code> extension point.
 * </p>
 * <p>
 * 
 * @deprecated This function is deprecated.
 */
public abstract class RuntimeTargetHandlerDelegate {
	/**
	 * Initializes the runtime target handler.
	 * 
	 * @param newHandler the new handler
	 */
	public final void initialize(IRuntimeTargetHandler newHandler) {
		throw new RuntimeException("Attempt to use deprecated code");
	}

	/**
	 * Returns the runtime target handler that this delegate is associated with.
	 * 
	 * @return the runtime target handler
	 */
	public IRuntimeTargetHandler getRuntimeTargetHandler() {
		throw new RuntimeException("Attempt to use deprecated code");
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
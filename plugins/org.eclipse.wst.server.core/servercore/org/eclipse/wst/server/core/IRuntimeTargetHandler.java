/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
/**
 * 
 * <p>This interface is not intended to be implemented by clients.</p>
 */
public interface IRuntimeTargetHandler extends IOrdered, IAdaptable {
	/**
	 * 
	 * @return
	 */
	public String getId();

	/**
	 * Returns true if this runtime target handler supports (can work with) the
	 * given runtime.
	 * 
	 * @param runtimeType
	 * @return
	 */
	public boolean supportsRuntimeType(IRuntimeType runtimeType);

	/**
	 * Set the runtime target on the given project.
	 *  
	 * @param project
	 * @param runtime
	 * @param monitor
	 * @throws CoreException
	 */
	public void setRuntimeTarget(IProject project, IRuntime runtime, IProgressMonitor monitor) throws CoreException;

	/**
	 * Remove the runtime target from the given project. This method will undo
	 * all changes made in setRuntimeTarget().
	 * 
	 * @param project
	 * @param runtime
	 * @param monitor
	 * @throws CoreException
	 */
	public void removeRuntimeTarget(IProject project, IRuntime runtime, IProgressMonitor monitor) throws CoreException;
}
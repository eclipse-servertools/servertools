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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServerExtension;
/**
 * 
 */
public abstract class RuntimeTargetHandlerDelegate implements IServerExtension {
	/**
	 * Set the runtime target on the given project.
	 * 
	 * @param project
	 * @param runtime
	 * @param monitor
	 * @throws CoreException
	 */
	public abstract void setRuntimeTarget(IProject project, IRuntime runtime, IProgressMonitor monitor) throws CoreException;
	
	/**
	 * Remove the runtime target from the given project.
	 * 
	 * @param project
	 * @param runtime
	 * @param monitor
	 * @throws CoreException
	 */
	public abstract void removeRuntimeTarget(IProject project, IRuntime runtime, IProgressMonitor monitor) throws CoreException;
}
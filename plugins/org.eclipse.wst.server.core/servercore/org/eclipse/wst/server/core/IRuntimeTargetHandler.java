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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
/**
 * A runtime target handler is used to apply some properties to a project
 * this is being targeted to a given runtime. For instance, the handler
 * might update the classpath of a Java project to include the runtime's
 * classes, add validation for the given runtime, or restrict the type of
 * resources that can be created.
 * 
 * <p>This interface is not intended to be implemented by clients.</p>
 */
public interface IRuntimeTargetHandler extends IAdaptable {
	/**
	 * Returns the id of this runtime target handler.
	 * Each known runtime target handler has a distinct id. 
	 * Ids are intended to be used internally as keys; they are not
	 * intended to be shown to end users.
	 * 
	 * @return the runtime target handler id
	 */
	public String getId();

	/**
	 * Returns <code>true</code> if this runtime target handler supports
	 * (can work with) the given runtime.
	 * 
	 * @param runtimeType a runtime type
	 * @return <code>true</code> if the handler can accept the given runtime type,
	 *    and <code>false</code> otherwise
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
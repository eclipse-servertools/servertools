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
 * <p>
 * <b>Provisional API:</b> This class/interface is part of an interim API that is still under development and expected to 
 * change significantly before reaching stability. It is being made available at this early stage to solicit feedback 
 * from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken 
 * (repeatedly) as the API evolves.
 * </p>
 * 
 * @plannedfor 1.0
 */
public interface IProjectProperties {
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
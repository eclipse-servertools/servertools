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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.model.IRuntimeTargetHandlerDelegate;
/**
 * 
 * <p>This interface is not intended to be implemented by clients.</p>
 */
public interface IRuntimeTargetHandler extends IOrdered {
	/**
	 * 
	 * @return
	 */
	public String getId();

	public boolean supportsRuntimeType(IRuntimeType runtimeType);

	public IRuntimeTargetHandlerDelegate getDelegate();

	public void setRuntimeTarget(IProject project, IRuntime runtime, IProgressMonitor monitor) throws CoreException;
	
	public void removeRuntimeTarget(IProject project, IRuntime runtime, IProgressMonitor monitor) throws CoreException;
}
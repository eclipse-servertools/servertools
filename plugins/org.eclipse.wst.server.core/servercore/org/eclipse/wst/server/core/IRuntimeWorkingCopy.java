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
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.model.IRuntimeWorkingCopyDelegate;
/**
 * 
 * <p>This interface is not intended to be implemented by clients.</p>
 */
public interface IRuntimeWorkingCopy extends IRuntime, IElementWorkingCopy {	
	public IRuntime getOriginal();
	
	public IRuntimeWorkingCopyDelegate getWorkingCopyDelegate();
	
	public void setLocation(IPath path);
	
	public IRuntime save(IProgressMonitor monitor) throws CoreException;
	
	public void setTestEnvironment(boolean b);
}
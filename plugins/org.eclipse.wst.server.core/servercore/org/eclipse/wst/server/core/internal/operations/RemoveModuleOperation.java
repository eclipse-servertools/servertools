/**********************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.internal.operations;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.server.core.IModule;
/**
 * Remove module operation.
 */
public class RemoveModuleOperation implements IServerOperation {
	private IModule module;

	public RemoveModuleOperation(IModule module) {
		this.module = module;
	}
	
	public IModule getModule() {
		return module;
	}
	
	public IStatus execute(IProgressMonitor monitor) throws CoreException {
		return null;
	}
}
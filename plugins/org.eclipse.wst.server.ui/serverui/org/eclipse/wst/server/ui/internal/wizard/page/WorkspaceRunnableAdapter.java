/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui.internal.wizard.page;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
/**
 * IRunnableWithProgressAdapter to allow it to run an IWorkspaceRunnable.
 */
public class WorkspaceRunnableAdapter implements IRunnableWithProgress {
	private IWorkspaceRunnable workspaceRunnable;
	
	public WorkspaceRunnableAdapter(IWorkspaceRunnable runnable) {
		workspaceRunnable = runnable;
	}

	/*
	 * @see IRunnableWithProgress#run(IProgressMonitor)
	 */
	public void run(IProgressMonitor monitor) throws InvocationTargetException {
		try {
			ResourcesPlugin.getWorkspace().run(workspaceRunnable, monitor);
		} catch (CoreException e) {
			throw new InvocationTargetException(e);
		}
	}
}
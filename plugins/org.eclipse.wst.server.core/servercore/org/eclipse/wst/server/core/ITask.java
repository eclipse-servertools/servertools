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
 * 
 * <p>This interface is not intended to be implemented by clients.</p>
 */
public interface ITask {
	/**
	 * Returns the label for this command.
	 *
	 * @return java.lang.String
	 */
	public String getName();

	/**
	 * Returns a description of this command.
	 *
	 * @return java.lang.String
	 */
	public String getDescription();

	public ITaskModel getTaskModel();

	public void setTaskModel(ITaskModel taskModel);

	public boolean canExecute();

	public void execute(IProgressMonitor monitor) throws CoreException;

	public boolean canUndo();
	
	public void undo();
}
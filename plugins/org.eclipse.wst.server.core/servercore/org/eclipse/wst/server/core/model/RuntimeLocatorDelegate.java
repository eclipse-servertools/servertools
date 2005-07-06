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

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
/**
 * A runtime locator provides the ability to locate or search for additional
 * runtimes of a particular type.
 * <p>
 * This abstract class is intended to be extended only by clients
 * to extend the <code>runtimeLocators</code> extension point.
 * </p>
 * 
 * @plannedfor 1.0
 */
public abstract class RuntimeLocatorDelegate {
	/**
	 * A callback listener used to report progress.
	 */
	public interface IRuntimeSearchListener {
		/**
		 * Called when a new runtime is found by the locator.
		 * The runtime must never be null.
		 * 
		 * @param runtime the runtime that was found.
		 */
		public void runtimeFound(IRuntimeWorkingCopy runtime);
	}

	/**
	 * Searches for local runtimes. 
	 * It uses the callback listener to report runtimes that are found.
	 * The path contains the absolute path of the folder to search in,
	 * or <code>null</code> to search the entire machine.
	 * 
	 * @param path the path to search for runtimes in
	 * @param listener a listener to report status to
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 */
	public abstract void searchForRuntimes(IPath path, IRuntimeSearchListener listener, IProgressMonitor monitor);
}
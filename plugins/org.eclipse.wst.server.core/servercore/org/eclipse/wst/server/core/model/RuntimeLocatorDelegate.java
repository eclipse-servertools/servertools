/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.model;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.IRuntimeLocator;
/**
 * A runtime locator provides the ability to locate or search for additional
 * runtimes of a particular type.
 * <p>
 * This abstract class is intended to be extended only by clients
 * to extend the <code>runtimeLocators</code> extension point.
 * </p>
 * 
 * @see IRuntimeLocator
 * @since 1.0
 */
public abstract class RuntimeLocatorDelegate {
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
	 * @see IRuntimeLocator.searchForRuntimes(IPath, IRuntimeLocator.RuntimeSearchListener, IProgressMonitor)
	 */
	public abstract void searchForRuntimes(IPath path, IRuntimeLocator.RuntimeSearchListener listener, IProgressMonitor monitor);
}
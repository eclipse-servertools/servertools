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

import org.eclipse.core.runtime.IProgressMonitor;
/**
 * An interface to allow searching for runtimes.
 */
public abstract class RuntimeLocatorDelegate {
	/**
	 * 
	 * @param listener
	 * @param monitor
	 */
	public abstract void searchForRuntimes(IRuntimeLocatorListener listener, IProgressMonitor monitor);
}
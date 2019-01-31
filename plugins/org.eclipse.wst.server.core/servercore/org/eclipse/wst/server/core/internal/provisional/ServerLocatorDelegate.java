/*******************************************************************************
 * Copyright (c) 2003, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.internal.provisional;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.IServerWorkingCopy;
/**
 * A server locator provides the ability to locate or search for additional
 * server of a particular type, on a particular host.
 * <p>
 * This abstract class is intended to be extended only by clients
 * to extend the <code>serverLocators</code> extension point.
 * </p>
 */
public abstract class ServerLocatorDelegate {
	/**
	 * A callback listener used to report progress.
	 */
	public interface IServerSearchListener {
		/**
		 * Called when a new server is found by the locator.
		 * The server must never be null.
		 * 
		 * @param server the runtime that was found.
		 */
		public void serverFound(IServerWorkingCopy server);
	}

	/**
	 * Searches for servers. 
	 * It uses the callback listener to report servers that are found.
	 * 
	 * @param host a host string conforming to RFC 2732
	 * @param listener a listener to report status to
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 */
	public abstract void searchForServers(String host, IServerSearchListener listener, IProgressMonitor monitor);
}
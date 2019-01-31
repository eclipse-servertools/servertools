/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
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
package org.eclipse.wst.server.core.internal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.IServerWorkingCopy;
/**
 * A server locator provides the ability to locate or search for additional
 * servers of a particular type, on a particular host.
 * <p>
 * Server locators are found via ServerCore.getServerLocators().
 * </p>
 * 
 * <p>This interface is not intended to be implemented by clients.</p>
 */
public interface IServerLocator {
	/**
	 * A callback listener interface.
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
	 * Returns the id of this server locator.
	 * Each known server locator has a distinct id. 
	 * Ids are intended to be used internally as keys; they are not
	 * intended to be shown to end users.
	 * 
	 * @return the server locator id
	 */
	public String getId();

	/**
	 * Returns true if the server locator can find servers of the given type.
	 * The id should never be null.
	 * 
	 * @param serverTypeId the id of a server type
	 * @return boolean
	 */
	public boolean supportsType(String serverTypeId);
	
	/**
	 * Returns <code>true</code> if this type of server can run on a remote host.
	 * Returns <code>false</code> if the server type can only be run on "localhost"
	 * (the local machine).
	 * 
	 * @return <code>true</code> if this type of server can run on
	 *    a remote host, and <code>false</code> if it cannot
	 */
	public boolean supportsRemoteHosts();

	/**
	 * Searches for servers. 
	 * It uses the callback listener to report servers that are found.
	 * 
	 * @param host a host string conforming to RFC 2732
	 * @param listener a listener to report status to
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @throws CoreException
	 */
	public void searchForServers(String host, IServerSearchListener listener, IProgressMonitor monitor) throws CoreException;
}
/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
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
import org.eclipse.core.runtime.IProgressMonitor;
/**
 * A server locator provides the ability to locate or search for additional
 * servers of a particular type, on a particular host.
 * 
 * <p>This interface is not intended to be implemented by clients.</p>
 * 
 * @since 1.0
 */
public interface IServerLocator {
	public interface Listener {
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
	 * Returns the displayable name for this server locator.
	 * <p>
	 * Note that this name is appropriate for the current locale.
	 * </p>
	 *
	 * @return a displayable name for this server locator
	 */
	public String getName();

	/**
	 * Returns the displayable description for this server locator.
	 * <p>
	 * Note that this description is appropriate for the current locale.
	 * </p>
	 *
	 * @return a displayable description for this server locator
	 */
	public String getDescription();

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
	 * <p>
	 * [issue: Should be renamed "supportsRemoteHost" (no "s").]
	 * </p>
	 * <p>
	 * [issue: Again, it seems odd to me that this is something
	 * hard-wired to a server type.]
	 * </p>
	 * 
	 * @return <code>true</code> if this type of server can run on
	 * a remote host, and <code>false</code> if it cannot
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
	public void searchForServers(String host, Listener listener, IProgressMonitor monitor) throws CoreException;
}
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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
/**
 * 
 * <p>This interface is not intended to be implemented by clients.</p>
 */
public interface IServerType extends IOrdered {
	//	--- State Set Constants ---
	// (returned from the getServerStateSet() method)
	
	// a server that can be directly started/stopped, etc.
	public static final byte SERVER_STATE_SET_MANAGED = 0;
	
	// a server that is attached to, typically for debugging
	public static final byte SERVER_STATE_SET_ATTACHED = 1;
	
	// a server that is only used for publishing
	public static final byte SERVER_STATE_SET_PUBLISHED = 2;

	/**
	 * 
	 * @return
	 */
	public String getId();

	/**
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * 
	 * @return
	 */
	public String getDescription();

	/**
	 * 
	 * @return
	 */
	public IRuntimeType getRuntimeType();
	
	public boolean hasRuntime();
	
	/**
	 * Returns true if this server can start or may already be started
	 * in the given mode, and false if not. Uses the launchConfigId attribute
	 * to find the server's launch configuration.
	 * 
	 * @param launchMode String
	 * @return boolean
	 */
	public boolean supportsLaunchMode(String launchMode);

	/**
	 * Returns an IStatus message to verify if a server of this type will be able
	 * to run the module immediately after being created, without any user
	 * interaction. If OK, this server may be used as a default server. This
	 * method should return ERROR if the user must supply any information to
	 * configure the server correctly, or if the module is not supported.
	 *
	 * @return org.eclipse.core.resources.IStatus
	 */
	//public IStatus isDefaultAvailable(IModule module);

	/**
	 * Returns the server state set that should be used to represent
	 * this server. If the state set is SERVER_STATE_SET_MANAGED, this is
	 * a runnable server that may be directly started and stopped.
	 * (i.e. it should be represented as starting, started in debug mode,
	 * etc.) If the state set is SERVER_STATE_SET_ATTACHED, this is a
	 * server that can be attached to, typically for debugging purposes.
	 * (i.e. it should be represented as attaching, attached for
	 * debugging, etc.)
	 *
	 * @return byte
	 */
	public byte getServerStateSet();

	public IServerConfigurationType getServerConfigurationType();

	public boolean hasServerConfiguration();

	public boolean supportsLocalhost();

	public boolean supportsRemoteHosts();

	/**
	 * Returns true if the "monitorable" attribute is set. If true, this
	 * server's delegate should implement IMonitorableServer.
	 * 
	 * @return boolean
	 */
	public boolean isMonitorable();

	/**
	 * Returns true if the "testEnvironment" attribute is set. If true, this
	 * server can only be created when there is an existing runtime that has
	 * the property "testEnvironment" set to true.
	 * 
	 * @return boolean
	 */
	public boolean isTestEnvironment();

	/**
	 * Create a server. If file is null, it will be created in metadata. Otherwise,
	 * it will be associated with the given file.
	 */
	public IServerWorkingCopy createServer(String id, IFile file, IRuntime runtime) throws CoreException;
	
	public IServerWorkingCopy createServer(String id, IFile file, IProgressMonitor monitor) throws CoreException;
}
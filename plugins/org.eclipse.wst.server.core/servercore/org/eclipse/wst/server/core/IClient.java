/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.ILaunch;

/**
 * A launchable client is a client side application or test
 * harness that can be launched (run) against a resource
 * running on a server.
 * 
 * <p>This interface is not intended to be implemented by clients.</p>
 */
public interface IClient {
	/**
	 * Returns the id of the adapter.
	 *
	 * @return java.lang.String
	 */
	public String getId();

	/**
	 * Returns the label (name) of this client.
	 * 
	 * @return java.lang.String
	 */
	public String getName();

	/**
	 * Returns the description of this client.
	 * 
	 * @return java.lang.String
	 */
	public String getDescription();

	/**
	 * Returns true if this launchable can be run by this client.
	 * 
	 * @param server org.eclipse.wst.server.core.IServer
	 * @param launchable org.eclipse.wst.server.core.ILaunchable
	 * @param launchMode String
	 * @return boolean
	 */
	public boolean supports(IServer server, ILaunchable launchable, String launchMode);

	/**
	 * Launches the client.
	 * 
	 * @param server org.eclipse.wst.server.core.IServer
	 * @param launchable org.eclipse.wst.server.core.ILaunchable
	 * @param launchMode String
	 * @param launch org.eclipse.debug.core.ILaunch
	 * @return org.eclipse.core.runtime.IStatus
	 */
	public IStatus launch(IServer server, ILaunchable launchable, String launchMode, ILaunch launch);
}
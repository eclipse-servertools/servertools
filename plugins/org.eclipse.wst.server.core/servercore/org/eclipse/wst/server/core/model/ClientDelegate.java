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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.wst.server.core.IServer;
/**
 * A launchable client is a client side application or test harness that can
 * be launched (run) against a resource running on a server.
 * <p>
 * This abstract class is intended to be extended only by clients
 * to extend the <code>clients</code> extension point.
 * </p>
 * 
 * @since 1.0
 */
public abstract class ClientDelegate {
	/**
	 * Returns true if this launchable can be run by this client.
	 * 
	 * @param server the server that the client is being run against
	 * @param launchable the object to run on the server
	 * @param launchMode the launch mode
	 * @return <code>true</code> if the client supports this combination, and <code>false</code>
	 *    otherwise
	 */
	public abstract boolean supports(IServer server, Object launchable, String launchMode);

	/**
	 * Opens or executes on the launchable.
	 * 
	 * @param server the server that the client is being run against
	 * @param launchable the object to run on the server
	 * @param launchMode the launch mode
	 * @param launch the launch of the server, if available
	 * @return status indicating what (if anything) went wrong
	 */
	public abstract IStatus launch(IServer server, Object launchable, String launchMode, ILaunch launch);
}
/**********************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.internal;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.wst.server.core.IServer;
/**
 * A launchable client is a client side application or test
 * harness that can be launched (run) against a resource
 * running on a server.
 * 
 * <p>This interface is not intended to be implemented by clients.</p>
 */
public interface IClient {
	/**
	 * Returns the id of this client. Each known client has a distinct id. 
	 * Ids are intended to be used internally as keys; they are not
	 * intended to be shown to end users.
	 * 
	 * @return the client id
	 */
	public String getId();

	/**
	 * Returns the displayable name for this client.
	 * <p>
	 * Note that this name is appropriate for the current locale.
	 * </p>
	 *
	 * @return a displayable name for this client
	 */
	public String getName();

	/**
	 * Returns the displayable description for this client.
	 * <p>
	 * Note that this description is appropriate for the current locale.
	 * </p>
	 *
	 * @return a displayable description for this client
	 */
	public String getDescription();

	/**
	 * Returns true if this launchable can be run by this client.
	 * 
	 * @param server the server
	 * @param launchable the launchable object
	 * @param launchMode the mode
	 * @return boolean
	 */
	public boolean supports(IServer server, Object launchable, String launchMode);

	/**
	 * Launches the client.
	 * 
	 * @param server the server
	 * @param launchable the launchable object
	 * @param launchMode the mode
	 * @param launch the launch
	 * @return a status object with code <code>IStatus.OK</code> if the launch was
	 *   successful, otherwise a status object indicating what went wrong
	 */
	public IStatus launch(IServer server, Object launchable, String launchMode, ILaunch launch);
}
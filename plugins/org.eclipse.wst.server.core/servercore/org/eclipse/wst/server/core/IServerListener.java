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
package org.eclipse.wst.server.core;

import org.eclipse.wst.server.core.util.ServerEvent;
/**
 * This interface is used by the server to broadcast a change of state.
 * Usually, the change of state will be caused by some user action,
 * (e.g. requesting to start a server) however, it is equally fine for
 * a server to broadcast a change of state through no direct user action.
 * (e.g. stopping because the server crashed) This information can be
 * used to inform the user of the change or update the UI.
 *
 * <p>Note: The server listener event MUST NOT directly be used to modify
 * the server's or module's state via one of the server's method. For example, 
 * a server stopped event cannot directly trigger a start(). Doing this may 
 * cause the thread to hang.</p>
 *   
 * @since 1.0
 */
public interface IServerListener {
	/**
	 * A server or module has been changed as specified in the event.
	 * 
	 * @param event a server event that contains information on the change
	 */
	public void serverChanged(ServerEvent event); 
}
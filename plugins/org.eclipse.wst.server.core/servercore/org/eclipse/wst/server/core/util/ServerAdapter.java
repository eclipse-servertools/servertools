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
package org.eclipse.wst.server.core.util;

import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerListener;
/**
 * Helper class which implements the IServerListener interface
 * with empty methods.
 * 
 * @see org.eclipse.wst.server.core.IServerListener
 * @since 1.0
 */
public class ServerAdapter implements IServerListener {
	/**
	 * @see IServerListener#restartStateChange(IServer)
	 */
	public void restartStateChange(IServer server) {
		// do nothing
	}

	/**
	 * @see IServerListener#serverStateChange(IServer)
	 */
	public void serverStateChange(IServer server) {
		// do nothing
	}

	/**
	 * @see IServerListener#modulesChanged(IServer)
	 */
	public void modulesChanged(IServer server) {
		// do nothing
	}

	/**
	 * @see IServerListener#moduleStateChange(IServer, IModule[])
	 */
	public void moduleStateChange(IServer server, IModule[] module) {
		// do nothing
	}
}
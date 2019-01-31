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
package org.eclipse.wst.server.core.util;

import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerLifecycleListener;
/**
 * Helper class which implements the IServerLifecycleListener interface
 * with empty methods.
 * 
 * @see org.eclipse.wst.server.core.IServerLifecycleListener
 * @since 1.0
 */
public class ServerLifecycleAdapter implements IServerLifecycleListener {
	/**
	 * @see IServerLifecycleListener#serverAdded(IServer)
	 */
	public void serverAdded(IServer server) {
		// do nothing
	}

	/**
	 * @see IServerLifecycleListener#serverChanged(IServer)
	 */
	public void serverChanged(IServer server) {
		// do nothing
	}

	/**
	 * @see IServerLifecycleListener#serverRemoved(IServer)
	 */
	public void serverRemoved(IServer server) {
		// do nothing
	}
}
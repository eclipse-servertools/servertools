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
package org.eclipse.wst.server.core.model;
/**
 * A server may implement this interface if it can
 * be restarted within the same process. If this interface is
 * implemented, the restartInProcess() method will be called
 * when the server is restarted, instead of calling stop(),
 * waiting, and then starting the server again.
 */
public interface IRestartableServer extends IStartableServer {
	/**
	 * Restart this server. The server should use the server
	 * listener to notify progress. It must use the same debug
	 * flags as was originally passed into the start() method.
	 */
	public void restart(String launchMode);
}
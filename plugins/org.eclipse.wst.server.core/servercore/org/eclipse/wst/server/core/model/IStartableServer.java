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
 * A startable server. This interface just provides the extra methods for starting,
 * stopping, and terminating the server.
 */
public interface IStartableServer extends IServerDelegate {
	/**
	 * Return true if the server should be terminated before the workbench
	 * shutdown and false if not. If the server is not terminated when
	 * workbench shutdown, then the server should get reconnected
	 * in the server load when the workbench startsup.
	 * 
	 * @return boolean
	 */
	public boolean isTerminateOnShutdown();

	/**
	 * Cleanly shuts down and stops this server. The
	 * server should return from this method quickly and
	 * use the server listener to notify shutdown progress.
	 *
	 * <p>This method should not be called directly! Use the
	 * IServerControl to correctly start and register
	 * the server.</p>
	 */
	public void stop();
	
	/**
	 * Terminate the server process(es). This method should only be
	 * used as a last resort after the stop() method fails to work.
	 * The server should return from this method quickly and
	 * use the server listener to notify shutdown progress.
	 * It MUST terminate the server completely and return it to
	 * the stopped state.
	 */
	public void terminate();
	
	/**
	 * Return the timeout (in ms) that should be used to wa
	 * 
	 * @return
	 */
	public int getStartTimeout();

	/**
	 * Return the timeout (in ms) to wait before assuming that the server
	 * has failed to stop.
	 *  
	 * @return
	 */
	public int getStopTimeout();
}
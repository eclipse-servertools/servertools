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
package org.eclipse.wst.server.core.internal;
/**
 * An interface to a TCP/IP monitor.
 * 
 * <p>This interface is not intended to be implemented by clients.</p>
 */
public interface IServerMonitor {
	/**
	 * Returns the id of the monitor.
	 *
	 * @return java.lang.String
	 */
	public String getId();

	/**
	 * Returns the label (name) of this monitor.
	 * 
	 * @return java.lang.String
	 */
	public String getName();

	/**
	 * Returns the description of this monitor.
	 * 
	 * @return java.lang.String
	 */
	public String getDescription();

	/**
	 * Start monitoring the given port, and return the port number to
	 * tunnel requests through. The monitorPort is the new port to use, or
	 * -1 to pick a random port.
	 * 
	 * @param port
	 * @return
	 */
	//public int startMonitoring(IServer server, IServerPort port, int monitorPort) throws CoreException;

	/**
	 * Stop monitoring the given port.
	 * 
	 * @param port
	 */
	//public void stopMonitoring(IServer server, IServerPort port);
}
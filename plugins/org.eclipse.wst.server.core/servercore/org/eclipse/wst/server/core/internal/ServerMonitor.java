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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerPort;
/**
 * 
 */
public class ServerMonitor implements IServerMonitor {
	private IConfigurationElement element;
	private ServerMonitorDelegate delegate;

	/**
	 * Monitor constructor comment.
	 */
	public ServerMonitor(IConfigurationElement element) {
		super();
		this.element = element;
	}

	/**
	 * Returns the id of this default server.
	 *
	 * @return java.lang.String
	 */
	public String getId() {
		return element.getAttribute("id");
	}

	/*
	 * @see IMonitor#getDescription()
	 */
	public String getDescription() {
		return element.getAttribute("description");
	}

	/*
	 * @see IMonitor#getLabel()
	 */
	public String getName() {
		String label = element.getAttribute("name");
		if (label == null)
			return "n/a";
		return label;
	}

	/*
	 * @see IMonitor#getDelegate()
	 */
	public ServerMonitorDelegate getDelegate() {
		if (delegate == null) {
			try {
				delegate = (ServerMonitorDelegate) element.createExecutableExtension("class");
			} catch (Throwable t) {
				Trace.trace(Trace.SEVERE, "Could not create delegate" + toString() + ": " + t.getMessage());
			}
		}
		return delegate;
	}

	/**
	 * Start monitoring the given port, and return the port number to
	 * tunnel requests through.
	 * @param port
	 * @return the port used for monitoring
	 */
	public int startMonitoring(IServer server, ServerPort port, int monitorPort) throws CoreException {
		try {
			return getDelegate().startMonitoring(server, port, monitorPort);
		} catch (CoreException ce) {
			throw ce;
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error calling delegate " + toString() + ": " + e.getMessage());
			return -1;
		}
	}

	/**
	 * Stop monitoring the given port.
	 * @param port
	 */
	public void stopMonitoring(IServer server, ServerPort port) {
		try {
			getDelegate().stopMonitoring(server, port);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error calling delegate " + toString() + ": " + e.getMessage());
		}
	}

	/**
	 * Return a string representation of this object.
	 * 
	 * @return java.lang.String
	 */
	public String toString() {
		return "Monitor[" + getId() + "]";
	}
}
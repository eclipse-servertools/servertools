/*******************************************************************************
 * Copyright (c) 2003, 2018 IBM Corporation and others.
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
package org.eclipse.wst.server.core.internal;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;

import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.model.ClientDelegate;
/**
 * 
 */
public class Client implements IClient {
	private IConfigurationElement element;
	private ClientDelegate delegate;

	/**
	 * Create a new client.
	 * 
	 * @param element a configuration element
	 */
	public Client(IConfigurationElement element) {
		super();
		this.element = element;
	}

	/**
	 * @see IClient#getId()
	 */
	public String getId() {
		return element.getAttribute("id");
	}

	/**
	 * Returns the relative priority of this adapter.
	 *
	 * @return a priority
	 */
	public int getPriority() {
		try {
			return Integer.parseInt(element.getAttribute("priority"));
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * @see IClient#getDescription()
	 */
	public String getDescription() {
		return element.getAttribute("description");
	}

	protected String getLaunchable() {
		return element.getAttribute("launchable");
	}

	/**
	 * @see IClient#getName()
	 */
	public String getName() {
		String label = element.getAttribute("name");
		if (label == null)
			return "n/a";
		return label;
	}

	/**
	 * Return the delegate.
	 * 
	 * @return the delegate, or <code>null</code> if it couldn't be loaded
	 */
	public ClientDelegate getDelegate() {
		if (delegate == null) {
			try {
				delegate = (ClientDelegate) element.createExecutableExtension("class");
			} catch (Exception e) {
				if (Trace.SEVERE) {
					Trace.trace(Trace.STRING_SEVERE, "Could not create delegate" + toString(), e);
				}
			}
		}
		return delegate;
	}

	/**
	 * @see IClient#supports(IServer, Object, String)
	 */
	public boolean supports(IServer server, Object launchable, String launchMode) {
		if (launchable == null)
			return false;
		//if (!launchable.getClass().getName().equals(getLaunchable()))
		//	return false;
		try {
			return getDelegate().supports(server, launchable, launchMode);
		} catch (Exception e) {
			if (Trace.SEVERE) {
				Trace.trace(Trace.STRING_SEVERE, "Error calling delegate " + toString(), e);
			}
			return false;
		}
	}

	/**
	 * @see IClient#launch(IServer, Object, String, ILaunch)
	 */
	public IStatus launch(IServer server, Object launchable, String launchMode, ILaunch launch) {
		try {
			return getDelegate().launch(server, launchable, launchMode, launch);
		} catch (Exception e) {
			if (Trace.SEVERE) {
				Trace.trace(Trace.STRING_SEVERE, "Error calling delegate " + toString(), e);
			}
			return new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, e.getMessage(), e);
		}
	}

	/**
	 * Return a string representation of this object.
	 * 
	 * @return String
	 */
	public String toString() {
		return "Client[" + getId() + "]";
	}
}
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
package org.eclipse.wst.server.core.internal;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.ILaunch;

import org.eclipse.wst.server.core.IClient;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.model.IClientDelegate;
import org.eclipse.wst.server.core.model.ILaunchable;
/**
 * 
 */
public class Client implements IClient {
	private IConfigurationElement element;
	private IClientDelegate delegate;

	/**
	 * LaunchableClient constructor comment.
	 */
	public Client(IConfigurationElement element) {
		super();
		this.element = element;
	}

	/**
	 * Returns the id of this LaunchableClient.
	 *
	 * @return java.lang.String
	 */
	public String getId() {
		return element.getAttribute("id");
	}

	/*
	 * @see IPublishManager#getDescription()
	 */
	public String getDescription() {
		return element.getAttribute("description");
	}

	/*
	 * @see IPublishManager#getLabel()
	 */
	public String getName() {
		String label = element.getAttribute("name");
		if (label == null)
			return "n/a";
		else
			return label;
	}

	/*
	 * @see IPublishManager#getDelegate()
	 */
	public IClientDelegate getDelegate() {
		if (delegate == null) {
			try {
				delegate = (IClientDelegate) element.createExecutableExtension("class");
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "Could not create delegate" + toString() + ": " + e.getMessage());
			}
		}
		return delegate;
	}

	/**
	 * 
	 */
	public boolean supports(IServer server, ILaunchable launchable, String launchMode) {
		try {
			return getDelegate().supports(server, launchable, launchMode);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error calling delegate " + toString() + ": " + e.getMessage());
			return false;
		}
	}

	/**
	 * Opens or executes on the launchable.
	 */
	public IStatus launch(IServer server, ILaunchable launchable, String launchMode, ILaunch launch) {
		try {
			return getDelegate().launch(server, launchable, launchMode, launch);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error calling delegate " + toString() + ": " + e.getMessage());
		}
		return null;
	}
	
	/**
	 * Return a string representation of this object.
	 * 
	 * @return java.lang.String
	 */
	public String toString() {
		return "LaunchableClient[" + getId() + "]";
	}
}

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

import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;

import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.model.*;
/**
 * 
 */
public class ServerTask implements IServerTask {
	private IConfigurationElement element;
	private ServerTaskDelegate delegate;

	/**
	 * ServerTask constructor comment.
	 */
	public ServerTask(IConfigurationElement element) {
		super();
		this.element = element;
	}

	/*
	 * @see
	 */
	public String getId() {
		return element.getAttribute("id");
	}

	/*
	 * @see
	 */
	protected String[] getTypeIds() {
		try {
			return ServerPlugin.tokenize(element.getAttribute("typeIds"), ",");
		} catch (Exception e) {
			return null;
		}
	}

	/*
	 * @see
	 */
	public boolean supportsType(String id) {
		return ServerPlugin.supportsType(getTypeIds(), id);
	}

	/*
	 * @see IServerTask#getDelegate()
	 */
	public ServerTaskDelegate getDelegate() {
		if (delegate == null) {
			try {
				delegate = (ServerTaskDelegate) element.createExecutableExtension("class");
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "Could not create delegate" + toString() + ": " + e.getMessage());
			}
		}
		return delegate;
	}
	
	/*
	 * @see
	 */
	public IOptionalTask[] getTasks(IServer server, IServerConfiguration configuration, List[] parents, IModule[] modules) {
		try {
			Trace.trace(Trace.FINEST, "Task.init " + this);
			return getDelegate().getTasks(server, configuration, parents, modules);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error calling delegate " + toString() + ": " + e.getMessage());
		}
		return new IOptionalTask[0];
	}

	/**
	 * Return a string representation of this object.
	 * 
	 * @return java.lang.String
	 */
	public String toString() {
		return "ServerTask[" + getId() + "]";
	}
}
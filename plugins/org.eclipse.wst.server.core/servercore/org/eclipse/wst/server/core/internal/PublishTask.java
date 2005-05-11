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

import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;

import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.model.*;
/**
 * 
 */
public class PublishTask implements IPublishTask {
	private IConfigurationElement element;
	private PublishTaskDelegate delegate;

	/**
	 * PublishTask constructor comment.
	 * 
	 * @param element a configuration element 
	 */
	public PublishTask(IConfigurationElement element) {
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
	 * @see IPublishTask#getDelegate()
	 */
	public PublishTaskDelegate getDelegate() {
		if (delegate == null) {
			try {
				delegate = (PublishTaskDelegate) element.createExecutableExtension("class");
			} catch (Throwable t) {
				Trace.trace(Trace.SEVERE, "Could not create delegate" + toString() + ": " + t.getMessage());
			}
		}
		return delegate;
	}
	
	/*
	 * @see
	 */
	public PublishOperation[] getTasks(IServer server, List modules) {
		try {
			Trace.trace(Trace.FINEST, "Task.init " + this);
			return getDelegate().getTasks(server, modules);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error calling delegate " + toString() + ": " + e.getMessage());
		}
		return new PublishOperation[0];
	}

	/**
	 * Return a string representation of this object.
	 * 
	 * @return java.lang.String
	 */
	public String toString() {
		return "PublishTask[" + getId() + "]";
	}
}
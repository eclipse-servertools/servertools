/*******************************************************************************
 * Copyright (c) 2003, 2011 IBM Corporation and others.
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

import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.model.PublishOperation;
import org.eclipse.wst.server.core.model.PublishTaskDelegate;
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

	public String getId() {
		return element.getAttribute("id");
	}

	protected String[] getTypeIds() {
		try {
			return ServerPlugin.tokenize(element.getAttribute("typeIds"), ",");
		} catch (Exception e) {
			return null;
		}
	}

	public boolean supportsType(String id) {
		return ServerPlugin.contains(getTypeIds(), id);
	}

	/*
	 * @see IPublishTask#getDelegate()
	 */
	public PublishTaskDelegate getDelegate() {
		if (delegate == null) {
			try {
				long time = System.currentTimeMillis();
				delegate = (PublishTaskDelegate) element.createExecutableExtension("class");
				if (Trace.PERFORMANCE) {
					Trace.trace(Trace.STRING_PERFORMANCE, "PublishTask.getDelegate(): <"
							+ (System.currentTimeMillis() - time) + "> " + getId());
				}
			} catch (Throwable t) {
				if (Trace.SEVERE) {
					Trace.trace(Trace.STRING_SEVERE, "Could not create delegate" + toString(), t);
				}
			}
		}
		return delegate;
	}

	/*
	 * @see
	 */
	public PublishOperation[] getTasks(IServer server, List modules) {
		try {
			if (Trace.FINEST) {
				Trace.trace(Trace.STRING_FINEST, "Task.init " + this);
			}
			PublishOperation[] po = getDelegate().getTasks(server, modules);
			if (po != null)
				return po;
		} catch (Exception e) {
			if (Trace.SEVERE) {
				Trace.trace(Trace.STRING_SEVERE, "Error calling delegate " + toString(), e);
			}
		}
		return new PublishOperation[0];
	}

	/*
	 * @see
	 */
	public PublishOperation[] getTasks(IServer server, int kind, List modules, List kindList) {
		try {
			if (Trace.FINEST) {
				Trace.trace(Trace.STRING_FINEST, "Task.init " + this);
			}
			PublishOperation[] po = getDelegate().getTasks(server, kind, modules, kindList);
			if (po != null)
				return po;
		} catch (Exception e) {
			if (Trace.SEVERE) {
				Trace.trace(Trace.STRING_SEVERE, "Error calling delegate " + toString(), e);
			}
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
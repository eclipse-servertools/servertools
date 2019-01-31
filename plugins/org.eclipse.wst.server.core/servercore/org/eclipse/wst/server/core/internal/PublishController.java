/*******************************************************************************
 * Copyright (c) 2010, 2011 IBM Corporation and others.
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

import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.model.PublishControllerDelegate;
/**
 * 
 */
public class PublishController {
	private IConfigurationElement element;
	private PublishControllerDelegate delegate;

	/**
	 * Publisher constructor comment.
	 * 
	 * @param element a configuration element 
	 */
	public PublishController(IConfigurationElement element) {
		super();
		this.element = element;
	}

	/*
	 * @see
	 */
	public String getId() {
		return element.getAttribute("id");
	}

	public String getName() {
		return element.getAttribute("name");
	}

	public String getDescription() {
		return element.getAttribute("description");
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
	 * @see IPublisher#getDelegate()
	 */
	public PublishControllerDelegate getDelegate() {
		if (delegate == null) {
			try {
				long time = System.currentTimeMillis();
				delegate = (PublishControllerDelegate) element.createExecutableExtension("class");
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

	public boolean isPublishRequired(IServer server, IResourceDelta delta) {
		try {
			if (Trace.FINEST) {
				Trace.trace(Trace.STRING_FINEST, "Task.init " + this);
			}
			return getDelegate().isPublishRequired(server, delta);
		} catch (Exception e) {
			if (Trace.SEVERE) {
				Trace.trace(Trace.STRING_SEVERE, "Error calling delegate " + toString(), e);
			}
			return true;
		}
	}
	

	/**
	 * Return a string representation of this object.
	 * 
	 * @return a string
	 */
	public String toString() {
		return "PublishController[" + getId() + "]";
	}
}
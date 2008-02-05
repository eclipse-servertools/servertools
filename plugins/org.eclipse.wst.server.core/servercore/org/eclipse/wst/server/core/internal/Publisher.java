/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
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
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.server.core.TaskModel;
import org.eclipse.wst.server.core.model.PublisherDelegate;
/**
 * 
 */
public class Publisher {
	private IConfigurationElement element;
	private PublisherDelegate delegate;

	/**
	 * Publisher constructor comment.
	 * 
	 * @param element a configuration element 
	 */
	public Publisher(IConfigurationElement element) {
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
	public PublisherDelegate getDelegate() {
		if (delegate == null) {
			try {
				long time = System.currentTimeMillis();
				delegate = (PublisherDelegate) element.createExecutableExtension("class");
				Trace.trace(Trace.PERFORMANCE, "PublishTask.getDelegate(): <" + (System.currentTimeMillis() - time) + "> " + getId());
			} catch (Throwable t) {
				Trace.trace(Trace.SEVERE, "Could not create delegate" + toString(), t);
			}
		}
		return delegate;
	}

	public IStatus execute(int kind, IProgressMonitor monitor, IAdaptable info) throws CoreException {
		try {
			Trace.trace(Trace.FINEST, "Task.init " + this);
			return getDelegate().execute(kind, monitor, info);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error calling delegate " + toString(), e);
			return new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, "Error in delegate", e); // TODO
		}
	}

	public void setTaskModel(TaskModel taskModel) {
		try {
			getDelegate().setTaskModel(taskModel);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error calling delegate " + toString(), e);
		}
	}

	/**
	 * Return a string representation of this object.
	 * 
	 * @return a string
	 */
	public String toString() {
		return "Publisher[" + getId() + "]";
	}
}
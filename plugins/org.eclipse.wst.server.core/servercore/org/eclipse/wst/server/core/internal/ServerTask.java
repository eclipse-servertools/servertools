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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.model.*;
/**
 * 
 */
public class ServerTask implements IServerTask {
	private IConfigurationElement element;
	private ServerTaskDelegate delegate;
	protected ITaskModel model;

	/**
	 * ServerTask constructor comment.
	 */
	public ServerTask(IConfigurationElement element) {
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
		return label;
	}
	
	/**
	 * Returns the index (ordering) of this task.
	 *
	 * @return int
	 */
	public int getOrder() {
		try {
			return Integer.parseInt(element.getAttribute("index"));
		} catch (NumberFormatException e) {
			return -1;
		}
	}
	
	public ITaskModel getTaskModel() {
		return model;
	}
	
	public void setTaskModel(ITaskModel taskModel) {
		this.model = taskModel;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.ui.internal.task.ITask#canExecute()
	 */
	public boolean canExecute() {
		return true;
	}
	
	/**
	 * Return the type ids that may be supported.
	 * 
	 * @return java.lang.String[]
	 */
	public String[] getTypeIds() {
		try {
			return ServerPlugin.tokenize(element.getAttribute("typeIds"), ",");
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Returns true if the given type (given by the id) can use this task. This
	 * result is based on the result of the getTypeIds() method.
	 *
	 * @return boolean
	 */
	public boolean supportsType(String id) {
		if (id == null || id.length() == 0)
			return false;

		String[] s = getTypeIds();
		if (s == null)
			return true;
		
		int size = s.length;
		for (int i = 0; i < size; i++) {
			if (s[i].endsWith("*")) {
				if (id.length() >= s[i].length() && id.startsWith(s[i].substring(0, s[i].length() - 1)))
					return true;
			} else if (id.equals(s[i]))
				return true;
		}
		return false;
	}

	/*
	 * @see IPublishManager#getDelegate()
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
	
	/**
	 * Lets the task know that it is about to be used. This method should
	 * be used to clean out any previously cached information, or start to
	 * create a new cache.
	 */
	public void init(IServer server, IServerConfiguration configuration, List[] parents, IModule[] modules) {
		try {
			Trace.trace(Trace.FINEST, "Task.init " + this);
			getDelegate().init(server, configuration, parents, modules);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error calling delegate " + toString() + ": " + e.getMessage());
		}
	}
	
	/**
	 * Returns the status of this task.
	 * 
	 * @return byte
	 */
	public byte getTaskStatus() {
		try {
			Trace.trace(Trace.FINEST, "Task.getTaskStatus " + this);
			return getDelegate().getTaskStatus();
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error calling delegate " + toString() + ": " + e.getMessage());
			return 0;
		}
	}

	/**
	 * Perform the task.
	 *
	 * @return org.eclipse.core.runtime.IStatus
	 */
	public void execute(IProgressMonitor monitor) throws CoreException {
		try {
			Trace.trace(Trace.FINEST, "Task.execute " + this);
			getDelegate().execute(monitor);
		} catch (CoreException ce) {
			Trace.trace(Trace.FINER, "Task error " + toString(), ce);
			throw ce;
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error calling delegate " + toString() + ": " + e.getMessage());
		}
	}
	
	public boolean canUndo() {
		return false;
	}
	
	public void undo() { }
	
	/**
	 * Return a string representation of this object.
	 * 
	 * @return java.lang.String
	 */
	public String toString() {
		return "ServerTask[" + getId() + "]";
	}
}

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

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.model.RuntimeTargetHandlerDelegate;
/**
 * 
 */
public class RuntimeTargetHandler implements IRuntimeTargetHandler, IOrdered {
	private IConfigurationElement element;
	private RuntimeTargetHandlerDelegate delegate;

	public RuntimeTargetHandler(IConfigurationElement element) {
		super();
		this.element = element;
	}
	
	protected IConfigurationElement getElement() {
		return element;
	}

	/**
	 * 
	 * @return
	 */
	public String getId() {
		return element.getAttribute("id");
	}

	/**
	 * Returns the order.
	 *
	 * @return int
	 */
	public int getOrder() {
		try {
			String o = element.getAttribute("order");
			return Integer.parseInt(o);
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	public String[] getRuntimeTypeIds() {
		try {
			List list = new ArrayList();
			StringTokenizer st = new StringTokenizer(element.getAttribute("runtimeTypeIds"), ",");
			while (st.hasMoreTokens()) {
				String str = st.nextToken();
				if (str != null && str.length() > 0)
					list.add(str.trim());
			}
			String[] s = new String[list.size()];
			list.toArray(s);
			return s;
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Could not parse runtime type ids: " + element);
			return null;
		}
	}

	/**
	 * Returns true if the given server resource type (given by the
	 * id) can be opened with this editor. This result is based on
	 * the result of the getServerResources() method.
	 *
	 * @return boolean
	 */
	public boolean supportsRuntimeType(IRuntimeType runtimeType) {
		if (runtimeType == null)
			return false;
		String id = runtimeType.getId();
		if (id == null || id.length() == 0)
			return false;

		String[] s = getRuntimeTypeIds();
		if (s == null)
			return false;
		
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
	public RuntimeTargetHandlerDelegate getDelegate() {
		if (delegate == null) {
			try {
				delegate = (RuntimeTargetHandlerDelegate) element.createExecutableExtension("class");
				delegate.initialize(this);
			} catch (Throwable t) {
				Trace.trace(Trace.SEVERE, "Could not create delegate " + toString() + ": " + t.getMessage());
			}
		}
		return delegate;
	}

	/**
	 * 
	 */
	public void setRuntimeTarget(IProject project, IRuntime runtime, IProgressMonitor monitor) {
		if (project == null || runtime == null)
			return;
		try {
			getDelegate().setRuntimeTarget(project, runtime, monitor);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error calling delegate " + toString() + ": " + e.getMessage());
		}
	}
	
	/**
	 * 
	 */
	public void removeRuntimeTarget(IProject project, IRuntime runtime, IProgressMonitor monitor) {
		if (project == null || runtime == null)
			return;
		try {
			getDelegate().removeRuntimeTarget(project, runtime, monitor);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error calling delegate " + toString() + ": " + e.getMessage());
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		RuntimeTargetHandlerDelegate delegate2 = getDelegate();
		if (adapter.isInstance(delegate2))
			return delegate;
		return null;
	}

	public String toString() {
		return "RuntimeTargetHandler[" + getId() + "]";
	}
}
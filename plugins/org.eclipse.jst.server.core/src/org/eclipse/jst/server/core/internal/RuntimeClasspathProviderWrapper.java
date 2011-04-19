/*******************************************************************************
 * Copyright (c) 2005, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.core.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jst.server.core.RuntimeClasspathProviderDelegate;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.internal.ServerPlugin;
/**
 * 
 */
public class RuntimeClasspathProviderWrapper {
	private IConfigurationElement element;
	private volatile RuntimeClasspathProviderDelegate delegate;

	/**
	 * Create a new runtime target handler.
	 * 
	 * @param element a configuration element
	 */
	public RuntimeClasspathProviderWrapper(IConfigurationElement element) {
		super();
		this.element = element;
	}
	
	protected IConfigurationElement getElement() {
		return element;
	}

	/**
	 * 
	 * @return the id
	 */
	public String getId() {
		return element.getAttribute("id");
	}

	/**
	 * Returns the order.
	 *
	 * @return the order
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
			List<String> list = new ArrayList<String>();
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
			if (Trace.SEVERE) {
				Trace.trace(Trace.STRING_SEVERE, "Could not parse runtime type ids: " + element);
			}
			return null;
		}
	}

	/**
	 * Returns true if the given server resource type (given by the
	 * id) can be opened with this editor. This result is based on
	 * the result of the getServerResources() method.
	 *
	 * @param runtimeType a runtime type
	 * @return boolean
	 */
	public boolean supportsRuntimeType(IRuntimeType runtimeType) {
		if (runtimeType == null)
			return false;
		return supportsRuntimeType(runtimeType.getId());
	}

	/**
	 * Returns true if the given server resource type (given by the
	 * id) can be opened with this editor. This result is based on
	 * the result of the getServerResources() method.
	 *
	 * @param id a runtime type id
	 * @return boolean
	 */
	public boolean supportsRuntimeType(String id) {
		if (id == null || id.length() == 0)
			return false;
		
		String[] s = getRuntimeTypeIds();
		if (s == null)
			return false;
		
		for (String ss : s) {
			if (ss.endsWith("*")) {
				if (id.length() >= ss.length() && id.startsWith(ss.substring(0, ss.length() - 1)))
					return true;
			} else if (id.equals(ss))
				return true;
		}
		return false;
	}

	/*
	 * Loads the delegate class.
	 */
	public RuntimeClasspathProviderDelegate getDelegate() {
		if (delegate == null) {
			try {
				// Create delegate unsynchronized to avoid possible deadlocks
				RuntimeClasspathProviderDelegate tempDelegate = (RuntimeClasspathProviderDelegate) element.createExecutableExtension("class");
				tempDelegate.initialize(getId());
				// If delegate is not already set, use this delegate
				synchronized (this) {
					if (delegate == null) {
						delegate = tempDelegate;
					}
				}
			} catch (Throwable t) {
				ServerPlugin.logExtensionFailure(toString(), t);
			}
		}
		return delegate;
	}

	/*
	 * @see RuntimeClasspathProviderDelegate#resolveClasspathContainerImpl(IProject, IRuntime)
	 */
	public IClasspathEntry[] resolveClasspathContainerImpl(IProject project, IRuntime runtime) {
		if (runtime == null)
			return null;
		try {
			return getDelegate().resolveClasspathContainerImpl(project, runtime);
		} catch (Exception e) {
			ServerPlugin.logExtensionFailure(toString(), e);
		}
		return null;
	}

	public void requestClasspathContainerUpdate(IRuntime runtime, IClasspathEntry[] entries) {
		if (runtime == null)
			return;
		try {
			getDelegate().requestClasspathContainerUpdate(runtime, entries);
		} catch (Exception e) {
			ServerPlugin.logExtensionFailure(toString(), e);
		}
	}

	/*
	 * @see RuntimeClasspathProviderDelegate#hasRuntimeClasspathChanged(IRuntime)
	 */
	public boolean hasRuntimeClasspathChanged(IRuntime runtime) {
		if (runtime == null)
			return false;
		try {
			return getDelegate().hasRuntimeClasspathChanged(runtime);
		} catch (Exception e) {
			ServerPlugin.logExtensionFailure(toString(), e);
		}
		return false;
	}

	public String toString() {
		return "RuntimeClasspathProviderWrapper[" + getId() + "]";
	}
}

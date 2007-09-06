/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
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
/**
 * 
 */
public class RuntimeClasspathProviderWrapper {
	private IConfigurationElement element;
	private RuntimeClasspathProviderDelegate delegate;

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
			Trace.trace(Trace.SEVERE, "Could not parse runtime type ids: " + element);
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
	 * Loads the delegate class.
	 */
	public RuntimeClasspathProviderDelegate getDelegate() {
		if (delegate == null) {
			try {
				delegate = (RuntimeClasspathProviderDelegate) element.createExecutableExtension("class");
				delegate.initialize(getId());
			} catch (Throwable t) {
				Trace.trace(Trace.SEVERE, "Could not create delegate " + toString(), t);
			}
		}
		return delegate;
	}

	private int count;
	/*
	 * @see RuntimeClasspathProviderDelegate#resolveClasspathContainerImpl(IProject, IRuntime)
	 */
	public IClasspathEntry[] resolveClasspathContainerImpl(IProject project, IRuntime runtime) {
		if (runtime == null)
			return null;
		try {
			count++;
			if (count % 500 == 0)
				System.out.println(count);
			return getDelegate().resolveClasspathContainerImpl(project, runtime);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error calling delegate " + toString(), e);
		}
		return null;
	}

	/*
	 * @see RuntimeClasspathProviderDelegate#requestClasspathContainerUpdate(IRuntime, IClasspathEntry[])
	 */
	public void requestClasspathContainerUpdate(IRuntime runtime, IClasspathEntry[] entries) {
		if (runtime == null)
			return;
		try {
			getDelegate().requestClasspathContainerUpdate(runtime, entries);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error calling delegate " + toString(), e);
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
			Trace.trace(Trace.SEVERE, "Error calling delegate " + toString(), e);
		}
		return false;
	}

	public String toString() {
		return "RuntimeClasspathProviderWrapper[" + getId() + "]";
	}
}

/*******************************************************************************
 * Copyright (c) 2006, 2011 IBM Corporation and others.
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

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponent;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.internal.ServerPlugin;
/**
 * 
 */
public class RuntimeComponentProviderWrapper {
	private IConfigurationElement element;
	private RuntimeComponentProviderDelegate delegate;

	/**
	 * Create a new runtime component handler.
	 * 
	 * @param element a configuration element
	 */
	public RuntimeComponentProviderWrapper(IConfigurationElement element) {
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
		String id = runtimeType.getId();
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
	protected RuntimeComponentProviderDelegate getDelegate() {
		if (delegate == null) {
			try {
				delegate = (RuntimeComponentProviderDelegate) element.createExecutableExtension("class");
			} catch (Throwable t) {
				ServerPlugin.logExtensionFailure(toString(), t);
			}
		}
		return delegate;
	}

	/*
	 * @see RuntimeFacetComponentProviderDelegate#getRuntimeComponents(IRuntime)
	 */
	public List<IRuntimeComponent> getComponents(IRuntime runtime) {
		if (runtime == null)
			return null;
		try {
			return getDelegate().getRuntimeComponents(runtime);
		} catch (Exception e) {
			ServerPlugin.logExtensionFailure(toString(), e);
		}
		return null;
	}

	public String toString() {
		return "RuntimeComponentProviderWrapper[" + getId() + "]";
	}
}
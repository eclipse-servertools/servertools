/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.internal;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.IRuntimeLocator;
import org.eclipse.wst.server.core.model.RuntimeLocatorDelegate;
/**
 * 
 */
public class RuntimeLocator implements IRuntimeLocator {
	private IConfigurationElement element;
	private RuntimeLocatorDelegate delegate;

	public RuntimeLocator(IConfigurationElement element) {
		super();
		this.element = element;
	}
	
	protected IConfigurationElement getElement() {
		return element;
	}

	/*
	 * @see IRuntimeLocator#getId()
	 */
	public String getId() {
		return element.getAttribute("id");
	}

	/*
	 * @see IRuntimeLocator#getName()
	 */
	public String getName() {
		return element.getAttribute("name");
	}

	/*
	 * @see IRuntimeLocator#getDescription()
	 */
	public String getDescription() {
		return element.getAttribute("description");
	}

	/*
	 * @see IRuntimeLocator
	 */
	protected String[] getTypeIds() {
		try {
			return ServerPlugin.tokenize(element.getAttribute("typeIds"), ",");
		} catch (Exception e) {
			return null;
		}
	}

	/*
	 * @see IRuntimeLocator
	 */
	public boolean supportsType(String id) {
		return ServerPlugin.supportsType(getTypeIds(), id);
	}

	protected RuntimeLocatorDelegate getDelegate() {
		if (delegate == null) {
			try {
				delegate = (RuntimeLocatorDelegate) element.createExecutableExtension("class");
			} catch (Throwable t) {
				Trace.trace(Trace.SEVERE, "Could not create delegate " + toString() + ": " + t.getMessage());
			}
		}
		return delegate;
	}

	/*
	 * @see IRuntimeLocator#searchForRuntimes()
	 */
	public void searchForRuntimes(IPath path, RuntimeSearchListener found, IProgressMonitor monitor) {
		try {
			getDelegate().searchForRuntimes(path, found, monitor);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error calling delegate " + toString() + ": " + e.getMessage());
		}
	}

	public String toString() {
		return "RuntimeLocator[" + getId() + ", " + getName() + "]";
	}
}
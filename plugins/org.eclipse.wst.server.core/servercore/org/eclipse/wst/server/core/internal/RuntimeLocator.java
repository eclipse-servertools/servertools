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

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.IRuntimeLocator;
import org.eclipse.wst.server.core.model.IRuntimeLocatorDelegate;
import org.eclipse.wst.server.core.model.IRuntimeLocatorListener;
/**
 * 
 */
public class RuntimeLocator implements IRuntimeLocator {
	private IConfigurationElement element;
	private IRuntimeLocatorDelegate delegate;

	public RuntimeLocator(IConfigurationElement element) {
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
	 * 
	 * @return
	 */
	public String getName() {
		return element.getAttribute("name");
	}

	/**
	 * 
	 * @return
	 */
	public String getDescription() {
		return element.getAttribute("description");
	}
	
	/*
	 * @see IPublishManager#getDelegate()
	 */
	protected IRuntimeLocatorDelegate getDelegate() {
		if (delegate == null) {
			try {
				delegate = (IRuntimeLocatorDelegate) element.createExecutableExtension("class");
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "Could not create delegate " + toString() + ": " + e.getMessage());
			}
		}
		return delegate;
	}

	/**
	 * 
	 */
	public void searchForRuntimes(IRuntimeLocatorListener found, IProgressMonitor monitor) {
		try {
			getDelegate().searchForRuntimes(found, monitor);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error calling delegate " + toString() + ": " + e.getMessage());
		}
	}

	public String toString() {
		return "RuntimeLocator[" + getId() + ", " + getName() + "]";
	}
}
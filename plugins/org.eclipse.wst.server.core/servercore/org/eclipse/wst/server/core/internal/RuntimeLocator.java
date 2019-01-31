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

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
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

	protected RuntimeLocatorDelegate getDelegate() {
		if (delegate == null) {
			try {
				delegate = (RuntimeLocatorDelegate) element.createExecutableExtension("class");
			} catch (Throwable t) {
				if (Trace.SEVERE) {
					Trace.trace(Trace.STRING_SEVERE, "Could not create delegate " + toString(), t);
				}
			}
		}
		return delegate;
	}

	/*
	 * @see IRuntimeLocator#searchForRuntimes()
	 */
	public void searchForRuntimes(IPath path, final IRuntimeSearchListener found, IProgressMonitor monitor) {
		try {
			//getDelegate().searchForRuntimes(path, found, monitor);
			getDelegate().searchForRuntimes(path, new RuntimeLocatorDelegate.IRuntimeSearchListener() {
				public void runtimeFound(IRuntimeWorkingCopy runtime) {
					found.runtimeFound(runtime);
				}
			}, monitor);
		} catch (Exception e) {
			if (Trace.SEVERE) {
				Trace.trace(Trace.STRING_SEVERE, "Error calling delegate " + toString(), e);
			}
		}
	}

	public String toString() {
		return "RuntimeLocator[" + getId() + "]";
	}
}

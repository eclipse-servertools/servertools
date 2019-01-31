/*******************************************************************************
 * Copyright (c) 2007, 2011 IBM Corporation and others.
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
package org.eclipse.jst.server.core.internal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.VMRunnerConfiguration;
import org.eclipse.jst.server.core.ServerProfilerDelegate;
/**
 * 
 */
public class ServerProfiler {
	private IConfigurationElement element;
	private ServerProfilerDelegate delegate;

	/**
	 * Create a new server profiler.
	 * 
	 * @param element a configuration element
	 */
	public ServerProfiler(IConfigurationElement element) {
		super();
		this.element = element;
	}

	/**
	 * 
	 * @return the id
	 */
	public String getId() {
		return element.getAttribute("id");
	}

	/**
	 * 
	 * @return the name
	 */
	public String getName() {
		return element.getAttribute("name");
	}

	/*
	 * Loads the delegate class.
	 */
	protected ServerProfilerDelegate getDelegate() {
		if (delegate == null) {
			if (element.getAttribute("class") == null)
				return null;
			try {
				delegate = (ServerProfilerDelegate) element.createExecutableExtension("class");
			} catch (Throwable t) {
				if (Trace.SEVERE) {
					Trace.trace(Trace.STRING_SEVERE, "Could not create delegate " + toString(), t);
				}
			}
		}
		return delegate;
	}

	/**
	 * @deprecated Switch to new API via TODO
	 * @return the VM args
	 */
	public String getVMArgs() {
		return null;
	}

	public void process(ILaunch launch, IVMInstall vmInstall, VMRunnerConfiguration vmConfig, IProgressMonitor monitor) throws CoreException {
		try {
			ServerProfilerDelegate del = getDelegate();
			if (del != null)
				del.process(launch, vmInstall, vmConfig, monitor);
		} catch (CoreException ce) {
			if (Trace.SEVERE) {
				Trace.trace(Trace.STRING_SEVERE, "Could not create delegate " + toString(), ce);
			}
			throw ce;
		}
	}

	public String toString() {
		return "ServerProfiler[" + getId() + "]";
	}
}
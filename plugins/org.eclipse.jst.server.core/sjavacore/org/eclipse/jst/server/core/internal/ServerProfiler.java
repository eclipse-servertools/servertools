/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.core.internal;

import org.eclipse.core.runtime.IConfigurationElement;
/**
 * 
 */
public class ServerProfiler {
	private IConfigurationElement element;

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

	/**
	 * 
	 * @return the VM args
	 */
	public String getVMArgs() {
		// about to launch with profiling. make sure that the profiling plugin is started
		JavaServerPlugin.getInstance().startContributor(element.getContributor());
		
		return element.getAttribute("vmArgs");
	}

	public String toString() {
		return "ServerProfiler[" + getId() + "]";
	}
}
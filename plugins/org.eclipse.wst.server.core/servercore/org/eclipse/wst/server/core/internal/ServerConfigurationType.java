/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.internal;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.wst.server.core.*;
/**
 * 
 */
public class ServerConfigurationType implements IServerConfigurationType, IOrdered {
	protected IConfigurationElement element;

	/**
	 * ServerConfigurationType constructor comment.
	 */
	public ServerConfigurationType(IConfigurationElement element) {
		super();
		this.element = element;
	}
	
	protected IConfigurationElement getElement() {
		return element;
	}

	/**
	 * Returns the id of this factory.
	 *
	 * @return java.lang.String
	 */
	public String getId() {
		return element.getAttribute("id");
	}
	
	public String getName() {
		return element.getAttribute("name");
	}

	public String getDescription() {
		return element.getAttribute("description");
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
	
	public boolean isFolder() {
		return "true".equalsIgnoreCase(element.getAttribute("isFolder"));
	}
	
	/**
	 * Return the import extensions.
	 *
	 * @return java.lang.String
	 */
	public String[] getImportFilterExtensions() {
		String importExt = element.getAttribute("importExtensions");
		if (importExt == null || importExt.length() < 1)
			return null;
		return ServerPlugin.tokenize(importExt, ",");
	}
	
	public IServerConfigurationWorkingCopy createServerConfiguration(String id, IFile file, IProgressMonitor monitor) {
		if (id == null || id.length() == 0)
			id = ServerPlugin.generateId();
		ServerConfigurationWorkingCopy scwc = new ServerConfigurationWorkingCopy(id, file, this);
		scwc.setDefaults(monitor);
		return scwc;
	}
	
	public IServerConfigurationWorkingCopy importFromRuntime(String id, IFile file, IRuntime runtime, IProgressMonitor monitor) throws CoreException {
		if (id == null || id.length() == 0)
			id = ServerPlugin.generateId();
		ServerConfigurationWorkingCopy scwc = new ServerConfigurationWorkingCopy(id, file, this);
		scwc.setDefaults(monitor);
		scwc.importFromRuntime(runtime, monitor);
		return scwc;
	}
	
	public IServerConfigurationWorkingCopy importFromPath(String id, IFile file, IPath path, IProgressMonitor monitor) throws CoreException {
		if (id == null || id.length() == 0)
			id = ServerPlugin.generateId();
		ServerConfigurationWorkingCopy scwc = new ServerConfigurationWorkingCopy(id, file, this);
		scwc.setDefaults(monitor);
		scwc.importFromPath(path, monitor);
		return scwc;
	}

	/**
	 * Return a string representation of this object.
	 * 
	 * @return java.lang.String
	 */
	public String toString() {
		return "ServerConfigurationType[" + getId() + "]";
	}
}
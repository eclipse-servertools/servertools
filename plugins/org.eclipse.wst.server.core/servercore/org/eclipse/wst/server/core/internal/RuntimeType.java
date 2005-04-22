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

import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.*;
/**
 * 
 */
public class RuntimeType implements IRuntimeType {
	private IConfigurationElement element;
	private List moduleTypes;

	public RuntimeType(IConfigurationElement element) {
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
	 * 
	 * @return the name
	 */
	public String getName() {
		return element.getAttribute("name");
	}

	/**
	 * 
	 * @return the description
	 */
	public String getDescription() {
		return element.getAttribute("description");
	}
	
	public String getVendor() {
		String vendor = element.getAttribute("vendor");
		if (vendor == null)
			return Messages.defaultVendor;
		return vendor;
	}
	
	public String getVersion() {
		String version = element.getAttribute("version");
		if (version == null)
			return Messages.defaultVersion;
		return version;
	}
	
	/**
	 * Return the supported module types.
	 * 
	 * @return an array of module types
	 */
	public IModuleType[] getModuleTypes() {
		if (moduleTypes == null)
			moduleTypes = ServerPlugin.getModuleTypes(element.getChildren("moduleType"));

		IModuleType[] mt = new IModuleType[moduleTypes.size()];
		moduleTypes.toArray(mt);
		return mt;
	}
	
	public boolean canCreate() {
		String a = element.getAttribute("class");
		return a != null && a.length() > 0;
	}

	public IRuntimeWorkingCopy createRuntime(String id, IProgressMonitor monitor) {
		RuntimeWorkingCopy rwc = new RuntimeWorkingCopy(null, id, this);
		rwc.setDefaults(monitor);
		return rwc;
	}

	public String toString() {
		return "RuntimeType[" + getId() + ", " + getName() + "]";
	}
}
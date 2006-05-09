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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.model.RuntimeDelegate;
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

	/**
	 * 
	 * @return the id
	 */
	public String getId() {
		try {
			return element.getAttribute("id");
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 
	 * @return the name
	 */
	public String getName() {
		try {
			return element.getAttribute("name");
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 
	 * @return the description
	 */
	public String getDescription() {
		try {
			return element.getAttribute("description");
		} catch (Exception e) {
			return null;
		}
	}
	
	public String getVendor() {
		try {
			String vendor = element.getAttribute("vendor");
			if (vendor != null)
				return vendor;
		} catch (Exception e) {
			// ignore
		}
		return Messages.defaultVendor;
	}
	
	public String getVersion() {
		try {
			String version = element.getAttribute("version");
			if (version != null)
				return version;
		} catch (Exception e) {
			// ignore
		}
		return Messages.defaultVersion;
	}
	
	protected RuntimeDelegate createRuntimeDelegate() throws CoreException {
		try {
			return (RuntimeDelegate) element.createExecutableExtension("class");
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Return the supported module types.
	 * 
	 * @return an array of module types
	 */
	public IModuleType[] getModuleTypes() {
		try {
			if (moduleTypes == null)
				moduleTypes = ServerPlugin.getModuleTypes(element.getChildren("moduleType"));
	
			IModuleType[] mt = new IModuleType[moduleTypes.size()];
			moduleTypes.toArray(mt);
			return mt;
		} catch (Exception e) {
			return new IModuleType[0];
		}
	}

	public boolean canCreate() {
		try {
			String a = element.getAttribute("class");
			return a != null && a.length() > 0;
		} catch (Exception e) {
			return false;
		}
	}

	public IRuntimeWorkingCopy createRuntime(String id, IProgressMonitor monitor) {
		if (element == null)
			return null;
		
		RuntimeWorkingCopy rwc = new RuntimeWorkingCopy(null, id, this);
		rwc.setDefaults(monitor);
		return rwc;
	}

	public void dispose() {
		element = null;
	}

	public String getNamespace() {
		if (element == null)
			return null;
		return element.getDeclaringExtension().getContributor().getName();
	}

	public String toString() {
		return "RuntimeType[" + getId() + ", " + getName() + "]";
	}
}
/*******************************************************************************
 * Copyright (c) 2003, 2014 IBM Corporation and others.
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

import java.util.List;

import org.eclipse.core.runtime.*;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.model.RuntimeDelegate;
/**
 * 
 */
public class RuntimeType implements IRuntimeType {
	private IConfigurationElement element;
	private List<IModuleType> moduleTypes;

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

	public String getFacetRuntimeComponent() {
		try {
			return element.getAttribute("facetRuntimeComponent");
		} catch (Exception e) {
			return null;
		}
	}

	public String getFacetRuntimeVersion() {
		try {
			return element.getAttribute("facetRuntimeVersion");
		} catch (Exception e) {
			return null;
		}
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
				loadModuleTypes();
	
			IModuleType[] mt = new IModuleType[moduleTypes.size()];
			moduleTypes.toArray(mt);
			return mt;
		} catch (Exception e) {
			return new IModuleType[0];
		}
	}
	
	protected void loadModuleTypes(){
		moduleTypes = ServerPlugin.getModuleTypes(element.getChildren("moduleType"));
		ServerPlugin.loadRuntimeModuleTypes(this);
	}
	
	/**
	 * Adds a Loose ModuleType to this runtime  
	 * @param moduleType
	 * @throws CoreException if the moduleType is null or if already added
	 */
	public void addModuleType(IConfigurationElement cfe) throws CoreException{
		if (cfe == null)
			throw new CoreException(new Status(IStatus.ERROR,ServerPlugin.PLUGIN_ID,"<null> moduleType"));
		
		IConfigurationElement [] childs = cfe.getChildren("moduleType");
		if (childs.length < 1)
			throw new CoreException(new Status(IStatus.ERROR,ServerPlugin.PLUGIN_ID,"No moduleType found for runtime"));
		
		List<IModuleType> extraModuleTypes = ServerPlugin.getModuleTypes(childs);
		moduleTypes.addAll(extraModuleTypes);
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
	
	/**
	 * Returns <code>true</code> if this type of runtime can be created manually
	 * from the runtime creation wizard.
	 * Returns <code>false</code> if the runtime type can only be programmatically
	 * and hide from the runtime creation wizard.
	 * 
	 * @return <code>true</code> if this type of runtime can be created manually
	 * from the runtime creation wizard, and <code>false</code> if it cannot.
	 * @since 1.6
	 */
	public boolean supportsManualCreation() {
		try {
			String supportsManualCreation = element.getAttribute("supportsManualCreation");
			return (supportsManualCreation == null || supportsManualCreation.toLowerCase().equals("true"));
		} catch (Exception e) {
			return true;
		}
	}

	public String toString() {
		return "RuntimeType[" + getId() + ", " + getName() + "]";
	}
}
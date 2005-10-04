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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IModuleType;
import org.eclipse.wst.server.core.model.InternalInitializer;
import org.eclipse.wst.server.core.model.ModuleFactoryDelegate;
/**
 * 
 */
public class ModuleFactory implements IOrdered {
	private IConfigurationElement element;
	private ModuleFactoryDelegate delegate;
	private List moduleTypes;
	
	private List modules;

	/**
	 * ModuleFactory constructor comment.
	 * 
	 * @param element a configuration element
	 */
	public ModuleFactory(IConfigurationElement element) {
		super();
		this.element = element;
	}

	/**
	 * Returns the id of this factory.
	 *
	 * @return java.lang.String
	 */
	public String getId() {
		return element.getAttribute("id");
	}
	
	/**
	 * Returns the index (ordering) of this task.
	 *
	 * @return int
	 */
	public int getOrder() {
		try {
			return Integer.parseInt(element.getAttribute("order"));
		} catch (NumberFormatException e) {
			return -1;
		}
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
	
	/**
	 * Returns true if this modules factory produces project modules.
	 *
	 * @return boolean
	 */
	public boolean isProjectModuleFactory() {
		return "true".equalsIgnoreCase(element.getAttribute("projects"));
	}

	/*
	 * @see IModuleFactoryDelegate#getDelegate()
	 */
	public ModuleFactoryDelegate getDelegate(IProgressMonitor monitor) {
		if (delegate == null) {
			try {
				delegate = (ModuleFactoryDelegate) element.createExecutableExtension("class");
				//delegate.initialize(this);
				InternalInitializer.initializeModuleFactoryDelegate(delegate, this, monitor);
				//ResourceManager.getInstance().addModuleFactoryListener(delegate);
			} catch (Throwable t) {
				Trace.trace(Trace.SEVERE, "Could not create delegate " + toString() + ": " + t.getMessage());
			}
		}
		return delegate;
	}

	/*
	 * @see
	 */
	public IModule getModule(String id) {
		IModule[] modules2 = getModules();
		if (modules2 != null) {
			int size = modules2.length;
			for (int i = 0; i < size; i++) {
				Module module = (Module) modules2[i];
				if (id.equals(module.getInternalId()))
					return module;
			}
		}
		return null;
	}

	public void clearModuleCache() {
		modules = null;
	}

	/*
	 * @see
	 */
	public IModule[] getModules() {
		//Trace.trace(Trace.FINER, "getModules() > " + this);
		//modules = null;
		if (modules == null) {
			try {
				modules = new ArrayList();
				IModule[] modules2 = getDelegate(null).getModules();
				if (modules2 != null) {
					int size = modules2.length;
					for (int i = 0; i < size; i++)
						modules.add(modules2[i]);
				}
			} catch (Throwable t) {
				Trace.trace(Trace.SEVERE, "Error calling delegate " + toString() + ": " + t.getMessage());
				return null;
			}
		}
		
		//Trace.trace(Trace.FINER, "getModules() < " + this);
		
		IModule[] m = new IModule[modules.size()];
		modules.toArray(m);
		return m;
	}
	
	/**
	 * Return a string representation of this object.
	 * 
	 * @return java.lang.String
	 */
	public String toString() {
		return "ModuleFactory[" + getId() + "]";
	}
}
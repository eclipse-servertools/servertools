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

import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;

import org.eclipse.wst.server.core.IModuleFactory;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.model.IModule;
import org.eclipse.wst.server.core.model.IModuleFactoryDelegate;
import org.eclipse.wst.server.core.model.IModuleFactoryListener;
/**
 * 
 */
public class ModuleFactory implements IModuleFactory {
	private IConfigurationElement element;
	private IModuleFactoryDelegate delegate;
	private List moduleTypes;

	/**
	 * ModuleFactory constructor comment.
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
			return Integer.parseInt(element.getAttribute("index"));
		} catch (NumberFormatException e) {
			return -1;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public List getModuleTypes() {
		if (moduleTypes == null)
			moduleTypes = ServerPlugin.getModuleTypes(element.getChildren("moduleType"));

		return moduleTypes;
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
	 * @see IPublishManager#getDelegate()
	 */
	public IModuleFactoryDelegate getDelegate() {
		if (delegate == null) {
			try {
				delegate = (IModuleFactoryDelegate) element.createExecutableExtension("class");
				ResourceManager rm = (ResourceManager) ServerCore.getResourceManager();
				rm.addModuleFactoryListener(delegate);
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "Could not create delegate " + toString() + ": " + e.getMessage());
			}
		}
		return delegate;
	}

	/**
	 * Gets a module from a memento.
	 * 
	 * @param memento java.lang.String
	 * @return org.eclipse.wst.server.core.model.IModule
	 */
	public IModule getModule(String memento) {
		try {
			return getDelegate().getModule(memento);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error calling delegate " + toString() + ": " + e.getMessage());
			return null;
		}
	}
	
	/**
	 * Return all modules that are available to be added
	 * to servers. This method might look through projects
	 * to find modules or may return modules from
	 * other sources.
	 *
	 * @return org.eclipse.wst.server.core.model.IModule[]
	 */
	public List getModules() {
		try {
			return getDelegate().getModules();
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error calling delegate " + toString() + ": " + e.getMessage());
			return null;
		}
	}
	
	/**
	 * Add a listener for modules that are added/removed from this
	 * factory.
	 * 
	 * @param listener org.eclipse.wst.server.core.model.IModuleFactoryListener
	 */
	public void addModuleFactoryListener(IModuleFactoryListener listener) {
		try {
			getDelegate().addModuleFactoryListener(listener);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error calling delegate " + toString() + ": " + e.getMessage());
		}
	}
	
	/**
	 * Add a listener for modules that are added/removed from this
	 * factory.
	 * 
	 * @param listener org.eclipse.wst.server.core.model.IModuleFactoryListener
	 */
	public void removeModuleFactoryListener(IModuleFactoryListener listener) {
		try {
			getDelegate().removeModuleFactoryListener(listener);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error calling delegate " + toString() + ": " + e.getMessage());
		}
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

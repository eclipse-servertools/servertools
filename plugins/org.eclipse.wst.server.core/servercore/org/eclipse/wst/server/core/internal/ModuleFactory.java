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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;

import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IModuleType;
import org.eclipse.wst.server.core.model.ModuleFactoryDelegate;
import org.eclipse.wst.server.core.model.IModuleFactoryListener;
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
	 * 
	 * @return
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
	public ModuleFactoryDelegate getDelegate() {
		if (delegate == null) {
			try {
				delegate = (ModuleFactoryDelegate) element.createExecutableExtension("class");
				delegate.initialize(this);
				ResourceManager.getInstance().addModuleFactoryListener(delegate);
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "Could not create delegate " + toString() + ": " + e.getMessage());
			}
		}
		return delegate;
	}

	/*
	 * @see
	 */
	public IModule getModule(String id) {
		if (modules == null)
			getModules();
		Iterator iterator = modules.iterator();
		while (iterator.hasNext()) {
			IModule module = (IModule) iterator.next();
			if (id.equals(module.getId()))
				return module;
		}
		return null;
	}
	
	/*
	 * @see
	 */
	public IModule[] getModules() {
		if (modules == null) {
			try {
				modules = new ArrayList();
				IModule[] modules2 = getDelegate().getModules();
				if (modules2 != null) {
					int size = modules2.length;
					for (int i = 0; i < size; i++)
						modules.add(modules2[i]);
				}
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "Error calling delegate " + toString() + ": " + e.getMessage());
				return null;
			}
		}
		
		IModule[] m = new IModule[modules.size()];
		modules.toArray(m);
		return m;
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
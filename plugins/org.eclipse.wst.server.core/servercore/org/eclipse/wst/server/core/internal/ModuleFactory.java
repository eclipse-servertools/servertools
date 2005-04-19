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

import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IModuleType;
import org.eclipse.wst.server.core.model.ModuleFactoryDelegate;
/**
 * 
 */
public class ModuleFactory implements IOrdered {
	private IConfigurationElement element;
	private ModuleFactoryDelegate delegate;
	private List moduleTypes;
	
	private List modules;
	
	// module factory listeners
	private transient List listeners;

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
	public ModuleFactoryDelegate getDelegate() {
		if (delegate == null) {
			try {
				delegate = (ModuleFactoryDelegate) element.createExecutableExtension("class");
				delegate.initialize(this);
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
	
	/*
	 * @see
	 */
	public IModule[] getModules() {
		modules = null;
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
	 * Adds the given listener to this module factory.
	 * Once registered, a listener starts receiving notification of 
	 * modules are added/removed. The listener continues to receive
	 * notifications until it is removed.
	 * Has no effect if an identical listener is already registered.
	 * <p>
	 * This method is normally called by the web server core framework.
	 * Clients (other than the delegate) should never call this method.
	 * </p>
	 *
	 * @param listener the module factory listener to add
	 * @see #removeModuleFactoryListener(IModuleFactoryListener)
	 */
	public void addModuleFactoryListener(IModuleFactoryListener listener) {
		Trace.trace(Trace.FINEST, "Adding module factory listener " + listener + " to " + this);
		
		if (listeners == null)
			listeners = new ArrayList();
		else if (listeners.contains(listener))
			return;
		listeners.add(listener);
	}
	
	/**
	 * Removes the given listener from this module factory.
	 * Has no effect if the listener is not registered.
	 * <p>
	 * This method is normally called by the web server core framework.
	 * Clients (other than the delegate) should never call this method.
	 * </p>
	 *
	 * @param listener the module factory listener to remove
	 * @see #addModuleFactoryListener(IModuleFactoryListener)
	 */
	public void removeModuleFactoryListener(IModuleFactoryListener listener) {
		Trace.trace(Trace.FINEST, "Removing module factory listener " + listener + " from " + this);
		
		if (listeners != null)
			listeners.remove(listener);
	}
	
	/**
	 * Fire a module factory event. This method is used by the factory delegate to
	 * fire events about module changes.
	 * 
	 * @param added a non-null array of modules that have been added
	 * @param removed a non-null array of modules that have been removed
	 */
	public void fireModuleFactoryEvent(IModule[] added, IModule[] removed) {
		Trace.trace(Trace.FINEST, "->- Firing module factory event: " + toString() + " ->-");

		if (listeners == null || listeners.isEmpty())
			return;

		int size = listeners.size();
		IModuleFactoryListener[] dfl = new IModuleFactoryListener[size];
		listeners.toArray(dfl);
		
		ModuleFactoryEvent event = new ModuleFactoryEvent(added, removed);
		
		for (int i = 0; i < size; i++) {
			try {
				Trace.trace(Trace.FINEST, "  Firing module factory event to: " + dfl[i]);
				dfl[i].moduleFactoryChanged(event);
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "  Error firing module factory event", e);
			}
		}
		Trace.trace(Trace.FINEST, "-<- Done firing module factory event -<-");
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
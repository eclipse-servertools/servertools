/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.internal.Module;
import org.eclipse.wst.server.core.internal.ModuleFactory;
import org.eclipse.wst.server.core.internal.Trace;
/**
 * A module factory delegate provides a mechanism for discovering
 * modules. A module factory delegate is specified by the
 * <code>class</code> attribute of a <code>moduleFactories</code> extension.
 * <p>
 * When the module factory needs to be given a delegate, the delegate class
 * specified for the module factory is instantiated with a 0-argument
 * constructor.
 * </p>
 * <p>
 * Module factory delegates may keep state in instance fields, but that state is
 * transient and will not be persisted across workbench sessions.
 * </p>
 * <p>
 * This interface is intended to be implemented by clients.
 * </p>
 * <p>
 * <it>Caveat: The server core API is still in an early form, and is
 * likely to change significantly before the initial release.</it>
 * </p>
 * 
 * @see org.eclipse.wst.server.core.IModule
 * @see ModuleDelegate
 * @since 1.0
 */
public abstract class ModuleFactoryDelegate {
	// module factory listeners
	private transient List listeners;

	private ModuleFactory factory;
	
	/**
	 * Delegates must have a public 0-arg constructor.
	 */
	public ModuleFactoryDelegate() {
		// do nothing
	}

	/**
	 * Initializes this module factory delegate with its life-long module
	 * factory instance.
	 * <p>
	 * This method is called by the server core framework.
	 * Clients should never call this method.
	 * </p>
	 * 
	 * @param newFactory the module factory instance
	 */
	public final void initialize(ModuleFactory newFactory) {
		factory = newFactory;
	}

	/**
	 * Returns the id of this factory.
	 * Each factory has a distinct id, fixed for its lifetime. Ids are intended to
	 * be used internally as keys; they are not intended to be shown to end users.
	 * 
	 * @return the factory id
	 */
	protected String getId() {
		return factory.getId();
	}

	/**
	 * Creates a module instance with the given static information. This method is used
	 * by module factory delegates to create module instances.
	 *  
	 * @param id the module id
	 * @param name the module name
	 * @param type the module type id
	 * @param version the module version id
	 * @param project the project that the module is contained in
	 * @return a module instance
	 */
	public IModule createModule(String id, String name, String type, String version, IProject project) {
		return new Module(factory, id, name, type, version, project);
	}

	/**
	 * Finds a module create by this factory with the given id.
	 * See the specification of
	 * {@link org.eclipse.wst.server.core.IModuleFactory#getModule(String)}
	 * for further details. 
	 * <p>
	 * This method is normally called by the web server core framework,
	 * in response to a call to {@link IModuleFactory#getModule(String)}.
	 * Clients (other than the delegate) should never call this method.
	 * </p>
	 * 
	 * @param id the module id
	 * @return the module with the given id, or <code>null</code>
	 * if none
	 */
	//public abstract IModule getModule(String memento);

	/**
	 * Creates the module delegate for a module with the given information.
	 * This method is called when a client needs to access the module delegate
	 * associated with the given module.
	 * 
	 * @param module a module
	 * @return the module delegate
	 */
	public abstract ModuleDelegate getModuleDelegate(IModule module);

	/**
	 * Return all modules created by this factory.
	 * See the specification of
	 * {@link org.eclipse.wst.server.core.IModuleFactory#getModules()}
	 * for further details. 
	 * <p>
	 * This method is normally called by the web server core framework,
	 * in response to a call to {@link IModuleFactory#getModules()}.
	 * Clients (other than the delegate) should never call this method.
	 * </p>
	 * <p>
	 * A new array is returned on each call, so clients may store or modify the result.
	 * </p>
	 * 
	 * @return a possibly-empty array of modules {@link IModule}
	 */
	public abstract IModule[] getModules();
	
	/**
	 * Adds the given listener to this module factory.
	 * Once registered, a listener starts receiving notification of 
	 * modules are added/removed. The listener continues to receive
	 * notifications until it is removed.
	 * Has no effect if an identical listener is already registered.
	 * <p>
	 * This method is normally called by the web server core framework,
	 * in response to a call to
	 * {@link IModuleFactory#addModuleFactoryListener(IModuleFactoryListener)}.
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
	 * This method is normally called by the web server core framework,
	 * in response to a call to
	 * {@link IModuleFactory#removeModuleFactoryListener(IModuleFactoryListener)}.
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
	protected void fireModuleFactoryEvent(IModule[] added, IModule[] removed) {
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
}
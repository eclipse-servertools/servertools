/*******************************************************************************
 * Copyright (c) 2004, 2007 IBM Corporation and others.
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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.internal.Module;
import org.eclipse.wst.server.core.internal.ModuleFactory;
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
 * This abstract class is intended to be extended only by clients
 * to extend the <code>moduleFactories</code> extension point.
 * </p>
 * 
 * @see org.eclipse.wst.server.core.IModule
 * @see ModuleDelegate
 * @since 1.0
 */
public abstract class ModuleFactoryDelegate {
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
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 */
	final void initialize(ModuleFactory newFactory, IProgressMonitor monitor) {
		factory = newFactory;
		initialize();
	}

	/**
	 * Initializes this module factory delegate. This method gives delegates a chance
	 * to do their own initialization.
	 * <p>
	 * This method is called by the server core framework.
	 * Clients should never call this method.
	 * </p>
	 */
	public void initialize() {
		// do nothing
	}

	/**
	 * Returns the id of this factory.
	 * Each factory has a distinct id, fixed for its lifetime. Ids are intended to
	 * be used internally as keys; they are not intended to be shown to end users.
	 * 
	 * @return the factory id
	 */
	protected final String getId() {
		return factory.getId();
	}

	/**
	 * Clears the cache of modules returned by getModules(). Delegates can call this
	 * method if they know that the results of getModules() is invalid and should be
	 * refreshed.
	 * 
	 * @deprecated This method is implementation specific and never called by the
	 *    framework. It shouldn't be part of the public API, but subclasses are still
	 *    welcome to provide their own method to clear the cache.
	 * @see #getModules()
	 */
	public void clearModuleCache() {
		// ignore
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
	protected final IModule createModule(String id, String name, String type, String version, IProject project) {
		return new Module(factory, id, name, type, version, project);
	}

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
	 * <p>
	 * This method is normally called by the web server core framework.
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
	 * Return all modules created by this factory that are contained within the
	 * given project. Subclasses should override this method if they do not need
	 * to filter through the entire project list.
	 * <p>
	 * This method is normally called by the web server core framework.
	 * Clients (other than the delegate) should never call this method.
	 * </p>
	 * <p>
	 * A new array is returned on each call, so clients may store or modify the result.
	 * </p>
	 * 
	 * @param project a project
	 * @return a possibly-empty array of modules {@link IModule}
	 * @since 2.0
	 */
	public IModule[] getModules(IProject project) {
		IModule[] modules = getModules();
		if (project != null && modules != null) {
			List list = new ArrayList(modules.length);
			int size = modules.length;
			for (int i = 0; i < size; i++) {
				if (project.equals(modules[i].getProject()))
					list.add(modules[i]);
			}
			
			IModule[] m = new IModule[list.size()];
			list.toArray(m);
			return m;
		}
		
		return new IModule[0];
	}

	/**
	 * Returns the module created by this factory that has the given id, or
	 * <code>null</code> if there is no module with the given id. The id must
	 * not be null.
	 * <p>
	 * Subclasses should override this method if they do not need to search
	 * through the entire project list.
	 * </p>
	 * <p>
	 * This method is normally called by the web server core framework.
	 * Clients (other than the delegate) should never call this method.
	 * </p>
	 * 
	 * @param id a module id
	 * @return the module with the given id, or <code>null</code> if no module
	 *    could be found {@link IModule}
	 * @since 2.0
	 */
	public IModule findModule(String id) {
		if (id == null)
			return null;
		
		IModule[] modules = getModules();
		if (id != null && modules != null) {
			int size = modules.length;
			for (int i = 0; i < size; i++) {
				String id2 = modules[i].getId();
				int index = id2.indexOf(":");
				if (index >= 0)
					id2 = id2.substring(index+1);
				
				if (id.equals(id2))
					return modules[i];
			}
		}
		
		return null;
	}
}
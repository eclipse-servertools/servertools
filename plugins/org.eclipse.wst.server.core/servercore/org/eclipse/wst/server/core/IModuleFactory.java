/**********************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core;

import org.eclipse.wst.server.core.model.IModuleFactoryListener;
/**
 * A factory for creating modules.
 * <p>
 * The server core framework supports
 * an open-ended set of module factories, which are contributed via
 * the <code>moduleFactories</code> extension point in the server core
 * plug-in. The global list of module factories is available via
 * {@link ServerCore#getModuleFactories()}. 
 * </p>
 * <p>
 * [issue: Module factories have no display name, suggesting that
 * they never need to be shown to users.]
 * </p>
 * <p>
 * [issue: Are module factories SPI-side objects, or do
 * normal clients need access to them?]
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * <p>
 * [issue: It is notoriously difficult to place any kind of
 * useful order on objects that are contributed independently by
 * non-collaborating parties. The IOrdered mechanism is weak, and
 * can't really solve the problem. Issues of presentation are usually
 * best left to the UI, which can sort objects based on arbitrary
 * properties.]
 * </p>
 * <p>
 * <it>Caveat: The server core API is still in an early form, and is
 * likely to change significantly before the initial release.</it>
 * </p>
 * 
 * @since 1.0
 */
public interface IModuleFactory extends IOrdered {

	/**
	 * Returns the id of this module factory.
	 * Each known module factory has a distinct id. 
	 * Ids are intended to be used internally as keys; they are not
	 * intended to be shown to end users.
	 * 
	 * @return the module factory id
	 */
	public String getId();
	
	/**
	 * Returns the types of modules that the factory is capable of
	 * producing.
	 * 
	 * @return an array of module types (@link IModuleType2}
	 */
	public IModuleType2[] getModuleTypes();

	/**
	 * Returns whether this module factory produces project modules.
	 * <p>
	 * [issue: This surfaces the "projects" attribute of the
	 * moduleFactory element. What is the significance of this?
	 * (There are no senders of this method.)]
	 * </p>
	 *
	 * @return <code>true</code> if it can produce project modules,
	 * and <code>false</code> if it cannot
	 */
	public boolean isProjectModuleFactory();

	/**
	 * Returns the delegate for this module factory.
	 * The module factory delegate is a module-factory-specific object.
	 * <p>
	 * [issue: If module factories are SPI-side objects, then
	 * exposing the delegate is probably fine. If module factories
	 * are available to clients, you'll want to keep the delegate
	 * out of the client's hands.]
	 * </p>
	 * 
	 * @return the module factory delegate
	 */
	//public ModuleFactoryDelegate2 getDelegate();

	/**
	 * Finds a module create by this factory with the given id.
	 * <p>
	 * [issue: Does this "create" a module with the given id?
	 * That's what you'd expect of a factory. But a module has
	 * a module type and module resources, neither of which are
	 * in evidence. And since a module factory can create
	 * several types of modules, that seems to rule this out
	 * as "creating" a module. Is "discovering" a module the
	 * correct interpretation?
	 * The (abstract) ProjectModuleFactoryDelegate class does indeed
	 * rip through all the projects in the workspace and ask which
	 * ones hold valid modules.] 
	 * </p>
	 * 
	 * @param id the module id
	 * @return the module with the given id, or <code>null</code>
	 * if none
	 */
	public IModule getModule(String id);

	/**
	 * Return all modules created by this factory.
	 * <p>
	 * Note: Implementations of this method might look through
	 * projects in the workspace to find modules, or might return
	 * modules from other sources.
	 * </p>
	 * <p>
	 * [issue: What does "modules from other sources" mean?
	 * All modules have to be in the workspace, right?]
	 * </p>
	 * <p>
	 * A new array is returned on each call, so clients may store or modify the result.
	 * </p>
	 * 
	 * @return an array of modules {@link IModule}
	 */
	public IModule[] getModules();

	/**
	 * Add a listener for modules that are added/removed from this
	 * factory.
	 * Has no effect if an identical listener is already registered.
	 * 
	 * @param listener org.eclipse.wst.server.core.model.IModuleFactoryListener
	 */
	public void addModuleFactoryListener(IModuleFactoryListener listener);

	/**
	 * Add a listener for modules that are added/removed from this
	 * factory.
	 * Has no effect if the listener is not registered.
	 * 
	 * @param listener org.eclipse.wst.server.core.model.IModuleFactoryListener
	 */
	public void removeModuleFactoryListener(IModuleFactoryListener listener);
}
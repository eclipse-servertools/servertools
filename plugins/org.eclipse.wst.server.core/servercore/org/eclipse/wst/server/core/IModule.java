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
package org.eclipse.wst.server.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.wst.server.core.model.IModuleListener;
/**
 * A module is a unit of "content" that can be published to a
 * server.
 * <p>
 * All modules have a module type, which is fixed for the
 * lifetime of the module. The set of module types (or
 * "kinds") is open-ended.
 * </p>
 * <p>
 * All modules are created by module factories using the moduleFactories
 * extension point.
 * </p>
 * <p>
 * The content of a module is a collection of file and folder
 * resources in the workspace.
 * </p>
 * <p>
 * In principle, a module exists independent of any
 * particular server. The same module instance can be associated
 * with multiple server instances at the same time. That is
 * why you cannot ask the module which server it's associated
 * with.
 * </p>
 * <p>
 * [issue: Equality/identify for modules?]
 * </p>
 * <p>
 * Concrete module types are represented by concrete classes
 * implementing this interface. The only legitimate reason
 * to declare a subclass is to implement a module factory.
 * </p>
 * <p>
 * <it>Caveat: The server core API is still in an early form, and is
 * likely to change significantly before the initial release.</it>
 * </p>
 * 
 * @since 1.0
 */
public interface IModule extends IAdaptable {
	/**
	 * Returns the id of this module.
	 * Each module has a distinct id, used to distinguish this
	 * module from all other modules in the workspace (and
	 * within a server). Ids are intended to be used internally
	 * as keys; they are not intended to be shown to end users.
	 * 
	 * @return the module id
	 */
	public String getId();

	/**
	 * Returns the displayable name for this module.
	 * <p>
	 * Note that this name is appropriate for the current locale.
	 * </p>
	 *
	 * @return a displayable name for this module
	 */
	public String getName();

	/**
	 * Returns the type of this module.
	 * 
	 * @return
	 */
	public IModuleType getModuleType();

	/**
	 * Returns whether this module currently exists.
	 * <p>
	 * [issue: The method touches on the important problem
	 * of when a module ceases to exist. Need to explain
	 * the full lifecycle of a module.
	 * Should it be synonymous with the module root 
	 * IContainer.exists()? That is, the module exists
	 * as long as the IContainer that holds all its module
	 * resources exists()?]
	 * </p>
	 *
	 * @return <code>true</code> this module currently exists, and
	 * <code>false</code> if it has been deleted or moved
	 */
	//public boolean exists();
	
	/**
	 * Returns the workbench project that this module is contained in,
	 * or null if the module is outside of the workspace.
	 * 
	 * @return org.eclipse.core.resources.IProject
	 */
	public IProject getProject();

	/**
	 * Add a listener for child modules that are added/removed from this
	 * module.
	 * Has no effect if an identical listener is already registered.
	 * 
	 * @param listener org.eclipse.wst.server.core.model.IModuleListener
	 */
	public void addModuleListener(IModuleListener listener);

	/**
	 * Add a listener for child modules that are added/removed from this
	 * module.
	 * Has no effect if the listener is not registered.
	 * 
	 * @param listener org.eclipse.wst.server.core.model.IModuleListener
	 */
	public void removeModuleListener(IModuleListener listener);
}
/*******************************************************************************
 * Copyright (c) 2004, 2008 IBM Corporation and others.
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
import org.eclipse.core.runtime.IProgressMonitor;
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
 * A module is equal to another module whenever the two ids are equal and the
 * (optional) project attribute is equal.
 * </p>
 * <p>This interface is not intended to be implemented by clients.</p>
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
	 * @return the module type
	 */
	public IModuleType getModuleType();

	/**
	 * Returns the workbench project that this module is contained in,
	 * or null if the module is outside of the workspace.
	 * 
	 * @return a project
	 */
	public IProject getProject();

	/**
	 * Returns <code>true</code> if the module is an external (non-workspace) module,
	 * and <code>false</code> otherwise
	 * 
	 * @return <code>true</code> if the module is an external module,
	 *   and <code>false</code> otherwise
	 */
	public boolean isExternal();

	/**
	 * Returns <code>true</code> if the module exists (e.g. is in the workspace)
	 * and <code>false</code> otherwise (e.g. if the module has been deleted).
	 * 
	 * @return <code>true</code> if the module exists,
	 *    and <code>false</code> otherwise
	 */
	public boolean exists();

	/**
	 * Returns an object which is an instance of the given class
	 * associated with this object. Returns <code>null</code> if
	 * no such object can be found, or if the delegate is not
	 * loaded.
	 * <p>
	 * This method will not check the delegate classes for adapting
	 * unless they are already loaded. No plugin loading will occur
	 * when calling this method. It is suitable for popup menus and
	 * other UI artifacts where performance is a concern.
	 * </p>
	 *
	 * @param adapter the adapter class to look up
	 * @return a object castable to the given class, 
	 *    or <code>null</code> if this object does not
	 *    have an adapter for the given class
	 * @see IAdaptable#getAdapter(Class)
	 * @see #loadAdapter(Class, IProgressMonitor)
	 */
	public Object getAdapter(Class adapter);

	/**
	 * Returns an object which is an instance of the given class
	 * associated with this object. Returns <code>null</code> only if
	 * no such object can be found after loading and initializing
	 * delegates.
	 * <p>
	 * This method will force a load and initialization of all delegate
	 * classes and check them for adapting.
	 * </p>
	 *
	 * @param adapter the adapter class to look up
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @return a object castable to the given class, 
	 *    or <code>null</code> if this object does not
	 *    have an adapter for the given class
	 * @see #getAdapter(Class)
	 */
	public Object loadAdapter(Class adapter, IProgressMonitor monitor);
}
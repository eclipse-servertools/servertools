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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
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
 * All modules are created by module factories
 * ({@link org.eclipse.wst.server.core.IModuleFactory}).
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
	 * Validates this module.
	 * <p>
	 * [issue: Conjecture: Each different type of module prescribes
	 * legal arrangements of, and the significance of, the files within
	 * it. This would be spelled out in the spec for the particular
	 * module types.
	 * This validate operation is suppose to check the actual
	 * arrangement of files in this module to see whether they
	 * meet expectations.
	 * It's an open question as to how "strenuous" a check this
	 * is.]
	 * </p>
	 * <p>
	 * [issue: Old comment said: "If there is an error
	 * that should block the server from starting (e.g. major errors)
	 * it should be returned from this method. This method can also be used to
	 * return warning for such things as an open (and dirty) editor."]
	 * </p>
	 * <p>
	 * [issue: All existing implementations of this return null,
	 * which is illegal.]
	 * </p>
	 * <p>
	 * [issue: Old comment said: "Returns an IStatus that is used to determine if this object can
	 * be published to the server." Since the same module can
	 * be associated with any number of servers, "the server" is
	 * ill-defined.]
	 * </p>
	 * <p>
	 * [issue: Old comment said: "Should return an error if there
	 * is a major problem with the resources, or can be used to
	 * return warnings on unsaved files, etc." It is usually
	 * difficult in principle for core-level infrastructure to
	 * detect whether there are open editors with unsaved changes.]
	 * </p>
	 *
	 * @return a status object with code <code>IStatus.OK</code> if the given
	 * module is valid, otherwise a status object indicating what is
	 * wrong with it
	 */
	public IStatus validate(IProgressMonitor monitor);

	/**
	 * Returns the root resources of this module. All members
     * belong to this module (as do their members, and so on).
	 * <p>
	 * [issue: What are the exact constraints on where these
	 * resources are located?
	 * Do they all have to be inside the workspace?
	 * Do they all have to be in the same project?
	 * When a folder is included, does that mean the entire
	 * subtree is published to server?]
	 * </p>
	 * 
	 * @return the members of this module
	 * @throws CoreException [missing]
	 */
	//public IModuleResource[] members() throws CoreException;

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
	 * [issue: The method touches on the important issue
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
	 * Add a listener for child module that are added/removed from this
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

	/**
	 * Returns the child modules of this module.
	 *
	 * @return org.eclipse.wst.server.core.model.IModule[]
	 */
	public IModule[] getChildModules(IProgressMonitor monitor);
}
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
package org.eclipse.wst.server.core.model;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.server.core.IModuleType;
import org.eclipse.wst.server.core.resources.IModuleResource;
/**
 * A resource (typically a project or folder) that can be deployed to a server.
 * This interface will be subclassed to provide further information about the
 * content.
 */
public interface IModule extends IModuleType {
	/**
	 * Returns a unique memento to distinguish this module
	 * from all other module instances on a server.
	 *
	 * @return java.lang.String
	 */
	public String getId();

	/**
	 * Returns an IStatus that validates the contents of this module.
	 * If there is an error that should block the server from starting,
	 * (e.g. major errors) it should be returned from this method. This
	 * method can also be used to return warning for such things as an
	 * open (and dirty) editor.
	 *
	 * @return org.eclipse.core.runtime.IStatus
	 */
	public IStatus validate();

	/**
	 * Returns an IStatus that is used to determine if this object can
	 * be published to the server. Should return an error if there is a
	 * major problem with the resources, or can be used to return warnings
	 * on unsaved files, etc.
	 *
	 * @return org.eclipse.core.runtime.IStatus
	 */
	public IStatus canPublish();

	/**
	 * Returns the base level resources for this module.
	 *
	 * @return IModuleResource[]
	 */
	public IModuleResource[] members() throws CoreException;

	/**
	 * Returns the name of this module.
	 *
	 * @return java.lang.String
	 */
	public String getName();

	/**
	 * Returns the id of the factory that created this module.
	 *
	 * @return java.lang.String
	 */
	public String getFactoryId();

	/**
	 * Returns true if this module currently exists, and false if it has
	 * been deleted or moved and is no longer represented by this module.
	 *
	 * @return boolean
	 */
	public boolean exists();

	/**
	 * Add a listener for child module that are added/removed from this
	 * module.
	 * 
	 * @param listener org.eclipse.wst.server.core.model.IModuleListener
	 */
	public void addModuleListener(IModuleListener listener);

	/**
	 * Add a listener for child modules that are added/removed from this
	 * module.
	 * 
	 * @param listener org.eclipse.wst.server.core.model.IModuleListener
	 */
	public void removeModuleListener(IModuleListener listener);

	/**
	 * Returns the child modules of this module.
	 *
	 * @return org.eclipse.wst.server.core.model.IModule[]
	 */
	public IModule[] getChildModules();
}
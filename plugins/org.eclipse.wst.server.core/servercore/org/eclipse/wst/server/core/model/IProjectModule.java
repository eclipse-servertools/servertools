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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.IPath;
import org.eclipse.wst.server.core.IModule;
/**
 * A module project is a connection between a module and a project in the
 * workspace. Typically, this is used to listen for resource changes (meaning that
 * the module may need to be republished) and associate the module with the
 * project in the UI.
 */
public interface IProjectModule extends IModule {
	/**
	 * Returns the workbench project that this module is contained in.
	 * 
	 * @return org.eclipse.core.resources.IProject
	 */
	public IProject getProject();

	/**
	 * Returns the mapped module resource delta from a
	 * workbench resource delta. This should convert from the 
	 * 
	 * @param delta org.eclipse.core.resources.IResourceDelta
	 * @return org.eclipse.wst.server.core.IModuleResourceDelta
	 */
	public IPath getModuleResourceDelta(IResourceDelta delta);
}
/*******************************************************************************
 * Copyright (c) 2003, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.core;

import org.eclipse.core.resources.IContainer;
/**
 * A J2EE module that can be deployed to a server.
 * 
 * <p>
 * <b>Provisional API:</b> This class/interface is part of an interim API that is still under development and expected to 
 * change significantly before reaching stability. It is being made available at this early stage to solicit feedback 
 * from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken 
 * (repeatedly) as the API evolves.
 * </p>
 * @plannedfor 3.0
 */
public interface IJ2EEModule {
	/**
	 * Returns the root folders for the resources in this module. Returns an empty
	 * array if this is a binary module.
	 * 
	 * @return a possibly-empty array of resource folders
	 */
	public IContainer[] getResourceFolders();

	/**
	 * Returns the root folders containing Java output in this module. Returns an
	 * empty array if this is a binary module.
	 * 
	 * @return a possibly-empty array of Java output folders
	 */
	public IContainer[] getJavaOutputFolders();

	/**
	 * Returns true if this is a binary (zipped) module, and
	 * false if it is expanded.
	 * 
	 * <p>If true, members() should return only a single element -
	 * the binary (jar or zip file) that contains the contents of
	 * this module. (a single IModuleResource, e.g.
	 * myejb.jar) Also, getLocation() should return the full path
	 * up to and including the binary itself. (e.g.
	 * c:\temp\myejb.jar)</p>
	 * 
	 * <p>If false, members() should return the entire contents
	 * of the module, starting at the root. There should be no
	 * preceeding directory structure. (an array of
	 * IModuleResources, e.g. index.html, WEB-INF/web.xml,
	 * ...) In this case, getLocation() should return the path to
	 * the root folder containing these resources.</p>
	 * 
	 * @return boolean
	 */
	public boolean isBinary();
}
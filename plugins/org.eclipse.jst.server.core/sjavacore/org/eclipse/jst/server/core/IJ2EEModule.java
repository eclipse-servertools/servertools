/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.core;

import org.eclipse.core.runtime.IPath;
/**
 * An abstract J2EE module that can be deployed to a server.
 */
public interface IJ2EEModule {
	/**
	 * Returns a version number in the form "x.y.z".
	 * 
	 * @param java.lang.String
	 */
	public String getJ2EESpecificationVersion();

	/**
	 * Returns the location of the root of the module. May
	 * return null if isUnitTest() returns false. This should
	 * be an absolute path that is not workbench relative.
	 * 
	 * @return org.eclipse.core.runtime.IPath
	 */
	public IPath getLocation();
	
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
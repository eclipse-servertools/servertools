/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.jst.server.j2ee;

import org.eclipse.core.runtime.IPath;

import org.eclipse.wst.server.core.model.IModule;
/**
 * 
 */
public interface IEnterpriseApplication extends IModule {
	/**
	 * Returns a version number in the form "x.y.z".
	 * 
	 * @return java.lang.String
	 */
	public String getJ2EESpecificationVersion();

	/**
	 * Returns the modules contained within this EAR.
	 *
	 * @return org.eclipse.jst.server.j2ee.IJ2EEModule[]
	 */
	public IJ2EEModule[] getModules();

	/**
	 * Returns the URI of the given J2EE module within this
	 * enterprise application.
	 *
	 * @param org.eclipse.jst.server.j2ee.IJ2EEModule
	 * @return java.lang.String
	 */
	public String getURI(IJ2EEModule module);

	/**
	 * Returns true if this EAR supports loose modules.
	 * 
	 * @return boolean
	 */
	public boolean containsLooseModules();
	
	/**
	 * Returns the location of the root of the application. May
	 * return null if isUnitTest() returns false. This should
	 * be an absolute path that is not workbench relative.
	 * 
	 * @return org.eclipse.core.runtime.IPath
	 */
	public IPath getLocation();
}

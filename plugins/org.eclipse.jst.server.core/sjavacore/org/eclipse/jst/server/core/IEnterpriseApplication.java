/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.core;

import org.eclipse.core.runtime.IPath;
import org.eclipse.wst.server.core.IModule;
/**
 * A representation of a J2EE enterprise application (EAR file).
 * <p>
 * <b>Provisional API:</b> This class/interface is part of an interim API that is still under development and expected to 
 * change significantly before reaching stability. It is being made available at this early stage to solicit feedback 
 * from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken 
 * (repeatedly) as the API evolves.
 * </p>
 * @since 1.0
 */
public interface IEnterpriseApplication {
	/**
	 * Returns a version number in the form "x.y.z".
	 * 
	 * @return the J2EE specification version
	 */
	public String getJ2EESpecificationVersion();

	/**
	 * Returns the modules contained within this EAR. The returned modules will
	 * all be adaptable to IJ2EEModule.
	 *
	 * @return a possibly empty array of modules contained within this application
	 */
	public IModule[] getModules();

	/**
	 * Returns the URI of the given J2EE module within this
	 * enterprise application.
	 *
	 * @param module a module within this application
	 * @return the URI of the given module, or <code>null</code> if the URI could
	 *    not be found
	 */
	public String getURI(IJ2EEModule module);

	/**
	 * Returns <code>true</code> if this EAR supports loose modules and <code>false</code>
	 * otherwise.
	 * 
	 * @return returns <code>true</code> if this module contains loose modules, or
	 *    <code>false</code> otherwise
	 */
	public boolean containsLooseModules();
	
	/**
	 * Returns the location of the root of the application. This should
	 * be an absolute path that is not workbench relative.
	 * 
	 * @return the absolute path to the root of this application
	 */
	public IPath getLocation();
}
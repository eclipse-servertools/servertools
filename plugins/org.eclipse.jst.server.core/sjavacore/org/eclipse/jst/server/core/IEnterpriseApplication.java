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
import org.eclipse.wst.server.core.IModule;
/**
 * A representation of a J2EE enterprise application (EAR file).
 * <p>
 * <b>Provisional API:</b> This class/interface is part of an interim API that is still under development and expected to 
 * change significantly before reaching stability. It is being made available at this early stage to solicit feedback 
 * from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken 
 * (repeatedly) as the API evolves.
 * </p>
 * @plannedfor 3.0
 */
public interface IEnterpriseApplication {
	/**
	 * Returns the modules contained within this EAR. The returned modules will
	 * either be J2EE modules (which will be adaptable to IJ2EEModule) or
	 * utility jars contained in the EAR.
	 * 
	 * @return a possibly empty array of modules contained within this application
	 */
	public IModule[] getModules();

	/**
	 * Returns the URI of the given module within this enterprise application.
	 * 
	 * @param module a module within this application
	 * @return the URI of the given module, or <code>null</code> if the URI could
	 *    not be found
	 */
	public String getURI(IModule module);

	/**
	 * Returns the root folders for the resources in this module.
	 * 
	 * @return a possibly-empty array of resource folders
	 */
	public IContainer[] getResourceFolders();
}
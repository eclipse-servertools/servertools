/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.model;

import org.eclipse.wst.server.core.IModule;

/**
 * A representation of a Module that contains children. 
 *  
 * <b>Provisional API:</b> This class/interface is part of an interim API that is still under development and expected to 
 * change significantly before reaching stability. It is being made available at this early stage to solicit feedback 
 * from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken 
 * (repeatedly) as the API evolves.
 * </p>
 */
public interface IParentModuleWithChild {

	/**
	 * Returns the children modules contained within the application. 
	 * 
	 * @return a possibly empty array of modules contained within this application
	 */
	public IModule[] getModules();
	
	/**
	 * Returns the path relative to its parent of the given module contained within this application
	 * 
	 * @param module a module within this application
	 * @return the path of the given module with respect to the parent, or <code>null</code> if the path could
	 *    not be found
	 */	
	public String getPath(IModule module);
	
}

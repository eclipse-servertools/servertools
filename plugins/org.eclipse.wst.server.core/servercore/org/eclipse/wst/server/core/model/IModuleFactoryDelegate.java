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

import java.util.List;
/**
 * 
 */
public interface IModuleFactoryDelegate {
	/**
	 * Gets a module from a memento.
	 * 
	 * @param memento java.lang.String
	 * @return org.eclipse.wst.server.core.model.IModule
	 */
	public IModule getModule(String memento);

	/**
	 * Return all modules that are available to be added
	 * to servers. This method might look through projects
	 * to find modules or may return modules from
	 * other sources.
	 *
	 * @return java.util.List
	 */
	public List getModules();
	
	/**
	 * Add a listener for modules that are added/removed from this
	 * factory.
	 * 
	 * @param listener org.eclipse.wst.server.core.model.IModuleFactoryListener
	 */
	public void addModuleFactoryListener(IModuleFactoryListener listener);

	/**
	 * Add a listener for modules that are added/removed from this
	 * factory.
	 * 
	 * @param listener org.eclipse.wst.server.core.model.IModuleFactoryListener
	 */
	public void removeModuleFactoryListener(IModuleFactoryListener listener);
}

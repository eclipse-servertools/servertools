/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core;
/**
 * A module object is a resource within a module,
 * which can be launched on the server. Examples of module
 * objects could include servlets, HTML pages, or EJB beans.
 */
public interface IModuleObject {
	/**
	 * Returns the id of this module object. Each known
	 * module object has a distinct id. 
	 * Ids are intended to be used internally as keys; they are not
	 * intended to be shown to end users.
	 * 
	 * @return the module object id
	 */
	public String getId();

	/**
	 * Returns the module that this object is a part of.
	 * 
	 * @return org.eclipse.wst.server.core.IModule
	 */
	public IModule getModule();
}
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
/**
 * A module object adapter converts from some view's model
 * object into a module object that is recognized by the
 * server.
 * 
 * <p>This is the implementation of a moduleObjectAdapter
 * extension point.</p>
 */
public interface IModuleObjectAdapterDelegate {
	/**
	 * Converts from a model object to an IModuleObject.
	 *
	 * @param obj java.lang.Object
	 * @return org.eclipse.wst.server.core.model.IModuleObject
	 */
	public IModuleObject getModuleObject(Object obj);
}

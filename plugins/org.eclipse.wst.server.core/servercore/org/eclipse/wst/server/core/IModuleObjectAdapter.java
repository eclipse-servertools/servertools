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
package org.eclipse.wst.server.core;

import org.eclipse.wst.server.core.model.IModuleObject;
import org.eclipse.wst.server.core.model.IModuleObjectAdapterDelegate;
/**
 * A module object adapter converts from some view's model
 * object into a module object that is recognized by the
 * server.
 * 
 * <p>This interface is not intended to be implemented by clients.</p>
 */
public interface IModuleObjectAdapter {
	/**
	 * Returns the id of the adapter.
	 *
	 * @return java.lang.String
	 */
	public String getId();
	
	/**
	 * Returns the (super) class name that this adapter can work with.
	 *
	 * @return java.lang.String
	 */
	public String getObjectClassName();
	
	/**
	 * Returns true if the plugin that loaded this class has been loaded.
	 *
	 * @return boolean
	 */
	public boolean isPluginActivated();	

	/**
	 * Returns the delegate for this module object adapter.
	 * 
	 * @return org.eclipse.wst.server.core.model.IModuleObjectAdapterDelegate
	 */
	public IModuleObjectAdapterDelegate getDelegate();

	/**
	 * Converts from a model object to an IModuleObject.
	 * 
	 * @param obj java.lang.Object
	 * @return org.eclipse.wst.server.core.model.IModuleObject
	 */
	public IModuleObject getModuleObject(Object obj);
}

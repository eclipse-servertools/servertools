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
package org.eclipse.wst.server.core.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.wst.server.core.internal.Trace;
import org.eclipse.wst.server.core.model.*;
/**
 * 
 */
public class MissingModuleFactoryDelegate implements IModuleFactoryDelegate {
	/*
	 * @see IModuleFactoryDelegate#getModule(String)
	 */
	public IModule getModule(String memento) {
		if (memento == null)
			return null;
		try {
			int index = memento.indexOf("/");
			if (index < 0)
				return null;
			String id = memento.substring(0, index);
			String name = memento.substring(index + 1);
			return new MissingModule(id, name);
		} catch (Exception e) {
			Trace.trace("Could not create module: " + e.getMessage());
		}
		return null;
	}

	/*
	 * @see IModuleFactoryDelegate#getModules()
	 */
	public List getModules() {
		return new ArrayList(0);
	}
	
	/**
	 * Add a listener to the module factory.
	 *
	 * @param listener org.eclipse.wst.server.core.model.IModuleFactoryListener
	 */
	public void addModuleFactoryListener(IModuleFactoryListener listener) { }
	
	/**
	 * Remove a listener from the module factory.
	 *
	 * @param listener org.eclipse.wst.server.core.model.IModuleFactoryListener
	 */
	public void removeModuleFactoryListener(IModuleFactoryListener listener) { }
}

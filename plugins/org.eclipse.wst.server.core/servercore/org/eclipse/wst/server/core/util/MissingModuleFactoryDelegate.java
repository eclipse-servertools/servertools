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

import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.internal.Trace;
import org.eclipse.wst.server.core.model.*;
/**
 * 
 */
public class MissingModuleFactoryDelegate extends ModuleFactoryDelegate {
	/*
	 * @see ModuleFactoryDelegate2#getModule(String)
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
			return new MissingModule();
		} catch (Exception e) {
			Trace.trace("Could not create module: " + e.getMessage());
		}
		return null;
	}

	/*
	 * @see ModuleFactoryDelegate2#getModules()
	 */
	public IModule[] getModules() {
		return new IModule[0];
	}
}
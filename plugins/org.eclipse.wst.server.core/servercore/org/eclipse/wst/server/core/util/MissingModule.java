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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.model.ModuleDelegate;
/**
 * A simple IModule that represents a missing or unavailable
 * module.
 */
public class MissingModule extends ModuleDelegate {

	public MissingModule() {
		// do nothing
	}
	
	/*
	 * @see ModuleDelegate#validate()
	 */
	public IStatus validate() {
		return null;
	}
	
	public IModule[] getChildModules() {
		return null;
	}
}
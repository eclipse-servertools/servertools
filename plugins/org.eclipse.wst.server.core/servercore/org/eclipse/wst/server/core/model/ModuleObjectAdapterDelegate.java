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
package org.eclipse.wst.server.core.model;

import org.eclipse.wst.server.core.IModuleArtifact;
/**
 * A module object adapter converts from some view's model
 * object into a module object that is recognized by the
 * server.
 * 
 * <p>This is the implementation of a moduleObjectAdapter extension point.</p>
 */
public abstract class ModuleObjectAdapterDelegate {
	/**
	 * Converts from a model object to an IModuleArtifact.
	 *
	 * @param obj
	 * @return
	 */
	public abstract IModuleArtifact getModuleObject(Object obj);
}
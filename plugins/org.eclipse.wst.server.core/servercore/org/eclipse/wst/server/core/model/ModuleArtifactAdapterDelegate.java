/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.model;

import org.eclipse.wst.server.core.IModuleArtifact;
/**
 * A module artifact adapter converts from some view's model
 * object into a module artifact that is recognized by one or
 * more server types.
 * 
 * <p>This is the implementation of a moduleArtifactAdapter
 * extension point.</p>
 * <p>
 * This abstract class is intended to be extended only by clients
 * to extend the <code>moduleArtifactAdapters</code> extension point.
 * </p>
 * 
 * @plannedfor 1.0
 */
public abstract class ModuleArtifactAdapterDelegate {
	/**
	 * Converts from an arbitrary object to an module artifact.
	 *
	 * @param obj an arbitrary object from a view or editor
	 * @return an module artifact, or <code>null</code> if this
	 *    adapter does not recognize or cannot adapt the object
	 */
	public abstract IModuleArtifact getModuleArtifact(Object obj);
}
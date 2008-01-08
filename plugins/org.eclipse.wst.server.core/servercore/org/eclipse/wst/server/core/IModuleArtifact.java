/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core;
/**
 * A module artifact is a resource within a module, which can be launched
 * on the server. Examples of module artifacts are servlets, HTML pages,
 * or EJB beans.
 * <p>
 * Objects that provide an adapter of this type will be considered by the
 * contextual Run on Server launch support. 
 * </p>
 * 
 * @see org.eclipse.wst.server.core.model.ModuleArtifactAdapterDelegate
 * @since 1.0
 */
public interface IModuleArtifact {
	/**
	 * Returns the module that this artifact is a part of.
	 * 
	 * @return the module that this artifact is contained in
	 */
	public IModule getModule();
}
/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.internal;

import org.eclipse.wst.server.core.model.ModuleDelegate;
/**
 * An ExternalModule is a unit of "content" that is already published to the server
 * but it doesn't contain a valid resource in the workspace.
 * <p>
 * ExternalModule are created using ServerBehaviourDelegate.
 */
public class ExternalModule extends Module {
	/**
	 * ExternalModule constructor
	 * 
	 * @param id the module id
	 * @param name the module name
	 * @param type the module type id
	 * @param version the module version id
	 * @param delegate
	 */
	public ExternalModule(String id, String name, String type, String version, ModuleDelegate delegate) {
		super(null, "external:" + id, name, type, version, null);
		this.delegate = delegate;
	}

	public boolean isExternal() {
		return true;
	}
}
/*******************************************************************************
 * Copyright (c) 2004, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.util;

import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.model.ModuleArtifactDelegate;
/**
 * A dummy module artifact.
 * 
 * @since 1.0
 */
public class NullModuleArtifact extends ModuleArtifactDelegate {
	/**
	 * Create a new reference to a module.
	 * 
	 * @param module the module
	 */
	public NullModuleArtifact(IModule module) {
		super(module);
	}

	/**
	 * Create a new empty reference to a module.
	 */
	public NullModuleArtifact() {
		super();
	}

	/*
	 * @see ModuleArtifactDelegate#getName()
	 */
	public String getName() {
		return "";
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return "NullModuleArtifact [module=" + getModule() + "]";
	}
}
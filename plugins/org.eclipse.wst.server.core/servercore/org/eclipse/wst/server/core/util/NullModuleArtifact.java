/**********************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
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
import org.eclipse.wst.server.core.IModuleArtifact;
/**
 * 
 */
public class NullModuleArtifact implements IModuleArtifact {
	public static final String ID = "org.eclipse.wst.server.core.null";

	private IModule module;

	public NullModuleArtifact(IModule module) {
		this.module = module;
	}

	public String getId() {
		return ID;
	}

	public IModule getModule() {
		return module;
	}

	public String toString() {
		return "NullModuleArtifact [module=" + module + "]";
	}
}
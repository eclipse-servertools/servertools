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
package org.eclipse.jst.server.core;

import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IModuleArtifact;
/**
 * 
 * @since 1.0
 */
public class Servlet implements IModuleArtifact {
	private IModule module;
	private String className;
	private String alias;
	
	public Servlet(IModule module, String className, String alias) {
		this.module = module;
		this.className = className;
		this.alias = alias;
	}

	public IModule getModule() {
		return module;
	}

	public String getServletClassName() {
		return className;
	}
	
	public String getAlias() {
		return alias;
	}
	
	public String toString() {
		return "Servlet [module=" + module + ", class=" + className + ", alias=" + alias + "]";
	}
}
/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.jst.server.j2ee;

import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IModuleArtifact;
/**
 * 
 */
public class Servlet implements IModuleArtifact {
	public static final String ID = "org.eclipse.jst.server.j2ee.servlet";

	private IModule module;
	private String className;
	private String alias;
	
	public Servlet(IModule module, String className, String alias) {
		this.module = module;
		this.className = className;
		this.alias = alias;
	}

	public String getId() {
		return ID;
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
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
	
	/**
	 * Create a reference to a servlet.
	 * 
	 * @param module the module that the servlet is contained in
	 * @param className the class name of the servlet
	 * @param alias the servlet's alias
	 */
	public Servlet(IModule module, String className, String alias) {
		this.module = module;
		this.className = className;
		this.alias = alias;
	}

	/*
	 * @see IModuleArtifact#getModule()
	 */
	public IModule getModule() {
		return module;
	}

	/**
	 * Return the class name of the servlet.
	 * 
	 * @return the class name of the servlet
	 */
	public String getServletClassName() {
		return className;
	}
	
	/**
	 * Return the servlet's alias.
	 * 
	 * @return the servlet's alias
	 */
	public String getAlias() {
		return alias;
	}

	/*
	 * @see Object#toString()
	 */
	public String toString() {
		return "Servlet [module=" + module + ", class=" + className + ", alias=" + alias + "]";
	}
}
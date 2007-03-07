/*******************************************************************************
 * Copyright (c) 2003, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.core;

import org.eclipse.jst.server.core.internal.Messages;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.model.ModuleArtifactDelegate;
/**
 * A J2EE Servlet.
 * <p>
 * <b>Provisional API:</b> This class/interface is part of an interim API that is still under development and expected to 
 * change significantly before reaching stability. It is being made available at this early stage to solicit feedback 
 * from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken 
 * (repeatedly) as the API evolves.
 * </p>
 * @plannedfor 3.0
 */
public class Servlet extends ModuleArtifactDelegate {
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
		super(module);
		this.className = className;
		this.alias = alias;
	}

	/**
	 * Create a new empty servlet.
	 */
	public Servlet() {
		super();
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
	 * @see ModuleArtifactDelegate#getName()
	 */
	public String getName() {
		return NLS.bind(Messages.artifactServlet, className);
	}

	/*
	 * @see ModuleArtifactDelegate#deserialize(String)
	 */
	public void deserialize(String s) {
		int ind = s.indexOf("//");
		super.deserialize(s.substring(0, ind));
		s = s.substring(ind+2);
		ind = s.indexOf("//");
		className = s.substring(0, ind);
		alias = s.substring(ind+2);
	}

	/*
	 * @see ModuleArtifactDelegate#serialize()
	 */
	public String serialize() {
		StringBuffer sb = new StringBuffer(super.serialize());
		sb.append("//");
		sb.append(className);
		sb.append("//");
		sb.append(alias);
		return sb.toString();
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return "Servlet [module=" + getModule() + ", class=" + className + ", alias=" + alias + "]";
	}
}
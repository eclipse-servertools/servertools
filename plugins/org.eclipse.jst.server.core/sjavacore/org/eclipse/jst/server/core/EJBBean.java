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
 * An EJB bean.
 * <p>
 * <b>Provisional API:</b> This class/interface is part of an interim API that is still under development and expected to 
 * change significantly before reaching stability. It is being made available at this early stage to solicit feedback 
 * from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken 
 * (repeatedly) as the API evolves.
 * </p>
 * @plannedfor 1.5
 */
public class EJBBean implements IModuleArtifact {
	private IModule module;
	private String jndiName;
	private boolean local;
	private boolean remote;

	/**
	 * Create a new EJBBean.
	 * 
	 * @param module the module that the EJB is contained in
	 * @param jndiName the JNDI name of the EJB
	 * @param remote <code>true</code> if the EJB has a remote interface, and
	 *    <code>false</code> otherwise
	 * @param local <code>true</code> if the EJB has a local interface, and
	 *    <code>false</code> otherwise
	 */
	public EJBBean(IModule module, String jndiName, boolean remote, boolean local) {
		this.module = module;
		this.jndiName = jndiName;
		this.remote = remote;
		this.local = local;
	}

	/**
	 * @see IModuleArtifact#getModule()
	 */
	public IModule getModule() {
		return module;
	}

	/**
	 * Returns the JNDI name of the EJB.
	 * 
	 * @return the JNDI name of the EJB
	 */
	public String getJndiName() {
		return jndiName;
	}

	/**
	 * Returns whether the EJB has a remote interface.
	 * 
	 * @return <code>true</code> if the EJB has a remote interface, and
	 *    <code>false</code> otherwise
	 */
	public boolean hasRemoteInterface() {
		return remote;
	}

	/**
	 * Returns whether the EJB has a local interface.
	 * 
	 * @return <code>true</code> if the EJB has a local interface, and
	 *    <code>false</code> otherwise
	 */
	public boolean hasLocalInterface() {
		return local;
	}
}
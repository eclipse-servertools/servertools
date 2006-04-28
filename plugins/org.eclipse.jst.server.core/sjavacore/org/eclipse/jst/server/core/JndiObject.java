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
 * A representation of an object in JNDI that can be tested on a server.
 * <p>
 * <b>Provisional API:</b> This class/interface is part of an interim API that is still under development and expected to 
 * change significantly before reaching stability. It is being made available at this early stage to solicit feedback 
 * from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken 
 * (repeatedly) as the API evolves.
 * </p>
 * @plannedfor 2.0
 */
public class JndiObject implements IModuleArtifact {
	private IModule module;
	private String jndiName;

	/**
	 * Create a reference to an object in JNDI.
	 * 
	 * @param module the module that the object is contained in
	 * @param jndiName the JNDI name of the object
	 */
	public JndiObject(IModule module, String jndiName) {
		this.module = module;
		this.jndiName = jndiName;
	}

	/**
	 * @see IModuleArtifact#getModule()
	 */
	public IModule getModule() {
		return module;
	}

	/**
	 * Return the JNDI name of the object.
	 * 
	 * @return the JNDI name of the object
	 */
	public String getJndiName() {
		return jndiName;
	}
}
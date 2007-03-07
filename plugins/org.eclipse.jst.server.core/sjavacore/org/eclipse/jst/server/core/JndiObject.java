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
 * A representation of an object in JNDI that can be tested on a server.
 * <p>
 * <b>Provisional API:</b> This class/interface is part of an interim API that is still under development and expected to 
 * change significantly before reaching stability. It is being made available at this early stage to solicit feedback 
 * from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken 
 * (repeatedly) as the API evolves.
 * </p>
 * @plannedfor 3.0
 */
public class JndiObject extends ModuleArtifactDelegate {
	private String jndiName;

	/**
	 * Create a reference to an object in JNDI.
	 * 
	 * @param module the module that the object is contained in
	 * @param jndiName the JNDI name of the object
	 */
	public JndiObject(IModule module, String jndiName) {
		super(module);
		this.jndiName = jndiName;
	}

	/**
	 * Create an empty reference to an object in JNDI.
	 */
	public JndiObject() {
		super();
	}

	/**
	 * Return the JNDI name of the object.
	 * 
	 * @return the JNDI name of the object
	 */
	public String getJndiName() {
		return jndiName;
	}

	/*
	 * @see ModuleArtifactDelegate#getName()
	 */
	public String getName() {
		return NLS.bind(Messages.artifactJNDI, jndiName);
	}

	/*
	 * @see ModuleArtifactDelegate#deserialize(String)
	 */
	public void deserialize(String s) {
		int ind = s.indexOf("//");
		super.deserialize(s.substring(0, ind));
		jndiName = s.substring(ind+2);
	}

	/*
	 * @see ModuleArtifactDelegate#serialize()
	 */
	public String serialize() {
		StringBuffer sb = new StringBuffer(super.serialize());
		sb.append("//");
		sb.append(jndiName);
		return sb.toString();
	}
}
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
package org.eclipse.jst.server.core;

import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IModuleArtifact;
/**
 * 
 */
public class JndiObject implements IModuleArtifact {
	public static final String ID = "org.eclipse.jst.server.j2ee.jndi";

	private IModule module;
	private String jndiName;
	
	public JndiObject(IModule module, String jndiName) {
		this.module = module;
		this.jndiName = jndiName;
	}

	public String getId() {
		return ID;
	}

	public IModule getModule() {
		return module;
	}

	public String getJndiName() {
		return jndiName;
	}
}
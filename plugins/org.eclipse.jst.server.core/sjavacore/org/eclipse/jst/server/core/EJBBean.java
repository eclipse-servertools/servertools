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

public class EJBBean implements IModuleArtifact {
	public static final String ID = "org.eclipse.jst.server.j2ee.ejb";

	private IModule module;
	private String jndiName;
	private boolean local;
	private boolean remote;

	/**
	 * @deprecated - use the other constructor
	 * @param module
	 * @param ejbName
	 */
	public EJBBean(IModule module, String ejbName) {
		this.module = module;
		this.jndiName = ejbName;
	}

	public EJBBean(IModule module, String jndiName, boolean remote, boolean local) {
		this.module = module;
		this.jndiName = jndiName;
		this.remote = remote;
		this.local = local;
	}

	/*
	 * @see IModuleArtifact#getId()
	 */
	public String getId() {
		return ID;
	}

	/*
	 * @see IModuleArtifact#getModule()
	 */
	public IModule getModule() {
		return module;
	}

	public String getJndiName() {
		return jndiName;
	}

	public boolean hasRemoteInterface() {
		return remote;
	}
	
	public boolean hasLocalInterface() {
		return local;
	}
}
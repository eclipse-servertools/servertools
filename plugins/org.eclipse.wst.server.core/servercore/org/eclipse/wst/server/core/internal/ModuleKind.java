/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.internal;

import org.eclipse.core.runtime.IConfigurationElement;

import org.eclipse.wst.server.core.IModuleKind;
/**
 * 
 */
public class ModuleKind implements IModuleKind {
	protected String id;
	protected String name;
	
	public ModuleKind(IConfigurationElement ce) {
		super();
		this.id = ce.getAttribute("id");
		this.name = ce.getAttribute("name");
	}
	
	public ModuleKind(String id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	
	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String toString() {
		return "ModuleType2[" + id + ", " + name + "]";
	}
}
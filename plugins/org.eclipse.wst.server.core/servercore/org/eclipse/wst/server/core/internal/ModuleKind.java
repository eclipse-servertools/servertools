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
package org.eclipse.wst.server.core.internal;

import org.eclipse.core.runtime.IConfigurationElement;
/**
 * 
 */
public class ModuleKind {
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
		return "ModuleType[" + id + ", " + name + "]";
	}
}
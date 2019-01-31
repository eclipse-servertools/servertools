/*******************************************************************************
 * Copyright (c) 2003, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
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
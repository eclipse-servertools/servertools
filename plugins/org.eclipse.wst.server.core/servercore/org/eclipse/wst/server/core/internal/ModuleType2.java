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

import org.eclipse.wst.server.core.IModuleType2;
/**
 * 
 */
public class ModuleType2 implements IModuleType2 {
	protected String id;
	protected String version;
	
	public ModuleType2(String id, String version) {
		super();
		this.id = id;
		this.version = version;
	}

	public String getId() {
		return id;
	}

	public String getVersion() {
		return version;
	}

	public String toString() {
		return "ModuleType2[" + id + ", " + version + "]";
	}
}
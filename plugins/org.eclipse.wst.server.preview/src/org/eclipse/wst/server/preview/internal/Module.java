/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.preview.internal;
/**
 * 
 */
public class Module {
	private String name;
	private boolean isStatic;
	private String context;
	private String projectPath;

	public Module(String name, boolean isStatic, String context, String projectPath) {
		this.name = name;
		this.isStatic = isStatic;
		this.context = context;
		this.projectPath = projectPath;
	}

	public String getName() {
		return name;
	}

	public boolean isStaticWeb() {
		return isStatic;
	}

	public String getContext() {
		return context;
	}

	public String getPath() {
		return projectPath;
	}
}
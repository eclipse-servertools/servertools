/**********************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.jst.server.tomcat.core;

import java.util.List;

import org.eclipse.jdt.launching.IVMInstall;

import org.eclipse.wst.server.core.IServerExtension;
/**
 * 
 */
public interface ITomcatRuntime extends IServerExtension {
	public String getVMInstallTypeId();

	public String getVMInstallId();

	public IVMInstall getVMInstall();
	
	public List getRuntimeClasspath();
}
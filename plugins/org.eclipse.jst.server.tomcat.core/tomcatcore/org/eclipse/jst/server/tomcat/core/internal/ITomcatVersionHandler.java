package org.eclipse.jst.server.tomcat.core.internal;
/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jst.server.j2ee.IWebModule;

/**
 * 
 */
public interface ITomcatVersionHandler {
	public boolean verifyInstallPath(IPath installPath);
	
	public String getRuntimeClass();
	
	public List getRuntimeClasspath(IPath installPath);
	
	public String[] getRuntimeProgramArguments(IPath configPath, boolean debug, boolean starting);
	
	public String[] getRuntimeVMArguments(IPath installPath, IPath configPath, boolean isSecure);

	/**
	 * Returns true if the given project is supported by this
	 * server, and false otherwise.
	 *
	 * @param project org.eclipse.core.resources.IProject
	 * @return boolean
	 */
	public IStatus canAddModule(IWebModule module);
}
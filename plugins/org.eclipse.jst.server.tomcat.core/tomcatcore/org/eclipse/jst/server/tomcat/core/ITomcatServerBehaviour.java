/**********************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.jst.server.tomcat.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;
/**
 * 
 */
public interface ITomcatServerBehaviour {
	/**
	 * Returns the main class that is used to launch the Tomcat server.
	 * 
	 * @return
	 */
	public String getRuntimeClass();

	/**
	 * Set the process that is monitored for Tomcat startup and shutdown.
	 * Warning: Do not call this method unless you know what you're doing;
	 * it should only be used in rare cases.
	 * 
	 * @param newProcess
	 */
	public void setProcess(IProcess newProcess);
	
	/**
	 * Setup for starting the server.
	 * 
	 * @param launch ILaunch
	 * @param launchMode String
	 * @param monitor IProgressMonitor
	 */
	public void setupLaunch(ILaunch launch, String launchMode, IProgressMonitor monitor) throws CoreException;
}
package org.eclipse.jst.server.tomcat.core;
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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;

import org.eclipse.wst.server.core.IServerExtension;
import org.eclipse.wst.server.core.model.IURLProvider;
/**
 * 
 */
public interface ITomcatServer extends IServerExtension, IURLProvider {
	public static final String PROPERTY_SECURE = "secure";
	public static final String PROPERTY_DEBUG = "debug";
	public static final String PROPERTY_TEST_ENVIRONMENT = "testEnvironment";

	/**
	 * Returns true if the process is set to run in debug mode.
	 * This feature only works with Tomcat v4.0.
	 *
	 * @return boolean
	 */
	public boolean isDebug();
	
	/**
	 * Returns true if the process is set to run in secure mode.
	 *
	 * @return boolean
	 */
	public boolean isSecure();
	
	/**
	 * Returns true if this is a test (run code out of the workbench) environment server.
	 *
	 * @return boolean
	 */
	public boolean isTestEnvironment();
	
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
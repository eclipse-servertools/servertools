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

import org.eclipse.wst.server.core.model.IURLProvider;
/**
 * 
 */
public interface ITomcatServer extends IURLProvider {
	public static final String PROPERTY_SECURE = "secure";
	public static final String PROPERTY_DEBUG = "debug";
	public static final String PROPERTY_TEST_ENVIRONMENT = "testEnvironment";

	/**
	 * Returns <code>true</code> if the server is set to run in Tomcat debug mode, and
	 * <code>false</code> otherwise.
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
	 * Returns true if this is a test (publish and run code out of the
	 * workbench) environment server.
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
	 * Return the Tomcat configuration model.
	 * 
	 * @return
	 */
	public ITomcatConfiguration getServerConfiguration();
}
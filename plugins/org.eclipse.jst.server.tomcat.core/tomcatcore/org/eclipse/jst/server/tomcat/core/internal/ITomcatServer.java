/**********************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.jst.server.tomcat.core.internal;

import org.eclipse.wst.server.core.model.IURLProvider;
/**
 *
 */
public interface ITomcatServer extends IURLProvider {
	/**
	 * Property which specifies whether this server is configured
	 * for testing environment.
	 */
	public static final String PROPERTY_TEST_ENVIRONMENT = "testEnvironment";
	
	/**
	 * Property which specifies the directory where the server instance
	 * exists.  If not specified, instance directory is derived
	 * from the textEnvironment setting.
	 */
	public static final String PROPERTY_INSTANCE_DIR = "instanceDir";
	/**
	 * Property which specifies the directory where web applications
	 * are published.
	 */
	public static final String PROPERTY_DEPLOY_DIR = "deployDir";

	/**
	 * Returns true if this is a test (publish and run code out of the
	 * workbench) environment server.
	 *
	 * @return boolean
	 */
	public boolean isTestEnvironment();
	
	/**
	 * Gets the directory where the server instance exists.  If not set,
	 * the instance directory is derived from the testEnvironment setting.  
	 * 
	 * @return directory where the server instance exists. Returns null
	 * if not set.
	 */
	public String getInstanceDirectory();

	/**
	 * Gets the directory to which web applications are to be deployed.
	 * If relative, it is relative to the runtime base directory for the
	 * server.
	 * 
	 * @return directory where web applications are deployed
	 */
	public String getDeployDirectory();
}
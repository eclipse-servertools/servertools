/*******************************************************************************
 * Copyright (c) 2005, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - Initial API and implementation
 *******************************************************************************/
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
	 * Property which specifies if modules should be served without
	 * publishing.
	 */
	public static final String PROPERTY_SERVE_MODULES_WITHOUT_PUBLISH = "serveModulesWithoutPublish";
	
	/**
	 * Property which specifies contexts in the server.xml file should
	 * be saved to separate context files.
	 */
	public static final String PROPERTY_SAVE_SEPARATE_CONTEXT_FILES = "saveSeparateContextFiles";
	
	/**
	 * Property which specifies contexts in the server.xml file should
	 * be reloadable by default.
	 */
	public static final String PROPERTY_MODULES_RELOADABLE_BY_DEFAULT = "modulesReloadableByDefault";

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
	
	/**
	 * Returns true if modules should be served directly from the project
	 * folders without publishing.
	 * 
	 * @return true if modules should not be published but served directly
	 */
	public boolean isServeModulesWithoutPublish();
	
	/**
	 * Returns true if contexts should be saved to separate context
	 * files instead of being kept within server.xml when the server
	 * is published.
	 * @return true if contexts should be saved to separate files
	 */
	public boolean isSaveSeparateContextFiles();
}

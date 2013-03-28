/*******************************************************************************
 * Copyright (c) 2013 Red Hat Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat Inc. - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core;

/**
 * A module that provides additional properties.
 * Variable constants that starts with "PROP_" are reserved for further purpose. Classes
 * that implements this class should not use variables with this naming convention.
 *
 * @see org.eclipse.wst.server.core.IModule
 * @since 1.5
 */
public interface IModule2 extends IModule {
	
	/**
	 * A property key to store a value for the module name to
	 * be displayed in servertools UI workflows
	 */
	public static final String PROP_DISPLAY_NAME = "org.eclipse.wst.server.core.displayName";

	/**
	 * A property key to store a value for the preferred name of the module 
	 * to be used by publishers when deploying.  
	 */
	public static final String PROP_DEPLOY_NAME = "org.eclipse.wst.server.core.deployName";

	/**
	 * Access a property of the module. 
	 * 
	 * @param key
	 * @see #PROP_DISPLAY_NAME
	 * @see #PROP_DEPLOY_NAME
	 * @return value of the property with the given key,
	 *   or <code>null</code> if the value is not available on this module.
	 */
	public String getProperty(String key);
}

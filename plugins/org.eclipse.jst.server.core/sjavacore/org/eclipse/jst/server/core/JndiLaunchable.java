/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.core;

import java.util.Properties;
/**
 * 
 * @since 1.0
 */
public class JndiLaunchable {
	private Properties props;
	private String jndiName;

	/**
	 * Create a reference to an object that is launchable via JNDI.
	 * 
	 * @param props the JNDI properties required to connect to the object
	 * @param jndiName the JNDI name of the object
	 */
	public JndiLaunchable(Properties props, String jndiName) {
		this.jndiName = jndiName;
		this.props = props;
	}

	/**
	 * Returns the JNDI properties required to connect to the object.
	 * 
	 * @return the JNDI properties required to connect to the object
	 */
	public Properties getProperties() {
		return props;
	}

	/**
	 * Returns the JNDI name of the object.
	 * 
	 * @return the JNDI name of the object
	 */
	public String getJNDIName() {
		return jndiName;
	}
}
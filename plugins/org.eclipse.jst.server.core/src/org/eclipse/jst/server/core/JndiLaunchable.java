/*******************************************************************************
 * Copyright (c) 2003, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.core;

import java.util.Properties;
/**
 * A representation of an object in JNDI that can be tested on a server.
 * <p>
 * <b>Provisional API:</b> This class/interface is part of an interim API that is still under development and expected to 
 * change significantly before reaching stability. It is being made available at this early stage to solicit feedback 
 * from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken 
 * (repeatedly) as the API evolves.
 * </p>
 * @plannedfor 3.0
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
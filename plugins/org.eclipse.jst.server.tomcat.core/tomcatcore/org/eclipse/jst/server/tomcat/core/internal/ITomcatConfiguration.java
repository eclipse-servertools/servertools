/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.tomcat.core.internal;

import java.util.List;
/**
 * 
 */
public interface ITomcatConfiguration {
	/**
	 * Returns a list of mime mappings.
	 * 
	 * @return mime mappings
	 */
	public List getMimeMappings();
	
	/**
	 * Returns a list of ServerPorts that this configuration uses.
	 *
	 * @return the server ports
	 */
	public List getServerPorts();

	/**
	 * Return a list of the web modules in this server.
	 * 
	 * @return the web modules
	 */
	public List getWebModules();
}
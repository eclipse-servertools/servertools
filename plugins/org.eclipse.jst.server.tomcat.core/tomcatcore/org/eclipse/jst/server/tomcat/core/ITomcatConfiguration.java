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

import java.util.List;

import org.eclipse.wst.server.core.IServerExtension;
/**
 * 
 */
public interface ITomcatConfiguration extends IServerExtension {
	/**
	 * Returns a list of mime mappings.
	 * @return java.util.List
	 */
	public List getMimeMappings();
	
	/**
	 * Returns a list of ServerPorts that this configuration uses.
	 *
	 * @return java.util.List
	 */
	public List getServerPorts();

	/**
	 * Return a list of the web modules in this server.
	 * @return java.util.List
	 */
	public List getWebModules();
}
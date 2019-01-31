/*******************************************************************************
 * Copyright (c) 2007, 2011 IBM Corporation and others.
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
package org.eclipse.wst.server.ui;

import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
/**
 * A helper interface for modules that are deployed to a server, commonly
 * used to help actions interact with modules in the Servers view.
 *
 * @since 1.1
 */
public interface IServerModule {
	/**
	 * Return the server that the module belongs to.
	 * 
	 * @return the server
	 */
	public IServer getServer();

	/**
	 * Returns the module.
	 * 
	 * @return the module
	 */
	public IModule[] getModule();
}
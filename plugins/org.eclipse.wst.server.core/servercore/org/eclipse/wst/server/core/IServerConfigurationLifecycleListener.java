/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core;
/**
 * Listener interface for server configuration changes.
 */
public interface IServerConfigurationLifecycleListener {
	/**
	 * A new server configuration has been created.
	 *
	 * @param serverConfiguration org.eclipse.wst.server.core.IServerConfiguration
	 */
	public void serverConfigurationAdded(IServerConfiguration serverConfiguration);

	/**
	 * An existing server configuration has been updated or modified.
	 *
	 * @param serverConfiguration org.eclipse.wst.server.core.IServerConfiguration
	 */
	public void serverConfigurationChanged(IServerConfiguration serverConfiguration);

	/**
	 * A existing configuration has been removed.
	 *
	 * @param serverConfiguration org.eclipse.wst.server.core.IServerConfiguration
	 */
	public void serverConfigurationRemoved(IServerConfiguration serverConfiguration);
}
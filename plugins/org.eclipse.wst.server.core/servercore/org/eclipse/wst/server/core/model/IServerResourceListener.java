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
package org.eclipse.wst.server.core.model;

import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerConfiguration;
/**
 * Listener interface for server and server configuration changes.
 */
public interface IServerResourceListener {
	/**
	 * A new runtime has been created.
	 *
	 * @param runtime org.eclipse.wst.server.core.IRuntime
	 */
	public void runtimeAdded(IRuntime runtime);

	/**
	 * An existing runtime has been updated or modified.
	 *
	 * @param runtime org.eclipse.wst.server.core.IRuntime
	 */
	public void runtimeChanged(IRuntime runtime);

	/**
	 * A existing runtime has been removed.
	 *
	 * @param runtime org.eclipse.wst.server.core.IRuntime
	 */
	public void runtimeRemoved(IRuntime runtime);

	/**
	 * A new server has been created.
	 *
	 * @param server org.eclipse.wst.server.core.IServer
	 */
	public void serverAdded(IServer server);

	/**
	 * An existing server has been updated or modified.
	 *
	 * @param server org.eclipse.wst.server.core.IServer
	 */
	public void serverChanged(IServer server);

	/**
	 * A existing server has been removed.
	 *
	 * @param server org.eclipse.wst.server.core.IServer
	 */
	public void serverRemoved(IServer server);
	
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

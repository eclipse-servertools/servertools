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
package org.eclipse.wst.server.core.util;

import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerConfiguration;
import org.eclipse.wst.server.core.model.IServerResourceListener;
/**
 * 
 */
public class ServerResourceAdapter implements IServerResourceListener {
	public void runtimeAdded(IRuntime runtime) { }

	public void runtimeChanged(IRuntime runtime) { }

	public void runtimeRemoved(IRuntime runtime) { }

	public void serverAdded(IServer server) { }

	public void serverChanged(IServer server) { }

	public void serverRemoved(IServer server) { }

	public void serverConfigurationAdded(IServerConfiguration serverConfiguration) { }

	public void serverConfigurationChanged(IServerConfiguration serverConfiguration) { }

	public void serverConfigurationRemoved(IServerConfiguration serverConfiguration) { }
}
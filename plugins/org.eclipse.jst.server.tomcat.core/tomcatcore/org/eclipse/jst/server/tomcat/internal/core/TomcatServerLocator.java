package org.eclipse.jst.server.tomcat.internal.core;
/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.model.IRuntimeLocatorListener;
import org.eclipse.wst.server.core.model.IServerLocatorDelegate;
import org.eclipse.wst.server.core.model.IServerLocatorListener;
/**
 * 
 */
public class TomcatServerLocator extends TomcatRuntimeLocator implements IServerLocatorDelegate {
	/*protected static final String[] serverTypes = new String[] {
		"org.eclipse.jst.server.tomcat.32",
		"org.eclipse.jst.server.tomcat.40",
		"org.eclipse.jst.server.tomcat.41",
		"org.eclipse.jst.server.tomcat.50"};*/

	public void searchForServers(final IServerLocatorListener listener, IProgressMonitor monitor) {
		IRuntimeLocatorListener listener2 = new IRuntimeLocatorListener() {
			public void runtimeFound(IRuntime runtime) {
				String runtimeTypeId = runtime.getRuntimeType().getId();
				String serverTypeId = runtimeTypeId.substring(0, runtimeTypeId.length() - 8);
				IServerType serverType = ServerCore.getServerType(serverTypeId);
				try {
					IServer server = serverType.createServer(serverTypeId, null, runtime);
					listener.serverFound(server);
				} catch (Exception e) {
					Trace.trace(Trace.WARNING, "Could not create Tomcat server", e);
				}
			}
		};
		searchForRuntimes(listener2, monitor);
	}
}
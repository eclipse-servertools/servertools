/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.jst.server.tomcat.core.internal;

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.model.ServerLocatorDelegate;
/**
 * 
 */
public class TomcatServerLocator extends ServerLocatorDelegate {
	public void searchForServers(String host, final IServerLocator.Listener listener, final IProgressMonitor monitor) {
		IRuntimeLocator.RuntimeSearchListener listener2 = new IRuntimeLocator.RuntimeSearchListener() {
			public void runtimeFound(IRuntimeWorkingCopy runtime) {
				String runtimeTypeId = runtime.getRuntimeType().getId();
				String serverTypeId = runtimeTypeId.substring(0, runtimeTypeId.length() - 8);
				IServerType serverType = ServerCore.findServerType(serverTypeId);
				try {
					IServerWorkingCopy server = serverType.createServer(serverTypeId, null, runtime, monitor);
					listener.serverFound(server);
				} catch (Exception e) {
					Trace.trace(Trace.WARNING, "Could not create Tomcat server", e);
				}
			}
		};
		TomcatRuntimeLocator.searchForRuntimes2(null, listener2, monitor);
	}
}
/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.preview.internal.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jst.server.preview.internal.Trace;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerType;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.internal.IStartup;

public class PreviewStartup implements IStartup {
	private static final String ID = "xyz"; 

	public void startup() {
		// create runtime
		IRuntime[] runtimes = ServerCore.getRuntimes();
		IRuntime runtime = null;
		
		int size = runtimes.length;
		for (int i = 0; i < size; i++) {
			if (runtimes[i].getRuntimeType() != null && PreviewRuntime.ID.equals(runtimes[i].getRuntimeType().getId())) {
				if (ID.equals(runtimes[i].getId()))
					runtime = runtimes[i];
			}
		}
		
		if (runtime == null) {
			try {
				IRuntimeType runtimeType = ServerCore.findRuntimeType(PreviewRuntime.ID);
				IRuntimeWorkingCopy wc = runtimeType.createRuntime(ID, null);
				wc.setName("My Preview");
				wc.setReadOnly(true);
				runtime = wc.save(true, null);
			} catch (CoreException ce) {
				Trace.trace(Trace.WARNING, "Could not create default preview runtime");
			}
		}
		
		// create server
		IServer[] servers = ServerCore.getServers();
		
		boolean found = false;
		size = servers.length;
		for (int i = 0; i < size; i++) {
			if (servers[i].getServerType() != null && PreviewServer.ID.equals(servers[i].getServerType().getId())) {
				if (ID.equals(servers[i].getId()))
					found = true;
			}
		}
		
		if (!found) {
			try {
				IServerType serverType = ServerCore.findServerType(PreviewServer.ID);
				IServerWorkingCopy wc = serverType.createServer(ID, null, runtime, null);
				wc.setName("My preview");
				wc.setHost("localhost");
				wc.setReadOnly(true);
				wc.save(true, null);
			} catch (CoreException ce) {
				Trace.trace(Trace.WARNING, "Could not create default preview server");
			}
		}
	}
}
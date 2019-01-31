/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
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
package org.eclipse.jst.server.preview.adapter.internal.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jst.server.preview.adapter.internal.Trace;
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
		
		for (IRuntime r : runtimes) {
			if (r.getRuntimeType() != null && PreviewRuntime.ID.equals(r.getRuntimeType().getId())) {
				if (ID.equals(r.getId()))
					runtime = r;
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
		for (IServer s : servers) {
			if (s.getServerType() != null && PreviewServer.ID.equals(s.getServerType().getId())) {
				if (ID.equals(s.getId()))
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
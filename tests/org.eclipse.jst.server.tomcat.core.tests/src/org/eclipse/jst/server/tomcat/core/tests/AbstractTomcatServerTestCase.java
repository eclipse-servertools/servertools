/**********************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.jst.server.tomcat.core.tests;

import junit.framework.Test;
import org.eclipse.core.runtime.Path;
import org.eclipse.jst.server.tomcat.core.internal.ITomcatConfigurationWorkingCopy;
import org.eclipse.jst.server.tomcat.core.internal.TomcatServer;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.tests.ext.AbstractServerTestCase;

public abstract class AbstractTomcatServerTestCase extends AbstractServerTestCase {
	public static Test suite() {
		return new OrderedTestSuite(AbstractTomcatServerTestCase.class, "TomcatServerTestCase");
	}

	protected abstract String getServerTypeId();

	public IServer createServer() throws Exception {
		try {
			IServerWorkingCopy wc = createServer(getServerTypeId());
			return wc.save(true, null);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void deleteServer(IServer server2) throws Exception {
		server2.getRuntime().delete();
		server2.delete();
	}

	protected IRuntime createRuntime() {
		try {
			IServerType st = ServerCore.findServerType(getServerTypeId());
			IRuntimeWorkingCopy wc = createRuntime(st.getRuntimeType());
			return wc.save(true, null);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	protected IRuntimeWorkingCopy createRuntime(IRuntimeType rt) throws Exception {
		IRuntimeWorkingCopy wc = rt.createRuntime(null, null);
		wc.setLocation(new Path(RuntimeLocation.runtimeLocation));
		return wc;
	}
	
	protected IServerWorkingCopy createServer(String serverTypeId) throws Exception {
		IServerType st = ServerCore.findServerType(serverTypeId);
		IRuntime runtime = createRuntime();
		IServerWorkingCopy wc = st.createServer(null, null, runtime, null);
		
		IServerPort[] ports = wc.getServerPorts();
		TomcatServer tomcatServer = (TomcatServer) wc.getAdapter(TomcatServer.class);
		ITomcatConfigurationWorkingCopy configuration = (ITomcatConfigurationWorkingCopy) tomcatServer.getServerConfiguration();
		if (ports != null) {
			int size = ports.length;
			for (int i = 0; i < size; i++) {
				configuration.modifyServerPort(ports[i].getId(), 22100 + i);
			}
		}
		
		return wc;
	}
}
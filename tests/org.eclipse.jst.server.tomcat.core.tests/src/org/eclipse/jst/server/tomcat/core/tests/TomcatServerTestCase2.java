/**********************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.jst.server.tomcat.core.tests;

import org.eclipse.core.runtime.Path;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.tests.ext.AbstractServerTestCase;

public class TomcatServerTestCase2 extends AbstractServerTestCase {
	private static final String RUNTIME_TYPE_ID_32 = "org.eclipse.jst.server.tomcat.runtime.32";
	private static final String SERVER_TYPE_ID_32 = "org.eclipse.jst.server.tomcat.32";

	/*public static Test suite() {
		return new OrderedTestSuite(TomcatRuntimeTestCase2.class, "TomcatRuntimeTestCase");
	}*/
	
	public IServer createServer() {
		try {
			IServerWorkingCopy wc = createServer(SERVER_TYPE_ID_32);
			return wc.save(true, null);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public IRuntime createRuntime() {
		try {
			IRuntimeWorkingCopy wc = createRuntime(RUNTIME_TYPE_ID_32);
			return wc.save(true, null);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	protected IRuntimeWorkingCopy createRuntime(String runtimeTypeId) throws Exception {
		IRuntimeType rt = ServerCore.findRuntimeType(runtimeTypeId);
		IRuntimeWorkingCopy wc = rt.createRuntime("a", null);
		wc.setLocation(new Path("c://test"));
		return wc;
	}
	
	protected IServerWorkingCopy createServer(String serverTypeId) throws Exception {
		IServerType st = ServerCore.findServerType(serverTypeId);
		IRuntime runtime = createRuntime();
		IServerWorkingCopy wc = st.createServer("a", null, runtime, null);
		wc.setRuntime(runtime);
		return wc;
	}
}
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

import junit.framework.Test;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.internal.Server;
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
		wc.setRuntime(runtime);
		
		IFolder folder = getServerProject().getFolder(wc.getName() + "-config");
		wc.setServerConfiguration(folder);
		
		((Server)wc).importConfiguration(runtime, null);
		
		return wc;
	}
	
	public static IProject getServerProject() {
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		if (projects != null) {
			int size = projects.length;
			for (int i = 0; i < size; i++) {
				if (ServerCore.getProjectProperties(projects[i]).isServerProject())
					return projects[i];
			}
		}
		
		String s = "Servers";
		return ResourcesPlugin.getWorkspace().getRoot().getProject(s);
	}
}
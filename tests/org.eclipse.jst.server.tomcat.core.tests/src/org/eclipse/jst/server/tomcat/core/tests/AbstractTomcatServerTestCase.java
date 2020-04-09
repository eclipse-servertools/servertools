/*******************************************************************************
 * Copyright (c) 2005, 2020 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.tomcat.core.tests;

import java.io.File;
import java.util.List;

import junit.framework.TestSuite;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.jst.server.tomcat.core.internal.ITomcatConfigurationWorkingCopy;
import org.eclipse.jst.server.tomcat.core.internal.ITomcatServerWorkingCopy;
import org.eclipse.jst.server.tomcat.core.internal.TomcatServer;
import org.eclipse.jst.server.tomcat.core.internal.TomcatServerBehaviour;
import org.eclipse.jst.server.tomcat.core.tests.module.ModuleTestCase;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerType;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.ServerPort;
import org.eclipse.wst.server.core.internal.Server;
import org.eclipse.wst.server.core.model.IModuleFolder;
import org.eclipse.wst.server.core.model.IModuleResource;
import org.eclipse.wst.server.core.tests.ext.AbstractServerTestCase;

public abstract class AbstractTomcatServerTestCase extends AbstractServerTestCase {
	// See Bug 419182: Due to lazy loading, launching Tomcat in a JUnit test can cause
	// the DebugUIPlugin to load and initialize on the job thread that will try to
	// start Tomcat.  There is a risk that in some environments that initialization
	// of the DebugUIPlugin may invoke some initialization that is required to
	// run in the main/UI thread. The exception that gets throw can cause Tomcat
	// not to start, resulting in test failures.  The following code forces the
	// DebugUIPlugin to load and initialize on the thread loading this test plug-in,
	// rather than taking the risk of loading it on the job thread when Tomcat is
	// launched.
	protected static boolean workaround = DebugUIPlugin.DEBUG; // Access something in DebugUIPlugin;

	protected abstract String getServerTypeId();
	
	public void deleteServer(IServer server2) throws Exception {
		server2.getRuntime().delete();
		server2.delete();
	}

	protected IRuntime createRuntime() throws Exception {
		IServerType st = ServerCore.findServerType(getServerTypeId());
		IRuntimeWorkingCopy wc = st.getRuntimeType().createRuntime(null, null);
		wc.setLocation(new Path(RuntimeLocation.runtimeLocation));
		return wc.save(true, null);
	}

	public IServer createServer() throws Exception {
		IServerType st = ServerCore.findServerType(getServerTypeId());
		IRuntime runtime = createRuntime();
		IServerWorkingCopy wc = st.createServer(null, null, runtime, null);
		
		ServerPort[] ports = wc.getServerPorts(null);
		TomcatServer tomcatServer = wc.getAdapter(TomcatServer.class);
		ITomcatConfigurationWorkingCopy configuration = (ITomcatConfigurationWorkingCopy) tomcatServer.getServerConfiguration();
		// if no ports from the server, use the configuration
		if (ports == null || ports.length == 0) {
			List portsList = configuration.getServerPorts();
			if (portsList != null && portsList.size() > 0) {
				ports = (ServerPort[])portsList.toArray(new ServerPort[portsList.size()]);
			}
		}
		if (ports != null) {
			int size = ports.length;
			for (int i = 0; i < size; i++) {
				configuration.modifyServerPort(ports[i].getId(), 22100 + i);
			}
		}
		
		return wc.save(true, null);
	}

	public TomcatServer getTomcatServer() throws Exception {
		return (TomcatServer)getServer().loadAdapter(TomcatServer.class, null);
	}
	
	public TomcatServerBehaviour getTomcatServerBehaviour() throws Exception {
		return (TomcatServerBehaviour)getServer().loadAdapter(TomcatServerBehaviour.class, null);
	}

	public static void addOrderedTests(Class testClass,TestSuite suite) {
		AbstractServerTestCase.addOrderedTests(testClass, suite);
		suite.addTest(TestSuite.createTest(testClass, "canAddModule"));
		suite.addTest(TestSuite.createTest(testClass, "hasModule"));
		suite.addTest(TestSuite.createTest(testClass, "addModule"));
		suite.addTest(TestSuite.createTest(testClass, "hasModule2"));
		suite.addTest(TestSuite.createTest(testClass, "removeModule"));
		suite.addTest(TestSuite.createTest(testClass, "hasModule3"));
		suite.addTest(TestSuite.createTest(testClass, "verifyDefaultDeployConfig"));
		suite.addTest(TestSuite.createTest(testClass, "verifyDefaultAddPublish"));
		suite.addTest(TestSuite.createTest(testClass, "verifyDefaultRemovePublish"));
		suite.addTest(TestSuite.createTest(testClass, "verifyLegacyDeployConfig"));
		suite.addTest(TestSuite.createTest(testClass, "verifyLegacyAddPublish"));
		suite.addTest(TestSuite.createTest(testClass, "verifyLegacyRemovePublish"));
		AbstractServerTestCase.addFinalTests(testClass, suite);
	}

	public void canAddModule() throws Exception {
		IModule webModule = ModuleTestCase.webModule;
		IStatus status = getServer().canModifyModules(new IModule[] { webModule }, null, null);
		assertTrue(status.isOK());
	}

	public void hasModule() throws Exception {
		IModule webModule = ModuleTestCase.webModule;
		IModule[] modules = getServer().getModules();
		int size = modules.length;
		boolean found = false;
		for (int i = 0; i < size; i++) {
			if (webModule.equals(modules[i]))
				found = true;
		}
		if (found)
			assertTrue(false);
	}

	public void addModule() throws Exception {
		IModule webModule = ModuleTestCase.webModule;
		IServerWorkingCopy wc = getServer().createWorkingCopy();
		wc.modifyModules(new IModule[] { webModule }, null, null);
		wc.save(true, null);
	}

	public void hasModule2() throws Exception {
		IModule webModule = ModuleTestCase.webModule;
		IModule[] modules = getServer().getModules();
		int size = modules.length;
		boolean found = false;
		for (int i = 0; i < size; i++) {
			if (webModule.equals(modules[i]))
				found = true;
		}
		if (!found)
			assertTrue(false);
	}

	public void removeModule() throws Exception {
		IModule webModule = ModuleTestCase.webModule;
		IServerWorkingCopy wc = getServer().createWorkingCopy();
		wc.modifyModules(null, new IModule[] { webModule }, null);
		wc.save(true, null);
	}

	public void hasModule3() throws Exception {
		IModule webModule = ModuleTestCase.webModule;
		IModule[] modules = getServer().getModules();
		int size = modules.length;
		boolean found = false;
		for (int i = 0; i < size; i++) {
			if (webModule.equals(modules[i]))
				found = true;
		}
		if (found)
			assertTrue(false);
	}
	
	/*
	 * Tests to verify modules are deployed correctly per configuration
	 */
	
	/**
	 * @throws Exception
	 */
	public void verifyDefaultDeployConfig() throws Exception {
		TomcatServer ts = getTomcatServer();
		assertNotNull(ts);
		TomcatServerBehaviour tsb = getTomcatServerBehaviour();
		assertNotNull(tsb);
		assertEquals(ITomcatServerWorkingCopy.DEFAULT_DEPLOYDIR, ts.getDeployDirectory());
		IPath tempDir = tsb.getTempDirectory();
		IPath baseDir = tsb.getRuntimeBaseDirectory();
		assertEquals(tempDir, baseDir);
		IPath deployDir = tsb.getServerDeployDirectory();
		assertEquals(baseDir.append(ITomcatServerWorkingCopy.DEFAULT_DEPLOYDIR), deployDir);
	}

	protected abstract void verifyPublishedModule(IPath baseDir, IModule module) throws Exception;
	
	protected void verifyPublishedModuleFiles(IModule module) throws Exception {
		File moduleDir = new File(getTomcatServerBehaviour().getModuleDeployDirectory(module).toOSString());
		assertTrue("Module " + module.getName() + " root directory doesn't exist: " + moduleDir.getPath(), moduleDir.exists());
		IModuleResource [] resources = ((Server)getServer()).getResources(new IModule [] { module });
		for (int i = 0; i < resources.length; i++) {
			if (resources[i] instanceof IModuleFolder) {
				verifyPublishedModuleFolder(moduleDir, (IModuleFolder)resources[i]);
			}
			else {
				String path = resources[i].getModuleRelativePath().append(resources[i].getName()).toOSString();
				File file = new File(moduleDir, path);
				assertTrue("Module file doesn't exist: " + file.getPath(), file.exists());
			}
		}
	}
	
	protected void verifyPublishedModuleFolder(File moduleDir, IModuleFolder mf) throws Exception {
		IModuleResource [] resources = mf.members();
		for (int i = 0; i < resources.length; i++) {
			if (resources[i] instanceof IModuleFolder) {
				verifyPublishedModuleFolder(moduleDir, (IModuleFolder)resources[i]);
			}
			else {
				String path = resources[i].getModuleRelativePath().append(resources[i].getName()).toOSString();
				File file = new File(moduleDir, path);
				assertTrue("Module file/folder doesn't exist: " + file.getPath(), file.exists());
			}
		}
	}	
	
	/**
	 * @throws Exception
	 */
	public void verifyDefaultAddPublish() throws Exception {
		IModule webModule = ModuleTestCase.webModule;
		IServerWorkingCopy wc = getServer().createWorkingCopy();
		wc.modifyModules(new IModule[] { webModule }, null, null);
		wc.save(true, null);
		getServer().publish(IServer.PUBLISH_FULL, null);
		
		TomcatServerBehaviour tsb = getTomcatServerBehaviour();
		IPath baseDir = tsb.getRuntimeBaseDirectory();
		IPath moduleDir = baseDir.append(ITomcatServerWorkingCopy.DEFAULT_DEPLOYDIR).append(ModuleTestCase.webModule.getName());
		assertTrue(moduleDir.toFile().exists());
		verifyPublishedModule(baseDir, ModuleTestCase.webModule);
	}
	
	/**
	 * @throws Exception
	 */
	public void verifyDefaultRemovePublish() throws Exception {
		IModule webModule = ModuleTestCase.webModule;
		IServerWorkingCopy wc = getServer().createWorkingCopy();
		wc.modifyModules(null, new IModule[] { webModule }, null);
		wc.save(true, null);
		getServer().publish(IServer.PUBLISH_FULL, null);
		
		TomcatServerBehaviour tsb = getTomcatServerBehaviour();
		IPath baseDir = tsb.getRuntimeBaseDirectory();
		IPath moduleDir = baseDir.append(ITomcatServerWorkingCopy.DEFAULT_DEPLOYDIR).append(ModuleTestCase.webModule.getName());
		assertFalse(moduleDir.toFile().exists());
	}

	/**
	 * Verify configuration when deployment directory is unset.
	 * Deployment directory should default to "webapps".
	 * @throws Exception
	 */
	public void verifyLegacyDeployConfig() throws Exception {
		TomcatServer ts = getTomcatServer();
		assertNotNull(ts);
		ts.setDeployDirectory("webapps");
		TomcatServerBehaviour tsb = getTomcatServerBehaviour();
		assertNotNull(tsb);
		assertEquals("webapps", ts.getDeployDirectory());
		// Verify that legacy setting results in attribute removal
		Server svr = (Server)getServer().loadAdapter(Server.class, null);
		assertNotNull(svr);
		String attr = svr.getAttribute("webapps", (String)null);
		assertNull(attr);

		IPath tempDir = tsb.getTempDirectory();
		IPath baseDir = tsb.getRuntimeBaseDirectory();
		assertEquals(tempDir, baseDir);
		IPath deployDir = tsb.getServerDeployDirectory();
		assertEquals(baseDir.append("webapps"), deployDir);
	}

	/**
	 * @throws Exception
	 */
	public void verifyLegacyAddPublish() throws Exception {
		IModule webModule = ModuleTestCase.webModule;
		IServerWorkingCopy wc = getServer().createWorkingCopy();
		// Unset the deployment directory
		((TomcatServer)wc.loadAdapter(TomcatServer.class, null)).setDeployDirectory(null);
		wc.modifyModules(new IModule[] { webModule }, null, null);
		wc.save(true, null);
		getServer().publish(IServer.PUBLISH_FULL, null);
		
		TomcatServerBehaviour tsb = getTomcatServerBehaviour();
		IPath baseDir = tsb.getRuntimeBaseDirectory();
		IPath moduleDir = baseDir.append("webapps").append(ModuleTestCase.webModule.getName());
		assertTrue(moduleDir.toFile().exists());
		verifyPublishedModule(baseDir, ModuleTestCase.webModule);
	}
	
	/**
	 * @throws Exception
	 */
	public void verifyLegacyRemovePublish() throws Exception {
		IModule webModule = ModuleTestCase.webModule;
		IServerWorkingCopy wc = getServer().createWorkingCopy();
		// Unset the deployment directory
		((TomcatServer)wc.loadAdapter(TomcatServer.class, null)).setDeployDirectory(null);
		wc.modifyModules(null, new IModule[] { webModule }, null);
		wc.save(true, null);
		getServer().publish(IServer.PUBLISH_FULL, null);
		
		TomcatServerBehaviour tsb = getTomcatServerBehaviour();
		IPath baseDir = tsb.getRuntimeBaseDirectory();
		IPath moduleDir = baseDir.append("webapps").append(ModuleTestCase.webModule.getName());
		assertFalse(moduleDir.toFile().exists());
	}
}

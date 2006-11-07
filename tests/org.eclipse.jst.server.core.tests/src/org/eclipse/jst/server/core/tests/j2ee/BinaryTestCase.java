/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.core.tests.j2ee;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jst.server.core.IEnterpriseApplication;
import org.eclipse.jst.server.core.IJ2EEModule;
import org.eclipse.jst.server.core.IWebModule;
import org.eclipse.wst.server.core.*;

import junit.framework.TestCase;

public class BinaryTestCase extends TestCase {
	private static final String[] PROJECT_NAMES = new String[] {
		"PublishTestEAR", "PublishTestEJB", "test", "test2",
		"PublishTestWeb", "PublishTestWeb2", "PublishTestConnector", "PublishTestClient"
	};

	protected static IRuntime runtime;
	protected static IModule module;
	protected static IEnterpriseApplication ent;
	protected static IWebModule webModule;
	protected static IJ2EEModule j2eeModule;

	public void test001ImportModules() throws Exception {
		ModuleHelper.importProject("PublishTestBinary.zip", new String[] { PROJECT_NAMES[0] } );
	}

	public void test002IncrementalBuild() throws Exception {
		ModuleHelper.buildIncremental();
	}

	public void test003NumModules() throws Exception {
		assertEquals(ServerUtil.getModules("jst.ear").length, 1);
	}

	public void test004NumModules() throws Exception {
		assertEquals(ServerUtil.getModules("jst.web").length, 2);
	}

	public void test005NumModules() throws Exception {
		assertEquals(ServerUtil.getModules("jst.ejb").length, 1);
	}

	public void test006NumModules() throws Exception {
		assertEquals(ServerUtil.getModules("jst.utility").length, 0);
	}

	public void test007NumModules() throws Exception {
		assertEquals(ServerUtil.getModules("jst.appclient").length, 1);
	}

	public void test008NumModules() throws Exception {
		assertEquals(ServerUtil.getModules("jst.connector").length, 1);
	}


	// ---------- EAR tests ----------

	public void test020EAR() throws Exception {
		//module = ModuleHelper.getModuleFromProject("PublishTestEAR");
		module = ModuleHelper.getModule("jst.ear", "PublishTestEAR");
		assertNotNull(module);
	}

	public void test021EAR() throws Exception {
		if (!ModuleHelper.fileExists(module, "META-INF/application.xml"))
			fail();
	}

	public void test022EAR() throws Exception {
		if (!ModuleHelper.fileExists(module, "PublishTestUtil2.jar"))
			fail();
	}

	public void test023EAR() throws Exception {
		assertEquals(ModuleHelper.countFiles(module), 4);
	}

	public void test024EAR() throws Exception {
		assertEquals(ModuleHelper.countFolders(module), 1);
	}

	public void test025EAR() throws Exception {
		assertEquals(module.getModuleType().getId(), "jst.ear");
	}

	public void test026EAR() throws Exception {
		ent = (IEnterpriseApplication) module.loadAdapter(IEnterpriseApplication.class, null);
		assertNotNull(ent);
	}

	public void test027EAR() throws Exception {
		assertEquals(ent.getResourceFolders().length, 1);
	}

	public void test028EAR() throws Exception {
		assertEquals(ent.getModules().length, 5);
	}

	public void test029EAR() throws Exception {
		IModule[] modules = ent.getModules();
		int size = modules.length;
		List list = new ArrayList(size);
		for (int i = 0; i < size; i++) {
			list.add(modules[i].getName());
		}
		
		String[] s = new String[] {
			"PublishTestEJB.jar", "PublishTestWeb.war",
			"PublishTestWeb2.war", "PublishTestConnector.rar", "PublishTestClient.jar"
		};
		
		size = s.length;
		for (int i = 0; i < size; i++) {
			if (!list.contains("lib/PublishTestEAR/EarContent/" + s[i]))
				fail("EAR does not contain " + s[i]);
		}
	}


	// ---------- EJB tests ----------

	public void test080EJB() throws Exception {
		module = ModuleHelper.getModule("jst.ejb", "lib/PublishTestEAR/EarContent/PublishTestEJB.jar");
		assertNotNull(module);
	}

	public void test081EJB() throws Exception {
		if (!ModuleHelper.fileExists(module, "PublishTestEJB.jar"))
			fail();
	}

	public void test084EJB() throws Exception {
		assertEquals(ModuleHelper.countFiles(module), 1);
	}

	public void test085EJB() throws Exception {
		assertEquals(ModuleHelper.countFolders(module), 0);
	}

	public void test086EJB() throws Exception {
		assertEquals(module.getModuleType().getId(), "jst.ejb");
	}

	public void test087EJB() throws Exception {
		j2eeModule = (IJ2EEModule) module.loadAdapter(IJ2EEModule.class, null);
		assertNotNull(j2eeModule);
	}

	public void test088EJB() throws Exception {
		assertEquals(j2eeModule.getResourceFolders().length, 1);
	}

	public void test089EJB() throws Exception {
		assertEquals(j2eeModule.getJavaOutputFolders().length, 0);
	}

	public void test090EJB() throws Exception {
		assertTrue(j2eeModule.isBinary());
	}


	// ---------- Connector tests ----------

	public void test100Connector() throws Exception {
		module = ModuleHelper.getModule("jst.connector", "lib/PublishTestEAR/EarContent/PublishTestConnector.rar");
		assertNotNull(module);
	}

	public void test101Connector() throws Exception {
		if (!ModuleHelper.fileExists(module, "PublishTestConnector.rar"))
			fail();
	}

	public void test104Connector() throws Exception {
		assertEquals(ModuleHelper.countFiles(module), 1);
	}

	public void test105Connector() throws Exception {
		assertEquals(ModuleHelper.countFolders(module), 0);
	}

	public void test106Connector() throws Exception {
		assertEquals(module.getModuleType().getId(), "jst.connector");
	}

	public void test107Connector() throws Exception {
		j2eeModule = (IJ2EEModule) module.loadAdapter(IJ2EEModule.class, null);
		assertNotNull(j2eeModule);
	}

	public void test108Connector() throws Exception {
		assertEquals(j2eeModule.getResourceFolders().length, 1);
	}

	public void test109Connector() throws Exception {
		assertEquals(j2eeModule.getJavaOutputFolders().length, 0);
	}

	public void test110Connector() throws Exception {
		assertTrue(j2eeModule.isBinary());
	}


	// ---------- Client tests ----------

	public void test120Client() throws Exception {
		module = ModuleHelper.getModule("jst.appclient", "lib/PublishTestEAR/EarContent/PublishTestClient.jar");
		assertNotNull(module);
	}

	public void test121Client() throws Exception {
		if (!ModuleHelper.fileExists(module, "PublishTestClient.jar"))
			fail();
	}

	public void test125Client() throws Exception {
		assertEquals(ModuleHelper.countFiles(module), 1);
	}

	public void test126Client() throws Exception {
		assertEquals(ModuleHelper.countFolders(module), 0);
	}

	public void test127Client() throws Exception {
		assertEquals(module.getModuleType().getId(), "jst.appclient");
	}

	public void test128Client() throws Exception {
		j2eeModule = (IJ2EEModule) module.loadAdapter(IJ2EEModule.class, null);
		assertNotNull(j2eeModule);
	}

	public void test129Client() throws Exception {
		assertEquals(j2eeModule.getResourceFolders().length, 1);
	}

	public void test130Client() throws Exception {
		assertEquals(j2eeModule.getJavaOutputFolders().length, 0);
	}

	public void test131Client() throws Exception {
		assertTrue(j2eeModule.isBinary());
	}


	// ---------- Web 1 tests ----------

	public void test140Web() throws Exception {
		module = ModuleHelper.getModule("jst.web", "lib/PublishTestEAR/EarContent/PublishTestWeb.war");
		assertNotNull(module);
	}

	public void test141Web() throws Exception {
		if (!ModuleHelper.fileExists(module, "PublishTestWeb.war"))
			fail();
	}

	public void test147Web() throws Exception {
		assertEquals(ModuleHelper.countFiles(module), 1);
	}

	public void test148Web() throws Exception {
		assertEquals(ModuleHelper.countFolders(module), 0);
	}

	public void test149Web() throws Exception {
		assertEquals(module.getModuleType().getId(), "jst.web");
	}

	public void test150Web() throws Exception {
		webModule = (IWebModule) module.loadAdapter(IWebModule.class, null);
		j2eeModule = webModule;
		assertNotNull(j2eeModule);
	}

	public void test151Web() throws Exception {
		assertEquals(j2eeModule.getResourceFolders().length, 1);
	}

	public void test152Web() throws Exception {
		assertEquals(j2eeModule.getJavaOutputFolders().length, 0);
	}

	public void test153Web() throws Exception {
		assertEquals(webModule.getModules().length, 0);
	}

	public void _test154Web() throws Exception {
		assertEquals(webModule.getContextRoot(), "PublishTestWeb");
	}

	public void test155Web() throws Exception {
		assertTrue(webModule.isBinary());
	}


	// ---------- Web 2 tests ----------

	public void test160Web() throws Exception {
		module = ModuleHelper.getModule("jst.web", "lib/PublishTestEAR/EarContent/PublishTestWeb2.war");
		assertNotNull(module);
	}

	public void test161Web() throws Exception {
		if (!ModuleHelper.fileExists(module, "PublishTestWeb2.war"))
			fail();
	}

	public void test166Web() throws Exception {
		assertEquals(ModuleHelper.countFiles(module), 1);
	}

	public void test167Web() throws Exception {
		assertEquals(ModuleHelper.countFolders(module), 0);
	}

	public void test168Web() throws Exception {
		assertEquals(module.getModuleType().getId(), "jst.web");
	}

	public void test169Web() throws Exception {
		webModule = (IWebModule) module.loadAdapter(IWebModule.class, null);
		j2eeModule = webModule;
		assertNotNull(j2eeModule);
	}

	public void test170Web() throws Exception {
		assertEquals(j2eeModule.getResourceFolders().length, 1);
	}

	public void test171Web() throws Exception {
		assertEquals(j2eeModule.getJavaOutputFolders().length, 0);
	}

	public void test172Web() throws Exception {
		assertEquals(webModule.getModules().length, 0);
	}

	public void _test173Web() throws Exception {
		assertEquals(webModule.getContextRoot(), "PublishTestWeb2");
	}

	public void test174Client() throws Exception {
		assertTrue(webModule.isBinary());
	}


	public void test199Cleanup() throws Exception {
		ModuleHelper.deleteProject(PROJECT_NAMES[0]);
	}
}
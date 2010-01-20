/*******************************************************************************
 * Copyright (c) 2006, 2010 IBM Corporation and others.
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

import org.eclipse.jst.server.core.*;
import org.eclipse.wst.server.core.*;

import junit.framework.TestCase;

public class NoSourceTestCase extends TestCase {
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
		ModuleHelper.importProject("PublishEARNoSource.zip", PROJECT_NAMES);
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
		assertEquals(ServerUtil.getModules("jst.utility").length, 2);
	}

	public void test007NumModules() throws Exception {
		assertEquals(ServerUtil.getModules("jst.appclient").length, 1);
	}
	
	public void test008NumModules() throws Exception {
		assertEquals(ServerUtil.getModules("jst.connector").length, 1);
	}


	// ---------- EAR tests ----------

	public void test020EAR() throws Exception {
		module = ModuleHelper.getModuleFromProject("PublishTestEAR");
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

	/*
     * This count is incremented because, at this moment
     * I don't know which should take precedence: 
     * the file inside the Ear project, or the reference.
     * For now we include both. 
     */
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
		assertEquals(ent.getModules().length, 6);
	}

	public void test029EAR() throws Exception {
		IModule[] modules = ent.getModules();
		List<String> list = new ArrayList<String>(modules.length);
		for (IModule m : modules)
			list.add(m.getName());
		
		/* getName() is for display purposes only, binary jars inside a project should display their full name */
		String[] s = new String[] {
			"PublishTestEJB.jar", "test2", "PublishTestWeb.war",
			"PublishTestWeb2.war", "PublishTestConnector.rar", "PublishTestClient.jar"
		};
		
		for (String ss : s) {
			if (!list.contains(ss))
				fail("EAR does not contain " + ss);
		}
	}


	// ---------- Utility 1 tests ----------

	public void test040Util() throws Exception {
		module = ModuleHelper.getModuleFromProject("test");
		assertNotNull(module);
	}

	public void test041Util() throws Exception {
		if (!ModuleHelper.fileExists(module, "META-INF/MANIFEST.MF"))
			fail();
	}

	public void test042Util() throws Exception {
		if (!ModuleHelper.fileExists(module, "test.properties"))
			fail();
	}

	public void test043Util() throws Exception {
		if (!ModuleHelper.fileExists(module, "publish/TestUtil.class"))
			fail();
	}

	public void test044Util() throws Exception {
		assertEquals(ModuleHelper.countFiles(module), 3);
	}

	public void test045Util() throws Exception {
		assertEquals(ModuleHelper.countFolders(module), 2);
	}

	public void test046Util() throws Exception {
		assertEquals(module.getModuleType().getId(), "jst.utility");
	}

	public void test047Util() throws Exception {
		j2eeModule = (IJ2EEModule) module.loadAdapter(IJ2EEModule.class, null);
		assertNotNull(j2eeModule);
	}

	public void test048Util() throws Exception {
		assertEquals(j2eeModule.getResourceFolders().length, 2);
	}

	public void test049Util() throws Exception {
		assertEquals(j2eeModule.getJavaOutputFolders().length, 1);
	}

	public void test050Util() throws Exception {
		assertFalse(j2eeModule.isBinary());
	}


	// ---------- Utility 2 tests ----------

	public void test060Util() throws Exception {
		module = ModuleHelper.getModuleFromProject("test2");
		assertNotNull(module);
	}

	public void test061Util() throws Exception {
		if (!ModuleHelper.fileExists(module, "META-INF/MANIFEST.MF"))
			fail();
	}

	public void test062Util() throws Exception {
		if (!ModuleHelper.fileExists(module, "test.properties"))
			fail();
	}

	public void test063Util() throws Exception {
		if (!ModuleHelper.fileExists(module, "publish/TestUtil.class"))
			fail();
	}

	public void test064Util() throws Exception {
		assertEquals(ModuleHelper.countFiles(module), 3);
	}

	public void test065Util() throws Exception {
		assertEquals(ModuleHelper.countFolders(module), 2);
	}

	public void test066Util() throws Exception {
		assertEquals(module.getModuleType().getId(), "jst.utility");
	}

	public void test067Util() throws Exception {
		j2eeModule = (IJ2EEModule) module.loadAdapter(IJ2EEModule.class, null);
		assertNotNull(j2eeModule);
	}

	public void test068Util() throws Exception {
		assertEquals(j2eeModule.getResourceFolders().length, 2);
	}

	public void test069Util() throws Exception {
		assertEquals(j2eeModule.getJavaOutputFolders().length, 1);
	}

	public void test070Util() throws Exception {
		assertFalse(j2eeModule.isBinary());
	}


	// ---------- EJB tests ----------

	public void test080EJB() throws Exception {
		module = ModuleHelper.getModuleFromProject("PublishTestEJB");
		assertNotNull(module);
	}

	public void test081EJB() throws Exception {
		if (!ModuleHelper.fileExists(module, "META-INF/MANIFEST.MF"))
			fail();
	}

	public void test082EJB() throws Exception {
		if (!ModuleHelper.fileExists(module, "META-INF/ejb-jar.xml"))
			fail();
	}

	public void test083EJB() throws Exception {
		if (!ModuleHelper.fileExists(module, "ejbs/MyBeanLocalHome.class"))
			fail();
	}

	public void test084EJB() throws Exception {
		assertEquals(ModuleHelper.countFiles(module), 7);
	}

	public void test085EJB() throws Exception {
		assertEquals(ModuleHelper.countFolders(module), 2);
	}

	public void test086EJB() throws Exception {
		assertEquals(module.getModuleType().getId(), "jst.ejb");
	}

	public void test087EJB() throws Exception {
		j2eeModule = (IJ2EEModule) module.loadAdapter(IJ2EEModule.class, null);
		assertNotNull(j2eeModule);
	}

	public void test088EJB() throws Exception {
		assertEquals(j2eeModule.getResourceFolders().length, 2);
	}

	public void test089EJB() throws Exception {
		assertEquals(j2eeModule.getJavaOutputFolders().length, 1);
	}

	public void test090EJB() throws Exception {
		assertFalse(j2eeModule.isBinary());
	}


	// ---------- Connector tests ----------

	public void test100Connector() throws Exception {
		module = ModuleHelper.getModuleFromProject("PublishTestConnector");
		assertNotNull(module);
	}

	public void test101Connector() throws Exception {
		if (!ModuleHelper.fileExists(module, "META-INF/MANIFEST.MF"))
			fail();
	}

	public void test102Connector() throws Exception {
		if (!ModuleHelper.fileExists(module, "META-INF/ra.xml"))
			fail();
	}

	public void _test103Connector() throws Exception {
		if (!ModuleHelper.fileExists(module, "test/MyOtherConnectorClass.class"))
			fail();
	}

	public void _test104Connector() throws Exception {
		assertEquals(ModuleHelper.countFiles(module), 5);
	}

	public void _test105Connector() throws Exception {
		assertEquals(ModuleHelper.countFolders(module), 3);
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
		assertEquals(j2eeModule.getJavaOutputFolders().length, 1);
	}

	public void test110Connector() throws Exception {
		assertFalse(j2eeModule.isBinary());
	}


	// ---------- Client tests ----------

	public void test120Client() throws Exception {
		module = ModuleHelper.getModuleFromProject("PublishTestClient");
		assertNotNull(module);
	}

	public void test121Client() throws Exception {
		if (!ModuleHelper.fileExists(module, "META-INF/MANIFEST.MF"))
			fail();
	}

	public void test122Client() throws Exception {
		if (!ModuleHelper.fileExists(module, "META-INF/application-client.xml"))
			fail();
	}

	public void test123Client() throws Exception {
		if (!ModuleHelper.fileExists(module, "Main.class"))
			fail();
	}

	public void test124Client() throws Exception {
		if (!ModuleHelper.fileExists(module, "Main2.class"))
			fail();
	}

	public void _test125Client() throws Exception {
		assertEquals(ModuleHelper.countFiles(module), 4);
	}

	public void test126Client() throws Exception {
		assertEquals(ModuleHelper.countFolders(module), 1);
	}

	public void test127Client() throws Exception {
		assertEquals(module.getModuleType().getId(), "jst.appclient");
	}

	public void test128Client() throws Exception {
		j2eeModule = (IJ2EEModule) module.loadAdapter(IJ2EEModule.class, null);
		assertNotNull(j2eeModule);
	}

	public void test129Client() throws Exception {
		assertEquals(j2eeModule.getResourceFolders().length, 2);
	}

	public void test130Client() throws Exception {
		assertEquals(j2eeModule.getJavaOutputFolders().length, 1);
	}

	public void test131Client() throws Exception {
		assertFalse(j2eeModule.isBinary());
	}


	// ---------- Web 1 tests ----------

	public void test140Web() throws Exception {
		module = ModuleHelper.getModuleFromProject("PublishTestWeb");
		assertNotNull(module);
	}

	public void test141Web() throws Exception {
		if (!ModuleHelper.fileExists(module, "META-INF/MANIFEST.MF"))
			fail();
	}

	public void test142Web() throws Exception {
		if (!ModuleHelper.fileExists(module, "WEB-INF/web.xml"))
			fail();
	}

	public void test143Web() throws Exception {
		if (!ModuleHelper.fileExists(module, "test.jsp"))
			fail();
	}

	public void test144Web() throws Exception {
		if (!ModuleHelper.fileExists(module, "WEB-INF/classes/servtest/TestServlet2.class"))
			fail();
	}

	public void test145Web() throws Exception {
		if (!ModuleHelper.fileExists(module, "WEB-INF/classes/servtest/TestServlet.class"))
			fail();
	}

	public void test146Web() throws Exception {
		if (!ModuleHelper.fileExists(module, "WEB-INF/lib/PublishTestUtil.jar"))
			fail();
	}

	public void _test147Web() throws Exception {
		assertEquals(ModuleHelper.countFiles(module), 6);
	}

	public void _test148Web() throws Exception {
		assertEquals(ModuleHelper.countFolders(module), 5);
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
		assertEquals(j2eeModule.getJavaOutputFolders().length, 1);
	}

	public void test153Web() throws Exception {
		assertEquals(webModule.getModules().length, 0);
	}

	public void test154Web() throws Exception {
		assertEquals(webModule.getContextRoot(), "PublishTestWeb");
	}

	public void test155Web() throws Exception {
		assertFalse(webModule.isBinary());
	}


	// ---------- Web 2 tests ----------

	public void test160Web() throws Exception {
		module = ModuleHelper.getModuleFromProject("PublishTestWeb2");
		assertNotNull(module);
	}

	public void test161Web() throws Exception {
		if (!ModuleHelper.fileExists(module, "META-INF/MANIFEST.MF"))
			fail();
	}

	public void test162Web() throws Exception {
		if (!ModuleHelper.fileExists(module, "WEB-INF/web.xml"))
			fail();
	}

	public void test163Web() throws Exception {
		if (!ModuleHelper.fileExists(module, "WEB-INF/lib/test.jar"))
			fail();
	}

	public void test164Web() throws Exception {
		if (!ModuleHelper.fileExists(module, "WEB-INF/classes/serv/AServlet.class"))
			fail();
	}

	public void test165Web() throws Exception {
		if (!ModuleHelper.fileExists(module, "WEB-INF/classes/serv/BServlet.class"))
			fail();
	}

	public void test166Web() throws Exception {
		assertEquals(ModuleHelper.countFiles(module), 5);
	}

	public void test167Web() throws Exception {
		assertEquals(ModuleHelper.countFolders(module), 5);
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
		assertEquals(j2eeModule.getJavaOutputFolders().length, 1);
	}

	/* 
	 * This project has a file WEB-INF/lib/test.jar
	 * but also references a utility project named test. 
	 * Which should prevail?
	 */
	public void test172Web() throws Exception {
		assertEquals(webModule.getModules().length, 1);
	}

	public void test173Web() throws Exception {
		assertEquals(webModule.getModules()[0].getName(), "test");
	}

	public void test174Web() throws Exception {
		assertEquals(webModule.getContextRoot(), "PublishTestWeb2");
	}

	public void test175Web() throws Exception {
		assertFalse(webModule.isBinary());
	}

	public void test199Cleanup() throws Exception {
		for (String projectName : PROJECT_NAMES) {
			// don't delete the ear - leave for binary tests
			ModuleHelper.deleteProject(projectName);
		}
	}
}
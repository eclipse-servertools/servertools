/*******************************************************************************
 * Copyright (c) 2006, 2010 IBM Corporation and others.
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
package org.eclipse.jst.server.core.tests.j2ee;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jst.server.core.*;
import org.eclipse.wst.server.core.*;

import junit.framework.TestCase;
import junit.framework.TestSuite;

public class NoSourceTestCase extends TestCase {
	private static final String[] PROJECT_NAMES = new String[] {
		"PublishTestEAR", "PublishTestEJB", "test", "test2",
		"PublishTestWeb", "PublishTestWeb2", "PublishTestConnector", "PublishTestClient"
	};

	private static boolean projectsCreated;

	@Override
	protected void setUp() throws Exception {
		if (!projectsCreated) {
			ModuleHelper.importProject("PublishEARNoSource.zip", PROJECT_NAMES);
			ModuleHelper.buildIncremental();
			projectsCreated = true;
		}
	}

	protected IModule getModule(String project) throws Exception {
		return ModuleHelper.getModuleFromProject(project);
	}

	protected IEnterpriseApplication getEnterpriseApp(String project) throws Exception {
		return (IEnterpriseApplication) getModule(project).loadAdapter(IEnterpriseApplication.class, null);
	}

	protected IJ2EEModule getJ2EEModule(String project) throws Exception {
		return (IJ2EEModule) getModule(project).loadAdapter(IJ2EEModule.class, null);
	}

	protected IWebModule getWebModule(String project) throws Exception {
		return (IWebModule) getModule(project).loadAdapter(IWebModule.class, null);
	}

	public static void addOrderedTests(TestSuite suite) {
		suite.addTest(TestSuite.createTest(NoSourceTestCase.class, "deleteProjects"));
	}

	public void testNumJstEarModules() throws Exception {
		assertEquals(ServerUtil.getModules("jst.ear").length, 1);
	}

	public void testNumJstWebModules() throws Exception {
		assertEquals(ServerUtil.getModules("jst.web").length, 2);
	}

	public void testNumJstEjbModules() throws Exception {
		assertEquals(ServerUtil.getModules("jst.ejb").length, 1);
	}

	public void testNumJstUtilModules() throws Exception {
		assertEquals(ServerUtil.getModules("jst.utility").length, 2);
	}

	public void testNumJstAppModules() throws Exception {
		assertEquals(ServerUtil.getModules("jst.appclient").length, 1);
	}
	
	public void testNumJstConnModules() throws Exception {
		assertEquals(ServerUtil.getModules("jst.connector").length, 1);
	}


	// ---------- EAR tests ----------

	public void testEARGetModule() throws Exception {
		assertNotNull(getModule("PublishTestEAR"));
	}

	public void testEARFileExists() throws Exception {
		if (!ModuleHelper.fileExists(getModule("PublishTestEAR"), "META-INF/application.xml"))
			fail();
	}

	public void testEARJarExists() throws Exception {
		if (!ModuleHelper.fileExists(getModule("PublishTestEAR"), "PublishTestUtil2.jar"))
			fail();
	}

	/*
     * This count is incremented because, at this moment
     * I don't know which should take precedence: 
     * the file inside the Ear project, or the reference.
     * For now we include both. 
     */
	public void testEARFileCount() throws Exception {
		assertEquals(ModuleHelper.countFiles(getModule("PublishTestEAR")), 4);
	}

	public void testEARFolderCount() throws Exception {
		assertEquals(ModuleHelper.countFolders(getModule("PublishTestEAR")), 1);
	}

	public void testEARModuleType() throws Exception {
		assertEquals(getModule("PublishTestEAR").getModuleType().getId(), "jst.ear");
	}

	public void testEARGetApp() throws Exception {
		assertNotNull(getEnterpriseApp("PublishTestEAR"));
	}

	public void testEARResourceFolderCount() throws Exception {
		assertEquals(getEnterpriseApp("PublishTestEAR").getResourceFolders().length, 1);
	}

	public void testEARModules() throws Exception {
		assertEquals(getEnterpriseApp("PublishTestEAR").getModules().length, 6);
	}

	public void testEARAModuleNames() throws Exception {
		IModule[] modules = getEnterpriseApp("PublishTestEAR").getModules();
		List<String> list = new ArrayList<String>(modules.length);
		for (IModule m : modules)
			list.add(m.getName());
		
		/* getName() is for display purposes only, binary jars inside a project should display their full name */
		String[] s = new String[] {
			"PublishTestEJB", "test2", "PublishTestWeb",
			"PublishTestWeb2", "PublishTestConnector", "PublishTestClient"
		};
		
		for (String ss : s) {
			if (!list.contains(ss))
				fail("EAR does not contain " + ss);
		}
	}


	// ---------- Utility 1 tests ----------

	public void testUtilGetModule() throws Exception {
		assertNotNull(getModule("test"));
	}

	public void testUtilFileExists() throws Exception {
		if (!ModuleHelper.fileExists(getModule("test"), "META-INF/MANIFEST.MF"))
			fail();
	}

	public void testUtilFileExists2() throws Exception {
		if (!ModuleHelper.fileExists(getModule("test"), "test.properties"))
			fail();
	}

	public void testUtilClassExists() throws Exception {
		if (!ModuleHelper.fileExists(getModule("test"), "publish/TestUtil.class"))
			fail();
	}

	public void testUtilFileCount() throws Exception {
		assertEquals(ModuleHelper.countFiles(getModule("test")), 3);
	}

	public void testUtilFolderCount() throws Exception {
		assertEquals(ModuleHelper.countFolders(getModule("test")), 2);
	}

	public void testUtilModuleType() throws Exception {
		assertEquals(getModule("test").getModuleType().getId(), "jst.utility");
	}

	public void testUtilJ2EEModule() throws Exception {
		assertNotNull(getJ2EEModule("test"));
	}

	public void testUtilResourceFolderCount() throws Exception {
		assertEquals(getJ2EEModule("test").getResourceFolders().length, 2);
	}

	public void testUtilJavaOutputFolderCount() throws Exception {
		assertEquals(getJ2EEModule("test").getJavaOutputFolders().length, 1);
	}

	public void testUtilIsBinary() throws Exception {
		assertFalse(getJ2EEModule("test").isBinary());
	}


	// ---------- Utility 2 tests ----------

	public void testUtil2Module() throws Exception {
		assertNotNull(getModule("test2"));
	}

	public void testUtil2FileExists() throws Exception {
		if (!ModuleHelper.fileExists(getModule("test2"), "META-INF/MANIFEST.MF"))
			fail();
	}

	public void testUtil2FileExists2() throws Exception {
		if (!ModuleHelper.fileExists(getModule("test2"), "test.properties"))
			fail();
	}

	public void testUtil2ClassExists() throws Exception {
		if (!ModuleHelper.fileExists(getModule("test2"), "publish/TestUtil.class"))
			fail();
	}

	public void testUtil2FileCount() throws Exception {
		assertEquals(ModuleHelper.countFiles(getModule("test2")), 3);
	}

	public void testUtil2FolderCount() throws Exception {
		assertEquals(ModuleHelper.countFolders(getModule("test2")), 2);
	}

	public void testUtil2ModuleType() throws Exception {
		assertEquals(getModule("test2").getModuleType().getId(), "jst.utility");
	}

	public void testUtil2J2EEModule() throws Exception {
		assertNotNull(getJ2EEModule("test2"));
	}

	public void testUtil2ResourceFolderCount() throws Exception {
		assertEquals(getJ2EEModule("test2").getResourceFolders().length, 2);
	}

	public void testUtil2JavaOutputFolderCount() throws Exception {
		assertEquals(getJ2EEModule("test2").getJavaOutputFolders().length, 1);
	}

	public void testUtil2IsBinary() throws Exception {
		assertFalse(getJ2EEModule("test2").isBinary());
	}


	// ---------- EJB tests ----------

	public void testEJBModule() throws Exception {
		assertNotNull(getModule("PublishTestEJB"));
	}

	public void testEJBFileExists() throws Exception {
		if (!ModuleHelper.fileExists(getModule("PublishTestEJB"), "META-INF/MANIFEST.MF"))
			fail();
	}

	public void testEJBFileExists2() throws Exception {
		if (!ModuleHelper.fileExists(getModule("PublishTestEJB"), "META-INF/ejb-jar.xml"))
			fail();
	}

	public void testEJBClassExists() throws Exception {
		if (!ModuleHelper.fileExists(getModule("PublishTestEJB"), "ejbs/MyBeanLocalHome.class"))
			fail();
	}

	public void testEJBFileCount() throws Exception {
		assertEquals(ModuleHelper.countFiles(getModule("PublishTestEJB")), 7);
	}

	public void testEJBFolderCount() throws Exception {
		assertEquals(ModuleHelper.countFolders(getModule("PublishTestEJB")), 2);
	}

	public void testEJBModuleType() throws Exception {
		assertEquals(getModule("PublishTestEJB").getModuleType().getId(), "jst.ejb");
	}

	public void testEJBJ2EEModule() throws Exception {
		assertNotNull(getJ2EEModule("PublishTestEJB"));
	}

	public void testEJBResourceFolderCount() throws Exception {
		assertEquals(getJ2EEModule("PublishTestEJB").getResourceFolders().length, 2);
	}

	public void testEJBJavaOutputFolderCount() throws Exception {
		assertEquals(getJ2EEModule("PublishTestEJB").getJavaOutputFolders().length, 1);
	}

	public void testEJBIsBinary() throws Exception {
		assertFalse(getJ2EEModule("PublishTestEJB").isBinary());
	}


	// ---------- Connector tests ----------

	public void testConnector() throws Exception {
		assertNotNull(getModule("PublishTestConnector"));
	}

	public void testConnectorFileExists() throws Exception {
		if (!ModuleHelper.fileExists(getModule("PublishTestConnector"), "META-INF/MANIFEST.MF"))
			fail();
	}

	public void testConnectorFileExists2() throws Exception {
		if (!ModuleHelper.fileExists(getModule("PublishTestConnector"), "META-INF/ra.xml"))
			fail();
	}

	public void _testConnectorClassExists() throws Exception {
		if (!ModuleHelper.fileExists(getModule("PublishTestConnector"), "test/MyOtherConnectorClass.class"))
			fail();
	}

	public void _testConnectorFileCount() throws Exception {
		assertEquals(ModuleHelper.countFiles(getModule("PublishTestConnector")), 5);
	}

	public void _testConnectorFolderCount() throws Exception {
		assertEquals(ModuleHelper.countFolders(getModule("PublishTestConnector")), 3);
	}

	public void testConnectorModuleType() throws Exception {
		assertEquals(getModule("PublishTestConnector").getModuleType().getId(), "jst.connector");
	}

	public void testConnectorJ2EEModule() throws Exception {
		assertNotNull(getJ2EEModule("PublishTestConnector"));
	}

	public void testConnectorResourceFolderCount() throws Exception {
		assertEquals(getJ2EEModule("PublishTestConnector").getResourceFolders().length, 1);
	}

	public void testConnectorJavaOutputFolderCount() throws Exception {
		assertEquals(getJ2EEModule("PublishTestConnector").getJavaOutputFolders().length, 1);
	}

	public void testConnectorIsBinary() throws Exception {
		assertFalse(getJ2EEModule("PublishTestConnector").isBinary());
	}


	// ---------- Client tests ----------

	public void testClientModule() throws Exception {
		assertNotNull(getModule("PublishTestClient"));
	}

	public void testClientFileExists() throws Exception {
		if (!ModuleHelper.fileExists(getModule("PublishTestClient"), "META-INF/MANIFEST.MF"))
			fail();
	}

	public void testClientFileExists2() throws Exception {
		if (!ModuleHelper.fileExists(getModule("PublishTestClient"), "META-INF/application-client.xml"))
			fail();
	}

	public void testClientClassExists() throws Exception {
		if (!ModuleHelper.fileExists(getModule("PublishTestClient"), "Main.class"))
			fail();
	}

	public void testClientClassExists2() throws Exception {
		if (!ModuleHelper.fileExists(getModule("PublishTestClient"), "Main2.class"))
			fail();
	}

	public void _testClientFileCount() throws Exception {
		assertEquals(ModuleHelper.countFiles(getModule("PublishTestClient")), 4);
	}

	public void testClientFolderCount() throws Exception {
		assertEquals(ModuleHelper.countFolders(getModule("PublishTestClient")), 1);
	}

	public void testClientModuleType() throws Exception {
		assertEquals(getModule("PublishTestClient").getModuleType().getId(), "jst.appclient");
	}

	public void testClientJ2EEModule() throws Exception {
		assertNotNull(getJ2EEModule("PublishTestClient"));
	}

	public void testClientResourceFolderCount() throws Exception {
		assertEquals(getJ2EEModule("PublishTestClient").getResourceFolders().length, 2);
	}

	public void testClientJavaOutputFolderCount() throws Exception {
		assertEquals(getJ2EEModule("PublishTestClient").getJavaOutputFolders().length, 1);
	}

	public void testClientIsBinary() throws Exception {
		assertFalse(getJ2EEModule("PublishTestClient").isBinary());
	}


	// ---------- Web 1 tests ----------

	public void testWebModule() throws Exception {
		assertNotNull(getModule("PublishTestWeb"));
	}

	public void testWebFileExists() throws Exception {
		if (!ModuleHelper.fileExists(getModule("PublishTestWeb"), "META-INF/MANIFEST.MF"))
			fail();
	}

	public void testWebWebXmlExists() throws Exception {
		if (!ModuleHelper.fileExists(getModule("PublishTestWeb"), "WEB-INF/web.xml"))
			fail();
	}

	public void testWebJSPExists() throws Exception {
		if (!ModuleHelper.fileExists(getModule("PublishTestWeb"), "test.jsp"))
			fail();
	}

	public void testWebClassExists() throws Exception {
		if (!ModuleHelper.fileExists(getModule("PublishTestWeb"), "WEB-INF/classes/servtest/TestServlet2.class"))
			fail();
	}

	public void testWebClassExists2() throws Exception {
		if (!ModuleHelper.fileExists(getModule("PublishTestWeb"), "WEB-INF/classes/servtest/TestServlet.class"))
			fail();
	}

	public void testWebJarExists() throws Exception {
		if (!ModuleHelper.fileExists(getModule("PublishTestWeb"), "WEB-INF/lib/PublishTestUtil.jar"))
			fail();
	}

	public void _testWebFileCount() throws Exception {
		assertEquals(ModuleHelper.countFiles(getModule("PublishTestWeb")), 6);
	}

	public void _testWebFolderCount() throws Exception {
		assertEquals(ModuleHelper.countFolders(getModule("PublishTestWeb")), 5);
	}

	public void testWebModuleType() throws Exception {
		assertEquals(getModule("PublishTestWeb").getModuleType().getId(), "jst.web");
	}

	public void testWebJ2EEModule() throws Exception {
		assertNotNull(getWebModule("PublishTestWeb"));
	}

	public void testWebResourceFolderCount() throws Exception {
		assertEquals(getWebModule("PublishTestWeb").getResourceFolders().length, 1);
	}

	public void testWebJavaOutputFolderCount() throws Exception {
		assertEquals(getWebModule("PublishTestWeb").getJavaOutputFolders().length, 1);
	}

	public void testWebGetModules() throws Exception {
		assertEquals(getWebModule("PublishTestWeb").getModules().length, 0);
	}

	public void testWebGetContextRoot() throws Exception {
		assertEquals(getWebModule("PublishTestWeb").getContextRoot(), "PublishTestWeb");
	}

	public void testWebIsBinary() throws Exception {
		assertFalse(getWebModule("PublishTestWeb").isBinary());
	}


	// ---------- Web 2 tests ----------

	public void testWeb2Module() throws Exception {
		assertNotNull(getModule("PublishTestWeb2"));
	}

	public void testWeb2FileExists() throws Exception {
		if (!ModuleHelper.fileExists(getModule("PublishTestWeb2"), "META-INF/MANIFEST.MF"))
			fail();
	}

	public void testWeb2WebXmlExists() throws Exception {
		if (!ModuleHelper.fileExists(getModule("PublishTestWeb2"), "WEB-INF/web.xml"))
			fail();
	}

	public void testWeb2JarExists() throws Exception {
		if (!ModuleHelper.fileExists(getModule("PublishTestWeb2"), "WEB-INF/lib/test.jar"))
			fail();
	}

	public void testWeb2ClassExists() throws Exception {
		if (!ModuleHelper.fileExists(getModule("PublishTestWeb2"), "WEB-INF/classes/serv/AServlet.class"))
			fail();
	}

	public void testWeb2ClassExists2() throws Exception {
		if (!ModuleHelper.fileExists(getModule("PublishTestWeb2"), "WEB-INF/classes/serv/BServlet.class"))
			fail();
	}

	public void testWeb2FileCount() throws Exception {
		assertEquals(ModuleHelper.countFiles(getModule("PublishTestWeb2")), 5);
	}

	public void testWeb2FolderCount() throws Exception {
		assertEquals(ModuleHelper.countFolders(getModule("PublishTestWeb2")), 5);
	}

	public void testWeb2ModuleType() throws Exception {
		assertEquals(getModule("PublishTestWeb2").getModuleType().getId(), "jst.web");
	}

	public void testWeb2J2EEModule() throws Exception {
		assertNotNull(getWebModule("PublishTestWeb2"));
	}

	public void testWeb2ResourceFolderCount() throws Exception {
		assertEquals(getWebModule("PublishTestWeb2").getResourceFolders().length, 1);
	}

	public void testWeb2JavaOutputFolderCount() throws Exception {
		assertEquals(getWebModule("PublishTestWeb2").getJavaOutputFolders().length, 1);
	}

	/* 
	 * This project has a file WEB-INF/lib/test.jar
	 * but also references a utility project named test. 
	 * Which should prevail?
	 */
	public void testWeb2ModuleCount() throws Exception {
		assertEquals(getWebModule("PublishTestWeb2").getModules().length, 1);
	}

	public void testWeb2GetModules() throws Exception {
		assertEquals(getWebModule("PublishTestWeb2").getModules()[0].getName(), "test");
	}

	public void testWeb2GetContextRoot() throws Exception {
		assertEquals(getWebModule("PublishTestWeb2").getContextRoot(), "PublishTestWeb2");
	}

	public void testWeb2IsBinary() throws Exception {
		assertFalse(getWebModule("PublishTestWeb2").isBinary());
	}

	public void deleteProjects() throws Exception {
		for (String projectName : PROJECT_NAMES) {
			ModuleHelper.deleteProject(projectName);
		}
	}
}
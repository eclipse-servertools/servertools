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

import org.eclipse.jst.server.core.IEnterpriseApplication;
import org.eclipse.jst.server.core.IJ2EEModule;
import org.eclipse.jst.server.core.IWebModule;
import org.eclipse.jst.server.core.tests.AllTests;
import org.eclipse.wst.server.core.*;

import junit.framework.TestCase;
import junit.framework.TestSuite;
/**
 * TODO Tests left to do:
 *    migrated modules
 *    flat project structure
 *    complex .component structures
 *    external jar references for EAR or WEB
 */
public class ModuleTestCase extends TestCase {
	private static final String RUNTIME_TYPE_ID = "org.eclipse.jst.server.core.runtimeType";

	private static final String[] PROJECT_NAMES = new String[] {
		"PublishTestEAR", "PublishTestEJB", "PublishTestUtil", "PublishTestUtil2",
		"PublishTestWeb", "PublishTestWeb2", "PublishTestConnector", "PublishTestClient"
	};

	protected static IRuntime runtime;

	private static boolean projectsCreated;

	@Override
	protected void setUp() throws Exception {
		if (!projectsCreated) {
			ModuleHelper.importProject("PublishEAR.zip", PROJECT_NAMES);
			ModuleHelper.buildIncremental();
			projectsCreated = true;
		}
	}

	protected IRuntime getRuntime() throws Exception {
		if (runtime == null) {
			IRuntimeType rt = ServerCore.findRuntimeType(RUNTIME_TYPE_ID);
			IRuntimeWorkingCopy wc = rt.createRuntime("RuntimeLibraries", null);
			wc.setLocation(AllTests.runtimeLocation);
			wc.setName("RuntimeLibraries");
			runtime = wc.save(false, null);
		}
		return runtime;
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
		suite.addTest(TestSuite.createTest(ModuleTestCase.class, "deleteProjects"));
	}

	public void testCreateRuntime() throws Exception {
		assertTrue(!getRuntime().isWorkingCopy());
	}

	public void testNumEARModules() throws Exception {
		assertEquals(ServerUtil.getModules("jst.ear").length, 1);
	}

	public void testNumWebModules() throws Exception {
		assertEquals(ServerUtil.getModules("jst.web").length, 2);
	}

	public void testNumEJBModules() throws Exception {
		assertEquals(ServerUtil.getModules("jst.ejb").length, 1);
	}

	public void testNumUtilityModules() throws Exception {
		assertEquals(ServerUtil.getModules("jst.utility").length, 2);
	}

	public void testNumClientModules() throws Exception {
		assertEquals(ServerUtil.getModules("jst.appclient").length, 1);
	}
	
	public void testNumConnectorModules() throws Exception {
		assertEquals(ServerUtil.getModules("jst.connector").length, 1);
	}


	// ---------- EAR tests ----------

	public void testEARModule() throws Exception {
		assertNotNull(getModule("PublishTestEAR"));
	}

	public void testEARFileExists() throws Exception {
		if (!ModuleHelper.fileExists(getModule("PublishTestEAR"), "META-INF/application.xml"))
			fail();
	}

	public void testEARJarExists() throws Exception {
		if (!ModuleHelper.fileExists(getModule("PublishTestEAR"), "test.jar"))
			fail();
	}

	public void testEARJarExists2() throws Exception {
		if (!ModuleHelper.fileExists(getModule("PublishTestEAR"), "jarfolder/test3.jar"))
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
		assertEquals(ModuleHelper.countFolders(getModule("PublishTestEAR")), 2);
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

	public void testEARModuleCount() throws Exception {
		assertEquals(getEnterpriseApp("PublishTestEAR").getModules().length, 6);
	}

	public void _testEARModuleNames() throws Exception {
		IModule[] modules = getEnterpriseApp("PublishTestEAR").getModules();
		List<String> list = new ArrayList<String>(modules.length);
		for (IModule m : modules) {
			System.out.println(m.getName());
			list.add(m.getName());
		}
		
		String[] s = new String[] {
			"PublishTestEJB", "PublishTestUtil2", "PublishTestWeb",
			"PublishTestWeb2", "PublishTestConnector", "PublishTestClient"
		};
		
		for (String ss : s) {
			if (!list.contains(ss))
				fail("EAR does not contain " + ss);
		}
	}


	// ---------- Utility 1 tests ----------

	public void testUtilModule() throws Exception {
		assertNotNull(getModule("PublishTestUtil"));
	}

	public void testUtilFileExists() throws Exception {
		if (!ModuleHelper.fileExists(getModule("PublishTestUtil"), "META-INF/MANIFEST.MF"))
			fail();
	}

	public void testUtilFileExists2() throws Exception {
		if (!ModuleHelper.fileExists(getModule("PublishTestUtil"), "test.properties"))
			fail();
	}

	public void testUtilClassExists() throws Exception {
		if (!ModuleHelper.fileExists(getModule("PublishTestUtil"), "publish/TestUtil.class"))
			fail();
	}

	public void testUtilFileCount() throws Exception {
		assertEquals(ModuleHelper.countFiles(getModule("PublishTestUtil")), 3);
	}

	public void testUtilFolderCount() throws Exception {
		assertEquals(ModuleHelper.countFolders(getModule("PublishTestUtil")), 2);
	}

	public void testUtilModuleType() throws Exception {
		assertEquals(getModule("PublishTestUtil").getModuleType().getId(), "jst.utility");
	}

	public void testUtilJ2EEModule() throws Exception {
		assertNotNull(getJ2EEModule("PublishTestUtil"));
	}

	public void testUtilResourceFolderCount() throws Exception {
		assertEquals(getJ2EEModule("PublishTestUtil").getResourceFolders().length, 1);
	}

	public void testUtilJavaOutputFolderCount() throws Exception {
		assertEquals(getJ2EEModule("PublishTestUtil").getJavaOutputFolders().length, 1);
	}

	public void testUtilIsBinary() throws Exception {
		assertFalse(getJ2EEModule("PublishTestUtil").isBinary());
	}


	// ---------- Utility 2 tests ----------

	public void testUtil2Module() throws Exception {
		assertNotNull(getModule("PublishTestUtil2"));
	}

	public void testUtil2FileExists() throws Exception {
		if (!ModuleHelper.fileExists(getModule("PublishTestUtil2"), "META-INF/MANIFEST.MF"))
			fail();
	}

	public void testUtil2FileExists2() throws Exception {
		if (!ModuleHelper.fileExists(getModule("PublishTestUtil2"), "temp/test.properties"))
			fail();
	}

	public void testUtil2ClassExists() throws Exception {
		if (!ModuleHelper.fileExists(getModule("PublishTestUtil2"), "publish/TestUtil2.class"))
			fail();
	}

	public void testUtil2FileCount() throws Exception {
		assertEquals(ModuleHelper.countFiles(getModule("PublishTestUtil2")), 3);
	}

	public void testUtil2FolderCount() throws Exception {
		assertEquals(ModuleHelper.countFolders(getModule("PublishTestUtil2")), 3);
	}

	public void testUtil2ModuleType() throws Exception {
		assertEquals(getModule("PublishTestUtil2").getModuleType().getId(), "jst.utility");
	}

	public void testUtil2J2EEModule() throws Exception {
		assertNotNull(getJ2EEModule("PublishTestUtil2"));
	}

	public void testUtil2ResourceFolderCount() throws Exception {
		assertEquals(getJ2EEModule("PublishTestUtil2").getResourceFolders().length, 1);
	}

	public void testUtil2JavaOutputFolderCount() throws Exception {
		assertEquals(getJ2EEModule("PublishTestUtil2").getJavaOutputFolders().length, 1);
	}

	public void testUtil2IsBinary() throws Exception {
		assertFalse(getJ2EEModule("PublishTestUtil2").isBinary());
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
		assertEquals(getJ2EEModule("PublishTestEJB").getResourceFolders().length, 1);
	}

	public void testEJBJavaOutputFolderCount() throws Exception {
		assertEquals(getJ2EEModule("PublishTestEJB").getJavaOutputFolders().length, 1);
	}

	public void testEJBIsBinary() throws Exception {
		assertFalse(getJ2EEModule("PublishTestEJB").isBinary());
	}


	// ---------- Connector tests ----------

	public void testConnectorModule() throws Exception {
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

	public void testConnectorClassExists() throws Exception {
		if (!ModuleHelper.fileExists(getModule("PublishTestConnector"), "test/MyOtherConnectorClass.class"))
			fail();
	}

	public void testConnectorFileCount() throws Exception {
		assertEquals(ModuleHelper.countFiles(getModule("PublishTestConnector")), 5);
	}

	public void testConnectorFolderCount() throws Exception {
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

	public void testClientFileCount() throws Exception {
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
		assertEquals(getJ2EEModule("PublishTestClient").getResourceFolders().length, 1);
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

	public void testWebFileCount() throws Exception {
		assertEquals(ModuleHelper.countFiles(getModule("PublishTestWeb")), 5);
	}

	public void testWebFolderCount() throws Exception {
		assertEquals(ModuleHelper.countFolders(getModule("PublishTestWeb")), 5);
	}

	public void testWebModuleType() throws Exception {
		assertEquals(getModule("PublishTestWeb").getModuleType().getId(), "jst.web");
	}

	public void test149Web() throws Exception {
		assertNotNull(getWebModule("PublishTestWeb"));
	}

	public void testWebResourceFolderCount() throws Exception {
		assertEquals(getWebModule("PublishTestWeb").getResourceFolders().length, 1);
	}

	public void testWebJavaOutputFolderCount() throws Exception {
		assertEquals(getWebModule("PublishTestWeb").getJavaOutputFolders().length, 1);
	}

	public void testWebModuleCount() throws Exception {
		assertEquals(getWebModule("PublishTestWeb").getModules().length, 1);
	}

	public void testWebModuleName() throws Exception {
		assertEquals(getWebModule("PublishTestWeb").getModules()[0].getName(), "PublishTestUtil");
	}

	public void testWebContextRoot() throws Exception {
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

	public void testWeb2ModuleCount() throws Exception {
		assertEquals(getWebModule("PublishTestWeb2").getModules().length, 0);
	}

	public void testWeb2ContextRoot() throws Exception {
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
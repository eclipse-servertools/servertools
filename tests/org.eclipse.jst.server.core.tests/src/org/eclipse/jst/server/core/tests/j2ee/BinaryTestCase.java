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
import org.eclipse.wst.server.core.*;

import junit.framework.TestCase;
import junit.framework.TestSuite;

public class BinaryTestCase extends TestCase {
	private static final String[] PROJECT_NAMES = new String[] {
		"PublishTestEAR", "PublishTestEJB", "test", "test2",
		"PublishTestWeb", "PublishTestWeb2", "PublishTestConnector", "PublishTestClient"
	};

	private static final String EJB_PATH = "lib/PublishTestEAR/EarContent/PublishTestEJB.jar";
	private static final String CONN_PATH = "lib/PublishTestEAR/EarContent/PublishTestConnector.rar";
	private static final String CLIENT_PATH = "lib/PublishTestEAR/EarContent/PublishTestClient.jar";
	private static final String WEBAPP_PATH = "lib/PublishTestEAR/EarContent/PublishTestWeb.war";
	private static final String WEBAPP2_PATH = "lib/PublishTestEAR/EarContent/PublishTestWeb2.war";

	private static boolean projectsCreated;

	@Override
	protected void setUp() throws Exception {
		if (!projectsCreated) {
			ModuleHelper.importProject("PublishTestBinary.zip", new String[] { PROJECT_NAMES[0] } );
			ModuleHelper.buildIncremental();
			projectsCreated = true;
		}
	}

	protected IModule getEARModule(String project) throws Exception {
		return ModuleHelper.getModule("jst.ear", project);
	}

	protected IEnterpriseApplication getEnterpriseApp(String project) throws Exception {
		return (IEnterpriseApplication) getEARModule(project).loadAdapter(IEnterpriseApplication.class, null);
	}

	protected IModule getEJBModule(String ejb) throws Exception {
		return ModuleHelper.getModule("jst.ejb", ejb);
	}

	protected IJ2EEModule getJ2EEEJBModule(String ejb) throws Exception {
		return (IJ2EEModule) getEJBModule(ejb).loadAdapter(IJ2EEModule.class, null);
	}

	protected IModule getConnectorModule(String conn) throws Exception {
		return ModuleHelper.getModule("jst.connector", conn);
	}

	protected IJ2EEModule getJ2EEConnectorModule(String conn) throws Exception {
		return (IJ2EEModule) getConnectorModule(conn).loadAdapter(IJ2EEModule.class, null);
	}

	protected IModule getAppClientModule(String client) throws Exception {
		return ModuleHelper.getModule("jst.appclient", client);
	}

	protected IJ2EEModule getJ2EEAppClientModule(String client) throws Exception {
		return (IJ2EEModule) getAppClientModule(client).loadAdapter(IJ2EEModule.class, null);
	}

	protected IModule getWebModule(String webapp) throws Exception {
		return ModuleHelper.getModule("jst.web", webapp);
	}

	protected IWebModule getJ2EEWebModule(String webapp) throws Exception {
		return (IWebModule) getWebModule(webapp).loadAdapter(IWebModule.class, null);
	}

	public static void addOrderedTests(TestSuite suite) {
		suite.addTest(TestSuite.createTest(BinaryTestCase.class, "deleteProjects"));
	}

	public void testNumEarModules() throws Exception {
		assertEquals(ServerUtil.getModules("jst.ear").length, 1);
	}

	public void testNumWebModules() throws Exception {
		assertEquals(ServerUtil.getModules("jst.web").length, 2);
	}

	public void testNumEjbModules() throws Exception {
		assertEquals(ServerUtil.getModules("jst.ejb").length, 1);
	}

	public void testNumUtilityModules() throws Exception {
		assertEquals(ServerUtil.getModules("jst.utility").length, 0);
	}

	public void testNumClientModules() throws Exception {
		assertEquals(ServerUtil.getModules("jst.appclient").length, 1);
	}

	public void testNumConnectorModules() throws Exception {
		assertEquals(ServerUtil.getModules("jst.connector").length, 1);
	}


	// ---------- EAR tests ----------

	public void testEARModule() throws Exception {
		assertNotNull(getEARModule("PublishTestEAR"));
	}

	public void testEARFileExists() throws Exception {
		if (!ModuleHelper.fileExists(getEARModule("PublishTestEAR"), "META-INF/application.xml"))
			fail();
	}

	public void test0EARJarExists() throws Exception {
		if (!ModuleHelper.fileExists(getEARModule("PublishTestEAR"), "PublishTestUtil2.jar"))
			fail();
	}

	public void testEARFileCount() throws Exception {
		assertEquals(ModuleHelper.countFiles(getEARModule("PublishTestEAR")), 4);
	}

	public void testEARFolderCount() throws Exception {
		assertEquals(ModuleHelper.countFolders(getEARModule("PublishTestEAR")), 1);
	}

	public void testEARModuleType() throws Exception {
		assertEquals(getEARModule("PublishTestEAR").getModuleType().getId(), "jst.ear");
	}

	public void testEARGetApp() throws Exception {
		assertNotNull(getEnterpriseApp("PublishTestEAR"));
	}

	public void testEARResourceFolderCount() throws Exception {
		assertEquals(getEnterpriseApp("PublishTestEAR").getResourceFolders().length, 1);
	}

	public void testEARModuleCount() throws Exception {
		assertEquals(getEnterpriseApp("PublishTestEAR").getModules().length, 5);
	}

	public void testEARModuleNames() throws Exception {
		IModule[] modules = getEnterpriseApp("PublishTestEAR").getModules();
		int size = modules.length;
		List<String> list = new ArrayList<String>(size);
		for (IModule m : modules)
			list.add(m.getName());
		
		String[] s = new String[] {
			"PublishTestEJB.jar", "PublishTestWeb.war",
			"PublishTestWeb2.war", "PublishTestConnector.rar", "PublishTestClient.jar"
		};
		
		for (String ss : s) {
			// New versions of the modules will not include the full path as the getName() / display purposes
			//if (!list.contains("lib/PublishTestEAR/EarContent/" + ss))
			if( !list.contains(ss))
				fail("EAR does not contain " + ss);
		}
	}


	// ---------- EJB tests ----------

	public void testEJBModule() throws Exception {
		assertNotNull(getEJBModule(EJB_PATH));
	}

	public void testEJBFileExists() throws Exception {
		/* PublishTestEJB should be exposed as a child module, NOT as a resource 
		if (!ModuleHelper.fileExists(module, "PublishTestEJB.jar"))
			fail();
		 */
	}

	public void testEJBFileCount() throws Exception {
		assertEquals(ModuleHelper.countFiles(getEJBModule(EJB_PATH)), 1);
	}

	public void test085EJB() throws Exception {
		assertEquals(ModuleHelper.countFolders(getEJBModule(EJB_PATH)), 0);
	}

	public void testEJBModuleType() throws Exception {
		assertEquals(getEJBModule(EJB_PATH).getModuleType().getId(), "jst.ejb");
	}

	public void testEJBJ2EEModule() throws Exception {
		assertNotNull(getJ2EEEJBModule(EJB_PATH));
	}

	public void testEJBResourceFolderCount() throws Exception {
		assertEquals(getJ2EEEJBModule(EJB_PATH).getResourceFolders().length, 1);
	}

	public void testEJBJavaOutputFolderCount() throws Exception {
		assertEquals(getJ2EEEJBModule(EJB_PATH).getJavaOutputFolders().length, 0);
	}

	public void testEJBIsBinary() throws Exception {
		assertTrue(getJ2EEEJBModule(EJB_PATH).isBinary());
	}


	// ---------- Connector tests ----------

	public void testConnectorModule() throws Exception {
		assertNotNull(getConnectorModule(CONN_PATH));
	}

	public void testConnectorFileExists() throws Exception {
		if (!ModuleHelper.fileExists(getConnectorModule(CONN_PATH), "PublishTestConnector.rar"))
			fail();
	}

	public void testConnectorFileCount() throws Exception {
		assertEquals(ModuleHelper.countFiles(getConnectorModule(CONN_PATH)), 1);
	}

	public void testConnectorFolderCount() throws Exception {
		assertEquals(ModuleHelper.countFolders(getConnectorModule(CONN_PATH)), 0);
	}

	public void testConnectorModuleType() throws Exception {
		assertEquals(getConnectorModule(CONN_PATH).getModuleType().getId(), "jst.connector");
	}

	public void testConnectorJ2EEModule() throws Exception {
		assertNotNull(getJ2EEConnectorModule(CONN_PATH));
	}

	public void testConnectorResourceFolderCount() throws Exception {
		assertEquals(getJ2EEConnectorModule(CONN_PATH).getResourceFolders().length, 1);
	}

	public void testConnectorJavaOutputFolderCount() throws Exception {
		assertEquals(getJ2EEConnectorModule(CONN_PATH).getJavaOutputFolders().length, 0);
	}

	public void testConnectorIsBinary() throws Exception {
		assertTrue(getJ2EEConnectorModule(CONN_PATH).isBinary());
	}


	// ---------- Client tests ----------

	public void testClientModule() throws Exception {
		assertNotNull(getAppClientModule(CLIENT_PATH));
	}

	public void testClientFileExists() throws Exception {
		if (!ModuleHelper.fileExists(getAppClientModule(CLIENT_PATH), "PublishTestClient.jar"))
			fail();
	}

	public void testClientFileCount() throws Exception {
		assertEquals(ModuleHelper.countFiles(getAppClientModule(CLIENT_PATH)), 1);
	}

	public void testClientFolderCount() throws Exception {
		assertEquals(ModuleHelper.countFolders(getAppClientModule(CLIENT_PATH)), 0);
	}

	public void testClientModuleTime() throws Exception {
		assertEquals(getAppClientModule(CLIENT_PATH).getModuleType().getId(), "jst.appclient");
	}

	public void testClientJ2EEModule() throws Exception {
		assertNotNull(getJ2EEAppClientModule(CLIENT_PATH));
	}

	public void testClientResourceFolderCount() throws Exception {
		assertEquals(getJ2EEAppClientModule(CLIENT_PATH).getResourceFolders().length, 1);
	}

	public void testClientJavaOutputFolderCount() throws Exception {
		assertEquals(getJ2EEAppClientModule(CLIENT_PATH).getJavaOutputFolders().length, 0);
	}

	public void testClientIsBinary() throws Exception {
		assertTrue(getJ2EEAppClientModule(CLIENT_PATH).isBinary());
	}


	// ---------- Web 1 tests ----------

	public void testWebModule() throws Exception {
		assertNotNull(getWebModule(WEBAPP_PATH));
	}

	public void testWebFileExists() throws Exception {
		if (!ModuleHelper.fileExists(getWebModule(WEBAPP_PATH), "PublishTestWeb.war"))
			fail();
	}

	public void testWebFileCount() throws Exception {
		assertEquals(ModuleHelper.countFiles(getWebModule(WEBAPP_PATH)), 1);
	}

	public void testWebFolderCount() throws Exception {
		assertEquals(ModuleHelper.countFolders(getWebModule(WEBAPP_PATH)), 0);
	}

	public void testWebModuleType() throws Exception {
		assertEquals(getWebModule(WEBAPP_PATH).getModuleType().getId(), "jst.web");
	}

	public void testWebJ2EEModule() throws Exception {
		assertNotNull(getJ2EEWebModule(WEBAPP_PATH));
	}

	public void testWebResourceFolderCount() throws Exception {
		assertEquals(getJ2EEWebModule(WEBAPP_PATH).getResourceFolders().length, 1);
	}

	public void testWebJavaOutputFolderCount() throws Exception {
		assertEquals(getJ2EEWebModule(WEBAPP_PATH).getJavaOutputFolders().length, 0);
	}

	public void testWebModuleCount() throws Exception {
		assertEquals(getJ2EEWebModule(WEBAPP_PATH).getModules().length, 0);
	}

	public void _testWebContextRoot() throws Exception {
		assertEquals(getJ2EEWebModule(WEBAPP_PATH).getContextRoot(), "PublishTestWeb");
	}

	public void testWebIsBinaary() throws Exception {
		assertTrue(getJ2EEWebModule(WEBAPP_PATH).isBinary());
	}


	// ---------- Web 2 tests ----------

	public void testWeb2Module() throws Exception {
		assertNotNull(getWebModule(WEBAPP2_PATH));
	}

	public void testWeb2FileExists() throws Exception {
		if (!ModuleHelper.fileExists(getWebModule(WEBAPP2_PATH), "PublishTestWeb2.war"))
			fail();
	}

	public void testWeb2FileCount() throws Exception {
		assertEquals(ModuleHelper.countFiles(getWebModule(WEBAPP2_PATH)), 1);
	}

	public void testWeb2FolderCount() throws Exception {
		assertEquals(ModuleHelper.countFolders(getWebModule(WEBAPP2_PATH)), 0);
	}

	public void testWeb2ModuleType() throws Exception {
		assertEquals(getWebModule(WEBAPP2_PATH).getModuleType().getId(), "jst.web");
	}

	public void testWeb2J2EEModule() throws Exception {
		assertNotNull(getJ2EEWebModule(WEBAPP2_PATH));
	}

	public void testWeb2ResourceFolderCount() throws Exception {
		assertEquals(getJ2EEWebModule(WEBAPP2_PATH).getResourceFolders().length, 1);
	}

	public void testWeb2JavaOutputFolderCount() throws Exception {
		assertEquals(getJ2EEWebModule(WEBAPP2_PATH).getJavaOutputFolders().length, 0);
	}

	public void testWeb2ModuleCount() throws Exception {
		assertEquals(getJ2EEWebModule(WEBAPP2_PATH).getModules().length, 0);
	}

	public void _testWeb2ContextRoot() throws Exception {
		assertEquals(getJ2EEWebModule(WEBAPP2_PATH).getContextRoot(), "PublishTestWeb2");
	}

	public void testWeb2IsBinary() throws Exception {
		assertTrue(getJ2EEWebModule(WEBAPP2_PATH).isBinary());
	}

	public void deleteProjects() throws Exception {
		ModuleHelper.deleteProject(PROJECT_NAMES[0]);
	}
}

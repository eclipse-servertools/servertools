/*******************************************************************************
 * Copyright (c) 2004 Eteration Bilisim A.S.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Naci M. Dai - initial API and implementation
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL ETERATION A.S. OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Eteration Bilisim A.S.  For more
 * information on eteration, please see
 * <http://www.eteration.com/>.
 ***************************************************************************/

package org.eclipse.jst.server.generic.tests;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jst.server.generic.modules.J2eeSpecModuleFactoryDelegate;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IModuleType;


public class ModuleFactoryTests extends TestCase {

	TestProject  aProject;
	public final static String webXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
	"<!DOCTYPE web-app PUBLIC \"-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN\" \"http://java.sun.com/dtd/web-app_2_3.dtd\">\n"+
	"<web-app id=\"WebApp\">\n"+
	"	<display-name>Web Project</display-name>\n"+
	"	<welcome-file-list>\n"+
	"		<welcome-file>index.html</welcome-file>\n"+
	"		<welcome-file>index.htm</welcome-file>\n"+
	"		<welcome-file>index.jsp</welcome-file>\n"+
	"		<welcome-file>default.html</welcome-file>\n"+
	"		<welcome-file>default.htm</welcome-file>\n"+
	"		<welcome-file>default.jsp</welcome-file>\n"+
	"	</welcome-file-list>\n"+
	"</web-app>\n";
 
	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		aProject = new TestProject();
		aProject.createWebModule("myWebApp",webXML);
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		aProject.dispose();
	}

	/**
	 * Constructor for PluginIntegrityTest.
	 * @param name
	 */
	public ModuleFactoryTests(String name) {
		super(name);
	}
	
	public void testProjectExists(){
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject("Project-1");
		assertNotNull(project);
	}
	
	public void testModules(){
		IModule[] moduleList = J2eeSpecModuleFactoryDelegate.getInstance().getModules();	
		assertNotNull(moduleList);
		assertEquals(1,moduleList.length);
		IModule aWebModule = (IModule)moduleList[0];
		assertNotNull(aWebModule);
		assertEquals("myWebApp",aWebModule.getName());
		IModuleType moduleType = aWebModule.getModuleType();
		assertNotNull("Web module does not have a type",moduleType);
		assertEquals("j2ee.web",moduleType.getId());
	}
	

}

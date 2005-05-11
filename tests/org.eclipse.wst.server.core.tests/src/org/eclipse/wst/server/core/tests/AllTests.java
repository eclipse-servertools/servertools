/*******************************************************************************
* Copyright (c) 2004 Eteration Bilisim A.S.
* All rights reserved. ? This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
* 
* Contributors:
*     Deniz Secilir - initial API and implementation
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
package org.eclipse.wst.server.core.tests;

import org.eclipse.wst.server.core.tests.extension.*;
import org.eclipse.wst.server.core.tests.model.*;
import org.eclipse.wst.server.core.tests.util.HTTPLaunchableTestCase;
import org.eclipse.wst.server.core.tests.util.NullModuleArtifactTestCase;
import org.eclipse.wst.server.core.tests.util.PingThreadTestCase;
import org.eclipse.wst.server.core.tests.util.ProjectModuleFactoryDelegateTestCase;
import org.eclipse.wst.server.core.tests.util.ProjectModuleTestCase;
import org.eclipse.wst.server.core.tests.util.SocketUtilTestCase;
import org.eclipse.wst.server.core.tests.util.StaticWebTestCase;
import org.eclipse.wst.server.core.tests.util.WebResourceTestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {
	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.eclipse.wtp.wst.server.core.tests");
		//$JUnit-BEGIN$
		suite.addTestSuite(ExistenceTest.class);
		suite.addTestSuite(StartupExtensionTestCase.class);
		suite.addTestSuite(ModuleFactoriesTestCase.class);
		suite.addTestSuite(RuntimeTypesTestCase.class);
		suite.addTestSuite(RuntimeTargetHandlersTestCase.class);
		suite.addTestSuite(RuntimeLocatorsTestCase.class);
		suite.addTestSuite(ServerTypesTestCase.class);
		suite.addTestSuite(ServerTasksTestCase.class);
		suite.addTestSuite(ServerLocatorsTestCase.class);
		suite.addTestSuite(LaunchableAdaptersTestCase.class);
		suite.addTestSuite(ClientsTestCase.class);
		
		suite.addTestSuite(ServerCoreTestCase.class);
		suite.addTestSuite(ServerUtilTestCase.class);
		suite.addTest(new OrderedTestSuite(ProjectPropertiesTestCase.class));
		suite.addTest(new OrderedTestSuite(ServerPreferencesTestCase.class));
		suite.addTest(new OrderedTestSuite(ServerPortTestCase.class));
		
		suite.addTestSuite(IModuleTestCase.class);
		suite.addTestSuite(IModuleArtifactTestCase.class);
		suite.addTestSuite(IModuleTypeTestCase.class);
		suite.addTestSuite(PublishOperationTestCase.class);
		suite.addTestSuite(TaskModelTestCase.class);
		
		suite.addTestSuite(RuntimeLifecycleListenerTestCase.class);
		suite.addTestSuite(ServerLifecycleListenerTestCase.class);
		suite.addTestSuite(ServerListenerTestCase.class);
		suite.addTestSuite(RuntimeLifecycleAdapterTestCase.class);
		suite.addTestSuite(ServerLifecycleAdapterTestCase.class);
		suite.addTest(new OrderedTestSuite(ServerEventTestCase.class));
		
		suite.addTest(new OrderedTestSuite(ClientDelegateTestCase.class));
		suite.addTest(new OrderedTestSuite(RuntimeLocatorDelegateTestCase.class));
		suite.addTest(new OrderedTestSuite(ModuleDelegateTestCase.class));
		suite.addTest(new OrderedTestSuite(RuntimeTargetHandlersTestCase.class));
		suite.addTest(new OrderedTestSuite(RuntimeDelegateTestCase.class));
		suite.addTest(new OrderedTestSuite(ServerDelegateTestCase.class));
		suite.addTest(new OrderedTestSuite(ServerBehaviourDelegateTestCase.class));
		suite.addTest(new OrderedTestSuite(ServerTaskDelegateTestCase.class));
		suite.addTest(new OrderedTestSuite(ServerLocatorDelegateTestCase.class));
		suite.addTest(new OrderedTestSuite(ModuleFileTestCase.class));
		suite.addTest(new OrderedTestSuite(ModuleFolderTestCase.class));
		suite.addTest(new OrderedTestSuite(ModuleResourceTestCase.class));
		suite.addTest(new OrderedTestSuite(ModuleResourceDeltaTestCase.class));
		suite.addTest(new OrderedTestSuite(ModuleResourceDeltaVisitorTestCase.class));
		suite.addTest(new OrderedTestSuite(ModuleArtifactAdapterDelegateTestCase.class));
		suite.addTest(new OrderedTestSuite(LaunchableAdapterDelegateTestCase.class));
		suite.addTest(new OrderedTestSuite(ModuleListenerTestCase.class));
		suite.addTest(new OrderedTestSuite(ModuleEventTestCase.class));
		suite.addTest(new OrderedTestSuite(ModuleFactoryEventTestCase.class));
		suite.addTest(new OrderedTestSuite(ModuleFactoryDelegateTestCase.class));
		
		suite.addTest(new OrderedTestSuite(PingThreadTestCase.class));
		suite.addTest(new OrderedTestSuite(ProjectModuleTestCase.class));
		suite.addTest(new OrderedTestSuite(ProjectModuleFactoryDelegateTestCase.class));
		
		suite.addTest(new OrderedTestSuite(HTTPLaunchableTestCase.class));
		suite.addTest(new OrderedTestSuite(StaticWebTestCase.class));
		suite.addTest(new OrderedTestSuite(WebResourceTestCase.class));
		suite.addTest(new OrderedTestSuite(NullModuleArtifactTestCase.class));
		suite.addTestSuite(SocketUtilTestCase.class);
		//$JUnit-END$
		return suite;
	}
}
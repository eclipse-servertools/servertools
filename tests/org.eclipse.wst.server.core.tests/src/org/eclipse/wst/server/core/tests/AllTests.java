/*******************************************************************************
* Copyright (c) 2004, 2010 Eteration Bilisim A.S. and others
* All rights reserved. ? This program and the accompanying materials
* are made available under the terms of the Eclipse Public License 2.0
* which accompanies this distribution, and is available at
* https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
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
import org.eclipse.wst.server.core.tests.util.*;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {
	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.eclipse.wtp.wst.server.core.tests");
		//$JUnit-BEGIN$
		
		// TODO - should add tests for specific module adapters and other extension points
		// that exist in WTP
		suite.addTestSuite(ExistenceTest.class);
		//suite.addTestSuite(MatchesTestCase.class);
		//suite.addTestSuite(ModificationStampTestCase.class);
		suite.addTestSuite(StartupExtensionTestCase.class);
		suite.addTestSuite(ModuleFactoriesTestCase.class);
		suite.addTestSuite(RuntimeTypesTestCase.class);
		suite.addTestSuite(RuntimeLocatorsTestCase.class);
		suite.addTestSuite(ServerTypesTestCase.class);
		suite.addTestSuite(ServerTasksTestCase.class);
		suite.addTestSuite(ServerLocatorsTestCase.class);
		suite.addTestSuite(LaunchableAdaptersTestCase.class);
		suite.addTestSuite(ClientsTestCase.class);
		
		suite.addTestSuite(ServerCoreTestCase.class);
		suite.addTestSuite(ServerUtilTestCase.class);
		TestSuite subSuite = new TestSuite(ProjectPropertiesTestCase.class);
		ProjectPropertiesTestCase.addOrderedTests(subSuite);
		suite.addTest(subSuite);
		suite.addTestSuite(ServerPreferencesTestCase.class);
		suite.addTestSuite(ServerPortTestCase.class);
		suite.addTestSuite(ServerOperationTestCase.class);
		
		suite.addTestSuite(IModuleTestCase.class);
		suite.addTestSuite(IModuleArtifactTestCase.class);
		suite.addTestSuite(IModuleTypeTestCase.class);
		suite.addTestSuite(PublishOperationTestCase.class);
		suite.addTestSuite(TaskModelTestCase.class);
		
		suite.addTestSuite(RuntimeLifecycleListenerTestCase.class);
		suite.addTestSuite(ServerLifecycleListenerTestCase.class);
		suite.addTestSuite(PublishListenerTestCase.class);
		suite.addTestSuite(ServerListenerTestCase.class);
		suite.addTestSuite(ServerEventTestCase.class);
		suite.addTestSuite(ServerTestCase.class);
		
		suite.addTestSuite(ClientDelegateTestCase.class);
		suite.addTestSuite(RuntimeLocatorDelegateTestCase.class);
		suite.addTestSuite(ModuleDelegateTestCase.class);
		suite.addTestSuite(RuntimeDelegateTestCase.class);
		suite.addTestSuite(ServerDelegateTestCase.class);
		suite.addTestSuite(ServerBehaviourDelegateTestCase.class);
		suite.addTestSuite(ServerTaskDelegateTestCase.class);
		suite.addTestSuite(ServerLocatorDelegateTestCase.class);
		suite.addTestSuite(ModuleFileTestCase.class);
		suite.addTestSuite(ModuleFolderTestCase.class);
		suite.addTestSuite(ModuleResourceTestCase.class);
		suite.addTestSuite(ModuleResourceDeltaTestCase.class);
		suite.addTestSuite(ModuleArtifactAdapterDelegateTestCase.class);
		suite.addTestSuite(LaunchableAdapterDelegateTestCase.class);
		suite.addTestSuite(ModuleFactoryDelegateTestCase.class);
		
		suite.addTestSuite(ProjectModuleTestCase.class);
		suite.addTestSuite(ProjectModuleFactoryDelegateTestCase.class);
		
		suite.addTestSuite(HTTPLaunchableTestCase.class);
		suite.addTestSuite(StaticWebTestCase.class);
		suite.addTestSuite(WebResourceTestCase.class);
		// Disable for now.  java.io.File.renameTo() isn't able to move a file on the build system, 
		// likely because the location of "temp" directory and workspace are on different drives
		//suite.addTest(new OrderedTestSuite(PublishUtilTestCase.class));
		suite.addTestSuite(NullModuleArtifactTestCase.class);
		suite.addTestSuite(SocketUtilTestCase.class);
		suite.addTestSuite(RuntimeLifecycleAdapterTestCase.class);
		suite.addTestSuite(ServerLifecycleAdapterTestCase.class);
		suite.addTestSuite(PublishAdapterTestCase.class);
		//$JUnit-END$
		return suite;
	}
}
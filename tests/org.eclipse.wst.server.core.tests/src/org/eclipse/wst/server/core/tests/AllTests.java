/*******************************************************************************
* Copyright (c) 2004 Eteration Bilisim A.S.
* All rights reserved. ? This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
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
		//$JUnit-END$
		return suite;
	}
}
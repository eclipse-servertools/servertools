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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jst.server.generic.internal.core.GenericServerRuntime;
import org.eclipse.jst.server.generic.internal.core.GenericServerWorkingCopy;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.IServerConfigurationType;
import org.eclipse.wst.server.core.IServerConfigurationWorkingCopy;
import org.eclipse.wst.server.core.IServerState;
import org.eclipse.wst.server.core.IServerType;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.ServerUtil;
import org.eclipse.wst.server.core.internal.RuntimeType;
import org.eclipse.wst.server.core.model.IModule;
import org.eclipse.wst.server.core.model.IServerWorkingCopyDelegate;

public class ServerCreationTest extends TestCase {

	RuntimeType j2eeRuntimeType = null;
	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		List listAll = ServerCore.getRuntimeTypes();
		if( listAll != null){
			Iterator iterator = listAll.iterator();
			while (iterator.hasNext()) {
				RuntimeType runtimeType = (RuntimeType) iterator.next();
				if("J2EE Runtime Library".equals(runtimeType.getName()))
					j2eeRuntimeType = runtimeType;
			}
		}
		
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Constructor for PluginIntegrityTest.
	 * 
	 * @param name
	 */
	public ServerCreationTest(String name) {
		super(name);
	}

	public void testGetTypes() {	
		assertNotNull(j2eeRuntimeType);
	}
	
	public void testCreateServer() throws Exception{	
		assertNotNull(j2eeRuntimeType);
		IRuntimeWorkingCopy runtimeWorkingCopy = j2eeRuntimeType.createRuntime(null);
		ServerUtil.setRuntimeDefaultName(runtimeWorkingCopy);
		assertNotNull(runtimeWorkingCopy.getName());
		HashMap props = new HashMap();
		props.put("mappernames","");
		props.put("classPathVariableName","JONAS");
		props.put("serverAddress","127.0.0.1");
		props.put("jonasBase","C:\\nmd\\dev\\java\\appservers\\JOnAS-4.1.4");
		props.put("jonasRoot","C:\\nmd\\dev\\java\\appservers\\JOnAS-4.1.4");
		props.put("classPath","C:\\nmd\\dev\\java\\appservers\\JOnAS-4.1.4");
		props.put("protocols","C:\\nmd\\dev\\java\\appservers\\JOnAS-4.1.4");
		props.put("port","9000");
		runtimeWorkingCopy.setAttribute(GenericServerRuntime.SERVER_DEFINITION_ID, "JonAS 4.1.4");
		runtimeWorkingCopy.setAttribute(GenericServerRuntime.SERVER_INSTANCE_PROPERTIES,props);
		runtimeWorkingCopy.save(null);
		List sTypes = ServerCore.getServerTypes();
		Iterator sTypeIter = sTypes.iterator();
		IServerType serverType = null;
		while (sTypeIter.hasNext()) {
			IServerType sType = (IServerType) sTypeIter.next();
			if("org.eclipse.jst.server.generic".equals(sType.getId()))
				serverType = sType;
		}
		IServerWorkingCopy server = serverType.createServer(null, null, (IRuntime)null);
		assertNotNull(server);
		IRuntimeType runtimeType = serverType.getRuntimeType();
		IRuntimeWorkingCopy runtime = null;
		runtime = runtimeType.createRuntime(null);
		runtime.setName(server.getName());
		server.setRuntime(runtime);
		IServerWorkingCopyDelegate wcd = getWorkingCopyDelegate(server,serverType);
		IServerConfigurationWorkingCopy serverConfiguration = getServerConfiguration(serverType.getServerConfigurationType(), null, runtime);
		if(serverConfiguration!=null)
		    server.setServerConfiguration(serverConfiguration);
		wcd.modifyModules(new IModule[0], new IModule[0], null);
		runtime.save(new NullProgressMonitor());
		server.save(new NullProgressMonitor());
		if(serverConfiguration!=null)
		    serverConfiguration.save(new NullProgressMonitor());
	
	}
	
	private IServerWorkingCopyDelegate getWorkingCopyDelegate(IServerWorkingCopy server,IServerType serverType) {
	
			GenericServerWorkingCopy workingCopyDelegate = new GenericServerWorkingCopy();
			workingCopyDelegate.initialize((IServerState) server);
			workingCopyDelegate.initialize(server);
			return workingCopyDelegate;
	}	

	private IServerConfigurationWorkingCopy getServerConfiguration(IServerConfigurationType type, IFile file, IRuntime runtime) throws CoreException {
	    if(type==null)
	        return null;
		IServerConfigurationWorkingCopy serverConfiguration = type.importFromRuntime(null, file, runtime, new NullProgressMonitor());
		ServerUtil.setServerConfigurationDefaultName(serverConfiguration);
		return serverConfiguration;
	}	
	
	public void testGetWebTypes() {
		
		List listWeb = ServerUtil.getRuntimeTypes("j2ee.web",null);
		assertNotNull(listWeb);
		Iterator iterator = listWeb.iterator();
		boolean found = false;
		while (iterator.hasNext()) {
			RuntimeType runtimeType = (RuntimeType) iterator.next();
			if("J2EE Runtime Library".equals(runtimeType.getName()))
				found = true;
		}
		assertTrue(found);
	}


}

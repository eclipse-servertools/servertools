/*******************************************************************************
 * Copyright (c) 2004 Eteration Bilisim A.S.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Gorkem Ercan - initial API and implementation
 *     Naci M. Dai
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
package org.eclipse.jst.server.generic.internal.core;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jst.server.generic.core.CorePlugin;
import org.eclipse.jst.server.generic.core.GenericServerCoreMessages;
import org.eclipse.jst.server.generic.modules.J2eeSpecModuleFactoryDelegate;
import org.eclipse.jst.server.generic.servertype.definition.Port;
import org.eclipse.jst.server.generic.servertype.definition.ServerRuntime;
import org.eclipse.jst.server.j2ee.IWebModule;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ITask;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.model.IModuleEvent;
import org.eclipse.wst.server.core.model.IModuleFactoryEvent;
import org.eclipse.wst.server.core.model.IURLProvider;
import org.eclipse.wst.server.core.model.RuntimeDelegate;
import org.eclipse.wst.server.core.model.ServerDelegate;
import org.eclipse.wst.server.core.util.ServerPort;

/**
 * Generic XML based server implementation.
 * 
 * @author Gorkem Ercan
 */
public class GenericServer extends ServerDelegate implements IURLProvider {
	

	


	
	
	private ServerRuntime fServerDefinition;

//	/**
//	 * Returns the project publisher that can be used to
//	 * publish the given project.
//	 *
//	 * @param project org.eclipse.core.resources.IProject
//	 * @return org.eclipse.wst.server.core.model.IProjectPublisher
//	 */
//	public IPublisher getPublisher(List parents, IModule module) {
//		return new AntPublisher(parents, module, this.getServerDefinition());
//	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.server.core.model.IServerDelegate#updateConfiguration()
	 */
	public void updateConfiguration() {
		Trace.trace(Trace.FINEST, "updateConfiguration" + this);
	}



	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.server.core.model.IServerDelegate#publishStart(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public IStatus publishStart(IProgressMonitor monitor) {
	    if(getModules().length<1)
	        return new Status(IStatus.CANCEL,CorePlugin.PLUGIN_ID,0,GenericServerCoreMessages.getString("cancelNoPublish"),null);
		return new Status(IStatus.OK, CorePlugin.PLUGIN_ID, 0, "PublishingStarted", null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.server.core.model.IServerDelegate#publishConfiguration(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public IStatus publishConfiguration(IProgressMonitor monitor) {

		return new Status(IStatus.OK, CorePlugin.PLUGIN_ID, 0, "Published Configuration", null);
	}

//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see org.eclipse.wst.server.core.model.IServerDelegate#publishStop(org.eclipse.core.runtime.IProgressMonitor)
//	 */
//	public IStatus publishStop(IProgressMonitor monitor) {
//		getServer().setConfigurationSyncState(IServer.SYNC_STATE_IN_SYNC);
//		return new Status(IStatus.OK, CorePlugin.PLUGIN_ID, 0, "Published Configuration", null);
//	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.server.core.model.IServerDelegate#canModifyModules(org.eclipse.wst.server.core.model.IModule[],
	 *      org.eclipse.wst.server.core.model.IModule[])
	 */
	public IStatus canModifyModules(IModule[] add, IModule[] remove) {
		// TODO Auto-generated method stub
		return new Status(IStatus.OK, CorePlugin.PLUGIN_ID, 0, "CanModifyModules", null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.server.core.model.IServerDelegate#getModules()
	 */
	public org.eclipse.wst.server.core.IModule[] getModules() {
		return J2eeSpecModuleFactoryDelegate.getInstance().getModules();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.server.core.model.IServerDelegate#getModuleState(org.eclipse.wst.server.core.model.IModule)
	 */
	public int getModuleState(IModule module) {
	    return IServer.STATE_STARTED;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.server.core.model.IServerDelegate#getRepairCommands(org.eclipse.wst.server.core.model.IModuleFactoryEvent[],
	 *      org.eclipse.wst.server.core.model.IModuleEvent[])
	 */
	public ITask[] getRepairCommands(IModuleFactoryEvent[] factoryEvent,
			IModuleEvent[] moduleEvent) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.server.core.model.IServerDelegate#getChildModules(org.eclipse.wst.server.core.model.IModule)
	 */
	public IModule[] getChildModules(IModule module) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.server.core.model.IServerDelegate#getParentModules(org.eclipse.wst.server.core.model.IModule)
	 */
	public IModule[] getParentModules(IModule module) throws CoreException {
			//FIXME This is valid for only web modules. A generic server should support any 
			// kind of j2ee module. Fix this after the server architectures are determined.
		if (module instanceof IWebModule) {
			IWebModule webModule = (IWebModule) module;
			IStatus status = canModifyModules(new IModule[] { module }, null);
			if (status == null || !status.isOK())
				throw new CoreException(status);
			ArrayList l = new ArrayList();
			l.add(webModule);
			return (IModule[])l.toArray(new IModule[l.size()]);
		}
		return null;
	
	}

	/**
	 * @return
	 */
	private Map getServerInstanceProperties() {
		Map runtimeProperties =getRuntimeDelegate().getAttribute(
				GenericServerRuntime.SERVER_INSTANCE_PROPERTIES, new HashMap());
		Map serverProperties = getAttribute(GenericServerRuntime.SERVER_INSTANCE_PROPERTIES,new HashMap(1));
		Map instanceProperties = new HashMap(runtimeProperties.size()+serverProperties.size());
		instanceProperties.putAll(runtimeProperties);
		instanceProperties.putAll(serverProperties);
		return instanceProperties;
	}
	
 	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.server.core.model.IMonitorableServer#getServerPorts()
	 */
	public org.eclipse.wst.server.core.IServerPort[] getServerPorts() {
		List ports = new ArrayList();
		Iterator pIter = this.getServerDefinition().getPort().iterator();
		while (pIter.hasNext()) {
			Port element = (Port) pIter.next();
			int port = Integer.parseInt(getServerDefinition().getResolver().resolveProperties(element.getNo()));
			ports.add(new ServerPort("server", element.getName(), port, element.getProtocol()));		
		}
	
		return (org.eclipse.wst.server.core.IServerPort[])ports.toArray(new org.eclipse.wst.server.core.IServerPort[ports.size()]);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.IStartableServer#isTerminateOnShutdown()
	 */
	public boolean isTerminateOnShutdown() {
		return true;
	}

	public int getStartTimeout() {
		return 300000;
	}
	
	public int getStopTimeout() {
		return 300000;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.wtp.server.core.model.IURLProvider#getModuleRootURL(org.eclipse.wtp.server.core.model.IModule)
	 */
	public URL getModuleRootURL(IModule module) {

		try {
			if (module == null || !(module instanceof IWebModule))
				return null;

			String url = "http://localhost";
			int port = 0;
			
			port = getHttpPort();
			port = ServerCore.getServerMonitorManager().getMonitoredPort(getServer(), port, "web");
			if (port != 80)
				url += ":" + port;

			url += "/"+module.getName();

			if (!url.endsWith("/"))
				url += "/";

			return new URL(url);
		} catch (Exception e) {
			Trace.trace("Could not get root URL", e);
			return null;
		}

	}

	/**
	 * @return
	 */
	private int getHttpPort() {
		int port=-1;
		Iterator pIter = this.getServerDefinition().getPort().iterator();
		while (pIter.hasNext()) {
			Port aPort = (Port) pIter.next();
			if(port== -1)
				port = Integer.parseInt(getServerDefinition().getResolver().resolveProperties(aPort.getNo()));
			else if( "http".equals(aPort.getProtocol() ) )
				port = Integer.parseInt(aPort.getNo());	
		}
		if( port == -1)
			port = 8080;
		return port;
	}

    public ServerRuntime getServerDefinition() {
    	if (fServerDefinition == null)
    		fServerDefinition = CorePlugin.getDefault()
    				.getServerTypeDefinitionManager()
    				.getServerRuntimeDefinition(getRuntimeDelegate().getAttribute(
    								GenericServerRuntime.SERVER_DEFINITION_ID,
    								""), getServerInstanceProperties());
    	return fServerDefinition;
    }

    private RuntimeDelegate getRuntimeDelegate()
    {
       return (RuntimeDelegate)getServer().getRuntime().getAdapter(RuntimeDelegate.class);
    }

    /* (non-Javadoc)
     * @see org.eclipse.wst.server.core.model.ServerDelegate#modifyModules(org.eclipse.wst.server.core.IModule[], org.eclipse.wst.server.core.IModule[], org.eclipse.core.runtime.IProgressMonitor)
     */
    public void modifyModules(IModule[] add, IModule[] remove, IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub
        
    }

}
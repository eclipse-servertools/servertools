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
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jst.server.generic.core.CorePlugin;
import org.eclipse.jst.server.generic.internal.xml.ClasspathItem;
import org.eclipse.jst.server.generic.internal.xml.ServerTypeDefinition;
import org.eclipse.jst.server.generic.modules.J2eeSpecModuleFactoryDelegate;
import org.eclipse.jst.server.j2ee.IWebModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerConfiguration;
import org.eclipse.wst.server.core.IServerState;
import org.eclipse.wst.server.core.ITask;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.model.IModule;
import org.eclipse.wst.server.core.model.IModuleEvent;
import org.eclipse.wst.server.core.model.IModuleFactoryEvent;
import org.eclipse.wst.server.core.model.IMonitorableServer;
import org.eclipse.wst.server.core.model.IPublisher;
import org.eclipse.wst.server.core.model.IServerDelegate;
import org.eclipse.wst.server.core.model.IServerPort;
import org.eclipse.wst.server.core.model.IStartableServer;
import org.eclipse.wst.server.core.model.IURLProvider;
import org.eclipse.wst.server.core.resources.IModuleResourceDelta;
import org.eclipse.wst.server.core.util.ServerPort;
import org.eclipse.wst.server.core.util.SocketUtil;

/**
 * Generic XML based server implementation.
 * 
 * @author Gorkem Ercan
 */
public class GenericServer implements IServerDelegate, IStartableServer, IMonitorableServer,IURLProvider {
	private IServerState fLiveServer;
	private static final String ATTR_STOP = "stop-server";
	
	// the thread used to ping the server to check for startup
	protected transient PingThread ping = null;
	protected transient IProcess process;
	protected transient IDebugEventSetListener processListener;
	
	private ServerTypeDefinition fServerDefinition;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.server.core.model.IServerDelegate#initialize(org.eclipse.wst.server.core.IServerState)
	 */
	public void initialize(IServerState liveServer) {
		this.fLiveServer = liveServer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.server.core.model.IServerDelegate#dispose()
	 */
	public void dispose() {
		this.fLiveServer = null;

	}

	/**
	 * Returns the project publisher that can be used to
	 * publish the given project.
	 *
	 * @param project org.eclipse.core.resources.IProject
	 * @return org.eclipse.wst.server.core.model.IProjectPublisher
	 */
	public IPublisher getPublisher(List parents, IModule module) {
		return new AntPublisher(parents, module, this.getServerDefinition());
	}

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
	 * @see org.eclipse.wst.server.core.model.IServerDelegate#updateModule(org.eclipse.wst.server.core.model.IModule,
	 *      org.eclipse.wst.server.core.resources.IModuleResourceDelta)
	 */
	public void updateModule(IModule module, IModuleResourceDelta delta) {
		// TODO Auto-generated method stub
		Trace.trace(Trace.FINEST, "Configuration updated " + this);
		//setConfigurationSyncState(SYNC_STATE_DIRTY);
		//setRestartNeeded(true);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.server.core.model.IServerDelegate#publishStart(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public IStatus publishStart(IProgressMonitor monitor) {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.server.core.model.IServerDelegate#publishStop(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public IStatus publishStop(IProgressMonitor monitor) {
		fLiveServer.setConfigurationSyncState(IServer.SYNC_STATE_IN_SYNC);
		return new Status(IStatus.OK, CorePlugin.PLUGIN_ID, 0, "Published Configuration", null);
	}

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
	public IModule[] getModules() {
		// TODO Auto-generated method stub
		List list =  J2eeSpecModuleFactoryDelegate.getInstance().getModules();
		return (IModule[])list.toArray(new IModule[list.size()]);
	}

	
	protected IWebModule getWebModule(IProject project) throws CoreException {
	
		return null;
	}


		
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.server.core.model.IServerDelegate#getModuleState(org.eclipse.wst.server.core.model.IModule)
	 */
	public byte getModuleState(IModule module) {
		// TODO Auto-generated method stub
		return IServer.MODULE_STATE_STARTED;
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
	public List getChildModules(IModule module) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.server.core.model.IServerDelegate#getParentModules(org.eclipse.wst.server.core.model.IModule)
	 */
	public List getParentModules(IModule module) throws CoreException {
			//FIXME This is valid for only web modules. A generic server should support any 
			// kind of j2ee module. Fix this after the server architectures are determined.
		if (module instanceof IWebModule) {
			IWebModule webModule = (IWebModule) module;
			IStatus status = canModifyModules(new IModule[] { module }, null);
			if (status == null || !status.isOK())
				throw new CoreException(status);
			ArrayList l = new ArrayList();
			l.add(webModule);
			return l;
		} else
			return null;
	
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.server.core.model.IServerDelegate#setLaunchDefaults(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
	 */
	public void setLaunchDefaults(ILaunchConfigurationWorkingCopy workingCopy) {
		fLiveServer.getRuntime().getDelegate();
		workingCopy.setAttribute(
				IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME,
				getStartClassName());

		GenericServerRuntime runtime = (GenericServerRuntime) fLiveServer
				.getRuntime().getDelegate();

		IVMInstall vmInstall = runtime.getVMInstall();
		workingCopy.setAttribute(
				IJavaLaunchConfigurationConstants.ATTR_VM_INSTALL_TYPE, runtime
						.getVMInstallTypeId());
		workingCopy.setAttribute(
				IJavaLaunchConfigurationConstants.ATTR_VM_INSTALL_NAME,
				vmInstall.getName());

		setupLaunchClasspath(workingCopy, vmInstall);


		workingCopy.setAttribute(
				IJavaLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY,
				getWorkingDirectory());
		workingCopy.setAttribute(
				IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS,
				getProgramArguments());
		workingCopy.setAttribute(
				IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS,
				getVmArguments());

		//workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_INSTALL_TYPE_SPECIFIC_ATTRS_MAP,);

	}

	private List getClasspathMementos() {
		List cpathList = getServerDefinition().getServerClassPath();
		ArrayList mementoList = new ArrayList();
		for (int i = 0; i < cpathList.size(); i++) {
			ClasspathItem item = (ClasspathItem) cpathList.get(i);
			String cpath = getServerDefinition().resolveProperties(
					item.getClasspath());
			String memento = null;
			try {
				memento = JavaRuntime.newArchiveRuntimeClasspathEntry(
						new Path(cpath)).getMemento();
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mementoList.add(memento);
		}
		return mementoList;
	}

	private String getVmArguments() {
		String vmParams = getServerDefinition().resolveProperties(
				getServerDefinition().getStartVmParameters());
		return vmParams;
	}

	private String getProgramArguments() {
		String startParams = getServerDefinition().resolveProperties(
				getServerDefinition().getStartProgramArguments());
		return startParams;
	}

	private String getWorkingDirectory() {
		String wDirectory = getServerDefinition().resolveProperties(
				getServerDefinition().getStartWorkingDirectory());
		return wDirectory;

	}

	public String getStartClassName() {
		return getServerDefinition().getStartClass();
	}

	/**
	 * @return
	 */
	private Map getServerInstanceProperties() {
		Map instanceProperties = fLiveServer.getRuntime().getAttribute(
				GenericServerRuntime.SERVER_INSTANCE_PROPERTIES, (Map) null);
		return instanceProperties;
	}

	public ServerTypeDefinition getServerDefinition() {
		
		if (fServerDefinition == null)
			fServerDefinition = CorePlugin.getDefault()
					.getServerTypeDefinitionManager()
					.getServerRuntimeDefinition(
							fLiveServer.getRuntime().getAttribute(
									GenericServerRuntime.SERVER_DEFINITION_ID,
									""), getServerInstanceProperties());
		return fServerDefinition;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.server.core.model.IMonitorableServer#getServerPorts()
	 */
	public List getServerPorts() {
		List ports = new ArrayList();

	
		try {
			int port = Integer.parseInt(this.getServerDefinition().getPort());
			ports.add(new ServerPort("server", "Server port", port, "TCPIP"));
		} catch (Exception e) {
		}

		return ports;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.IStartableServer#isTerminateOnShutdown()
	 */
	public boolean isTerminateOnShutdown() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Setup for starting the server.
	 * 
	 * @param launch ILaunch
	 * @param launchMode String
	 * @param monitor IProgressMonitor
	 */
	public void setupLaunch(ILaunch launch, String launchMode, IProgressMonitor monitor) throws CoreException {
		if ("true".equals(launch.getLaunchConfiguration().getAttribute(ATTR_STOP, "false")))
			return;
//		IStatus status = getRuntime().validate();
//		if (status != null && !status.isOK())
//			throw new CoreException(status);

	
		Iterator iterator = this.getServerPorts().iterator();
		IServerPort sp = null;
		while (iterator.hasNext()) {
			sp = (IServerPort) iterator.next();
			if (SocketUtil.isPortInUse(sp.getPort(), 5))
				throw new CoreException(new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, 0, "Server Part In Use "+sp.getPort() + "- " +sp.getName() ,null));
		}
		
		fLiveServer.setServerState(IServer.SERVER_STARTING);
	
		// ping server to check for startup
		try {
			String url = "http://localhost";
			int port = sp.getPort();
			if (port != 80)
				url += ":" + port;
			ping = new PingThread(this, fLiveServer, url, launchMode);
			ping.start();
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Can't ping for server startup.");
		}
	}
	
	/**
	 * Cleanly shuts down and terminates the server.
	 */
	public void stop() {
		byte state = this.fLiveServer.getServerState();
		if (state == IServer.SERVER_STOPPED)
			return;
		else if (state == IServer.SERVER_STARTING || state == IServer.SERVER_STOPPING) {
			terminate();
			return;
		}

		try {
			Trace.trace(Trace.FINEST, "Stopping Server");
			if (state != IServer.SERVER_STOPPED)
				fLiveServer.setServerState(IServer.SERVER_STOPPING);
			ILaunchManager mgr = DebugPlugin.getDefault().getLaunchManager();

			ILaunchConfigurationType type =
				mgr.getLaunchConfigurationType(
					IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION);

			String launchName = "GenericServerStopper";
			String uniqueLaunchName =
				mgr.generateUniqueLaunchConfigurationNameFrom(launchName);
			ILaunchConfiguration conf = null;

			ILaunchConfiguration[] lch = mgr.getLaunchConfigurations(type);
			for (int i = 0; i < lch.length; i++) {
				if (launchName.equals(lch[i].getName())) {
					conf = lch[i];
					break;
				}
			}

			ILaunchConfigurationWorkingCopy wc = null;
			if (conf != null) {
				wc = conf.getWorkingCopy();
			} else {
				wc = type.newInstance(null, uniqueLaunchName);
			}
			//To stop from appearing in history lists
			wc.setAttribute(IDebugUIConstants.ATTR_PRIVATE, true);			
	
			wc.setAttribute(
					IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME,
					this.getServerDefinition().getStopClass());

			GenericServerRuntime runtime = (GenericServerRuntime) fLiveServer
					.getRuntime().getDelegate();

			IVMInstall vmInstall = runtime.getVMInstall();
			wc.setAttribute(
					IJavaLaunchConfigurationConstants.ATTR_VM_INSTALL_TYPE, runtime
							.getVMInstallTypeId());
			wc.setAttribute(
					IJavaLaunchConfigurationConstants.ATTR_VM_INSTALL_NAME,
					vmInstall.getName());

			setupLaunchClasspath(wc, vmInstall);

			wc.setAttribute(
					IJavaLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY,
					getWorkingDirectory());
			wc.setAttribute(
					IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS,
					getServerDefinition().resolveProperties(
							getServerDefinition().getStopProgramArguments()));
			wc.setAttribute(
					IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS,
					getServerDefinition().resolveProperties(
							getServerDefinition().getStopVmParameters()));				
			wc.setAttribute(ATTR_STOP, "true");
			wc.launch(ILaunchManager.RUN_MODE, new NullProgressMonitor());
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error stopping Server", e);
		}
	}


	/**
	 * @param wc
	 * @param vmInstall
	 */
	private void setupLaunchClasspath(ILaunchConfigurationWorkingCopy wc, IVMInstall vmInstall) {
		List cp = getClasspathMementos();

		// add tools.jar to the path
		if (vmInstall != null) {
			try {
				cp
						.add(JavaRuntime
								.newRuntimeContainerClasspathEntry(
										new Path(JavaRuntime.JRE_CONTAINER)
												.append(
														"org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType")
												.append(vmInstall.getName()),
										IRuntimeClasspathEntry.BOOTSTRAP_CLASSES)
								.getMemento());
			} catch (Exception e) {
			}

			IPath jrePath = new Path(vmInstall.getInstallLocation()
					.getAbsolutePath());
			if (jrePath != null) {
				IPath toolsPath = jrePath.append("lib").append("tools.jar");
				if (toolsPath.toFile().exists()) {
					try {
						cp.add(JavaRuntime.newArchiveRuntimeClasspathEntry(
								toolsPath).getMemento());
					} catch (CoreException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		}

		wc.setAttribute(
				IJavaLaunchConfigurationConstants.ATTR_CLASSPATH, cp);
		wc.setAttribute(
						IJavaLaunchConfigurationConstants.ATTR_DEFAULT_CLASSPATH,
						false);
	}

	/**
	 * Terminates the server.
	 */
	public void terminate() {
		if (fLiveServer.getServerState() == IServer.SERVER_STOPPED)
			return;

		try {
			fLiveServer.setServerState(IServer.SERVER_STOPPING);
			Trace.trace(Trace.FINEST, "Killing the Server process");
			if (process != null && !process.isTerminated()) {
				process.terminate();
				stopImpl();
			}
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error killing the process", e);
		}
	}
	
	protected void stopImpl() {
		if (ping != null) {
			ping.stopPinging();
			ping = null;
		}
		if (process != null) {
			process = null;
			DebugPlugin.getDefault().removeDebugEventListener(processListener);
			processListener = null;
		}
		fLiveServer.setServerState(IServer.SERVER_STOPPED);
	}	
	public void setProcess(final IProcess newProcess) {
		if (process != null)
			return;

		process = newProcess;
		processListener = new IDebugEventSetListener() {
			public void handleDebugEvents(DebugEvent[] events) {
				if (events != null) {
					int size = events.length;
					for (int i = 0; i < size; i++) {
						if (process.equals(events[i].getSource()) && events[i].getKind() == DebugEvent.TERMINATE) {
							DebugPlugin.getDefault().removeDebugEventListener(this);
							stopImpl();
						}
					}
				}
			}
		};
		DebugPlugin.getDefault().addDebugEventListener(processListener);
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

			IServerConfiguration serverConfig = fLiveServer
					.getServerConfiguration();
			if (serverConfig == null)
				return null;

			String url = "http://localhost";
			int port = Integer.parseInt(getServerDefinition().getPort());
			port = ServerCore.getServerMonitorManager().getMonitoredPort(
					fLiveServer, port, "web");
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

}
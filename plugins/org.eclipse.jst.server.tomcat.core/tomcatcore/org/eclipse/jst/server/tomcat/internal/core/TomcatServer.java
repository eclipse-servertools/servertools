package org.eclipse.jst.server.tomcat.internal.core;
/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.*;
import org.eclipse.debug.core.*;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jst.server.j2ee.IWebModule;
import org.eclipse.jst.server.tomcat.core.ITomcatConfiguration;
import org.eclipse.jst.server.tomcat.core.ITomcatRuntime;
import org.eclipse.jst.server.tomcat.core.ITomcatServer;
import org.eclipse.jst.server.tomcat.core.WebModule;
import org.eclipse.jst.server.tomcat.core.internal.command.RemoveWebModuleTask;
import org.eclipse.jst.server.tomcat.core.internal.command.SetWebModulePathTask;

import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.model.*;
import org.eclipse.wst.server.core.resources.IModuleResourceDelta;
import org.eclipse.wst.server.core.util.SocketUtil;
/**
 * Generic Tomcat server.
 */
public class TomcatServer implements ITomcatServer, IStartableServer, IMonitorableServer {
	private static final String ATTR_STOP = "stop-server";
	
	protected transient IPath tempDirectory;
	
	protected IServerState server;

	// the thread used to ping the server to check for startup
	protected transient PingThread ping = null;
	protected transient IProcess process;
	protected transient IDebugEventSetListener processListener;

	/**
	 * TomcatServer.
	 */
	public TomcatServer() {
		super();
	}

	public void initialize(IServerState server2) {
		this.server = server2;
	}
	
	public void dispose() { }

	public TomcatRuntime getTomcatRuntime() {
		if (server.getRuntime() == null)
			return null;
		else
			return (TomcatRuntime) server.getRuntime().getDelegate();
	}
	
	public ITomcatVersionHandler getTomcatVersionHandler() {
		if (server.getRuntime() == null)
			return null;

		TomcatRuntime runtime = (TomcatRuntime) server.getRuntime().getDelegate();
		return runtime.getVersionHandler();
	}
	
	public TomcatConfiguration getTomcatConfiguration() {
		IServerConfiguration configuration = server.getServerConfiguration();
		if (configuration == null)
			return null;
		else
			return (TomcatConfiguration) configuration.getDelegate();
	}

	/**
	 * Returns the project publisher that can be used to
	 * publish the given project.
	 *
	 * @param project org.eclipse.core.resources.IProject
	 * @return org.eclipse.wst.server.core.model.IProjectPublisher
	 */
	public IPublisher getPublisher(List parents, IModule module) {
		if (isTestEnvironment())
			return null;
		else
			return new TomcatWebModulePublisher((IWebModule) module, server.getRuntime().getLocation());
	}

	/**
	 * Return the root URL of this module.
	 * @param module org.eclipse.wst.server.core.model.IModule
	 * @return java.net.URL
	 */
	public URL getModuleRootURL(IModule module) {
		try {
			if (module == null || !(module instanceof IWebModule))
				return null;
	
			IServerConfiguration serverConfig = server.getServerConfiguration();
			if (serverConfig == null)
				return null;
	
			TomcatConfiguration config = (TomcatConfiguration) serverConfig.getDelegate();
			if (config == null)
				return null;
	
			String url = "http://localhost";
			int port = config.getMainPort().getPort();
			port = ServerCore.getServerMonitorManager().getMonitoredPort(server, port, "web");
			if (port != 80)
				url += ":" + port;

			IWebModule module2 = (IWebModule) module;
			url += config.getWebModuleURL(module2);
			
			if (!url.endsWith("/"))
				url += "/";

			return new URL(url);
		} catch (Exception e) {
			Trace.trace("Could not get root URL", e);
			return null;
		}
	}

	/**
	 * Return the runtime class name.
	 *
	 * @return java.lang.String
	 */
	public String getRuntimeClass() {
		return getTomcatVersionHandler().getRuntimeClass();
	}

	/**
	 * Return the program's runtime arguments to start or stop.
	 *
	 * @param boolean starting
	 * @return java.lang.String
	 */
	protected String[] getRuntimeProgramArguments(boolean starting) {
		IPath configPath = null;
		if (isTestEnvironment())
			configPath = getTempDirectory();
		return getTomcatVersionHandler().getRuntimeProgramArguments(configPath, isDebug(), starting);
	}

	/**
	 * Return the runtime (VM) arguments.
	 *
	 * @return java.lang.String
	 */
	protected String[] getRuntimeVMArguments() {
		IPath configPath = null;
		if (isTestEnvironment())
			configPath = getTempDirectory();
		return getTomcatVersionHandler().getRuntimeVMArguments(server.getRuntime().getLocation(), configPath, isSecure());
	}

	/**
	 * Obtain a temporary directory if this server doesn't
	 * already have one. Otherwise, return the existing one.
	 * @return java.io.File
	 */
	public IPath getTempDirectory() {
		if (tempDirectory == null)
			tempDirectory = server.getTempDirectory();
		return tempDirectory;
	}

	/**
	 * Returns true if the process is set to run in debug mode.
	 * This feature only works with Tomcat v4.0.
	 *
	 * @return boolean
	 */
	public boolean isDebug() {
		return server.getAttribute(PROPERTY_DEBUG, false);
	}

	/**
	 * Returns true if this is a test (run code out of the workbench) server.
	 *
	 * @return boolean
	 */
	public boolean isTestEnvironment() {
		return server.getAttribute(PROPERTY_TEST_ENVIRONMENT, false);
	}

	/**
	 * Returns true if the process is set to run in secure mode.
	 *
	 * @return boolean
	 */
	public boolean isSecure() {
		return server.getAttribute(PROPERTY_SECURE, false);
	}
	
	protected static String renderCommandLine(String[] commandLine, String separator) {
		if (commandLine == null || commandLine.length < 1)
			return "";
		StringBuffer buf= new StringBuffer(commandLine[0]);
		for (int i = 1; i < commandLine.length; i++) {
			buf.append(separator);
			buf.append(commandLine[i]);
		}	
		return buf.toString();
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
		server.setServerState(IServer.SERVER_STOPPED);
	}

	/**
	 * Methods called to notify that publishing is about to begin.
	 * This allows the server to open a connection to the server
	 * or get any global information ready.
	 *
	 * @param monitor org.eclipse.core.runtime.IProgressMonitor
	 */
	public IStatus publishStart(IProgressMonitor monitor) {
		return new Status(IStatus.OK, TomcatPlugin.PLUGIN_ID, 0, TomcatPlugin.getResource("%publishingStarted"), null);
	}
	
	public IStatus publishConfiguration(IProgressMonitor monitor) {
		IPath confDir = null;
		if (isTestEnvironment()) {
			confDir = getTempDirectory();
			File temp = confDir.append("conf").toFile();
			if (!temp.exists())
				temp.mkdirs();
		} else
			confDir = server.getRuntime().getLocation();
		return getTomcatConfiguration().backupAndPublish(confDir, !isTestEnvironment(), monitor);
	}

	/**
	 * Methods called to notify that publishing has finished.
	 * The server can close any open connections to the server
	 * and do any cleanup operations.
	 *
	 * @param monitor org.eclipse.core.runtime.IProgressMonitor
	 */
	public IStatus publishStop(IProgressMonitor monitor) {
		server.setConfigurationSyncState(IServer.SYNC_STATE_IN_SYNC);
		return new Status(IStatus.OK, TomcatPlugin.PLUGIN_ID, 0, TomcatPlugin.getResource("%publishingStopped"), null);
	}

	/**
	 * Return true if the server should be terminated before the workbench
	 * shutdown and false if not. If the server is not terminated when
	 * workbench shutdown, then the server should get reconnected
	 * in the server load when the workbench startsup.
	 * 
	 * @return boolean
	 **/
	public boolean isTerminateOnShutdown() {
		return true;
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
		IStatus status = getTomcatRuntime().validate();
		if (status != null && !status.isOK())
			throw new CoreException(status);

		//setRestartNeeded(false);
		TomcatConfiguration configuration = getTomcatConfiguration();
	
		// check that ports are free
		Iterator iterator = configuration.getServerPorts().iterator();
		while (iterator.hasNext()) {
			IServerPort sp = (IServerPort) iterator.next();
			if (SocketUtil.isPortInUse(sp.getPort(), 5))
				throw new CoreException(new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, TomcatPlugin.getResource("%errorPortInUse", new String[] {sp.getPort() + "", sp.getName()}), null));
		}
		
		server.setServerState(IServer.SERVER_STARTING);
	
		// ping server to check for startup
		try {
			String url = "http://localhost";
			int port = configuration.getMainPort().getPort();
			if (port != 80)
				url += ":" + port;
			ping = new PingThread(this, server, url, launchMode);
			ping.start();
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Can't ping for Tomcat startup.");
		}
	}

	/**
	 * Cleanly shuts down and terminates the server.
	 */
	public void stop() {
		byte state = server.getServerState();
		if (state == IServer.SERVER_STOPPED)
			return;
		else if (state == IServer.SERVER_STARTING || state == IServer.SERVER_STOPPING) {
			terminate();
			return;
		}

		try {
			Trace.trace(Trace.FINER, "Stopping Tomcat");
			if (state != IServer.SERVER_STOPPED)
				server.setServerState(IServer.SERVER_STOPPING);
	
			ILaunchConfiguration launchConfig = server.getLaunchConfiguration(true);
			ILaunchConfigurationWorkingCopy wc = launchConfig.getWorkingCopy();
			
			String args = renderCommandLine(getRuntimeProgramArguments(false), " ");
			wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, args);
			wc.setAttribute(ATTR_STOP, "true");
			wc.launch(ILaunchManager.RUN_MODE, new NullProgressMonitor());
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error stopping Tomcat", e);
		}
	}

	/**
	 * Terminates the server.
	 */
	public void terminate() {
		if (server.getServerState() == IServer.SERVER_STOPPED)
			return;

		try {
			server.setServerState(IServer.SERVER_STOPPING);
			Trace.trace(Trace.FINER, "Killing the Tomcat process");
			if (process != null && !process.isTerminated()) {
				process.terminate();
				stopImpl();
			}
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error killing the process", e);
		}
	}
	
	public int getStartTimeout() {
		return 45000;
	}
	
	public int getStopTimeout() {
		return 10000;
	}

	/**
	 * Return a string representation of this object.
	 * @return java.lang.String
	 */
	public String toString() {
		return "TomcatServer";
	}

	/**
	 * Update the given configuration in the server.
	 * (i.e. publish any changes to the server, and restart if necessary)
	 * @param config org.eclipse.wst.server.core.model.IServerConfiguration
	 */
	public void updateConfiguration() {
		Trace.trace(Trace.FINEST, "Configuration updated " + this);
		//setConfigurationSyncState(SYNC_STATE_DIRTY);
		//setRestartNeeded(true);
	}

	/**
	 * Respond to updates within the project tree.
	 */
	public void updateModule(final IModule module, IModuleResourceDelta delta) { }

	public void setLaunchDefaults(ILaunchConfigurationWorkingCopy workingCopy) {
		ITomcatRuntime runtime = getTomcatRuntime();
		workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_INSTALL_TYPE, runtime.getVMInstallTypeId());
		workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_INSTALL_NAME, runtime.getVMInstall().getName());
		
		String[] args = getRuntimeProgramArguments(true);
		String args2 = renderCommandLine(args, " ");
		workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, args2);

		args = getRuntimeVMArguments();
		args2 = renderCommandLine(args, " ");
		workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, args2);
		
		List cp = runtime.getRuntimeClasspath();
		
		// add tools.jar to the path
		IVMInstall vmInstall = runtime.getVMInstall();
		if (vmInstall != null) {
			try {
				cp.add(JavaRuntime.newRuntimeContainerClasspathEntry(new Path(JavaRuntime.JRE_CONTAINER).append("org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType").append(vmInstall.getName()), IRuntimeClasspathEntry.BOOTSTRAP_CLASSES));
			} catch (Exception e) { }			
			
			IPath jrePath = new Path(vmInstall.getInstallLocation().getAbsolutePath());
			if (jrePath != null) {
				IPath toolsPath = jrePath.append("lib").append("tools.jar");
				if (toolsPath.toFile().exists()) {
					cp.add(JavaRuntime.newArchiveRuntimeClasspathEntry(toolsPath));
				}
			}
		}
		
		Iterator cpi = cp.iterator();
		List list = new ArrayList();
		while (cpi.hasNext()) {
			IRuntimeClasspathEntry entry = (IRuntimeClasspathEntry) cpi.next();
			try {
				list.add(entry.getMemento());
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "Could not resolve classpath entry: " + entry, e);
			}
		}
		workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_CLASSPATH, list);
		workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_DEFAULT_CLASSPATH, false);
	}

	/**
	 * Returns the child project(s) of this project. If this
	 * project contains other projects, it should list those
	 * projects. If not, it should return an empty list.
	 *
	 * @param project org.eclipse.core.resources.IProject
	 * @return java.util.List
	 */
	public List getChildModules(IModule project) {
		return new ArrayList(0);
	}

	/**
	 * Returns the parent project(s) of this project. When
	 * determining if a given project can run on a server
	 * configuration, this method will be used to find the
	 * actual project that will be run on the server. For
	 * instance, a Web project may return a list of Ear projects
	 * that it is contained in if the server only supports Ear
	 * projects.
	 *
	 * <p>If the given project will directly run on the server,
	 * it should just be returned.</p>
	 *
	 * @param project org.eclipse.core.resources.IProject
	 * @return java.util.List
	 */
	public List getParentModules(IModule module) throws CoreException {
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
	
	/**
	 * Returns the project references for projects that are in
	 * this configuration.
	 *
	 * @return java.lang.String[]
	 */
	public IModule[] getModules() {
		List list = new ArrayList();
		
		ITomcatConfiguration config = getTomcatConfiguration();
		if (config != null) {
			List modules = config.getWebModules();
			int size = modules.size();
			for (int i = 0; i < size; i++) {
				WebModule module = (WebModule) modules.get(i);
				
				String memento = module.getMemento();
				if (memento != null) {
					int index = memento.indexOf(":");
					if (index > 0) {
						String factoryId = memento.substring(0, index);
						String mem = memento.substring(index + 1);
						IModule module2 = ServerUtil.getModule(factoryId, mem);
						if (module2 != null)
							list.add(module2);
					}
				}
			}
		}
		
		IModule[] s = new IModule[list.size()];
		list.toArray(s);
		
		return s;
	}
	
	public byte getModuleState(IModule module) {
		return IServer.MODULE_STATE_STARTED;
	}

	/**
	 * Returns true if the given project is supported by this
	 * server, and false otherwise.
	 *
	 * @param project org.eclipse.core.resources.IProject
	 * @return boolean
	 */
	public IStatus canModifyModules(IModule[] add, IModule[] remove) {
		if (add != null) {
			int size = add.length;
			for (int i = 0; i < size; i++) {
				IModule module = add[i];
				if (!(module instanceof IWebModule))
					return new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, TomcatPlugin.getResource("%errorWebModulesOnly"), null);
				
				IStatus status = getTomcatVersionHandler().canAddModule((IWebModule) module);
				if (status != null && !status.isOK())
					return status;
			}
		}
		
		return new Status(IStatus.OK, TomcatPlugin.PLUGIN_ID, 0, "%canModifyModules", null);
	}

	/**
	 * Method called when changes to the modules or module factories
	 * within this configuration occur. Return any necessary commands to repair
	 * or modify the server configuration in response to these changes.
	 * 
	 * @param org.eclipse.wst.server.core.model.IModuleFactoryEvent[]
	 * @param org.eclipse.wst.server.core.model.IModuleEvent[]
	 * @return org.eclipse.wst.server.core.model.ITask[]
	 */
	public ITask[] getRepairCommands(IModuleFactoryEvent[] factoryEvent, IModuleEvent[] moduleEvent) {
		List list = new ArrayList();
		// check for Web modules being removed
		if (factoryEvent != null) {
			List modules = getTomcatConfiguration().getWebModules();
			int size = modules.size();
			for (int i = 0; i < size; i++) {
				WebModule module = (WebModule) modules.get(i);
				
				String memento = module.getMemento();
				if (memento != null) {
					boolean found = false;
					int index = memento.indexOf(":");
					String factoryId = memento.substring(0, index);
					String mem = memento.substring(index + 1);
					
					int size2 = factoryEvent.length;
					for (int j = 0; !found && j < size2; j++) {
						IModule[] removed = factoryEvent[j].getRemovedModules();
						if (removed != null) {
							int size3 = removed.length;
							for (int k = 0; !found && k < size3; k++) {
								if (removed[k] != null && removed[k].getFactoryId().equals(factoryId) &&
										removed[k].getId().equals(mem)) {
									list.add(new RemoveWebModuleTask(i));
									found = true;
								}
							}
						}
					}
				}
			}
		}
		
		// check for changing context roots
		if (moduleEvent != null) {
			int size2 = moduleEvent.length;
			for (int j = 0; j < size2; j++) {
				if (moduleEvent[j].getModule() instanceof IWebModule && moduleEvent[j].isChanged()) {
					IWebModule webModule = (IWebModule) moduleEvent[j].getModule();
					
					String contextRoot = webModule.getContextRoot();
					if (contextRoot != null && !contextRoot.startsWith("/"))
						contextRoot = "/" + contextRoot;
					
					List modules = getTomcatConfiguration().getWebModules();
					int size = modules.size();
					boolean found = false;
					for (int i = 0; !found && i < size; i++) {
						WebModule module = (WebModule) modules.get(i);
						
						String memento = module.getMemento();
						if (memento != null) {
							int index = memento.indexOf(":");
							String factoryId = memento.substring(0, index);
							String mem = memento.substring(index + 1);
							if (webModule.getFactoryId().equals(factoryId) && webModule.getId().equals(mem)) {
								if (!module.getPath().equals(contextRoot)) {
									list.add(new SetWebModulePathTask(i, contextRoot));
									found = true;
								}
							}
						}
					}
				}
			}
		}

		ITask[] commands = new ITask[list.size()];
		list.toArray(commands);
		return commands;
	}

	public List getServerPorts() {
		if (server.getServerConfiguration() == null)
			return new ArrayList();
		return getTomcatConfiguration().getServerPorts();
	}
}

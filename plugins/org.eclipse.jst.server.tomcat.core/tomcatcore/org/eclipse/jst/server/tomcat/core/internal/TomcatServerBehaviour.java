/**********************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.jst.server.tomcat.core.internal;

import java.io.File;
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
import org.eclipse.jst.server.core.IWebModule;
import org.eclipse.jst.server.tomcat.core.ITomcatRuntime;
import org.eclipse.jst.server.tomcat.core.ITomcatServerBehaviour;

import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.model.*;
import org.eclipse.wst.server.core.util.FileUtil;
import org.eclipse.wst.server.core.util.PingThread;
import org.eclipse.wst.server.core.util.SocketUtil;
/**
 * Generic Tomcat server.
 */
public class TomcatServerBehaviour extends ServerBehaviourDelegate implements ITomcatServerBehaviour {
	private static final String ATTR_STOP = "stop-server";
	
	protected transient IPath tempDirectory;

	// the thread used to ping the server to check for startup
	protected transient PingThread ping = null;
	protected transient IProcess process;
	protected transient IDebugEventSetListener processListener;

	/**
	 * TomcatServer.
	 */
	public TomcatServerBehaviour() {
		super();
	}
	
	public void initialize() {
		setModules(getServer().getModules());
	}

	public TomcatRuntime getTomcatRuntime() {
		if (getServer().getRuntime() == null)
			return null;
		
		return (TomcatRuntime) getServer().getRuntime().getAdapter(TomcatRuntime.class);
	}
	
	public ITomcatVersionHandler getTomcatVersionHandler() {
		if (getServer().getRuntime() == null)
			return null;

		return getTomcatRuntime().getVersionHandler();
	}
	
	public TomcatConfiguration getTomcatConfiguration() {
		return getTomcatServer().getTomcatConfiguration();
	}

	public TomcatServer getTomcatServer() {
		return (TomcatServer) getServer().getAdapter(TomcatServer.class);
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
		if (getTomcatServer().isTestEnvironment())
			configPath = getTempDirectory();
		return getTomcatVersionHandler().getRuntimeProgramArguments(configPath, getTomcatServer().isDebug(), starting);
	}

	/**
	 * Return the runtime (VM) arguments.
	 *
	 * @return java.lang.String
	 */
	protected String[] getRuntimeVMArguments() {
		IPath installPath = getServer().getRuntime().getLocation();
		IPath configPath = null;
		if (getTomcatServer().isTestEnvironment())
			configPath = getTempDirectory();
		else
			configPath = installPath;
		return getTomcatVersionHandler().getRuntimeVMArguments(installPath, configPath, getTomcatServer().isSecure());
	}

	/**
	 * Obtain a temporary directory if this server doesn't
	 * already have one. Otherwise, return the existing one.
	 * @return IPath
	 */
	public IPath getTempDirectory() {
		if (tempDirectory == null)
			tempDirectory = getServer().getTempDirectory();
		return tempDirectory;
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
		setServerState(IServer.STATE_STOPPED);
	}

	public void publishServer(int kind, IProgressMonitor monitor) throws CoreException {
		IPath installDir = getServer().getRuntime().getLocation();
		IPath confDir = null;
		if (getTomcatServer().isTestEnvironment()) {
			confDir = getTempDirectory();
			File temp = confDir.append("conf").toFile();
			if (!temp.exists())
				temp.mkdirs();
		} else
			confDir = installDir;

		monitor = ProgressUtil.getMonitorFor(monitor);
		monitor.beginTask(TomcatPlugin.getResource("%publishServerTask"), 500);
		
		IStatus status = getTomcatConfiguration().cleanupServer(confDir, installDir, ProgressUtil.getSubMonitorFor(monitor, 100));
		if (status != null && !status.isOK())
			throw new CoreException(status);
		
		status = getTomcatConfiguration().backupAndPublish(confDir, !getTomcatServer().isTestEnvironment(), ProgressUtil.getSubMonitorFor(monitor, 400));
		if (status != null && !status.isOK())
			throw new CoreException(status);
		
		monitor.done();
		
		setServerPublishState(IServer.PUBLISH_STATE_NONE);
		setModules(getServer().getModules());
	}

	/*
	 * Publishes the given module to the server.
	 */
	public void publishModule(int kind, int deltaKind, IModule[] parents, IModule module, IProgressMonitor monitor) {
		if (getTomcatServer().isTestEnvironment())
			return;

		IWebModule webModule = (IWebModule) module.getAdapter(IWebModule.class);
		IPath from = webModule.getLocation();
		IPath to = getServer().getRuntime().getLocation().append("webapps").append(webModule.getContextRoot());
		if (deltaKind == IServer.REMOVED)
			FileUtil.deleteDirectory(to.toFile(), monitor);
		else
			FileUtil.smartCopyDirectory(from.toOSString(), to.toOSString(), monitor);
		
		setModulePublishState(module, IServer.PUBLISH_STATE_NONE);
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
		if (status != null && status.getSeverity() == IStatus.ERROR)
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
		
		setServerState(IServer.STATE_STARTING);
		setMode(launchMode);
	
		// ping server to check for startup
		try {
			String url = "http://localhost";
			int port = configuration.getMainPort().getPort();
			if (port != 80)
				url += ":" + port;
			ping = new PingThread(getServer(), this, url, 50);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Can't ping for Tomcat startup.");
		}
	}

	/**
	 * Cleanly shuts down and terminates the server.
	 */
	public void stop(boolean force) {
		if (force) {
			terminate();
			return;
		}
		int state = getServer().getServerState();
		if (state == IServer.STATE_STOPPED)
			return;
		else if (state == IServer.STATE_STARTING || state == IServer.STATE_STOPPING) {
			terminate();
			return;
		}

		try {
			Trace.trace(Trace.FINER, "Stopping Tomcat");
			if (state != IServer.STATE_STOPPED)
				setServerState(IServer.STATE_STOPPING);
	
			ILaunchConfiguration launchConfig = getServer().getLaunchConfiguration(true, null);
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
	protected void terminate() {
		if (getServer().getServerState() == IServer.STATE_STOPPED)
			return;

		try {
			setServerState(IServer.STATE_STOPPING);
			Trace.trace(Trace.FINER, "Killing the Tomcat process");
			if (process != null && !process.isTerminated()) {
				process.terminate();
				stopImpl();
			}
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error killing the process", e);
		}
	}

	/**
	 * Return a string representation of this object.
	 * @return java.lang.String
	 */
	public String toString() {
		return "TomcatServer";
	}

	public void setupLaunchConfiguration(ILaunchConfigurationWorkingCopy workingCopy, IProgressMonitor monitor) throws CoreException {
		String existingProgArgs = workingCopy.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, (String)null);
		String progArgs = renderCommandLine(getRuntimeProgramArguments(true), " ");
		if (existingProgArgs == null || existingProgArgs.indexOf(progArgs) < 0)
			workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, progArgs);

		String existingVMArgs = workingCopy.getAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, (String)null);
		String vmArgs = renderCommandLine(getRuntimeVMArguments(), " ");
		if (existingVMArgs == null || existingVMArgs.indexOf(vmArgs) < 0)
			workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, vmArgs);
		
		ITomcatRuntime runtime = getTomcatRuntime();
		IVMInstall vmInstall = runtime.getVMInstall();
		if (vmInstall != null) {
			workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_INSTALL_TYPE, vmInstall.getVMInstallType().getId());
			workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_INSTALL_NAME, vmInstall.getName());
		}
		
		// add tools.jar to the path
		List cp = runtime.getRuntimeClasspath();
		if (vmInstall != null) {
			try {
				cp.add(JavaRuntime.newRuntimeContainerClasspathEntry(new Path(JavaRuntime.JRE_CONTAINER).append("org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType").append(vmInstall.getName()), IRuntimeClasspathEntry.BOOTSTRAP_CLASSES));
			} catch (Exception e) {
				// ignore
			}			
			
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
}
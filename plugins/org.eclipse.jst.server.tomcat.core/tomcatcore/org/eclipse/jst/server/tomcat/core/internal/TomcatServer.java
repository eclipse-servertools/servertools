/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.jst.server.tomcat.core.internal;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFolder;
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
import org.eclipse.jst.server.tomcat.core.ITomcatServerWorkingCopy;
import org.eclipse.jst.server.tomcat.core.WebModule;

import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.model.*;
import org.eclipse.wst.server.core.util.PingThread;
/**
 * Generic Tomcat server.
 */
public class TomcatServer extends ServerDelegate implements ITomcatServer, ITomcatServerWorkingCopy {
	protected transient IPath tempDirectory;

	// the thread used to ping the server to check for startup
	protected transient PingThread ping = null;
	protected transient IProcess process;
	protected transient IDebugEventSetListener processListener;
	
	protected transient TomcatConfiguration configuration;

	/**
	 * TomcatServer.
	 */
	public TomcatServer() {
		super();
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

	public ITomcatConfiguration getServerConfiguration() {
		return getTomcatConfiguration();
	}

	public TomcatConfiguration getTomcatConfiguration() {
		if (configuration == null) {
			IFolder folder = getServer().getServerConfiguration();
			/*IPath path = null;
			if (getServerWC() != null && getServerWC().getRuntime() != null)
				path = getServerWC().getRuntime().getLocation().append("conf");
			else if (getServer() != null && getServer().getRuntime() != null)
				path = getServer().getRuntime().getLocation().append("conf");
			else
				return null;*/
			
			String id = getServer().getServerType().getId();
			if (id.indexOf("32") > 0)
				configuration = new Tomcat32Configuration(folder);
			else if (id.indexOf("40") > 0)
				configuration = new Tomcat40Configuration(folder);
			else if (id.indexOf("41") > 0)
				configuration = new Tomcat41Configuration(folder);
			else if (id.indexOf("50") > 0)
				configuration = new Tomcat50Configuration(folder);
			else if (id.indexOf("55") > 0)
				configuration = new Tomcat55Configuration(folder);
			try {
				configuration.load(folder, null);
			} catch (CoreException ce) {
				// ignore
			}
		}
		return configuration;
	}

	public void importConfiguration(IRuntime runtime, IProgressMonitor monitor) {
		IPath path = runtime.getLocation().append("conf");
		
		String id = getServer().getServerType().getId();
		if (id.indexOf("32") > 0)
			configuration = new Tomcat32Configuration(null);
		else if (id.indexOf("40") > 0)
			configuration = new Tomcat40Configuration(null);
		else if (id.indexOf("41") > 0)
			configuration = new Tomcat41Configuration(null);
		else if (id.indexOf("50") > 0)
			configuration = new Tomcat50Configuration(null);
		else if (id.indexOf("55") > 0)
			configuration = new Tomcat55Configuration(null);
		try {
			configuration.load(path, monitor);
		} catch (CoreException ce) {
			// ignore
		}
	}

	public void saveConfiguration(IProgressMonitor monitor) throws CoreException {
		TomcatConfiguration config = getTomcatConfiguration();
		config.save(getServer().getServerConfiguration(), monitor);
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
	
			IFolder serverConfig = getServer().getServerConfiguration();
			if (serverConfig == null)
				return null;
	
			TomcatConfiguration config = getTomcatConfiguration();
			if (config == null)
				return null;
	
			String url = "http://localhost";
			int port = config.getMainPort().getPort();
			port = ServerCore.getServerMonitorManager().getMonitoredPort(getServer(), port, "web");
			if (port != 80)
				url += ":" + port;

			url += config.getWebModuleURL(module);
			
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
		return getTomcatVersionHandler().getRuntimeVMArguments(getServer().getRuntime().getLocation(), configPath, isSecure());
	}

	/**
	 * Obtain a temporary directory if this server doesn't
	 * already have one. Otherwise, return the existing one.
	 * @return java.io.File
	 */
	public IPath getTempDirectory() {
		if (tempDirectory == null)
			tempDirectory = getServer().getTempDirectory();
		return tempDirectory;
	}

	/**
	 * Returns true if the process is set to run in debug mode.
	 * This feature only works with Tomcat v4.0.
	 *
	 * @return boolean
	 */
	public boolean isDebug() {
		return getAttribute(PROPERTY_DEBUG, false);
	}

	/**
	 * Returns true if this is a test (run code out of the workbench) server.
	 *
	 * @return boolean
	 */
	public boolean isTestEnvironment() {
		return getAttribute(PROPERTY_TEST_ENVIRONMENT, false);
	}

	/**
	 * Returns true if the process is set to run in secure mode.
	 *
	 * @return boolean
	 */
	public boolean isSecure() {
		return getAttribute(PROPERTY_SECURE, false);
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
	/*public void updateConfiguration() {
		Trace.trace(Trace.FINEST, "Configuration updated " + this);
		//setConfigurationSyncState(SYNC_STATE_DIRTY);
		//setRestartNeeded(true);
	}*/

	/**
	 * Respond to updates within the project tree.
	 */
	//public void updateModule(final IModule module, IModuleResourceDelta delta) { }

	public void setLaunchDefaults(ILaunchConfigurationWorkingCopy workingCopy) {
		ITomcatRuntime runtime = getTomcatRuntime();
		IVMInstall vmInstall = runtime.getVMInstall();
		if (vmInstall != null) {
			workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_INSTALL_TYPE, vmInstall.getVMInstallType().getId());
			workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_INSTALL_NAME, vmInstall.getName());
		}
		
		String[] args = getRuntimeProgramArguments(true);
		String args2 = renderCommandLine(args, " ");
		workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, args2);

		args = getRuntimeVMArguments();
		args2 = renderCommandLine(args, " ");
		workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, args2);
		
		List cp = runtime.getRuntimeClasspath();
		
		// add tools.jar to the path
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

	/**
	 * Returns the child project(s) of this project. If this
	 * project contains other projects, it should list those
	 * projects. If not, it should return an empty list.
	 *
	 * @param project org.eclipse.core.resources.IProject
	 * @return java.util.List
	 */
	public IModule[] getChildModules(IModule project) {
		return new IModule[0];
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
	public IModule[] getParentModules(IModule module) throws CoreException {
		if (module.getAdapter(IWebModule.class) != null) {
			IStatus status = canModifyModules(new IModule[] { module }, null);
			if (status == null || !status.isOK())
				throw new CoreException(status);
			return new IModule[] { module };
		}
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
					IModule module2 = ServerUtil.getModule(memento);
					if (module2 != null)
						list.add(module2);
				}
			}
		}
		
		IModule[] s = new IModule[list.size()];
		list.toArray(s);
		
		return s;
	}
	
	public byte getModuleState(IModule module) {
		return IServer.STATE_STARTED;
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
				IWebModule webModule = (IWebModule) module.getAdapter(IWebModule.class);
				if (webModule == null)
					return new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, TomcatPlugin.getResource("%errorWebModulesOnly"), null);
				
				IStatus status = getTomcatVersionHandler().canAddModule(webModule);
				if (status != null && !status.isOK())
					return status;
			}
		}
		
		return new Status(IStatus.OK, TomcatPlugin.PLUGIN_ID, 0, "%canModifyModules", null);
	}

	public IServerPort[] getServerPorts() {
		if (getServer().getServerConfiguration() == null)
			return new IServerPort[0];
		
		List list = getTomcatConfiguration().getServerPorts();
		IServerPort[] sp = new IServerPort[list.size()];
		list.toArray(sp);
		return sp;
	}
	
	public void setDefaults() {
		setTestEnvironment(true);
	}
	
	/**
	 * Sets this process to debug mode. This feature only works
	 * with Tomcat v4.0.
	 *
	 * @param b boolean
	 */
	public void setDebug(boolean b) {
		setAttribute(PROPERTY_DEBUG, b);
	}

	/**
	 * Sets this process to secure mode.
	 * @param b boolean
	 */
	public void setSecure(boolean b) {
		setAttribute(PROPERTY_SECURE, b);
	}

	/**
	 * Sets this server to test environment mode.
	 * 
	 * @param b boolean
	 */
	public void setTestEnvironment(boolean b) {
		setAttribute(PROPERTY_TEST_ENVIRONMENT, b);
	}
	
	/**
	 * Add the given project to this configuration. The project
	 * has already been verified using isSupportedProject() and
	 * does not already exist in the configuration.
	 *
	 * @param ref java.lang.String
	 */
	public void modifyModules(IModule[] add, IModule[] remove, IProgressMonitor monitor) throws CoreException {
		IStatus status = canModifyModules(add, remove);
		if (status == null || !status.isOK())
			throw new CoreException(status);
		
		TomcatConfiguration config = getTomcatConfiguration();

		if (add != null) {
			int size = add.length;
			for (int i = 0; i < size; i++) {
				IModule module3 = add[i];
				IWebModule module = (IWebModule) module3.getAdapter(IWebModule.class);
				String contextRoot = module.getContextRoot();
				if (contextRoot != null && !contextRoot.startsWith("/"))
					contextRoot = "/" + contextRoot;
				WebModule module2 = new WebModule(contextRoot,
						module.getLocation().toOSString(), module3.getId(), true);
				config.addWebModule(-1, module2);
			}
		}
		
		if (remove != null) {
			int size2 = remove.length;
			for (int j = 0; j < size2; j++) {
				IModule module3 = remove[j];
				String memento = module3.getId();
				List modules = getTomcatConfiguration().getWebModules();
				int size = modules.size();
				for (int i = 0; i < size; i++) {
					WebModule module = (WebModule) modules.get(i);
					if (memento.equals(module.getMemento()))
						config.removeWebModule(i);
				}
			}
		}
		config.save(config.getFolder(), monitor);
	}
}
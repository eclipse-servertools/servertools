/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.internal;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;

import org.eclipse.wst.server.core.*;
/**
 * 
 */
public class ServerType implements IServerType, IOrdered {
	protected IConfigurationElement element;

	/**
	 * ServerType constructor comment.
	 */
	public ServerType(IConfigurationElement element) {
		super();
		this.element = element;
	}
	
	protected IConfigurationElement getElement() {
		return element;
	}

	/**
	 * Returns the id of this factory.
	 *
	 * @return java.lang.String
	 */
	public String getId() {
		return element.getAttribute("id");
	}
	
	public String getName() {
		return element.getAttribute("name");
	}

	public String getDescription() {
		return element.getAttribute("description");
	}
	
	public IRuntimeType getRuntimeType() {
		String typeId = element.getAttribute("runtimeTypeId");
		if (typeId == null)
			return null;
		return ServerCore.findRuntimeType(typeId);
	}

	public boolean hasRuntime() {
		String s = element.getAttribute("runtime");
		return "true".equals(s);
	}
	
	/**
	 * Returns the order.
	 *
	 * @return int
	 */
	public int getOrder() {
		try {
			String o = element.getAttribute("order");
			return Integer.parseInt(o);
		} catch (NumberFormatException e) {
			return -1;
		}
	}
	
	protected ILaunchConfigurationType getLaunchConfigurationType() {
		String launchConfigId = element.getAttribute("launchConfigId");
		if (launchConfigId == null)
			return null;
		ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
		return launchManager.getLaunchConfigurationType(launchConfigId);
	}
	
	/**
	 * Returns true if this server can start or may already be started
	 * in the given mode, and false if not. Uses the launchMode attribute,
	 * which may contain the strings "run", "debug", and/or "profile".
	 * 
	 * @param launchMode String
	 * @return boolean
	 */
	public boolean supportsLaunchMode(String launchMode) {
		ILaunchConfigurationType configType = getLaunchConfigurationType();
		if (configType == null) {
			String mode = element.getAttribute("launchModes");
			if (mode == null)
				return false;
			return mode.indexOf(launchMode) >= 0;
		}
		return configType.supportsMode(launchMode);
	}

	/*public IServerConfigurationType getServerConfigurationType() {
		String configurationTypeId = element.getAttribute("configurationTypeId");
		return ServerCore.findServerConfigurationType(configurationTypeId);
	}*/
	
	public boolean supportsRemoteHosts() {
		String hosts = element.getAttribute("supportsRemoteHosts");
		return (hosts != null && hosts.toLowerCase().equals("true"));
	}

	public byte getInitialState() {
		String stateString = element.getAttribute("initialState");
		if (stateString != null)
			stateString = stateString.toLowerCase();
		if ("stopped".equals(stateString))
			return IServer.STATE_STOPPED;
		else if ("started".equals(stateString))
			return IServer.STATE_STARTED;
		return IServer.STATE_UNKNOWN;
	}
	
	public int getServerStateSet() {
		String stateSet = element.getAttribute("stateSet");
		if (stateSet == null)
			return SERVER_STATE_SET_MANAGED;
		else if (stateSet.toLowerCase().indexOf("attach") >= 0)
			return SERVER_STATE_SET_ATTACHED;
		else if (stateSet.toLowerCase().indexOf("publish") >= 0)
			return SERVER_STATE_SET_PUBLISHED;
		else
			return SERVER_STATE_SET_MANAGED;
	}

	public boolean hasServerConfiguration() {
		return ("true".equalsIgnoreCase(element.getAttribute("hasConfiguration")));
	}

	public IServerWorkingCopy createServer(String id, IFile file, IRuntime runtime, IProgressMonitor monitor) throws CoreException {
		if (id == null || id.length() == 0)
			id = ServerPlugin.generateId();
		ServerWorkingCopy swc = new ServerWorkingCopy(id, file, runtime, this);
		swc.setDefaults(monitor);
		swc.setRuntime(runtime);
		
		// TODO
		if (swc.getServerType().hasServerConfiguration()) {
			IFolder folder = getServerProject().getFolder(swc.getName() + "-config");
			if (!folder.exists())
				folder.create(true, true, null);
			swc.setServerConfiguration(folder);
			
			((Server)swc).importConfiguration(runtime, null);
		}
		
		return swc;
	}
	
	/**
	 * Returns an array of all known runtime instances of
	 * the given runtime type. This convenience method filters the list of known
	 * runtime ({@link #getRuntimes()}) for ones with a matching
	 * runtime type ({@link IRuntime#getRuntimeType()}). The array will not
	 * contain any working copies.
	 * <p>
	 * A new array is returned on each call, so clients may store or modify the result.
	 * </p>
	 * 
	 * @param runtimeType the runtime type
	 * @return a possibly-empty list of runtime instances {@link IRuntime}
	 * of the given runtime type
	 */
	protected static IRuntime[] getRuntimes(IRuntimeType runtimeType) {
		List list = new ArrayList();
		IRuntime[] runtimes = ServerCore.getRuntimes();
		if (runtimes != null) {
			int size = runtimes.length;
			for (int i = 0; i < size; i++) {
				if (runtimes[i].getRuntimeType() != null && runtimes[i].getRuntimeType().equals(runtimeType))
					list.add(runtimes[i]);
			}
		}
		
		IRuntime[] r = new IRuntime[list.size()];
		list.toArray(r);
		return r;
	}

	public IServerWorkingCopy createServer(String id, IFile file, IProgressMonitor monitor) throws CoreException {
		if (id == null || id.length() == 0)
			id = ServerPlugin.generateId();
		
		IRuntime runtime = null;
		if (hasRuntime()) {
			// look for existing runtime
			IRuntimeType runtimeType = getRuntimeType();
			IRuntime[] runtimes = getRuntimes(runtimeType);
			if (runtimes != null && runtimes.length > 0)
				runtime = runtimes[0];
			else {
				// create runtime
				try {
					IRuntimeWorkingCopy runtimeWC = runtimeType.createRuntime(id + "-runtime", monitor);
					ServerUtil.setRuntimeDefaultName(runtimeWC);
					runtime = runtimeWC;
				} catch (Exception e) {
					Trace.trace(Trace.SEVERE, "Couldn't create runtime", e);
				}
			}
		}

		ServerWorkingCopy swc = new ServerWorkingCopy(id, file, runtime, this);
		ServerUtil.setServerDefaultName(swc);
		if (runtime != null)
			swc.setRuntime(runtime);
		
		if (swc.getServerType().hasServerConfiguration()) {
			// TODO: config
			IFolder folder = getServerProject().getFolder(swc.getName() + "-config");
			if (!folder.exists())
				folder.create(true, true, null);
			swc.setServerConfiguration(folder);
			
			((Server)swc).importConfiguration(runtime, null);
		}
		
		//TODO: import server config
		/* IServerConfigurationWorkingCopy config = null;
		if (hasServerConfiguration()) {
			if (runtime != null)
				config = getServerConfigurationType().importFromRuntime(id + "-config", file, runtime, monitor);
			if (config == null)
				config = getServerConfigurationType().createServerConfiguration(id + "-config", file, monitor);
			ServerUtil.setServerConfigurationDefaultName(config);
			if (config != null)
				swc.setServerConfiguration(config);
		}*/
		
		swc.setDefaults(monitor);
		
		return swc;
	}
	
	public static IProject getServerProject() throws CoreException {
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		if (projects != null) {
			int size = projects.length;
			for (int i = 0; i < size; i++) {
				if (ServerCore.getProjectProperties(projects[i]).isServerProject())
					return projects[i];
			}
		}
		
		String s = findUnusedServerProjectName();
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(s);
		project.create(null);
		project.open(null);
		return project;
	}

	/**
	 * Finds an unused project name to use as a server project.
	 * 
	 * @return java.lang.String
	 */
	protected static String findUnusedServerProjectName() {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		String name = ServerPlugin.getResource("%defaultServerProjectName", "");
		int count = 1;
		while (root.getProject(name).exists()) {
			name = ServerPlugin.getResource("%defaultServerProjectName", ++count + "");
		}
		return name;
	}
	
	/**
	 * Return the timeout (in ms) that should be used to wait for the server to start.
	 * Returns -1 if there is no timeout.
	 * 
	 * @return
	 */
	public int getStartTimeout() {
		try {
			return Integer.parseInt(element.getAttribute("startTimeout"));
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	/**
	 * Return the timeout (in ms) to wait before assuming that the server
	 * has failed to stop. Returns -1 if there is no timeout.
	 *  
	 * @return
	 */
	public int getStopTimeout() {
		try {
			return Integer.parseInt(element.getAttribute("stopTimeout"));
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	/**
	 * Return a string representation of this object.
	 * 
	 * @return java.lang.String
	 */
	public String toString() {
		return "ServerType[" + getId() + "]";
	}
}
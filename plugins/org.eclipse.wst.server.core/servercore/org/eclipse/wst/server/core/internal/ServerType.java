/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.internal;

import java.util.List;

import org.eclipse.core.resources.IFile;
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
public class ServerType implements IServerType {
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
		return ServerCore.getRuntimeType(element.getAttribute("runtimeTypeId"));
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
			else
				return mode.indexOf(launchMode) >= 0;
		} else
			return configType.supportsMode(launchMode);
	}

	public IServerConfigurationType getServerConfigurationType() {
		String configurationTypeId = element.getAttribute("configurationTypeId");
		return ServerCore.getServerConfigurationType(configurationTypeId);
	}
	
	public boolean supportsLocalhost() {
		String hosts = element.getAttribute("hosts");
		return (hosts == null || hosts.toLowerCase().indexOf("localhost") >= 0 
				|| hosts.indexOf("127.0.0.1") >= 0);
	}
	
	public boolean supportsRemoteHosts() {
		String hosts = element.getAttribute("hosts");
		return (hosts == null || hosts.toLowerCase().indexOf("remote") >= 0);
	}
	
	public byte getInitialState() {
		byte state = IServer.SERVER_UNKNOWN;
		String stateString = element.getAttribute("initialState");
		if ("stopped".equals(stateString))
			state = IServer.SERVER_STOPPED;
		else if ("started".equals(stateString))
			state = IServer.SERVER_STARTED;
		return state;
	}

	/**
	 * Returns an IStatus message to verify if a server of this type will be able
	 * to run the module immediately after being created, without any user
	 * interaction. If OK, this server may be used as a default server. This
	 * method should return ERROR if the user must supply any information to
	 * configure the server correctly, or if the module is not supported.
	 *
	 * @return org.eclipse.core.resources.IStatus
	 */
	/*public IStatus isDefaultAvailable(IModule module) {
		return null;
	}*/
	
	public byte getServerStateSet() {
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
		String configurationTypeId = element.getAttribute("configurationTypeId");
		return configurationTypeId != null && configurationTypeId.length() > 0;
	}
	
	public boolean isMonitorable() {
		return "true".equalsIgnoreCase(element.getAttribute("monitorable"));
	}
	
	public boolean isTestEnvironment() {
		return "true".equalsIgnoreCase(element.getAttribute("testEnvironment"));
	}
	
	public IServerWorkingCopy createServer(String id, IFile file, IRuntime runtime) {
		if (id == null || id.length() == 0)
			id = ServerPlugin.generateId();
		ServerWorkingCopy swc = new ServerWorkingCopy(id, file, runtime, this);
		swc.setDefaults();
		return swc;
	}

	public IServerWorkingCopy createServer(String id, IFile file, IProgressMonitor monitor) throws CoreException {
		if (id == null || id.length() == 0)
			id = ServerPlugin.generateId();
		
		IRuntime runtime = null;
		if (hasRuntime()) {
			// look for existing runtime
			IRuntimeType runtimeType = getRuntimeType();
			List list = ServerCore.getResourceManager().getRuntimes(runtimeType);
			if (!list.isEmpty()) {
				runtime = (IRuntime) list.get(0);
			} else {
				// create runtime
				try {
					IRuntimeWorkingCopy runtimeWC = runtimeType.createRuntime(id + "-runtime");
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
		
		IServerConfigurationWorkingCopy config = null;
		if (hasServerConfiguration()) {
			if (runtime != null)
				config = getServerConfigurationType().importFromRuntime(id + "-config", file, runtime, monitor);
			if (config == null)
				config = getServerConfigurationType().createServerConfiguration(id + "-config", file, monitor);
			ServerUtil.setServerConfigurationDefaultName(config);
			if (config != null)
				swc.setServerConfiguration(config);
		}
		
		swc.setDefaults();
		
		return swc;
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
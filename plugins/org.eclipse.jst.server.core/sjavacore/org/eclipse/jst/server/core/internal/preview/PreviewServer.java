/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.core.internal.preview;

import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jst.server.core.internal.JavaServerPlugin;
import org.eclipse.jst.server.core.internal.Messages;
import org.eclipse.jst.server.core.internal.Trace;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerType;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.ServerPort;
import org.eclipse.wst.server.core.model.IURLProvider;
import org.eclipse.wst.server.core.model.ServerDelegate;
/**
 * Generic HTTP server.
 */
public class PreviewServer extends ServerDelegate implements IURLProvider {
	public static final String ID = "org.eclipse.jst.server.core.preview.serverType";
	public static final String PROPERTY_PORT = "port";

	/**
	 * PreviewServer.
	 */
	public PreviewServer() {
		super();
	}

	protected void initialize() {
		// do nothing
	}

	public PreviewRuntime getPreviewRuntime() {
		if (getServer().getRuntime() == null)
			return null;
		
		return (PreviewRuntime) getServer().getRuntime().loadAdapter(PreviewRuntime.class, null);
	}

	/**
	 * Return the root URL of this module.
	 * 
	 * @param module org.eclipse.wst.server.core.model.IModule
	 * @return java.net.URL
	 */
	public URL getModuleRootURL(IModule module) {
		try {
			String base = "http://localhost";
			
			int port = getPort();
			if (port == 80)
				return new URL(base + "/");
			
			return new URL(base + ":" + port + "/");
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Could not get root URL", e);
			return null;
		}
	}

	/*
	 * Returns the child module(s) of this module.
	 */
	public IModule[] getChildModules(IModule[] module) {
		return new IModule[0];
	}

	/*
	 * Returns the root module(s) of this module.
	 */
	public IModule[] getRootModules(IModule module) throws CoreException {
		return new IModule[] { module };
	}

	/**
	 * Returns true if the given project is supported by this server, and false
	 * otherwise.
	 * 
	 * @param add modules
	 * @param remove modules
	 * @return the status
	 */
	public IStatus canModifyModules(IModule[] add, IModule[] remove) {
		return new Status(IStatus.OK, JavaServerPlugin.PLUGIN_ID, 0, Messages.canModifyModules, null);
	}

	public ServerPort[] getServerPorts() {
		int port = getPort();
		ServerPort[] ports = { new ServerPort("http", Messages.httpPort, port, "http") };
		return ports;
	}

	public int getPort() {
		return getAttribute(PreviewServer.PROPERTY_PORT, 80);
	}

	public void setPort(int port) {
		setAttribute(PreviewServer.PROPERTY_PORT, port);
	}

	public static IServer createPreviewServer(String serverName) {
		try {
			NullProgressMonitor monitor = new NullProgressMonitor();
			IRuntimeType runtimeType = ServerCore.findRuntimeType(PreviewRuntime.ID);
			IRuntimeWorkingCopy runtimeCopy = runtimeType.createRuntime(PreviewRuntime.ID, monitor);
			IRuntime runtime = runtimeCopy.save(true, monitor);
			
			IServerType serverType = ServerCore.findServerType(ID);
			IServerWorkingCopy workingCopy = serverType.createServer(ID, null, runtime, monitor);
			workingCopy.setName(serverName);
			workingCopy.setHost("localhost");
			return workingCopy.save(true, monitor);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error creating server", e);
		}
		
		return null;
	}

	public static IServer findPreviewServer(String id) {
		IServer[] servers = ServerCore.getServers();
		for (int i = 0; i < servers.length; i++) {
			if (servers[i].getId().equals(id)) {
				return servers[i];
			}
		}
		return null;
	}

	public void modifyModules(IModule[] add, IModule[] remove, IProgressMonitor monitor) throws CoreException {
		// do nothing
	}

	/**
	 * Return a string representation of this object.
	 * 
	 * @return java.lang.String
	 */
	public String toString() {
		return "PreviewServer";
	}
}
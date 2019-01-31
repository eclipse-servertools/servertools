/*******************************************************************************
 * Copyright (c) 2007, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.http.core.internal;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.model.IModuleResource;
import org.eclipse.wst.server.core.model.ServerBehaviourDelegate;
import org.eclipse.wst.server.core.util.IStaticWeb;
import org.eclipse.wst.server.core.util.PublishHelper;
/**
 * Generic HTTP server implementation.
 */
public class HttpServerBehaviour extends ServerBehaviourDelegate {
	// the thread used to ping the server to check for startup
	protected transient PingThread ping = null;

	/**
	 * HttpServer.
	 */
	public HttpServerBehaviour() {
		super();
	}

	public void initialize(IProgressMonitor monitor) {
		// do nothing
	}

	public HttpRuntime getHttpRuntime() {
		if (getServer().getRuntime() == null)
			return null;

		return (HttpRuntime) getServer().getRuntime().loadAdapter(HttpRuntime.class, null);
	}

	public HttpServer getHttpServer() {
		return (HttpServer) getServer().getAdapter(HttpServer.class);
	}

	protected void setServerStarted() {
		setServerState(IServer.STATE_STARTED);
	}

	protected void publishServer(int kind, IProgressMonitor monitor) throws CoreException {
		monitor = ProgressUtil.getMonitorFor(monitor);
		monitor.done();

		setServerPublishState(IServer.PUBLISH_STATE_NONE);
	}

	/*
	 * Publishes the given module to the server.
	 */
	protected void publishModule(int kind, int deltaKind, IModule[] moduleTree, IProgressMonitor monitor) throws CoreException {
		if (!getHttpServer().isPublishing())
			return;
		
		String contextRoot = null;
		IModule module = moduleTree[moduleTree.length - 1]; 
		IStaticWeb sw = (IStaticWeb) module.loadAdapter(IStaticWeb.class, monitor);
		if (sw != null)
			contextRoot = sw.getContextRoot();
		else
			contextRoot = module.getName();
		
		IPath to = getServer().getRuntime().getLocation();
		File temp = null;
		try {
			if (to.removeLastSegments(1).toFile().exists())
				temp = to.removeLastSegments(1).append("temp").toFile();
		} catch (Exception e) {
			// ignore - use null temp folder
		}
		if (contextRoot != null && !contextRoot.equals(""))
			to = to.append(contextRoot);
		
		IModuleResource[] res = getResources(moduleTree);
		PublishHelper pubHelper = new PublishHelper(temp);
		IStatus[] status = pubHelper.publishSmart(res, to, monitor);
		if (temp.exists())
			temp.delete();
		throwException(status);
		
		setModulePublishState(moduleTree, IServer.PUBLISH_STATE_NONE);
	}

	/**
	 * Utility method to throw a CoreException based on the contents of a list of
	 * error and warning status.
	 * 
	 * @param status a List containing error and warning IStatus
	 * @throws CoreException
	 */
	private static void throwException(IStatus[] status) throws CoreException {
		if (status == null || status.length == 0)
			return;
		
		if (status.length == 1)
			throw new CoreException(status[0]);
		
		String message = Messages.errorPublish;
		MultiStatus status2 = new MultiStatus(HttpCorePlugin.PLUGIN_ID, 0, status, message, null);
		throw new CoreException(status2);
	}

	public void restart(String launchMode) throws CoreException {
		setServerState(IServer.STATE_STOPPED);
		setServerState(IServer.STATE_STARTED);
	}

	/**
	 * Cleanly shuts down and terminates the server.
	 * 
	 * @param force <code>true</code> to kill the server
	 */
	public void stop(boolean force) {
		setServerState(IServer.STATE_STOPPED);
	}

	/**
	 * Return a string representation of this object.
	 * 
	 * @return java.lang.String
	 */
	public String toString() {
		return "HttpServer";
	}
}
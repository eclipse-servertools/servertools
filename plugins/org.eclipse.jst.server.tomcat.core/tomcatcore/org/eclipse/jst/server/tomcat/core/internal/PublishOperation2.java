/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.tomcat.core.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.*;
import org.eclipse.jst.server.core.PublishUtil;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.model.*;
/**
 * Tomcat publish helper.
 */
public class PublishOperation2 extends PublishOperation {
	protected TomcatServerBehaviour server;
	protected IModule[] module;
	protected int kind;
	protected int deltaKind;

	public PublishOperation2(TomcatServerBehaviour server, int kind, IModule[] module, int deltaKind) {
		super("Publish to server", "Publish Web module to Tomcat server");
		this.server = server;
		this.module = module;
		this.kind = kind;
		this.deltaKind = deltaKind;
	}

	public int getOrder() {
		return 0;
	}

	public int getKind() {
		return REQUIRED;
	}

	public void execute(IProgressMonitor monitor, IAdaptable info) throws CoreException {
		List status = new ArrayList();
		if (module.length == 1) { // web module
			publishDir(module[0], status, monitor);
		} else { // utility module
			publishJar(status, monitor);
		}
		throwException(status);
		server.setModulePublishState2(module, IServer.PUBLISH_STATE_NONE);
	}

	private void publishDir(IModule module2, List status, IProgressMonitor monitor) throws CoreException {
		IPath path = server.getTempDirectory().append("webapps");
		path = path.append(module2.getName());
		
		if (kind == IServer.PUBLISH_CLEAN || deltaKind == ServerBehaviourDelegate.REMOVED) { // clean and republish from scratch
			File f = path.toFile();
			if (f.exists()) {
				IStatus[] stat = PublishUtil.deleteDirectory(f, monitor);
				addArrayToList(status, stat);
			}
			
			if (deltaKind == ServerBehaviourDelegate.REMOVED)
				return;
		}
		
		if (kind == IServer.PUBLISH_CLEAN || kind == IServer.PUBLISH_FULL) {
			IModuleResource[] mr = server.getResources(module);
			IStatus[] stat = PublishUtil.publishFull(mr, path, monitor);
			addArrayToList(status, stat);
			return;
		}
		
		IModuleResourceDelta[] delta = server.getPublishedResourceDelta(module);
		
		int size = delta.length;
		for (int i = 0; i < size; i++) {
			IStatus[] stat = PublishUtil.publishDelta(delta[i], path, monitor);
			addArrayToList(status, stat);
		}
	}

	private void publishJar(List status, IProgressMonitor monitor) throws CoreException {
		IPath path = server.getTempDirectory().append("webapps");
		path = path.append(module[0].getName()).append("WEB-INF").append("lib");
		IPath jarPath = path.append(module[1].getName() + ".jar");
		
		if (kind == IServer.PUBLISH_CLEAN || deltaKind == ServerBehaviourDelegate.REMOVED) { // clean and republish from scratch
			if (jarPath.toFile().exists())
				jarPath.toFile().delete();
			
			if (deltaKind == ServerBehaviourDelegate.REMOVED)
				return;
		}
		if (kind != IServer.PUBLISH_CLEAN && kind != IServer.PUBLISH_FULL) {
			// avoid changes if no changes to module since last publish
			IModuleResourceDelta[] delta = server.getPublishedResourceDelta(module);
			if (delta == null || delta.length == 0)
				return;
		}
		
		// make directory if it doesn't exist
		if (!path.toFile().exists())
			path.toFile().mkdirs();
		
		IModuleResource[] mr = server.getResources(module);
		IStatus[] stat = PublishUtil.publishZip(mr, jarPath, monitor);
		addArrayToList(status, stat);
	}

	/**
	 * Utility method to throw a CoreException based on the contents of a list of
	 * error and warning status.
	 * 
	 * @param status a List containing error and warning IStatus
	 * @throws CoreException
	 */
	protected static void throwException(List status) throws CoreException {
		if (status == null)
			status = new ArrayList();
		
		if (status == null || status.size() == 0)
			return;
		if (status.size() == 1) {
			IStatus status2 = (IStatus) status.get(0);
			throw new CoreException(status2);
		}
		IStatus[] children = new IStatus[status.size()];
		status.toArray(children);
		String message = Messages.errorPublish;
		MultiStatus status2 = new MultiStatus(TomcatPlugin.PLUGIN_ID, 0, children, message, null);
		throw new CoreException(status2);
	}

	protected static void addArrayToList(List list, IStatus[] a) {
		if (list == null || a == null || a.length == 0)
			return;
		
		int size = a.length;
		for (int i = 0; i < size; i++)
			list.add(a[i]);
	}
}
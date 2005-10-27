/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.tomcat.core.internal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jst.server.core.PublishUtil;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.model.IModuleResource;
import org.eclipse.wst.server.core.model.IModuleResourceDelta;
import org.eclipse.wst.server.core.model.PublishOperation;
import org.eclipse.wst.server.core.util.ProjectModule;

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
		if (module.length == 1) { // web module
			publishDir(module[0], monitor);
		} else { // utility module
			publishJar(monitor);
		}
	}

	private void publishDir(IModule module2, IProgressMonitor monitor) throws CoreException {
		IPath path = server.getTempDirectory().append("webapps");
		path = path.append(module2.getName());
		
		if (kind == IServer.PUBLISH_CLEAN) { // clean and republish from scratch
			PublishUtil.deleteDirectory(path.toFile(), monitor);
		}
		
		if (kind == IServer.PUBLISH_CLEAN || kind == IServer.PUBLISH_FULL) {
			ProjectModule pm = (ProjectModule) module2.loadAdapter(ProjectModule.class, monitor);
			IModuleResource[] mr = pm.members();
			PublishUtil.copy(mr, path);
			return;
		}
		
		IModuleResourceDelta[] delta = server.getPublishedResourceDelta(module);
		
		int size = delta.length;
		for (int i = 0; i < size; i++) {
			PublishUtil.handleDelta(kind, path, delta[i]);
		}
	}

	private void publishJar(IProgressMonitor monitor) throws CoreException {
		IModule module2 = null;
		IPath path = server.getTempDirectory().append("webapps");
		path = path.append(module[0].getName()).append("WEB-INF").append("lib").append(module[1].getName() + ".jar");
		module2 = module[1];
		
		if (kind == IServer.PUBLISH_CLEAN) { // clean and republish from scratch
			path.toFile().delete();
		}
		
		ProjectModule pm = (ProjectModule) module2.loadAdapter(ProjectModule.class, monitor);
		IModuleResource[] mr = pm.members();
		PublishUtil.createZipFile(mr, path);
		
		/*if (kind == IServer.PUBLISH_CLEAN || kind == IServer.PUBLISH_FULL) {
			ProjectModule pm = (ProjectModule) module2.loadAdapter(ProjectModule.class, monitor);
			IModuleResource[] mr = pm.members();
			PublishUtil.copy(mr, path);
			return;
		}*/
		
		/*IModuleResourceDelta[] delta = server.getPublishedResourceDelta(module);
		
		int size = delta.length;
		for (int i = 0; i < size; i++) {
			PublishUtil.handleDelta(kind, path, delta[i]);
		}*/
	}
}
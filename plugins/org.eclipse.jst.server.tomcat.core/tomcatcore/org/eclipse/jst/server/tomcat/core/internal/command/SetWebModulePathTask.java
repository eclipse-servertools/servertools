/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.tomcat.core.internal.command;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jst.server.tomcat.core.internal.TomcatConfiguration;
import org.eclipse.jst.server.tomcat.core.internal.TomcatPlugin;
import org.eclipse.jst.server.tomcat.core.internal.TomcatServer;
import org.eclipse.jst.server.tomcat.core.internal.WebModule;

import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.TaskModel;
import org.eclipse.wst.server.core.util.Task;
/**
 * Task to modify the path of a Web module.
 */
public class SetWebModulePathTask extends Task {
	protected int index;
	protected WebModule oldModule;
	protected String path;

	/**
	 * SetWebModulePathTask constructor comment.
	 * 
	 * @param index
	 * @param contextRoot
	 */
	public SetWebModulePathTask(int index, String contextRoot) {
		super();
		this.index = index;
		this.path = contextRoot;
	}

	/**
	 * Execute the command.
	 * 
	 * @param monitor a progress monitor
	 * @throws CoreException
	 */
	public void execute(IProgressMonitor monitor) throws CoreException {
		IServerWorkingCopy wc = (IServerWorkingCopy) getTaskModel().getObject(TaskModel.TASK_SERVER);
		TomcatServer server = (TomcatServer) wc.getAdapter(TomcatServer.class);
		TomcatConfiguration configuration = server.getTomcatConfiguration();
		oldModule = (WebModule) configuration.getWebModules().get(index);
		configuration.removeWebModule(index);
		
		WebModule module = new WebModule(path, oldModule.getDocumentBase(), oldModule.getMemento(), oldModule.isReloadable());
		configuration.addWebModule(index, module);
	}

	/**
	 * Returns this command's description.
	 * 
	 * @return String
	 */
	public String getDescription() {
		if (oldModule == null) {
			try {
				IServerWorkingCopy wc = (IServerWorkingCopy) getTaskModel().getObject(TaskModel.TASK_SERVER);
				TomcatServer server = (TomcatServer) wc.getAdapter(TomcatServer.class);
				TomcatConfiguration configuration = server.getTomcatConfiguration();
				oldModule = (WebModule) configuration.getWebModules().get(index);
			} catch (Exception e) {
				// ignore
			}
		}
		
		if (oldModule == null)
			return TomcatPlugin.getResource("%configurationEditorActionEditWebModuleDescription", "<>", path);
		
		return TomcatPlugin.getResource("%configurationEditorActionEditWebModuleDescription", oldModule.getPath(), path);
	}

	/**
	 * Returns this command's name.
	 * 
	 * @return String
	 */
	public String getName() {
		return TomcatPlugin.getResource("%configurationEditorActionEditWebModulePath");
	}

	/**
	 * Undo the command.
	 */
	public void undo() {
		try {
			IServerWorkingCopy wc = (IServerWorkingCopy) getTaskModel().getObject(TaskModel.TASK_SERVER);
			TomcatServer server = (TomcatServer) wc.getAdapter(TomcatServer.class);
			TomcatConfiguration configuration = server.getTomcatConfiguration();
			configuration.removeWebModule(index);
			configuration.addWebModule(index, oldModule);
		} catch (Exception e) {
			// ignore
		}
	}
}
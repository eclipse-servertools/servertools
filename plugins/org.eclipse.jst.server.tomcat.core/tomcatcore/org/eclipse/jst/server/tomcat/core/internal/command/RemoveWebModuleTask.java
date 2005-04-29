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
import org.eclipse.jst.server.tomcat.core.internal.Messages;
import org.eclipse.jst.server.tomcat.core.internal.TomcatConfiguration;
import org.eclipse.jst.server.tomcat.core.internal.TomcatServer;
import org.eclipse.jst.server.tomcat.core.internal.WebModule;
import org.eclipse.osgi.util.NLS;

import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.TaskModel;
import org.eclipse.wst.server.core.util.Task;
/**
 * Task to remove a web module.
 */
public class RemoveWebModuleTask extends Task {
	protected int index;
	protected WebModule module;

	/**
	 * RemoveWebModuleTask constructor comment.
	 * 
	 * @param index
	 */
	public RemoveWebModuleTask(int index) {
		super();
		this.index = index;
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
		if (configuration.getWebModules().size() <= index)
			return;
		module = (WebModule) configuration.getWebModules().get(index);
		configuration.removeWebModule(index);
	}

	/**
	 * Returns this command's description.
	 * 
	 * @return String
	 */
	public String getDescription() {
		if (module == null) {
			try {
				IServerWorkingCopy wc = (IServerWorkingCopy) getTaskModel().getObject(TaskModel.TASK_SERVER);
				TomcatServer server = (TomcatServer) wc.getAdapter(TomcatServer.class);
				TomcatConfiguration configuration = server.getTomcatConfiguration();
				module = (WebModule) configuration.getWebModules().get(index);
			} catch (Exception e) {
				// ignore
			}
		}
		if (module == null)
			return NLS.bind(Messages.configurationEditorActionRemoveWebModuleDescription, "<>");
		
		return NLS.bind(Messages.configurationEditorActionRemoveWebModuleDescription, module.getPath());
	}

	/**
	 * Returns this command's name.
	 * 
	 * @return String
	 */
	public String getName() {
		return Messages.configurationEditorActionRemoveWebModule;
	}

	/**
	 * Undo the command.
	 */
	public void undo() {
		try {
			IServerWorkingCopy wc = (IServerWorkingCopy) getTaskModel().getObject(TaskModel.TASK_SERVER);
			TomcatServer server = (TomcatServer) wc.getAdapter(TomcatServer.class);
			TomcatConfiguration configuration = server.getTomcatConfiguration();
			configuration.addWebModule(index, module);
		} catch (Exception e) {
			// ignore
		}
	}
}
/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.jst.server.tomcat.core.internal.command;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jst.server.tomcat.core.internal.TomcatConfiguration;
import org.eclipse.jst.server.tomcat.core.internal.TomcatPlugin;
import org.eclipse.jst.server.tomcat.core.internal.TomcatServer;
import org.eclipse.jst.server.tomcat.core.internal.WebModule;

import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.ITaskModel;
import org.eclipse.wst.server.core.util.Task;
/**
 * Task to remove a web module.
 */
public class RemoveWebModuleTask extends Task {
	protected int index;
	protected WebModule module;

	/**
	 * RemoveWebModuleTask constructor comment.
	 */
	public RemoveWebModuleTask(int index) {
		super();
		this.index = index;
	}

	/**
	 * Execute the command.
	 * @return boolean
	 */
	public void execute(IProgressMonitor monitor) throws CoreException {
		IServerWorkingCopy wc = (IServerWorkingCopy) getTaskModel().getObject(ITaskModel.TASK_SERVER);
		TomcatServer server = (TomcatServer) wc.getAdapter(TomcatServer.class);
		TomcatConfiguration configuration = server.getTomcatConfiguration();
		if (configuration.getWebModules().size() <= index)
			return;
		module = (WebModule) configuration.getWebModules().get(index);
		configuration.removeWebModule(index);
	}

	/**
	 * Returns this command's description.
	 * @return java.lang.String
	 */
	public String getDescription() {
		if (module == null) {
			IServerWorkingCopy wc = (IServerWorkingCopy) getTaskModel().getObject(ITaskModel.TASK_SERVER);
			TomcatServer server = (TomcatServer) wc.getAdapter(TomcatServer.class);
			TomcatConfiguration configuration = server.getTomcatConfiguration();
			module = (WebModule) configuration.getWebModules().get(index);
		}
		return TomcatPlugin.getResource("%configurationEditorActionRemoveWebModuleDescription", module.getPath());
	}

	/**
	 * Returns this command's label.
	 * @return java.lang.String
	 */
	public String getName() {
		return TomcatPlugin.getResource("%configurationEditorActionRemoveWebModule");
	}

	/**
	 * Undo the command.
	 */
	public void undo() {
		try {
			IServerWorkingCopy wc = (IServerWorkingCopy) getTaskModel().getObject(ITaskModel.TASK_SERVER);
			TomcatServer server = (TomcatServer) wc.getAdapter(TomcatServer.class);
			TomcatConfiguration configuration = server.getTomcatConfiguration();
			configuration.addWebModule(index, module);
		} catch (Exception e) {
			// ignore
		}
	}
}
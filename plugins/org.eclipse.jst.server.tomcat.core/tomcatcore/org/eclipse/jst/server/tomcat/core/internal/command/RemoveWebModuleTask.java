package org.eclipse.jst.server.tomcat.core.internal.command;
/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jst.server.tomcat.core.ITomcatConfiguration;
import org.eclipse.jst.server.tomcat.core.ITomcatConfigurationWorkingCopy;
import org.eclipse.jst.server.tomcat.core.WebModule;
import org.eclipse.jst.server.tomcat.internal.core.TomcatPlugin;

import org.eclipse.wst.server.core.IServerConfiguration;
import org.eclipse.wst.server.core.IServerConfigurationWorkingCopy;
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
		IServerConfigurationWorkingCopy wc = (IServerConfigurationWorkingCopy) getTaskModel().getObject(ITaskModel.TASK_SERVER_CONFIGURATION);
		ITomcatConfigurationWorkingCopy configuration = (ITomcatConfigurationWorkingCopy) wc.getWorkingCopyDelegate();
		module = (WebModule) configuration.getWebModules().get(index);
		configuration.removeWebModule(index);
	}

	/**
	 * Returns this command's description.
	 * @return java.lang.String
	 */
	public String getDescription() {
		if (module == null) {
			IServerConfiguration config = (IServerConfiguration) getTaskModel().getObject(ITaskModel.TASK_SERVER_CONFIGURATION);
			ITomcatConfiguration configuration = (ITomcatConfiguration) config.getDelegate();
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
			IServerConfigurationWorkingCopy wc = (IServerConfigurationWorkingCopy) getTaskModel().getObject(ITaskModel.TASK_SERVER_CONFIGURATION);
			ITomcatConfigurationWorkingCopy configuration = (ITomcatConfigurationWorkingCopy) wc.getWorkingCopyDelegate();
			configuration.addWebModule(index, module);
		} catch (Exception e) { }
	}
}
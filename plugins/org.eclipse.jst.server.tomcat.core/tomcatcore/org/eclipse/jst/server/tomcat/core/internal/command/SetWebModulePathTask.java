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
import org.eclipse.jst.server.tomcat.core.internal.TomcatPlugin;

import org.eclipse.wst.server.core.IServerConfiguration;
import org.eclipse.wst.server.core.IServerConfigurationWorkingCopy;
import org.eclipse.wst.server.core.ITaskModel;
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
	 */
	public SetWebModulePathTask(int index, String contextRoot) {
		super();
		this.index = index;
		this.path = contextRoot;
	}
	
	/**
	 * Execute the command.
	 * @return boolean
	 */
	public void execute(IProgressMonitor monitor) throws CoreException {
		IServerConfigurationWorkingCopy wc = (IServerConfigurationWorkingCopy) getTaskModel().getObject(ITaskModel.TASK_SERVER_CONFIGURATION);
		ITomcatConfigurationWorkingCopy configuration = (ITomcatConfigurationWorkingCopy) wc.getWorkingCopyDelegate();
		oldModule = (WebModule) configuration.getWebModules().get(index);
		configuration.removeWebModule(index);
		
		WebModule module = new WebModule(path, oldModule.getDocumentBase(), oldModule.getMemento(), oldModule.isReloadable());
		configuration.addWebModule(index, module);
	}

	/**
	 * Returns this command's description.
	 * @return java.lang.String
	 */
	public String getDescription() {
		if (oldModule == null) {
			IServerConfiguration config = (IServerConfiguration) getTaskModel().getObject(ITaskModel.TASK_SERVER_CONFIGURATION);
			ITomcatConfiguration configuration = (ITomcatConfiguration) config.getDelegate();
			oldModule = (WebModule) configuration.getWebModules().get(index);
		}
		
		return TomcatPlugin.getResource("%configurationEditorActionEditWebModuleDescription", oldModule.getPath(), path);
	}

	/**
	 * Returns this command's label.
	 * @return java.lang.String
	 */
	public String getName() {
		return TomcatPlugin.getResource("%configurationEditorActionEditWebModulePath");
	}

	/**
	 * Undo the command.
	 */
	public void undo() {
		try {
			IServerConfigurationWorkingCopy wc = (IServerConfigurationWorkingCopy) getTaskModel().getObject(ITaskModel.TASK_SERVER_CONFIGURATION);
			ITomcatConfigurationWorkingCopy configuration = (ITomcatConfigurationWorkingCopy) wc.getWorkingCopyDelegate();
			configuration.removeWebModule(index);
			configuration.addWebModule(index, oldModule);
		} catch (Exception e) { }
	}
}
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

import org.eclipse.jst.server.tomcat.core.internal.ITomcatConfigurationWorkingCopy;
import org.eclipse.jst.server.tomcat.core.internal.TomcatPlugin;
import org.eclipse.jst.server.tomcat.core.internal.WebModule;
/**
 * Command to modify the path of a Web module.
 */
public class SetWebModulePathCommand extends ConfigurationCommand {
	protected int index;
	protected WebModule oldModule;
	protected String path;

	/**
	 * SetWebModulePathCommand constructor comment.
	 * 
	 * @param configuration a tomcat configuration
	 * @param index an index
	 * @param contextRoot the context root
	 */
	public SetWebModulePathCommand(ITomcatConfigurationWorkingCopy configuration, int index, String contextRoot) {
		super(configuration);
		this.index = index;
		this.path = contextRoot;
	}

	/**
	 * Execute the command.
	 * @return boolean
	 */
	public boolean execute() {
		oldModule = (WebModule) configuration.getWebModules().get(index);
		configuration.removeWebModule(index);
		
		WebModule module = new WebModule(path, oldModule.getDocumentBase(), oldModule.getMemento(), oldModule.isReloadable());
		configuration.addWebModule(index, module);
		return true;
	}

	/**
	 * Returns this command's description.
	 * @return java.lang.String
	 */
	public String getDescription() {
		if (oldModule == null)
			oldModule = (WebModule) configuration.getWebModules().get(index);
		
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
		configuration.removeWebModule(index);
		configuration.addWebModule(index, oldModule);
	}
}
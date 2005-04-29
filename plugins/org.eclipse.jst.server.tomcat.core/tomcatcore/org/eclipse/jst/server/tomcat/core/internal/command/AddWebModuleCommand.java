/**********************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.jst.server.tomcat.core.internal.command;

import org.eclipse.jst.server.tomcat.core.internal.ITomcatConfigurationWorkingCopy;
import org.eclipse.jst.server.tomcat.core.internal.Messages;
import org.eclipse.jst.server.tomcat.core.internal.WebModule;
/**
 * Command to add a web module.
 */
public class AddWebModuleCommand extends ConfigurationCommand {
	protected WebModule module;
	protected int modules = -1;

	/**
	 * AddWebModuleCommand constructor comment.
	 * 
	 * @param configuration a tomcat configuration
	 * @param module a web module
	 */
	public AddWebModuleCommand(ITomcatConfigurationWorkingCopy configuration, WebModule module) {
		super(configuration);
		this.module = module;
	}

	/**
	 * Execute the command.
	 * @return boolean
	 */
	public boolean execute() {
		modules = configuration.getWebModules().size();
		configuration.addWebModule(-1, module);
		return true;
	}

	/**
	 * Returns this command's description.
	 * @return java.lang.String
	 */
	public String getDescription() {
		return Messages.configurationEditorActionAddWebModuleDescription;
	}

	/**
	 * Returns this command's label.
	 * @return java.lang.String
	 */
	public String getName() {
		return Messages.configurationEditorActionAddWebModule;
	}

	/**
	 * Undo the command.
	 */
	public void undo() {
		configuration.removeWebModule(modules);
	}
}
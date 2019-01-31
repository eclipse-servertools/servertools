/*******************************************************************************
 * Copyright (c) 2003, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    IBM Corporation - Initial API and implementation
 *******************************************************************************/
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
		super(configuration, Messages.configurationEditorActionAddWebModule);
		this.module = module;
	}

	/**
	 * Execute the command.
	 */
	public void execute() {
		modules = configuration.getWebModules().size();
		configuration.addWebModule(-1, module);
	}

	/**
	 * Undo the command.
	 */
	public void undo() {
		configuration.removeWebModule(modules);
	}
}

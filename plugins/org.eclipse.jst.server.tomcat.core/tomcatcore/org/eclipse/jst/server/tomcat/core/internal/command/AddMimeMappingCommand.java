/**********************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.jst.server.tomcat.core.internal.command;

import org.eclipse.jst.server.tomcat.core.internal.*;
/**
 * Command to add a mime mapping.
 */
public class AddMimeMappingCommand extends ConfigurationCommand {
	protected MimeMapping map;

	/**
	 * ModifyMimeTypeExtensionCommand constructor comment.
	 */
	public AddMimeMappingCommand(ITomcatConfigurationWorkingCopy configuration, MimeMapping map) {
		super(configuration);
		this.map = map;
	}

	/**
	 * Execute the command.
	 * @return boolean
	 */
	public boolean execute() {
		configuration.addMimeMapping(0, map);
		return true;
	}

	/**
	 * Returns this command's description.
	 * @return java.lang.String
	 */
	public String getDescription() {
		return TomcatPlugin.getResource("%configurationEditorActionAddMimeMappingDescription");
	}

	/**
	 * Returns this command's label.
	 * @return java.lang.String
	 */
	public String getName() {
		return TomcatPlugin.getResource("%configurationEditorActionAddMimeMapping");
	}

	/**
	 * Undo the command.
	 */
	public void undo() {
		configuration.removeMimeMapping(0);
	}
}
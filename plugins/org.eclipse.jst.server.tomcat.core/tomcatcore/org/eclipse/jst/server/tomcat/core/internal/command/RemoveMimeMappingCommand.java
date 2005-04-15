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
import org.eclipse.jst.server.tomcat.core.internal.MimeMapping;
import org.eclipse.jst.server.tomcat.core.internal.TomcatPlugin;
/**
 * Command to remove a mime mapping.
 */
public class RemoveMimeMappingCommand extends ConfigurationCommand {
	protected int index;
	protected MimeMapping mapping;

	/**
	 * RemoveMimeMappingCommand constructor.
	 * 
	 * @param configuration a tomcat configuration
	 * @param index an index
	 */
	public RemoveMimeMappingCommand(ITomcatConfigurationWorkingCopy configuration, int index) {
		super(configuration);
		this.index = index;
	}

	/**
	 * Execute the command.
	 * @return boolean
	 */
	public boolean execute() {
		mapping = (MimeMapping) configuration.getMimeMappings().get(index);
		configuration.removeMimeMapping(index);
		return true;
	}

	/**
	 * Returns this command's description.
	 * @return java.lang.String
	 */
	public String getDescription() {
		return TomcatPlugin.getResource("%configurationEditorActionRemoveMimeMappingDescription");
	}

	/**
	 * Returns this command's label.
	 * @return java.lang.String
	 */
	public String getName() {
		return TomcatPlugin.getResource("%configurationEditorActionRemoveMimeMapping");
	}

	/**
	 * Undo the command.
	 */
	public void undo() {
		configuration.addMimeMapping(index, mapping);
	}
}
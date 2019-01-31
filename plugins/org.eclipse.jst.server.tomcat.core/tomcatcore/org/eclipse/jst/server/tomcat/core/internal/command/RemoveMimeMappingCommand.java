/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.tomcat.core.internal.command;

import org.eclipse.jst.server.tomcat.core.internal.ITomcatConfigurationWorkingCopy;
import org.eclipse.jst.server.tomcat.core.internal.Messages;
import org.eclipse.jst.server.tomcat.core.internal.MimeMapping;
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
		super(configuration, Messages.configurationEditorActionRemoveMimeMapping);
		this.index = index;
	}

	/**
	 * Execute the command.
	 */
	public void execute() {
		mapping = (MimeMapping) configuration.getMimeMappings().get(index);
		configuration.removeMimeMapping(index);
	}

	/**
	 * Undo the command.
	 */
	public void undo() {
		configuration.addMimeMapping(index, mapping);
	}
}
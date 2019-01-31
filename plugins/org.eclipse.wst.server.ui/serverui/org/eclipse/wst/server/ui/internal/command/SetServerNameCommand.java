/*******************************************************************************
 * Copyright (c) 2003, 2017 IBM Corporation and others.
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
package org.eclipse.wst.server.ui.internal.command;

import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.ui.internal.Messages;
/**
 * Command to change the server name.
 */
public class SetServerNameCommand extends ServerCommand {
	protected String name;
	protected String oldName;
	protected Validator validator;

	public static interface Validator {
		public void validate();
	}
	
	/**
	 * SetServerNameCommand constructor.
	 * 
	 * @param server a server
	 * @param name a name for the server
	 */
	public SetServerNameCommand(IServerWorkingCopy server, String name, Validator validator) {
		super(server, Messages.serverEditorOverviewServerNameCommand);
		this.name = name;
		this.validator = validator;
	}

	/**
	 * Execute the command.
	 */
	public void execute() {
		oldName = server.getName();
		server.setName(name);
		if( validator != null )
			validator.validate();
	}

	/**
	 * Undo the command.
	 */
	public void undo() {
		server.setName(oldName);
		if( validator != null )
			validator.validate();
	}
}
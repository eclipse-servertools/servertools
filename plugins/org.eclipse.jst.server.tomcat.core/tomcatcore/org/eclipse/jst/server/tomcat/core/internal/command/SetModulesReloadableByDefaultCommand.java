/*******************************************************************************
 * Copyright (c) 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.tomcat.core.internal.command;

import org.eclipse.jst.server.tomcat.core.internal.ITomcatServerWorkingCopy;
import org.eclipse.jst.server.tomcat.core.internal.Messages;
/**
 * Command to enable or disable serving modules without publishing
 */
public class SetModulesReloadableByDefaultCommand extends ServerCommand {
	protected boolean mrbd;
	protected boolean oldMrbd;

	/**
	 * SetSeparateContextFilesCommand constructor comment.
	 * 
	 * @param server a Tomcat server
	 * @param mrbd <code>true</code> to enable saving separate context XML
	 * files. Otherwise contexts are kept in server.xml when published.
	 */
	public SetModulesReloadableByDefaultCommand(ITomcatServerWorkingCopy server, boolean mrbd) {
		super(server, Messages.serverEditorActionSetModulesReloadableByDefault);
		this.mrbd = mrbd;
	}

	/**
	 * Execute the command.
	 */
	public void execute() {
		oldMrbd = server.isModulesReloadableByDefault();
		server.setModulesReloadableByDefault(mrbd);
	}

	/**
	 * Undo the command.
	 */
	public void undo() {
		server.setModulesReloadableByDefault(oldMrbd);
	}
}
/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.tomcat.core.internal.command;

import org.eclipse.jst.server.tomcat.core.ITomcatServerWorkingCopy;
import org.eclipse.jst.server.tomcat.core.internal.TomcatPlugin;
/**
 * Command to change the server debug mode.
 */
public class SetTestEnvironmentCommand extends ServerCommand {
	protected boolean te;
	protected boolean oldTe;

	/**
	 * SetTestEnvironmentCommand constructor comment.
	 */
	public SetTestEnvironmentCommand(ITomcatServerWorkingCopy server, boolean te) {
		super(server);
		this.te = te;
	}

	/**
	 * Execute the command.
	 * @return boolean
	 */
	public boolean execute() {
		oldTe = server.isTestEnvironment();
		server.setTestEnvironment(te);
		return true;
	}

	/**
	 * Returns this command's description.
	 * @return java.lang.String
	 */
	public String getDescription() {
		return TomcatPlugin.getResource("%serverEditorActionSetTestEnvironmentDescription");
	}

	/**
	 * Returns this command's label.
	 * @return java.lang.String
	 */
	public String getName() {
		return TomcatPlugin.getResource("%serverEditorActionSetTestEnvironment");
	}

	/**
	 * Undo the command.
	 */
	public void undo() {
		server.setTestEnvironment(oldTe);
	}
}
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
public class SetDebugModeCommand extends ServerCommand {
	protected boolean debug;
	protected boolean oldDebug;

	/**
	 * SetDebugModeCommand constructor comment.
	 */
	public SetDebugModeCommand(ITomcatServerWorkingCopy server, boolean debug) {
		super(server);
		this.debug = debug;
	}

	/**
	 * Execute the command.
	 * @return boolean
	 */
	public boolean execute() {
		oldDebug = server.isDebug();
		server.setDebug(debug);
		return true;
	}

	/**
	 * Returns this command's description.
	 * @return java.lang.String
	 */
	public String getDescription() {
		return TomcatPlugin.getResource("%serverEditorActionSetDebugModeDescription");
	}

	/**
	 * Returns this command's label.
	 * @return java.lang.String
	 */
	public String getName() {
		return TomcatPlugin.getResource("%serverEditorActionSetDebugMode");
	}

	/**
	 * Undo the command.
	 */
	public void undo() {
		server.setDebug(oldDebug);
	}
}
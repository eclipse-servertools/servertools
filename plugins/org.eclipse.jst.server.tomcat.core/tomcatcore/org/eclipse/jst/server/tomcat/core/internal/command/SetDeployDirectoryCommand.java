/*******************************************************************************
 * Copyright (c) 2007 SAS Institute, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Larry Isaacs - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.tomcat.core.internal.command;

import org.eclipse.jst.server.tomcat.core.internal.ITomcatServerWorkingCopy;
import org.eclipse.jst.server.tomcat.core.internal.Messages;

/**
 * Command to change the deploy directory
 */
public class SetDeployDirectoryCommand extends ServerCommand {
	protected String deployDir;
	protected String oldDeployDir;

	/**
	 * Constructs command to set the deploy directory.
	 * 
	 * @param server a Tomcat server
	 * @param deployDir deployment directory to set
	 */
	public SetDeployDirectoryCommand(ITomcatServerWorkingCopy server,
			String deployDir) {
		super(server, Messages.serverEditorActionSetDeployDirectory);
		this.deployDir = deployDir;
	}

	/**
	 * Execute setting the deploy directory
	 */
	public void execute() {
		oldDeployDir = server.getDeployDirectory();
		server.setDeployDirectory(deployDir);
	}

	/**
	 * Restore prior deploy directory
	 */
	public void undo() {
		server.setDeployDirectory(oldDeployDir);
	}
}

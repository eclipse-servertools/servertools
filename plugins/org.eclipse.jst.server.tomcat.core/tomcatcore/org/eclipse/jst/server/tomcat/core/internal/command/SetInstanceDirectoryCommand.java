/*******************************************************************************
 * Copyright (c) 2007 SAS Institute, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
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
public class SetInstanceDirectoryCommand extends ServerCommand {
	protected String instanceDir;
	protected String oldInstanceDir;
	protected boolean oldTestEnvironment;

	/**
	 * Constructs command to set the instance directory. Setting
	 * the instance directory also sets testEnvironment true;
	 * 
	 * @param server a Tomcat server
	 * @param instanceDir instance directory to set
	 */
	public SetInstanceDirectoryCommand(ITomcatServerWorkingCopy server, String instanceDir) {
		super(server, Messages.serverEditorActionSetServerDirectory);
		this.instanceDir = instanceDir;
	}

	/**
	 * Execute setting the deploy directory
	 */
	public void execute() {
		oldTestEnvironment = server.isTestEnvironment();
		oldInstanceDir = server.getInstanceDirectory();
		if (!oldTestEnvironment)
			server.setTestEnvironment(true);
		server.setInstanceDirectory(instanceDir);
	}

	/**
	 * Restore prior deploy directory
	 */
	public void undo() {
		if (!oldTestEnvironment)
			server.setTestEnvironment(false);
		server.setInstanceDirectory(oldInstanceDir);
	}
}

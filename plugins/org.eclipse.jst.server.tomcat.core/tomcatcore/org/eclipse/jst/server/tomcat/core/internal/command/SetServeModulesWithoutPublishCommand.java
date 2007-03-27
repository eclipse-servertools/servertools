/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
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
public class SetServeModulesWithoutPublishCommand extends ServerCommand {
	protected boolean smwp;
	protected boolean oldSmwp;

	/**
	 * SetTestEnvironmentCommand constructor comment.
	 * 
	 * @param server a Tomcat server
	 * @param smwp <code>true</code> to enable serving modules without
	 * publishing. Otherwise modules are served with standard publishing.
	 */
	public SetServeModulesWithoutPublishCommand(ITomcatServerWorkingCopy server, boolean smwp) {
		super(server, Messages.serverEditorActionSetServeWithoutPublish);
		this.smwp = smwp;
	}

	/**
	 * Execute the command.
	 */
	public void execute() {
		oldSmwp = server.isServeModulesWithoutPublish();
		server.setServeModulesWithoutPublish(smwp);
	}

	/**
	 * Undo the command.
	 */
	public void undo() {
		server.setServeModulesWithoutPublish(oldSmwp);
	}
}
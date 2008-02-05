/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.command;

import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.internal.Publisher;
import org.eclipse.wst.server.core.internal.ServerWorkingCopy;
import org.eclipse.wst.server.ui.internal.Messages;
/**
 * Command to change the server's publisher setting.
 */
public class SetPublisherEnablementCommand extends ServerCommand {
	protected Publisher pub;
	protected boolean enabled;
	protected boolean oldEnabled;

	/**
	 * SetPublisherEnablementCommand constructor.
	 * 
	 * @param server a server
	 * @param pub a publisher
	 * @param enabled whether the publisher should be enabled or disabled
	 */
	public SetPublisherEnablementCommand(IServerWorkingCopy server, Publisher pub, boolean enabled) {
		super(server, Messages.serverEditorOverviewPublishCommand);
		this.pub = pub;
		this.enabled = enabled;
	}

	/**
	 * Execute the command.
	 */
	public void execute() {
		ServerWorkingCopy swc = (ServerWorkingCopy) server;
		oldEnabled = swc.isPublisherEnabled(pub);
		swc.setPublisherEnabled(pub, enabled);
	}

	/**
	 * Undo the command.
	 */
	public void undo() {
		ServerWorkingCopy swc = (ServerWorkingCopy) server;
		swc.setPublisherEnabled(pub, oldEnabled);
	}
}
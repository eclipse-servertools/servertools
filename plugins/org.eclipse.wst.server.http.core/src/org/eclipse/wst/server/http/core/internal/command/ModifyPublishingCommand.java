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
package org.eclipse.wst.server.http.core.internal.command;

import org.eclipse.wst.server.http.core.internal.HttpServer;
import org.eclipse.wst.server.http.core.internal.Messages;
/**
 * Command to change the publishing state.
 */
public class ModifyPublishingCommand extends ServerCommand {
	protected boolean shouldPublish;
	protected boolean oldShouldPublish;

	/**
	 * ModifyPublishingCommand constructor.
	 * 
	 * @param server a server
	 * @param shouldPublish 
	 */
	public ModifyPublishingCommand(HttpServer server, boolean shouldPublish) {
		super(server, Messages.actionModifyPublishing);
		this.shouldPublish = shouldPublish;
	}

	/**
	 * Execute the command.
	 */
	public void execute() {
		oldShouldPublish = server.isPublishing();
		
		// make the change
		server.setPublishing(shouldPublish);
	}

	/**
	 * Undo the command.
	 */
	public void undo() {
		server.setPublishing(oldShouldPublish);
	}
}
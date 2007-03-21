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

public class ModifyPublishDirectoryCommand extends ServerCommand {
	protected String pubDir;
	protected String oldPubDir;

	public ModifyPublishDirectoryCommand(HttpServer server, String pubDir) {
		super(server, "set the publish directory");
		this.pubDir = pubDir;
	}

	/**
	 * Execute the command.
	 */
	public void execute() {
		oldPubDir = server.getPublishDirectory();
		server.setPublishDirectory(pubDir);
	}

	/**
	 * Undo the command.
	 */
	public void undo() {
		server.setPublishDirectory(oldPubDir);
	}
}
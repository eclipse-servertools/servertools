/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.tomcat.core.internal.command;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jst.server.tomcat.core.internal.ITomcatServerWorkingCopy;
import org.eclipse.jst.server.tomcat.core.internal.TomcatServer;
import org.eclipse.wst.server.core.util.Task;
/**
 * A command on a Tomcat server.
 */
public abstract class ServerCommand extends Task {
	protected TomcatServer server;

	/**
	 * ServerCommand constructor comment.
	 */
	public ServerCommand(ITomcatServerWorkingCopy server) {
		super();
		this.server = (TomcatServer) server;
	}
	
	/**
	 * Returns true if this command can be undone.
	 * @return boolean
	 */
	public boolean canUndo() {
		return true;
	}
	
	public abstract boolean execute();
	
	public void execute(IProgressMonitor monitor) {
		execute();
	}
}
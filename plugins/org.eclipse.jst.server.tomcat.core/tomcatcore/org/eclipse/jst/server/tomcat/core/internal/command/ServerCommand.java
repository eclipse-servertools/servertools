package org.eclipse.jst.server.tomcat.core.internal.command;
/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jst.server.tomcat.core.ITomcatServerWorkingCopy;
import org.eclipse.wst.server.core.util.Task;
/**
 * A command on a Tomcat server.
 */
public abstract class ServerCommand extends Task {
	protected ITomcatServerWorkingCopy server;

	/**
	 * ServerCommand constructor comment.
	 */
	public ServerCommand(ITomcatServerWorkingCopy server) {
		super();
		this.server = server;
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
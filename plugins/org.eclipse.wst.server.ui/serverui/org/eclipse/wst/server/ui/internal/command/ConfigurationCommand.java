package org.eclipse.wst.server.ui.internal.command;
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

import org.eclipse.wst.server.core.IServerConfigurationWorkingCopy;
import org.eclipse.wst.server.core.util.Task;
/**
 * Configuration command.
 */
public abstract class ConfigurationCommand extends Task {
	protected IServerConfigurationWorkingCopy configuration;

	/**
	 * ConfigurationCommand constructor comment.
	 */
	public ConfigurationCommand(IServerConfigurationWorkingCopy configuration) {
		super();
		this.configuration = configuration;
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
/*******************************************************************************
 * Copyright (c) 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.server.core.IServer;

/**
 * A modifier class that allows performing custom action on the delete server dialog.
 * It also allows injecting custom GUI into the delete server dialog.
 */
public abstract class DeleteServerDialogExtension {

	protected IServer[] servers = null;

	/**
	 * Create the custom UI on the delete server dialog.
	 * 
	 * @param parent - parent composite.
	 */
	public abstract void createControl(Composite parent);
		
	/**
	 * Check if this extension is enabled. This method can be used to inspect the list of servers
	 * provided by setServers() to decide if this extension applies.
	 * @return true if extension is enabled; otherwise, return false.
	 */
	public abstract boolean isEnabled();
	
	/**
	 * Enables the extender to perform custom action before the actual servers get deleted.
	 * 
	 * @param monitor - progress monitor
	 */
	public abstract void performPreDeleteAction(IProgressMonitor monitor);

	/**
	 * Enables the extender to perform custom action after the actual servers get deleted.
	 * 
	 * @param monitor - progress monitor
	 */
	public abstract void performPostDeleteAction(IProgressMonitor monitor);

	/**
	* Sets servers for which the delete dialog is triggered.
	* 
	* @param servers - array of servers that will be deleted.
	*/
	public void setServers(IServer[] servers) {
		this.servers = servers;
	}	
}

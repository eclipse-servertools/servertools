/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.tomcat.core.internal.command;

import java.util.Iterator;

import org.eclipse.jst.server.tomcat.core.internal.ITomcatConfigurationWorkingCopy;
import org.eclipse.jst.server.tomcat.core.internal.TomcatPlugin;
import org.eclipse.wst.server.core.IServerPort;
/**
 * Command to change the configuration port.
 */
public class ModifyPortCommand extends ConfigurationCommand {
	protected String id;
	protected int port;
	protected int oldPort;

	/**
	 * ChangePortCommand constructor comment.
	 */
	public ModifyPortCommand(ITomcatConfigurationWorkingCopy configuration, String id, int port) {
		super(configuration);
		this.id = id;
		this.port = port;
	}

	/**
	 * Execute the command.
	 * @return boolean
	 */
	public boolean execute() {
		// find old port number
		Iterator iterator = configuration.getServerPorts().iterator();
		while (iterator.hasNext()) {
			IServerPort temp = (IServerPort) iterator.next();
			if (id.equals(temp.getId()))
				oldPort = temp.getPort();
		}
	
		// make the change
		configuration.modifyServerPort(id, port);
		return true;
	}

	/**
	 * Returns this command's description.
	 * @return java.lang.String
	 */
	public String getDescription() {
		return TomcatPlugin.getResource("%configurationEditorActionModifyPortDescription");
	}

	/**
	 * Returns this command's label.
	 * @return java.lang.String
	 */
	public String getName() {
		return TomcatPlugin.getResource("%configurationEditorActionModifyPort");
	}

	/**
	 * Undo the command.
	 */
	public void undo() {
		configuration.modifyServerPort(id, oldPort);
	}
}
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
import org.eclipse.jst.server.tomcat.core.ITomcatServerWorkingCopy;
import org.eclipse.jst.server.tomcat.internal.core.TomcatPlugin;
/**
 * Command to change the server security option.
 */
public class SetSecureCommand extends ServerCommand {
	protected boolean secure;
	protected boolean oldSecure;

	/**
	 * SetSecureCommand constructor comment.
	 */
	public SetSecureCommand(ITomcatServerWorkingCopy server, boolean secure) {
		super(server);
		this.secure = secure;
	}

	/**
	 * Execute the command.
	 * @return boolean
	 */
	public boolean execute() {
		oldSecure = server.isSecure();
		server.setSecure(secure);
		return true;
	}

	/**
	 * Returns this command's description.
	 * @return java.lang.String
	 */
	public String getDescription() {
		return TomcatPlugin.getResource("%serverEditorActionSetSecureDescription");
	}

	/**
	 * Returns this command's label.
	 * @return java.lang.String
	 */
	public String getName() {
		return TomcatPlugin.getResource("%serverEditorActionSetSecure");
	}

	/**
	 * Undo the command.
	 */
	public void undo() {
		server.setSecure(oldSecure);
	}
}
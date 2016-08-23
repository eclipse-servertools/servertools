/*******************************************************************************
 * Copyright (c) 2016 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui;

import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServerAttributes;

/**
 * Extension to override the message that is displayed in the confirmation dialog that appears 
 * when removing a module or removing multiple modules from the server.
 * Customization is based on the server type id of the server.
 *
 */
public abstract class RemoveModuleMessageExtension {
	
	private String serverTypeId = "";
	
	/**
	 * Get the server type id for which this custom message extension applies
	 * Extenders are not expected to override this.
	 * @return
	 */
	public String getServerTypeId() {
		return serverTypeId;
	}
	
	/**
	 * Set the server type id for which this custom message extension applies
	 * Extenders are not expected to override this.
	 * 
	 * @param serverTypeId
	 */
	public void setServerTypeId(String serverTypeId) {
		this.serverTypeId = serverTypeId;
	}
	
	/**
	 * Extenders may provide a custom message when removing one or more modules from the server.  Use the provided
	 * parameters to include the server name and the names of the modules in the message, if desired.  Return a null value
	 * to use the default string provided by the framework.
	 * 
	 * @param server
	 * @param modules
	 * @return the custom message when removing one or modules from the server.  Return null to use the default string provided by the framework.
	 */
	public abstract String getConfirmationMessage(IServerAttributes server, IModule [] modules);
}

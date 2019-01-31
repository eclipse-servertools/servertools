/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.provisional;

import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.ui.internal.cnf.ServerDecorator;

/**
 * A default or parent class that provides the default labels: Start, Stop, Starting, etc..
 * 
 * @author arvera
 *
 */
public class AbstractServerLabelProvider{

	/**
	 * Based on a server return the server state label to display in the UI
	 * @param server
	 * @return String
	 */
	public String getServerStateLabel(IServer server) {
		if (server == null || server.getServerType() == null)
			return null;
		
		return ServerDecorator.getStateLabel(server.getServerType(), server.getServerState(), server.getMode());
	}

}

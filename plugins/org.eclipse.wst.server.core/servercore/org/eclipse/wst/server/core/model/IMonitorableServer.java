/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.model;

import java.util.List;
/**
 * 
 */
public interface IMonitorableServer {
	/**
	 * Returns a list of IServerPorts that this server has.
	 *
	 * @return java.util.List
	 */
	public abstract List getServerPorts();
}
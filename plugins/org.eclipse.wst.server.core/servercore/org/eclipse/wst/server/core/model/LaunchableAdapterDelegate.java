/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.model;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.server.core.ILaunchable;
import org.eclipse.wst.server.core.IModuleObject;
import org.eclipse.wst.server.core.IServer;
/**
 * This interface, typically implemented by the server code, converts from
 * an IModuleObject to an ILaunchable.
 * 
 * <p>This is the implementation of a launchableAdapter extension point.</p>
 */
public abstract class LaunchableAdapterDelegate {
	/**
	 * Returns a launchable object from this module object.
	 * 
	 * @param server
	 * @param moduleObject
	 * @return 
	 * @exception 
	 */
	public abstract ILaunchable getLaunchable(IServer server, IModuleObject moduleObject) throws CoreException;
}
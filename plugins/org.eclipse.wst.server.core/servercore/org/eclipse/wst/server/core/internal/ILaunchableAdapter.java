/**********************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.internal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.server.core.ILaunchable;
import org.eclipse.wst.server.core.IModuleArtifact;
import org.eclipse.wst.server.core.IServer;
/**
 * This interface, typically implemented by the server
 * code, converts from an IModuleArtifact to an
 * ILaunchable.
 * 
 * <p>This interface is not intended to be implemented by clients.</p>
 */
public interface ILaunchableAdapter {
	/**
	 * Returns the id of this adapter. Each known adapter has a distinct id. 
	 * Ids are intended to be used internally as keys; they are not
	 * intended to be shown to end users.
	 * 
	 * @return the adapter id
	 */
	public String getId();

	/**
	 * Returns a launchable object from this module object.
	 * 
	 * @param server
	 * @param moduleObject
	 * @return
	 * @exception
	 */
	public ILaunchable getLaunchable(IServer server, IModuleArtifact moduleObject) throws CoreException;
}
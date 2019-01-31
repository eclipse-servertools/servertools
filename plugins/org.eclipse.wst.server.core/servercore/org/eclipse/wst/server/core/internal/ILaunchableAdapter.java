/*******************************************************************************
 * Copyright (c) 2003, 2006 IBM Corporation and others.
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
package org.eclipse.wst.server.core.internal;

import org.eclipse.core.runtime.CoreException;
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
	 * @param server the server
	 * @param moduleArtifact an artifact in the module
	 * @return a launchable object
	 * @exception CoreException thrown if there is a problem returning the launchable
	 */
	public Object getLaunchable(IServer server, IModuleArtifact moduleArtifact) throws CoreException;
}
/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.model;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.server.core.ILaunchable;
import org.eclipse.wst.server.core.IModuleArtifact;
import org.eclipse.wst.server.core.IServer;
/**
 * This interface, typically implemented by the server code, converts from
 * an IModuleArtifact to an ILaunchable.
 * 
 * <p>This is the implementation of a launchableAdapter extension point.</p>
 * 
 * @since 1.0
 */
public abstract class LaunchableAdapterDelegate {
	/**
	 * Returns a launchable object from this module artifact.
	 * 
	 * @param server
	 * @param moduleObject
	 * @return the launchable object
	 * @exception if there was an error doing the conversion
	 */
	public abstract ILaunchable getLaunchable(IServer server, IModuleArtifact moduleArtifact) throws CoreException;
}
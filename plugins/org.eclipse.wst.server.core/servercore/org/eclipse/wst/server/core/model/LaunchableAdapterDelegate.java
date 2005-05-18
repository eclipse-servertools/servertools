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
import org.eclipse.wst.server.core.IModuleArtifact;
import org.eclipse.wst.server.core.IServer;
/**
 * This interface, typically implemented by the server code, converts from
 * an IModuleArtifact to an object launchable on the server.
 * 
 * <p>This is the implementation of a launchableAdapter extension point.</p>
 * <p>
 * This abstract class is intended to be extended only by clients
 * to extend the <code>launchableAdapters</code> extension point.
 * </p>
 * 
 * @since 1.0
 */
public abstract class LaunchableAdapterDelegate {
	/**
	 * Returns a launchable object from this module artifact.
	 * 
	 * @param server the server
	 * @param moduleArtifact a module artifact
	 * [issue: if the launchable object cannot be found, should it throw a CoreExcpetion or return null?]
	 * @return the launchable object
	 * @throws CoreException if there was an error doing the conversion
	 */
	public abstract Object getLaunchable(IServer server, IModuleArtifact moduleArtifact) throws CoreException;
}
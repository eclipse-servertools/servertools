/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core;

import org.eclipse.core.resources.IProject;
/**
 * A project properties listener. Fires events when the default server or
 * runtime target changes. Note that this interface should be used for
 * informational purposes only. If (e.g.) you have code that needs to respond
 * to a specific runtime target, you should use the runtimeTargetHandler
 * extension point. The extension point will allow your code to be automatically
 * loaded when necessary (instead of having to preload and add a listener),
 * will not cause unnecessary plugin loading, and will allow ordering of
 * setting/unsetting the runtime target. 
 */
public interface IProjectPropertiesListener {
	/**
	 * Fired when the default server for the project changes.
	 *
	 * @param project org.eclipse.core.resources.IProject
	 * @param server org.eclipse.wst.server.core.IServer
	 */
	public void defaultServerChanged(IProject project, IServer server);

	/**
	 * Fired when the runtime target for the project changes.
	 *
	 * @param project org.eclipse.core.resources.IProject
	 * @param server org.eclipse.wst.server.core.IRuntime
	 */
	public void runtimeTargetChanged(IProject project, IRuntime runtime);
}
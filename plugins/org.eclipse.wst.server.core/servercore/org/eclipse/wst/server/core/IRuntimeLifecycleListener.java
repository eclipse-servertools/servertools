/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core;
/**
 * Listener interface for runtime changes.
 * 
 * @since 1.0
 */
public interface IRuntimeLifecycleListener {
	/**
	 * A new runtime has been created.
	 *
	 * @param runtime org.eclipse.wst.server.core.IRuntime
	 */
	public void runtimeAdded(IRuntime runtime);

	/**
	 * An existing runtime has been updated or modified.
	 *
	 * @param runtime org.eclipse.wst.server.core.IRuntime
	 */
	public void runtimeChanged(IRuntime runtime);

	/**
	 * A existing runtime has been removed.
	 *
	 * @param runtime org.eclipse.wst.server.core.IRuntime
	 */
	public void runtimeRemoved(IRuntime runtime);
}
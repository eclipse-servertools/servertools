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
package org.eclipse.wst.server.core.util;

import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeLifecycleListener;
/**
 * Helper class which implements the IRuntimeLifecycleListener interface
 * with empty methods.
 * 
 * @see org.eclipse.wst.server.core.IRuntimeLifecycleListener
 * @plannedfor 1.0
 */
public class RuntimeLifecycleAdapter implements IRuntimeLifecycleListener {
	/**
	 * @see IRuntimeLifecycleListener#runtimeAdded(IRuntime)
	 */
	public void runtimeAdded(IRuntime runtime) {
		// do nothing
	}

	/**
	 * @see IRuntimeLifecycleListener#runtimeChanged(IRuntime)
	 */
	public void runtimeChanged(IRuntime runtime) {
		// do nothing
	}

	/**
	 * @see IRuntimeLifecycleListener#runtimeRemoved(IRuntime)
	 */
	public void runtimeRemoved(IRuntime runtime) {
		// do nothing
	}
}
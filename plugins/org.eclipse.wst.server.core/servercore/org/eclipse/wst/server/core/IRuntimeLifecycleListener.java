/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
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
package org.eclipse.wst.server.core;
/**
 * Listener interface for changes to runtimes.
 * <p>
 * This interface is fired whenever a runtime is added, modified, or removed.
 * All events are fired post-change, so that all server tools API called as a
 * result of the event will return the updated results. (for example, on
 * runtimeAdded the new server will be in the global list of runtimes
 * ({@link ServerCore#getRuntimes()}), and on runtimeRemoved the runtime will
 * not be in the list.
 * </p>
 * 
 * @see ServerCore
 * @see IRuntime
 * @since 1.0
 */
public interface IRuntimeLifecycleListener {
	/**
	 * A new runtime has been created.
	 *
	 * @param runtime the new runtime
	 */
	public void runtimeAdded(IRuntime runtime);

	/**
	 * An existing runtime has been updated or modified.
	 *
	 * @param runtime the modified runtime
	 */
	public void runtimeChanged(IRuntime runtime);

	/**
	 * A existing runtime has been removed.
	 *
	 * @param runtime the removed runtime
	 */
	public void runtimeRemoved(IRuntime runtime);
}
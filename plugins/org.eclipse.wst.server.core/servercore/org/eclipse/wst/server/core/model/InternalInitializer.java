/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.model;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.internal.ModuleFactory;
import org.eclipse.wst.server.core.internal.Runtime;
import org.eclipse.wst.server.core.internal.Server;
/**
 * This is an internal utility class that is used by the server framework.
 * It is not API and should never be used by clients.
 */
public class InternalInitializer {
	/**
	 * Internal method - do not call.
	 * 
	 * @param delegate
	 * @param newServer
	 * @param monitor
	 */
	public static void initializeServerDelegate(ServerDelegate delegate, Server newServer, IProgressMonitor monitor) {
		delegate.initialize(newServer, monitor);
	}

	/**
	 * Internal method - do not call.
	 * 
	 * @param delegate
	 * @param newServer
	 * @param monitor
	 */
	public static void initializeServerBehaviourDelegate(ServerBehaviourDelegate delegate, Server newServer, IProgressMonitor monitor) {
		delegate.initialize(newServer, monitor);
	}

	/**
	 * Internal method - do not call.
	 * 
	 * @param delegate
	 * @param newRuntime
	 * @param monitor
	 */
	public static void initializeRuntimeDelegate(RuntimeDelegate delegate, Runtime newRuntime, IProgressMonitor monitor) {
		delegate.initialize(newRuntime, monitor);
	}

	/**
	 * Internal method - do not call.
	 * 
	 * @param delegate
	 * @param newModuleFactory
	 * @param monitor
	 */
	public static void initializeModuleFactoryDelegate(ModuleFactoryDelegate delegate, ModuleFactory newModuleFactory, IProgressMonitor monitor) {
		delegate.initialize(newModuleFactory, monitor);
	}
}
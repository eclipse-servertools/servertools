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

import org.eclipse.wst.server.core.internal.ModuleFactory;
import org.eclipse.wst.server.core.internal.Runtime;
import org.eclipse.wst.server.core.internal.Server;
/**
 * This is an internal utility class that is used by the server framework.
 * It is not API and should never be used by clients.
 */
public class InternalInitializer {
	public static void initializeServerDelegate(ServerDelegate delegate, Server newServer) {
		delegate.initialize(newServer);
	}
	
	public static void initializeServerBehaviourDelegate(ServerBehaviourDelegate delegate, Server newServer) {
		delegate.initialize(newServer);
	}
	
	public static void initializeRuntimeDelegate(RuntimeDelegate delegate, Runtime newRuntime) {
		delegate.initialize(newRuntime);
	}

	public static void initializeModuleFactoryDelegate(ModuleFactoryDelegate delegate, ModuleFactory newModuleFactory) {
		delegate.initialize(newModuleFactory);
	}
}
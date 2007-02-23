/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.core;
/**
 * A server profiler delegate.
 * <p>
 * <b>Provisional API:</b> This class/interface is part of an interim API that is still under development and expected to 
 * change significantly before reaching stability. It is being made available at this early stage to solicit feedback 
 * from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken 
 * (repeatedly) as the API evolves.
 * </p>
 */
public abstract class ServerProfilerDelegate {
	/**
	 * Create a new server profiler delegate.
	 */
	public ServerProfilerDelegate() {
		// ignore
	}

	/** 
	 * Returns an array of environment variables to be used when launching the profiler,
	 * or <code>null</code> if none are required.
	 * 
	 * @return an array of environment variables, or <code>null</code>
	 */	
	public String[] getEnvironmentVariables() {
		return null;
	}

	/**
	 * Returns the VM arguments to be used when launching the profiler, or
	 * <code>null</code> if none are required.
	 * 
	 * @return the VM arguments for the profiler, or <code>null</code>
	 */
	public String getVMArguments() {
		return null;
	}
}
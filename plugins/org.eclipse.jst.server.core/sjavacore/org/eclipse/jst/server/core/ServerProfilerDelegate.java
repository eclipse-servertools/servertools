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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.VMRunnerConfiguration;
import org.eclipse.jst.server.core.internal.JavaServerPlugin;
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
	 * Create a new server profiler delegate. This class must have a public default constructor.
	 */
	public ServerProfilerDelegate() {
		// ignore
	}

	/**
	 * Processes the Java launch configuration about to be run to support profiling.
	 * VM args or environment variables can be set to allow profiling.
	 * 
	 * @param launch the launch
	 * @param vmInstall the vm install being run against
	 * @param vmConfig the configuration to process
	 * @param monitor a progress monitor
	 * @throws CoreException if there is a problem during configuration
	 */
	public abstract void process(ILaunch launch, IVMInstall vmInstall, VMRunnerConfiguration vmConfig, IProgressMonitor monitor) throws CoreException;

	/**
	 * Processes the Java launch configuration about to be run to support profiling.
	 * VM args or environment variables can be set to allow profiling.
	 * 
	 * @param launch the launch
	 * @param vmInstall the vm install being run against
	 * @param vmConfig the configuration to process
	 * @param monitor a progress monitor
	 * @throws CoreException if there are no profilers configured or there is a problem
	 *   configuring the launch
	 */
	public static void configureProfiling(ILaunch launch, IVMInstall vmInstall, VMRunnerConfiguration vmConfig, IProgressMonitor monitor) throws CoreException {
		JavaServerPlugin.configureProfiling(launch, vmInstall, vmConfig, monitor);
	}
}
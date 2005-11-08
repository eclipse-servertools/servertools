/***************************************************************************************************
 * Copyright (c) 2005 Eteration A.S. and Gorkem Ercan. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Gorkem Ercan - initial API and implementation
 *               
 **************************************************************************************************/
package org.eclipse.jst.server.generic.core.internal;

import java.io.File;
import java.util.Map;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.launching.AbstractJavaLaunchConfigurationDelegate;
import org.eclipse.jdt.launching.ExecutionArguments;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.VMRunnerConfiguration;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerUtil;
import org.eclipse.wst.server.core.model.ServerBehaviourDelegate;
/**
 * ServerLaunchConfiguration for the generic server.
 *
 * @author Gorkem Ercan
 */
public class GenericServerLaunchConfigurationDelegate extends AbstractJavaLaunchConfigurationDelegate {

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ILaunchConfigurationDelegate#launch(org.eclipse.debug.core.ILaunchConfiguration, java.lang.String, org.eclipse.debug.core.ILaunch, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {

		IServer server = ServerUtil.getServer(configuration);
		if (server == null) {
			abort("Server does not exist", null,
					IJavaLaunchConfigurationConstants.ERR_INTERNAL_ERROR);
		}
		GenericServerBehaviour genericServer = (GenericServerBehaviour) server.loadAdapter(ServerBehaviourDelegate.class, null);

		try {
			genericServer.setupLaunch(launch, mode, monitor);
			String mainTypeName = genericServer.getStartClassName();
			IVMInstall vm = verifyVMInstall(configuration);
			IVMRunner runner = vm.getVMRunner(mode);
			if(runner== null){
				throw new CoreException(new Status(IStatus.ERROR,CorePlugin.PLUGIN_ID,0,GenericServerCoreMessages.runModeNotSupported,null));
			}
			File workingDir = verifyWorkingDirectory(configuration);
			String workingDirName = null;
			if (workingDir != null)
				workingDirName = workingDir.getAbsolutePath();

			// Program & VM args
			String pgmArgs = getProgramArguments(configuration);
			String vmArgs = getVMArguments(configuration);

			ExecutionArguments execArgs = new ExecutionArguments(vmArgs,
					pgmArgs);

			// VM-specific attributes
			Map vmAttributesMap = getVMSpecificAttributesMap(configuration);

			// Classpath
			String[] classpath = getClasspath(configuration);

			// Create VM config
			VMRunnerConfiguration runConfig = new VMRunnerConfiguration(
					mainTypeName, classpath);
			runConfig.setProgramArguments(execArgs.getProgramArgumentsArray());
			runConfig.setVMArguments(execArgs.getVMArgumentsArray());
			runConfig.setWorkingDirectory(workingDirName);
			runConfig.setVMSpecificAttributesMap(vmAttributesMap);

			// Bootpath
			String[] bootpath = getBootpath(configuration);
			if (bootpath != null && bootpath.length > 0)
				runConfig.setBootClassPath(bootpath);

			setDefaultSourceLocator(launch, configuration);
			// Launch the configuration
			runner.run(runConfig, launch, monitor);
			genericServer.setProcess(launch.getProcesses()[0]);
		} catch (CoreException e) {
			Trace.trace(Trace.SEVERE,"error lauching generic server",e);
			genericServer.terminate();
			throw e;
		}
	}

	/**
	 * Throws a core exception with the given message and optional
	 * exception. The exception's status code will indicate an error.
	 * 
	 * @param message error message
	 * @param exception cause of the error, or <code>null</code>
	 * @exception CoreException with the given message and underlying
	 *  exception
	 */
	protected void abort(String message, Throwable exception, int code) throws CoreException {
		throw new CoreException(new Status(IStatus.ERROR, CorePlugin.getDefault().getBundle().getSymbolicName(), code, message, exception));
	}
	
}

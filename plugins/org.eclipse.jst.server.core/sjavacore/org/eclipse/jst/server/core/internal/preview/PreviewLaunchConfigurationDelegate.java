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
package org.eclipse.jst.server.core.internal.preview;

import java.io.File;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.launching.*;
import org.eclipse.jst.server.core.internal.JavaServerPlugin;
import org.eclipse.jst.server.core.internal.Messages;
import org.eclipse.jst.server.core.internal.ServerProfiler;
import org.eclipse.jst.server.core.internal.Trace;

import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerUtil;
import org.osgi.framework.Bundle;
/**
 * 
 */
public class PreviewLaunchConfigurationDelegate extends AbstractJavaLaunchConfigurationDelegate {
	private static final String[] REQUIRED_BUNDLE_IDS = new String[] {
		"org.eclipse.core.runtime",
		"org.apache.commons.logging",
		"javax.servlet",
		"javax.servlet.jsp",
		"org.mortbay.jetty",
		"org.eclipse.jst.server.preview"
	};

	private static final String MAIN_CLASS = "org.eclipse.jst.server.preview.internal.PreviewStarter";

	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException {
		IServer server = ServerUtil.getServer(configuration);
		if (server == null) {
			Trace.trace(Trace.FINEST, "Launch configuration could not find server");
			// throw CoreException();
			return;
		}
		
		PreviewServerBehaviour previewServer = (PreviewServerBehaviour) server.loadAdapter(PreviewServerBehaviour.class, null);
		
		int size = REQUIRED_BUNDLE_IDS.length;
		String[] jars = new String[size];
		for (int i = 0; i < size; i++) {
			Bundle b = Platform.getBundle(REQUIRED_BUNDLE_IDS[i]);
			IPath path = null;
			if (b != null)
				path = PreviewRuntime.getJarredPluginPath(b);
			if (path == null)
				throw new CoreException(new Status(IStatus.ERROR, JavaServerPlugin.PLUGIN_ID, "Could not find required bundle " + REQUIRED_BUNDLE_IDS[i]));
			jars[i] = path.toOSString();
		}
		
		if (new File(jars[5] + "bin").exists())
			jars[5] = jars[5] + "bin";
		
		IVMInstall vm = verifyVMInstall(configuration);
		
		IVMRunner runner = vm.getVMRunner(mode);
		if (runner == null)
			runner = vm.getVMRunner(ILaunchManager.RUN_MODE);
		
		File workingDir = verifyWorkingDirectory(configuration);
		String workingDirName = null;
		if (workingDir != null)
			workingDirName = workingDir.getAbsolutePath();
		
		// Program & VM args
		String pgmArgs = previewServer.getTempDirectory().append("preview.xml").toOSString(); 
			//getProgramArguments(configuration);
		String vmArgs = getVMArguments(configuration);
		String[] envp = getEnvironment(configuration);
		
		if (ILaunchManager.PROFILE_MODE.equals(mode)) {
			ServerProfiler[] sp = JavaServerPlugin.getServerProfilers();
			if (sp == null || sp.length == 0 || runner == null)
				throw new CoreException(new Status(IStatus.ERROR, JavaServerPlugin.PLUGIN_ID, 0, Messages.errorNoProfiler, null));
			
			String vmArgs2 = sp[0].getVMArgs();
			if (vmArgs2 != null)
				vmArgs = vmArgs + " " + vmArgs2;
			
			String[] env = sp[0].getEnvironmentVariables();
			if (env != null && env.length > 0) {
				if (envp == null)
					envp = env;
				else {
					String[] s = new String[env.length + envp.length];
					System.arraycopy(envp, 0, s, 0, envp.length);
					System.arraycopy(env, 0, s, envp.length, env.length);
					envp = s;
				}
			}
		}
		
		ExecutionArguments execArgs = new ExecutionArguments(vmArgs, pgmArgs);
		
		// VM-specific attributes
		Map vmAttributesMap = getVMSpecificAttributesMap(configuration);
		
		// Classpath
		String[] classpath2 = getClasspath(configuration);
		String[] classpath = new String[classpath2.length + REQUIRED_BUNDLE_IDS.length];
		System.arraycopy(jars, 0, classpath, 0, REQUIRED_BUNDLE_IDS.length);
		System.arraycopy(classpath2, 0, classpath, REQUIRED_BUNDLE_IDS.length, classpath2.length);
		
		// Create VM config
		VMRunnerConfiguration runConfig = new VMRunnerConfiguration(MAIN_CLASS, classpath);
		runConfig.setProgramArguments(execArgs.getProgramArgumentsArray());
		runConfig.setVMArguments(execArgs.getVMArgumentsArray());
		runConfig.setWorkingDirectory(workingDirName);
		runConfig.setEnvironment(envp);
		runConfig.setVMSpecificAttributesMap(vmAttributesMap);

		// Bootpath
		String[] bootpath = getBootpath(configuration);
		if (bootpath != null && bootpath.length > 0)
			runConfig.setBootClassPath(bootpath);
		
		setDefaultSourceLocator(launch, configuration);
		
		// Launch the configuration
		previewServer.setupLaunch(launch, mode, monitor);
		try {
			runner.run(runConfig, launch, monitor);
			previewServer.setProcess(launch.getProcesses()[0]);
		} catch (Exception e) {
			// ignore - process failed
		}
	}
}
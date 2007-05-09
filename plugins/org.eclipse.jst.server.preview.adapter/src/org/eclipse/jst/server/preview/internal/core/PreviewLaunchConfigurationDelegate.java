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
package org.eclipse.jst.server.preview.internal.core;

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
import org.eclipse.jst.server.core.ServerProfilerDelegate;
import org.eclipse.jst.server.preview.internal.PreviewPlugin;
import org.eclipse.jst.server.preview.internal.Trace;

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
		"org.eclipse.wst.server.preview"
	};

	private static final String MAIN_CLASS = "org.eclipse.wst.server.preview.internal.PreviewStarter";

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
				throw new CoreException(new Status(IStatus.ERROR, PreviewPlugin.PLUGIN_ID, "Could not find required bundle " + REQUIRED_BUNDLE_IDS[i]));
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
		
		if (ILaunchManager.PROFILE_MODE.equals(mode))
			ServerProfilerDelegate.configureProfiling(launch, vm, runConfig, monitor);
		
		try {
			runner.run(runConfig, launch, monitor);
			previewServer.setProcess(launch.getProcesses()[0]);
		} catch (Exception e) {
			// ignore - process failed
		}
	}
}
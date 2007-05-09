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
package org.eclipse.wst.server.preview.adapter.internal.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.eclipse.osgi.service.environment.Constants;

import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerUtil;
import org.osgi.framework.Bundle;
/**
 * 
 */
public class PreviewLaunchConfigurationDelegate extends LaunchConfigurationDelegate {
	private static final String[] REQUIRED_BUNDLE_IDS = new String[] {
		"org.eclipse.core.runtime",
		"org.apache.commons.logging",
		"javax.servlet",
		"javax.servlet.jsp",
		"org.mortbay.jetty",
		"org.eclipse.wst.server.preview"
	};

	private static final String[] fgCandidateJavaFiles = {"javaw", "javaw.exe", "java",
		"java.exe", "j9w", "j9w.exe", "j9", "j9.exe"};
	private static final String[] fgCandidateJavaLocations = {"bin" + File.separatorChar,
		"jre" + File.separatorChar + "bin" + File.separatorChar};

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
		//String[] jars = new String[size];
		StringBuffer cp = new StringBuffer();
		for (int i = 0; i < size; i++) {
			Bundle b = Platform.getBundle(REQUIRED_BUNDLE_IDS[i]);
			IPath path = null;
			if (b != null)
				path = PreviewRuntime.getJarredPluginPath(b);
			if (path == null)
				throw new CoreException(new Status(IStatus.ERROR, PreviewPlugin.PLUGIN_ID, "Could not find required bundle " + REQUIRED_BUNDLE_IDS[i]));
			
			if (i == 5 && path.append("bin").toFile().exists())
				path = path.append("bin");
			
			if (i > 0)
				cp.append(File.pathSeparator);
			cp.append(path.toOSString());
		}
		
		List cmds = new ArrayList();
		
		// jre
		File java = getJavaExecutable();
		if (java == null)
			throw new CoreException(new Status(IStatus.ERROR, PreviewPlugin.PLUGIN_ID, "Could not find JRE executable"));
		
		cmds.add(java.getAbsolutePath());
		
		cmds.add("-classpath");
		cmds.add(cp.toString());
		
		cmds.add(MAIN_CLASS);
		
		cmds.add(previewServer.getTempDirectory().append("preview.xml").toOSString());
		
		//setDefaultSourceLocator(launch, configuration);
		
		// launch the configuration
		previewServer.setupLaunch(launch, mode, monitor);
		
		try {
			String[] cmdLine = new String[cmds.size()];
			cmds.toArray(cmdLine);
			Process p = DebugPlugin.exec(cmdLine, null);
			if (p != null) {
				IProcess pr = DebugPlugin.newProcess(launch, p, "Preview!");
				if (pr != null)
					launch.addProcess(pr);
			}
			previewServer.setProcess(launch.getProcesses()[0]);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Problem creating preview process");
		}
	}

	protected static File getJavaExecutable() {
		// do not detect on the Mac OS
		if (Platform.getOS().equals(Constants.OS_MACOSX))
			return null;
		
		// Retrieve the 'java.home' system property.  If that directory doesn't exist, 
		// return null.
		File javaHome; 
		try {
			javaHome = new File(System.getProperty("java.home")).getCanonicalFile();
		} catch (IOException e) {
			return null;
		}
		if (!javaHome.exists())
			return null;
		
		// Find the 'java' executable file under the java home directory.  If it can't be
		// found, return null.
		return findJavaExecutable(javaHome);
	}

	protected static File findJavaExecutable(File vmInstallLocation) {
		// Try each candidate in order.  The first one found wins.  Thus, the order
		// of fgCandidateJavaLocations and fgCandidateJavaFiles is significant.
		for (int i = 0; i < fgCandidateJavaFiles.length; i++) {
			for (int j = 0; j < fgCandidateJavaLocations.length; j++) {
				File javaFile = new File(vmInstallLocation, fgCandidateJavaLocations[j] + fgCandidateJavaFiles[i]);
				if (javaFile.isFile()) {
					return javaFile;
				}				
			}
		}		
		return null;							
	}
}
/*******************************************************************************
 * Copyright (c) 2007, 2012 IBM Corporation and others.
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
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.ServerUtil;
import org.osgi.framework.Bundle;
/**
 * 
 */
public class PreviewLaunchConfigurationDelegate extends LaunchConfigurationDelegate {
	// To support running from the workbench, be careful when adding and removing 
	// bundles to this array. For instance, org.eclipse.wst.server.preview is a 
	// plug-in that can be checked out in the workbench. If it is, the classpath
	// needs to point to the bin directory of this plug-in. This plug-in is tracked
	// in the array with CLASSPATH_BIN_INDEX_PREVIEW_SERVER. Therefore, when updating
	// this array, please ensure the index of org.eclipse.wst.server.preview 
	// corresponds to CLASSPATH_BIN_INDEX_PREVIEW_SERVER	
	private static final String[] REQUIRED_BUNDLE_IDS = new String[] {
		"javax.servlet",
		"org.eclipse.jetty.server",
		"org.eclipse.jetty.util",
		"org.eclipse.jetty.continuation",
		"org.eclipse.jetty.http",
		"org.eclipse.jetty.io",
		"org.eclipse.wst.server.preview"
	};
	
	// The index of org.eclipse.wst.server.preview in REQUIRED_BUNDLE_IDS, for supporting
	// running on the workbench when the plug-in is checked out
	private static final int CLASSPATH_BIN_INDEX_PREVIEW_SERVER = REQUIRED_BUNDLE_IDS.length-1;	

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
		
		if (server.shouldPublish() && ServerCore.isAutoPublishing())
			server.publish(IServer.PUBLISH_INCREMENTAL, monitor);
		
		PreviewServerBehaviour previewServer = (PreviewServerBehaviour) server.loadAdapter(PreviewServerBehaviour.class, null);
		
		StringBuffer cp = new StringBuffer();
		int size = REQUIRED_BUNDLE_IDS.length;
		for (int i = 0; i < size; i++) {
			Bundle b = Platform.getBundle(REQUIRED_BUNDLE_IDS[i]);
			IPath path = null;
			if (b != null)
				path = PreviewRuntime.getJarredPluginPath(b);
			if (path == null)
				throw new CoreException(new Status(IStatus.ERROR, PreviewPlugin.PLUGIN_ID, "Could not find required bundle " + REQUIRED_BUNDLE_IDS[i]));
			
			// run from workbench support
			if (i == CLASSPATH_BIN_INDEX_PREVIEW_SERVER && path.append("bin").toFile().exists())
				path = path.append("bin");
			
			if (i > 0)
				cp.append(File.pathSeparator);
			cp.append(path.toOSString());
		}
		
		List<String> cmds = new ArrayList<String>();
		
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
				IProcess pr = DebugPlugin.newProcess(launch, p, cmdLine[0]);
				pr.setAttribute(IProcess.ATTR_CMDLINE, renderCommandLine(cmdLine));
				launch.addProcess(pr);
				previewServer.addProcessListener(pr);
			}
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Problem creating preview process");
		}
	}

	/**
	 * Prepares the command line from the specified array of strings.
	 * 
	 * @param commandLine
	 * @return the command line string
	 */
	protected static String renderCommandLine(String[] commandLine) {
		if (commandLine.length < 1)
			return ""; //$NON-NLS-1$
		StringBuffer buf= new StringBuffer();
		for (int i= 0; i < commandLine.length; i++) {
			buf.append(' ');
			char[] characters= commandLine[i].toCharArray();
			StringBuffer command= new StringBuffer();
			boolean containsSpace= false;
			for (int j = 0; j < characters.length; j++) {
				char character= characters[j];
				if (character == '\"') {
					command.append('\\');
				} else if (character == ' ') {
					containsSpace = true;
				}
				command.append(character);
			}
			if (containsSpace) {
				buf.append('\"');
				buf.append(command.toString());
				buf.append('\"');
			} else {
				buf.append(command.toString());
			}
		}	
		return buf.toString();
	}

	protected static File getJavaExecutable() {
		// do not detect on the Mac OS
		if (Platform.getOS().equals(Constants.OS_MACOSX))
			return null;
		
		// retrieve the 'java.home' system property. If that directory doesn't exist, return null
		File javaHome; 
		try {
			javaHome = new File(System.getProperty("java.home")).getCanonicalFile();
		} catch (IOException e) {
			return null;
		}
		if (!javaHome.exists())
			return null;
		
		// find the 'java' executable file under the java home directory. If it can't be
		// found, return null
		return findJavaExecutable(javaHome);
	}

	protected static File findJavaExecutable(File vmInstallLocation) {
		// try each candidate in order, the first one found wins. Thus, the order
		// of fgCandidateJavaLocations and fgCandidateJavaFiles is significant
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
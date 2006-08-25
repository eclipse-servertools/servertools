/*******************************************************************************
 * Copyright (c) 2005 BEA Systems, Inc. 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    rfrost@bea.com
 *    tyip@bea.com
 *    
 *    Based on GenericServerLaunchConfigurationDelegate by Gorkem Ercan
 *******************************************************************************/

package org.eclipse.jst.server.generic.core.internal;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.tools.ant.taskdefs.Execute;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.RuntimeProcess;
import org.eclipse.jdt.launching.AbstractJavaLaunchConfigurationDelegate;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerUtil;
import org.eclipse.wst.server.core.model.ServerBehaviourDelegate;

/**
 * <p>Extension of <code>AbstractJavaLaunchConfigurationDelegate</code> that supports 
 * servers which are started/stopped via external executables (e.g. scripts).</p>
 * 
 * <p>Note: <code>AbstractJavaLaunchConfigurationDelegate</code> is extended simply to take advantage
 * of a set of useful code that is not directly related to launching a JVM-based app.</p>
 */
public class ExternalLaunchConfigurationDelegate extends AbstractJavaLaunchConfigurationDelegate {

	/**
	 * Identifier for the executable server configuration type
	 * (value <code>"org.eclipse.jst.server.generic.core.ExternalLaunchConfigurationType"</code>).
	 */
	public static final String ID_EXTERNAL_LAUNCH_TYPE = CorePlugin.PLUGIN_ID + ".ExternalLaunchConfigurationType"; //$NON-NLS-1$

	/**
	 * Name of the launch configuration attribute that holds the external executable commandline.
	 */
	public static final String COMMANDLINE = CorePlugin.PLUGIN_ID  + ".COMMANDLINE"; //$NON-NLS-1$

	/**
	 * Name of the launch configuration attribute that holds a descriptive name for the external executable.
	 */
	public static final String EXECUTABLE_NAME = CorePlugin.PLUGIN_ID + ".EXECUTABLE_NAME"; //$NON-NLS-1$

	/**
	 * Name of the launch configuration attribute that holds the debug port.
	 */
	public static final String DEBUG_PORT = CorePlugin.PLUGIN_ID + ".DEBUG_PORT"; //$NON-NLS-1$
	
	/**
	 * Default value for the descriptive name for the external executable.
	 */
	public static final String DEFAULT_EXECUTABLE_NAME = "External Generic Server"; //$NON-NLS-1$
	
	/**
	 * Debugging launch configuration delegate.
	 */
	private static ExternalDebugLaunchConfigurationDelegate debuggingDelegate =
        new ExternalDebugLaunchConfigurationDelegate();
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ILaunchConfigurationDelegate#launch(org.eclipse.debug.core.ILaunchConfiguration, java.lang.String, org.eclipse.debug.core.ILaunch, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void launch(ILaunchConfiguration configuration, 
			       String mode,
			       ILaunch launch, 
			       IProgressMonitor monitor) throws CoreException {
		IServer server = ServerUtil.getServer(configuration);
		if (server == null) {
		    abort(GenericServerCoreMessages.missingServer, null, IJavaLaunchConfigurationConstants.ERR_INTERNAL_ERROR);
		}	

		ExternalServerBehaviour serverBehavior = (ExternalServerBehaviour) server.loadAdapter(ServerBehaviourDelegate.class, null);
		
		// initialize the server, check the ports and start the PingThread that will check 
		// server state
		serverBehavior.setupLaunch(launch, mode, monitor);
		
		// get the "external" command
		String commandline = configuration.getAttribute(COMMANDLINE, (String) null);
		if (commandline == null || commandline.length() == 0) {
			abort(GenericServerCoreMessages.commandlineUnspecified, null, IJavaLaunchConfigurationConstants.ERR_INTERNAL_ERROR);			
		}
		
		// parse the "external" command into multiple args
		String[] cmdArgs = DebugPlugin.parseArguments(commandline);
		// get the "programArguments", parsed into multiple args
		String[] pgmArgs = DebugPlugin.parseArguments(getProgramArguments(configuration));
		
		// Create the full array of cmds
		String[] cmds = new String[cmdArgs.length + pgmArgs.length];
		System.arraycopy(cmdArgs, 0, cmds, 0, cmdArgs.length);
		System.arraycopy(pgmArgs, 0, cmds, cmdArgs.length, pgmArgs.length);
		
		// get a descriptive name for the executable
		String executableName = configuration.getAttribute(EXECUTABLE_NAME, DEFAULT_EXECUTABLE_NAME);
		
		// get the executable environment
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		String[] env = manager.getEnvironment(configuration);
		
		// get the working directory
		File workingDir = verifyWorkingDirectory(configuration);
		if (workingDir == null) {
			abort(GenericServerCoreMessages.workingdirUnspecified, null, IJavaLaunchConfigurationConstants.ERR_INTERNAL_ERROR);			
		}
		
		// Launch the executable for the configuration using the Ant Execute class
		try {
			Process process = Execute.launch(null, cmds, env, workingDir, true);
			serverBehavior.startPingThread();
			IProcess runtimeProcess = new RuntimeProcess(launch, process, executableName, null);
			launch.addProcess(runtimeProcess);
			serverBehavior.setProcess(runtimeProcess);
		} catch (IOException ioe) {
			abort(GenericServerCoreMessages.errorLaunchingExecutable, ioe,  IJavaLaunchConfigurationConstants.ERR_INTERNAL_ERROR);
		}

		if (mode.equals("debug")) { //$NON-NLS-1$
			ILaunchConfigurationWorkingCopy wc = createDebuggingConfig(configuration);
			// if we're launching the debugging we need to wait for the config to start
			// before launching the debugging session
			serverBehavior.setDebuggingConfig(wc, mode, launch, monitor);
		}
	}

	private ILaunchConfigurationWorkingCopy createDebuggingConfig(ILaunchConfiguration configuration) 
	throws CoreException {
        ILaunchConfigurationWorkingCopy wc = configuration.getWorkingCopy();
        setDebugArgument(wc, IJavaLaunchConfigurationConstants.ATTR_CONNECT_MAP, "hostname", "localhost");  //$NON-NLS-1$//$NON-NLS-2$
        String port = configuration.getAttribute(DEBUG_PORT, (String) null);
        if (port==null || port.length()==0) {
        	abort(GenericServerCoreMessages.debugPortUnspecified, null, IJavaLaunchConfigurationConstants.ERR_INTERNAL_ERROR);
        }
        setDebugArgument(wc, IJavaLaunchConfigurationConstants.ATTR_CONNECT_MAP, "port", port); //$NON-NLS-1$
        return wc;
	}
	
	/**
	 * Starts the debugging session
	 */
	protected static void startDebugging(ILaunchConfigurationWorkingCopy wc,
			       						 String mode,
			       						 ILaunch launch, 
			       						 IProgressMonitor monitor) throws CoreException {
		Trace.trace(Trace.FINEST, "Starting debugging"); //$NON-NLS-1$
		debuggingDelegate.launch(wc, mode, launch, monitor);
	}
	  
    private void setDebugArgument(ILaunchConfigurationWorkingCopy config, String attribKey, String key, String arg) {
        try {
            Map args = config.getAttribute(attribKey, (Map)null);
            if (args!=null) {
                args = new HashMap(args);
            } else {
                args = new HashMap();
            }
            args.put(key, String.valueOf(arg));
            config.setAttribute(attribKey, args);
        } catch (CoreException ce) {
            // ignore
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

/*******************************************************************************
 * Copyright (c) 2005, 2009 BEA Systems, Inc. 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    rfrost@bea.com - initial API and implementation
 *    
 *    Based on GenericServerBehavior by Gorkem Ercan
 *******************************************************************************/
package org.eclipse.jst.server.generic.core.internal;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jst.server.generic.internal.xml.Resolver;
import org.eclipse.jst.server.generic.servertype.definition.External;
import org.eclipse.jst.server.generic.servertype.definition.ServerRuntime;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerPort;
import org.eclipse.wst.server.core.util.SocketUtil;

/**
 * Subclass of <code>GenericServerBehavior</code> that supports 
 * servers which are started/stopped via external executables (e.g. scripts).
 */
public class ExternalServerBehaviour extends GenericServerBehaviour {
	
	// config for debugging session
	private ILaunchConfigurationWorkingCopy fLaunchConfigurationWC;
    private String fMode;
    private ILaunch fLaunch; 
    private IProgressMonitor fProgressMonitor;
    
    /**
     * Override to reset the status if the state was unknown
     * @param force 
     */
    public void stop(boolean force) {
    	resetStatus(getServer().getServerState());
    	super.stop(force);
    }

    /**
     * Override to set status to unknown if the port was in use and to reset the status if the state was 
     * unknown and an exception was not thrown. Will want to change logic once external generic server pings
     * server process to determine state instead of maintaining handle to process. 
     */
    protected void setupLaunch(ILaunch launch, String launchMode, IProgressMonitor monitor) throws CoreException {
    	int state = getServer().getServerState();
    	try {
    		super.setupLaunch(launch, launchMode, monitor);
    	} catch (CoreException ce) {
    		ServerPort portInUse = portInUse();
    		if (portInUse != null) {
    			Trace.trace(Trace.WARNING, "Port " + portInUse.getPort() + " is currently in use");  //$NON-NLS-1$//$NON-NLS-2$
				Status status = new Status(IStatus.WARNING, CorePlugin.PLUGIN_ID, IStatus.OK, 
							NLS.bind(GenericServerCoreMessages.errorPortInUse,Integer.toString(portInUse.getPort()),portInUse.getName()), null);
				setServerStatus(status);
				setServerState(IServer.STATE_UNKNOWN);
    		}
    		throw ce;
    	}
    	resetStatus(state);
    }
    
    private ServerPort portInUse() {
    	ServerPort[] ports = getServer().getServerPorts(null);
    	ServerPort sp;
    	for(int i=0;i<ports.length;i++){
    		sp = ports[i];
    		if (SocketUtil.isPortInUse(sp.getPort(), 5)) {
    			return sp;
    		}
    	}
    	return null;
	}
    
	/**
	 * Override to trigger the launch of the debugging session (if appropriate).
	 */
	protected synchronized void setServerStarted() {
		if (fLaunchConfigurationWC != null) {
			try {
				setupSourceLocator( fLaunch );
				ExternalLaunchConfigurationDelegate.startDebugging(fLaunchConfigurationWC, fMode, fLaunch, fProgressMonitor);
			} catch (CoreException ce) {
				// failed to start debugging, so set mode to run
				setMode(ILaunchManager.RUN_MODE);
				final Status status = new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, 1,
							GenericServerCoreMessages.errorStartingExternalDebugging, ce); 
				CorePlugin.getDefault().getLog().log(status);
				Trace.trace(Trace.SEVERE, GenericServerCoreMessages.errorStartingExternalDebugging, ce);
			} finally {
				clearDebuggingConfig();
			}
		}
		setServerState(IServer.STATE_STARTED);
 	}
	
	/**
	 * Subclasses may override this method to replace default source locator or add additional
	 * sourceLookupParticipant, if necessary 
	 * @param launch 	the ILaunch object of the debug session
	 */
	protected void setupSourceLocator(ILaunch launch) {
        //nothing to do
	}

	/*
	 * If the server state is unknown, reset the status to OK
	 */
	private void resetStatus(int state) {
		if (state == IServer.STATE_UNKNOWN) {
			setServerStatus(null);
		}
	}
	
	/**
	 * Since terminate() is called during restart, need to override to
	 * call shutdown instead of just killing the original process.
	 */
	protected void terminate() {
		int state = getServer().getServerState();
		if (state == IServer.STATE_STOPPED) 
    		return;
    
		// cache a ref to the current process
		IProcess currentProcess = process;
		// set the process var to null so that GenericServerBehavior.setProcess()
		// will grab the stop executable (and declare the server stopped when it exits)
		process = null;

		// execute the standard shutdown
		shutdown(state);
		
		// if the shutdown did not terminate the process, forcibly terminate it
		try {
    		if (currentProcess != null && !currentProcess.isTerminated()) {
    			Trace.trace(Trace.FINER, "About to kill process: " + currentProcess); //$NON-NLS-1$
    			currentProcess.terminate();
    			currentProcess = null;
    		}
    	} catch (Exception e) {
    		Trace.trace(Trace.SEVERE, "Error killing the process", e); //$NON-NLS-1$
    	}
	}
	
	/**
	 * Override superclass method to correctly setup the launch configuration for starting an external
	 * server.
	 * @param workingCopy
	 * @param monitor
	 * @throws CoreException 
	 */
	public void setupLaunchConfiguration(ILaunchConfigurationWorkingCopy workingCopy,
										 IProgressMonitor monitor) throws CoreException {
		clearDebuggingConfig();
		ServerRuntime serverDef = getServerDefinition();
		Resolver resolver = serverDef.getResolver();
		workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY,
					resolver.resolveProperties(serverDef.getStart().getWorkingDirectory()));
		String external = resolver.resolveProperties(getExternalForOS(serverDef.getStart().getExternal()));
		workingCopy.setAttribute(ExternalLaunchConfigurationDelegate.COMMANDLINE, external);
		workingCopy.setAttribute(ExternalLaunchConfigurationDelegate.DEBUG_PORT, 
					resolver.resolveProperties(serverDef.getStart().getDebugPort()));
		workingCopy.setAttribute(ExternalLaunchConfigurationDelegate.HOST, getServer().getHost());
		
		// just use the commandline for now
		workingCopy.setAttribute(ExternalLaunchConfigurationDelegate.EXECUTABLE_NAME, external);
        Map environVars = getEnvironmentVariables(getServerDefinition().getStart());
        if(!environVars.isEmpty()){
        	workingCopy.setAttribute(ILaunchManager.ATTR_ENVIRONMENT_VARIABLES,environVars);
        }
        String existingProgArgs  = workingCopy.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, (String)null);
        String serverProgArgs =  getProgramArguments();
        if(existingProgArgs==null || existingProgArgs.indexOf(serverProgArgs)<0) {
            workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS,serverProgArgs);
        }
	}

	/*
	 * Returns the first external whose "os" attribute matches (case insensitive) the beginning 
	 * of the name of the current OS (as determined by the System "os.name" property). If
	 * no such match is found, returns the first external that does not have an OS attribute.
	 */
	private String getExternalForOS(List externals) {
		String currentOS = System.getProperty("os.name").toLowerCase(); //$NON-NLS-1$
		External external;
		String matchingExternal = null;
		String externalOS;
		Iterator i = externals.iterator();
		while (i.hasNext()) {
			external= (External) i.next();
			externalOS = external.getOs();
			if (externalOS == null) {
				if (matchingExternal == null) {
					matchingExternal = external.getValue();
				}
			} else if (currentOS.startsWith(externalOS.toLowerCase())) {
				matchingExternal = external.getValue();
				break;
			}
		}
		return matchingExternal;
	}

	/**
     * Returns the String ID of the launch configuration type.
     * @return launchTypeID
     */
	protected String getConfigTypeID() {
		return ExternalLaunchConfigurationDelegate.ID_EXTERNAL_LAUNCH_TYPE;
	}

	/**
	 * Returns the String name of the stop launch configuration.
	 * @return launcherName
	 */
	protected String getStopLaunchName() {
		return GenericServerCoreMessages.externalStopLauncher;
	}
	
	/**
	 * Sets up the launch configuration for stopping the server.
	 * 
	 */
	protected void setupStopLaunchConfiguration(GenericServerRuntime runtime, ILaunchConfigurationWorkingCopy wc) {
		clearDebuggingConfig();
		ServerRuntime serverDef = getServerDefinition();
		Resolver resolver = serverDef.getResolver(); 
		wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY,
					resolver.resolveProperties(serverDef.getStop().getWorkingDirectory()));
		String external = resolver.resolveProperties(getExternalForOS(serverDef.getStop().getExternal()));
		wc.setAttribute(ExternalLaunchConfigurationDelegate.COMMANDLINE, external);
		// just use commandline for now
        Map environVars = getEnvironmentVariables(getServerDefinition().getStop());
        if(!environVars.isEmpty()){
        	wc.setAttribute(ILaunchManager.ATTR_ENVIRONMENT_VARIABLES,environVars);
        }
		wc.setAttribute(
				IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS,
				resolver.resolveProperties(serverDef.getStop().getProgramArgumentsAsString()));
		wc.setAttribute(ExternalLaunchConfigurationDelegate.EXECUTABLE_NAME, external); 	
		wc.setAttribute(ATTR_SERVER_ID, getServer().getId());
	}
	
	/**
	 * Sets the configuration to use for launching a debugging session
	 */
	protected synchronized void setDebuggingConfig(ILaunchConfigurationWorkingCopy wc,
					 			      String mode,
					 			      ILaunch launch, 
					 			      IProgressMonitor monitor) {
		this.fLaunchConfigurationWC = wc;
		this.fMode = mode;
		this.fLaunch = launch;
		this.fProgressMonitor = monitor;
	}
	
	private synchronized void clearDebuggingConfig() {
		this.fLaunchConfigurationWC = null;
		this.fMode = null;
		this.fLaunch = null;
		this.fProgressMonitor = null;
	}
	
	
}

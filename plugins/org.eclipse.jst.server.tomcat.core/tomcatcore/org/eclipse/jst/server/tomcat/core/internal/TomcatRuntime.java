/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.jst.server.tomcat.core.internal;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.Launch;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamsProxy;
import org.eclipse.jdt.internal.launching.StandardVMType;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstallType;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jst.server.tomcat.core.ITomcatRuntime;
import org.eclipse.jst.server.tomcat.core.ITomcatRuntimeWorkingCopy;

import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.model.RuntimeDelegate;
/**
 * 
 */
public class TomcatRuntime extends RuntimeDelegate implements ITomcatRuntime, ITomcatRuntimeWorkingCopy {
	protected static final String PROP_VM_INSTALL_TYPE_ID = "vm-install-type-id";
	protected static final String PROP_VM_INSTALL_ID = "vm-install-id";
	
	protected static Map sdkMap = new HashMap(2);

	public TomcatRuntime() {
		// do nothing
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.IRuntime#getLocation()
	 */
	public ITomcatVersionHandler getVersionHandler() {
		IRuntimeType type = getRuntime().getRuntimeType();
		return TomcatPlugin.getTomcatVersionHandler(type.getId());
	}

	protected String getVMInstallTypeId() {
		return getAttribute(PROP_VM_INSTALL_TYPE_ID, (String)null);
	}

	protected String getVMInstallId() {
		return getAttribute(PROP_VM_INSTALL_ID, (String)null);
	}

	public IVMInstall getVMInstall() {
		try {
			IVMInstallType vmInstallType = JavaRuntime.getVMInstallType(getVMInstallTypeId());
			IVMInstall[] vmInstalls = vmInstallType.getVMInstalls();
			int size = vmInstalls.length;
			String id = getVMInstallId();
			for (int i = 0; i < size; i++) {
				if (id.equals(vmInstalls[i].getId()))
					return vmInstalls[i];
			}
		} catch (Exception e) {
			// ignore
		}
		return null;
	}

	public List getRuntimeClasspath() {
		return getVersionHandler().getRuntimeClasspath(getRuntime().getLocation());
	}

	/**
	 * Verifies the Tomcat installation directory. If it is
	 * correct, true is returned. Otherwise, the user is notified
	 * and false is returned.
	 * @return boolean
	 */
	public boolean verifyLocation() {
		return getVersionHandler().verifyInstallPath(getRuntime().getLocation());
	}
	
	/*
	 * Validate the runtime
	 */
	public IStatus validate() {
		IStatus status = super.validate();
		if (!status.isOK())
			return status;
	
		if (!verifyLocation())
			return new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, TomcatPlugin.getResource("%errorInstallDir"), null);
		else if (getVMInstall() == null)
			return new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, TomcatPlugin.getResource("%errorJRE"), null);

		// check for tools.jar (contains the javac compiler on Windows & Linux) to see whether
		// Tomcat will be able to compile JSPs.
		boolean found = false;
		File file = getVMInstall().getInstallLocation();
		if (file != null) {
			File toolsJar = new File(file, "lib" + File.separator + "tools.jar");
			if (toolsJar.exists())
				found = true;
		}
		
		// on Mac, tools.jar is merged into classes.zip. if tools.jar wasn't found,
		// try loading the javac class by running a check inside the VM
		if (!found)
			found = checkForCompiler(getVMInstall().getInstallLocation());
		
		if (!found)
			return new Status(IStatus.WARNING, TomcatPlugin.PLUGIN_ID, 0, TomcatPlugin.getResource("%warningJRE"), null);
		
		return new Status(IStatus.OK, TomcatPlugin.PLUGIN_ID, 0, "", null);
	}

	public void setDefaults() {
		IVMInstall vmInstall = JavaRuntime.getDefaultVMInstall();
		setVMInstall(vmInstall.getVMInstallType().getId(), vmInstall.getId());
		
		IRuntimeType type = getRuntimeWorkingCopy().getRuntimeType();
		getRuntimeWorkingCopy().setLocation(new Path(TomcatPlugin.getPreference("location" + type.getId())));
	}

	public void setVMInstall(IVMInstall vmInstall) {
		if (vmInstall == null) {
			setVMInstall(null, null);
		} else
			setVMInstall(vmInstall.getVMInstallType().getId(), vmInstall.getId());
	}
	
	protected void setVMInstall(String typeId, String id) {
		if (typeId == null)
			setAttribute(PROP_VM_INSTALL_TYPE_ID, (String)null);
		else
			setAttribute(PROP_VM_INSTALL_TYPE_ID, typeId);
		
		if (id == null)
			setAttribute(PROP_VM_INSTALL_ID, (String)null);
		else
			setAttribute(PROP_VM_INSTALL_ID, id);
	}
	
	/**
	 * Checks for the existance of the Java compiler in the given java
	 * executable. A main program is run (<code>org.eclipse.jst.tomcat.core.
	 * internal.ClassDetector</code>), that dumps a true or false value
	 * depending on whether the compiler is found. This output is then
	 * parsed and cached for future reference.
	 * 
	 * @return true if the compiler was found
	 */	
	protected boolean checkForCompiler(File javaHome) {
		// first try the cache
		try {
			Boolean b = (Boolean) sdkMap.get(javaHome);
			return b.booleanValue();
		} catch (Exception e) {
			// ignore
		}

		// locate tomcatcore.jar - it contains the class detector main program
		File file = TomcatPlugin.getFileInPlugin(new Path("tomcatcore.jar"));
		if (file != null && file.exists()) {	
			File javaExecutable = StandardVMType.findJavaExecutable(javaHome);
			String javaExecutablePath = javaExecutable.getAbsolutePath();
			String[] cmdLine = new String[] {javaExecutablePath, "-classpath", file.getAbsolutePath(), "org.eclipse.jst.server.tomcat.core.internal.ClassDetector", "com.sun.tools.javac.Main"};
			Process p = null;
			try {
				p = Runtime.getRuntime().exec(cmdLine);
				IProcess process = DebugPlugin.newProcess(new Launch(null, ILaunchManager.RUN_MODE, null), p, "Compiler Detection");
				for (int i= 0; i < 200; i++) {
					// wait no more than 10 seconds (200 * 50 mils)
					if (process.isTerminated()) {
						break;
					}
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						// ignore
					}
				}
				IStreamsProxy streamsProxy = process.getStreamsProxy();
				String text = null;
				if (streamsProxy != null) {
					text = streamsProxy.getOutputStreamMonitor().getContents();
				
					if (text != null && text.length() > 0) {
						boolean found = false;
						if ("true".equals(text))
							found = true;
						
						sdkMap.put(javaHome, new Boolean(found));
						return found;
					}
				}
			} catch (IOException ioe) {
				TomcatPlugin.log(ioe);
			} finally {
				if (p != null) {
					p.destroy();
				}
			}
		}
		
		// log error that we were unable to check for the compiler
		TomcatPlugin.log(MessageFormat.format("Failed compiler check for {0}", new String[] { javaHome.getAbsolutePath() }));
		return false;
	}
}
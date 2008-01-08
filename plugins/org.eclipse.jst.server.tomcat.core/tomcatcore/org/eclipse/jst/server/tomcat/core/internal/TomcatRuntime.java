/*******************************************************************************
 * Copyright (c) 2003, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.tomcat.core.internal;

import java.io.File;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.Launch;
import org.eclipse.debug.core.model.IStreamsProxy;
import org.eclipse.jdt.launching.*;

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
	
	public boolean isUsingDefaultJRE() {
		return getVMInstallTypeId() == null;
	}

	public IVMInstall getVMInstall() {
		if (getVMInstallTypeId() == null)
			return JavaRuntime.getDefaultVMInstall();
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
	public IStatus verifyLocation() {
		return getVersionHandler().verifyInstallPath(getRuntime().getLocation());
	}
	
	/*
	 * Validate the runtime
	 */
	public IStatus validate() {
		IStatus status = super.validate();
		if (!status.isOK())
			return status;
	
		status = verifyLocation();
		if (!status.isOK())
			return status;
//			return new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, Messages.errorInstallDir, null);
		// don't accept trailing space since that can cause startup problems
		if (getRuntime().getLocation().hasTrailingSeparator())
			return new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, Messages.errorInstallDirTrailingSlash, null);
		if (getVMInstall() == null)
			return new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, Messages.errorJRE, null);

		// check for tools.jar (contains the javac compiler on Windows & Linux) to see whether
		// Tomcat will be able to compile JSPs.
		boolean found = false;
		File file = getVMInstall().getInstallLocation();
		if (file != null) {
			File toolsJar = new File(file, "lib" + File.separator + "tools.jar");
			if (toolsJar.exists())
				found = true;
		}
		
		// on Tomcat 5.5 and 6.0, the Eclipse JDT compiler is used for JSP's
		String id = getRuntime().getRuntimeType().getId();
		if (!found) {
			if (id != null && (id.indexOf("55") > 0 || id.indexOf("60") > 0))
				found = true;
		}
		
		// on Mac, tools.jar is merged into classes.zip. if tools.jar wasn't found,
		// try loading the javac class by running a check inside the VM
		if (!found) {
			String os = Platform.getOS();
			if (os != null && os.toLowerCase().indexOf("mac") >= 0)
				found = checkForCompiler();
		}
		
		if (!found)
			return new Status(IStatus.WARNING, TomcatPlugin.PLUGIN_ID, 0, Messages.warningJRE, null);
		
		File f = getRuntime().getLocation().append("conf").toFile();
		File[] conf = f.listFiles();
		if (conf != null) {
			int size = conf.length;
			for (int i = 0; i < size; i++) {
				if (!f.canRead())
					return new Status(IStatus.WARNING, TomcatPlugin.PLUGIN_ID, 0, Messages.warningCantReadConfig, null);
			}
		}
		
		// For Tomcat 6.0, ensure we have J2SE 5.0
		if (id != null && id.indexOf("60") > 0) {
			IVMInstall vmInstall = getVMInstall();
			if (vmInstall instanceof IVMInstall2) {
				String javaVersion = ((IVMInstall2)vmInstall).getJavaVersion();
				if (javaVersion != null && javaVersion.compareTo("1.5") < 0) {
					return new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, Messages.errorJRETomcat60, null);
				}
			}
		}
		
		return Status.OK_STATUS;
	}

	/**
	 * @see RuntimeDelegate#setDefaults(IProgressMonitor)
	 */
	public void setDefaults(IProgressMonitor monitor) {
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
	 * Checks for the existence of the Java compiler in the given java
	 * executable. A main program is run (<code>org.eclipse.jst.tomcat.core.
	 * internal.ClassDetector</code>), that dumps a true or false value
	 * depending on whether the compiler is found. This output is then
	 * parsed and cached for future reference.
	 * 
	 * @return true if the compiler was found
	 */	
	protected boolean checkForCompiler() {
		// first try the cache
		File javaHome = getVMInstall().getInstallLocation();
		try {
			Boolean b = (Boolean) sdkMap.get(javaHome);
			return b.booleanValue();
		} catch (Exception e) {
			// ignore
		}

		// locate tomcatcore.jar - it contains the class detector main program
		File file = TomcatPlugin.getPlugin();
		if (file != null && file.exists()) {
			IVMRunner vmRunner = getVMInstall().getVMRunner(ILaunchManager.RUN_MODE);
			VMRunnerConfiguration config = new VMRunnerConfiguration("org.eclipse.jst.server.tomcat.core.internal.ClassDetector", new String[] { file.getAbsolutePath() });
			config.setProgramArguments(new String[] { "com.sun.tools.javac.Main" });
			ILaunch launch = new Launch(null, ILaunchManager.RUN_MODE, null);
			try {
				vmRunner.run(config, launch, null);
				for (int i = 0; i < 600; i++) {
					// wait no more than 30 seconds (600 * 50 mils)
					if (launch.isTerminated()) {
						break;
					}
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						// ignore
					}
				}
				IStreamsProxy streamsProxy = launch.getProcesses()[0].getStreamsProxy();
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
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "Error checking for JDK", e);
			} finally {
				if (!launch.isTerminated()) {
					try {
						launch.terminate();
					} catch (Exception ex) {
						// ignore
					}
				}
			}
		}
		
		// log error that we were unable to check for the compiler
		TomcatPlugin.log(MessageFormat.format("Failed compiler check for {0}", new String[] { javaHome.getAbsolutePath() }));
		return false;
	}
}

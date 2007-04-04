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

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.*;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstallType;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jst.server.core.IJavaRuntime;
import org.eclipse.jst.server.core.internal.JavaServerPlugin;
import org.eclipse.jst.server.core.internal.Messages;
import org.eclipse.wst.server.core.model.RuntimeDelegate;
import org.osgi.framework.Bundle;
/**
 * 
 */
public class PreviewRuntime extends RuntimeDelegate implements IJavaRuntime {
	public static final String ID = "org.eclipse.jst.server.preview.runtime";

	protected static final String PROP_VM_INSTALL_TYPE_ID = "vm-install-type-id";
	protected static final String PROP_VM_INSTALL_ID = "vm-install-id";

	/**
	 * Create a new preview runtime.
	 */
	public PreviewRuntime() {
		// do nothing
	}

	/**
	 * Returns the path that corresponds to the specified bundle.
	 * 
	 * @return a path
	 */
	protected static Path getPluginPath(Bundle bundle) {
		try {
			URL installURL = bundle.getEntry("/");
			URL localURL = FileLocator.toFileURL(installURL);
			return new Path(localURL.getFile());
		} catch (IOException ioe) {
			return null;
		}
	}

	protected static IPath getJarredPluginPath(Bundle bundle) {
		Path runtimeLibFullPath = null;
		String jarPluginLocation = bundle.getLocation().substring(7);
		
		// handle case where jars are installed outside of eclipse installation
		Path jarPluginPath = new Path(jarPluginLocation);
		if (jarPluginPath.isAbsolute())
			runtimeLibFullPath = jarPluginPath;
		// handle normal case where all plugins under eclipse install
		else {
			int ind = jarPluginLocation.lastIndexOf(":");
			if (ind > 0)
				jarPluginLocation = jarPluginLocation.substring(ind+1);
			
			String installPath = Platform.getInstallLocation().getURL().getPath();
			runtimeLibFullPath = new Path(installPath+"/"+jarPluginLocation);
		}
		return runtimeLibFullPath;
	}

	protected String getVMInstallTypeId() {
		return getAttribute(PROP_VM_INSTALL_TYPE_ID, (String)null);
	}

	protected String getVMInstallId() {
		return getAttribute(PROP_VM_INSTALL_ID, (String)null);
	}

	/**
	 * @see RuntimeDelegate#setDefaults(IProgressMonitor)
	 */
	public void setDefaults(IProgressMonitor monitor) {
		getRuntimeWorkingCopy().setLocation(new Path(""));
	}

	/**
	 * Returns <code>true</code> if the runtime is using the default JRE.
	 * 
	 * @return <code>true</code> if the runtime is using the default JRE,
	 *    and <code>false</code> otherwise
	 */
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

	/**
	 * @see RuntimeDelegate#validate()
	 */
	public IStatus validate() {
		IStatus status = super.validate();
		if (!status.isOK())
			return status;
		
		if (getVMInstall() == null)
			return new Status(IStatus.ERROR, JavaServerPlugin.PLUGIN_ID, 0, Messages.errorJRE, null);
		
		return Status.OK_STATUS;
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
}
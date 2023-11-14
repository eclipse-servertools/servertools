/*******************************************************************************
 * Copyright (c) 2007, 2023 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.preview.adapter.internal.core;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstallType;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jst.server.core.IJavaRuntimeWorkingCopy;
import org.eclipse.jst.server.preview.adapter.internal.Messages;
import org.eclipse.jst.server.preview.adapter.internal.PreviewPlugin;
import org.eclipse.wst.server.core.model.RuntimeDelegate;
import org.osgi.framework.Bundle;
/**
 * J2EE preview runtime.
 */
public class PreviewRuntime extends RuntimeDelegate implements IJavaRuntimeWorkingCopy {
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
		try {
			File file = FileLocator.getBundleFile(bundle);
			return new Path(file.getCanonicalPath());
		} catch (IOException e) {
			// ignore, return null
			return null;
		}
	}

	protected String getVMInstallTypeId() {
		return getAttribute(PROP_VM_INSTALL_TYPE_ID, (String)null);
	}

	protected String getVMInstallId() {
		return getAttribute(PROP_VM_INSTALL_ID, (String)null);
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
			String id = getVMInstallId();
			for (IVMInstall vmInstall : vmInstalls) {
				if (id.equals(vmInstall.getId()))
					return vmInstall;
			}
		} catch (Exception e) {
			PreviewPlugin.getInstance().getLog().log(new Status(IStatus.ERROR, PreviewPlugin.PLUGIN_ID, Messages.errorNoJRE, e));
		}
		return null;
	}

	/**
	 * @see RuntimeDelegate#validate()
	 */
	public IStatus validate() {
		IStatus status = super.validate();
		if (!status.isOK() && status.getMessage().length() > 0)
			return status;
		
		if (getVMInstall() == null)
			return new Status(IStatus.ERROR, PreviewPlugin.PLUGIN_ID, 0, Messages.errorNoJRE, null);
		
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
/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.jst.server.core.internal;

import org.eclipse.core.runtime.*;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstallType;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jst.server.core.IGenericRuntime;
import org.eclipse.jst.server.core.IGenericRuntimeWorkingCopy;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.ServerUtil;
import org.eclipse.wst.server.core.model.RuntimeDelegate;
/**
 * 
 */
public class GenericRuntime extends RuntimeDelegate implements IGenericRuntime, IGenericRuntimeWorkingCopy {
	protected static final String PROP_VM_INSTALL_TYPE_ID = "vm-install-type-id";
	protected static final String PROP_VM_INSTALL_ID = "vm-install-id";

	public GenericRuntime() {
		// do nothing
	}

	public String getVMInstallTypeId() {
		return getAttribute(PROP_VM_INSTALL_TYPE_ID, (String)null);
	}

	public String getVMInstallId() {
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
	
	public IStatus validate() {
		IRuntime runtime = getRuntime();
		if (runtime.getName() == null || runtime.getName().length() == 0)
			return new Status(IStatus.ERROR, JavaServerPlugin.PLUGIN_ID, 0, JavaServerPlugin.getResource("%errorName"), null);

		if (ServerUtil.isNameInUse(runtime))
			return new Status(IStatus.ERROR, JavaServerPlugin.PLUGIN_ID, 0, JavaServerPlugin.getResource("%errorDuplicateRuntimeName"), null);
		
		IPath path = runtime.getLocation();
		if (path == null || path.isEmpty())
			return new Status(IStatus.ERROR, JavaServerPlugin.PLUGIN_ID, 0, "", null);
		else if (!path.toFile().exists())
			return new Status(IStatus.ERROR, JavaServerPlugin.PLUGIN_ID, 0, JavaServerPlugin.getResource("%errorLocation"), null);
		else if (getVMInstall() == null)
			return new Status(IStatus.ERROR, JavaServerPlugin.PLUGIN_ID, 0, JavaServerPlugin.getResource("%errorJRE"), null);
		else
			return new Status(IStatus.OK, JavaServerPlugin.PLUGIN_ID, 0, "", null);
	}
	
	public void setDefaults() {
		IVMInstall vmInstall = JavaRuntime.getDefaultVMInstall();
		setVMInstall(vmInstall.getVMInstallType().getId(), vmInstall.getId());
	}

	public void setVMInstall(String typeId, String id) {
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
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
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.ServerUtil;
/**
 * 
 */
public class GenericRuntime implements IGenericRuntime {
	protected static final String PROP_VM_INSTALL_TYPE_ID = "vm-install-type-id";
	protected static final String PROP_VM_INSTALL_ID = "vm-install-id";

	protected IRuntime runtime;

	public GenericRuntime() { }

	public void initialize(IRuntime newRuntime) {
		this.runtime = newRuntime;
	}

	public String getVMInstallTypeId() {
		return runtime.getAttribute(PROP_VM_INSTALL_TYPE_ID, (String)null);
	}

	public String getVMInstallId() {
		return runtime.getAttribute(PROP_VM_INSTALL_ID, (String)null);
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
		} catch (Exception e) { }
		return null;
	}
	
	public IStatus validate() {
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
	
	public void dispose() { }
}
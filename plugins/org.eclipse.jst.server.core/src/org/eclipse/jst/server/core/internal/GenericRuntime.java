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
package org.eclipse.jst.server.core.internal;

import org.eclipse.core.runtime.*;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstallType;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.model.RuntimeDelegate;
/**
 *
 */
public class GenericRuntime extends RuntimeDelegate implements IGenericRuntime, IGenericRuntimeWorkingCopy {
	protected static final String PROP_VM_INSTALL_TYPE_ID = "vm-install-type-id";
	protected static final String PROP_VM_INSTALL_ID = "vm-install-id";

	/**
	 * Create a new generic runtime.
	 */
	public GenericRuntime() {
		// do nothing
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

	/**
	 * @see IGenericRuntime#getVMInstall()
	 */
	public IVMInstall getVMInstall() {
		if (getVMInstallTypeId() == null)
			return JavaRuntime.getDefaultVMInstall();
		try {
			IVMInstallType vmInstallType = JavaRuntime.getVMInstallType(getVMInstallTypeId());
			IVMInstall[] vmInstalls = vmInstallType.getVMInstalls();
			String id = getVMInstallId();
			for (IVMInstall vmInst : vmInstalls) {
				if (id.equals(vmInst.getId()))
					return vmInst;
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
		
		IRuntime runtime = getRuntime();

		IPath path = runtime.getLocation();
		if (!path.toFile().exists())
			return new Status(IStatus.ERROR, JavaServerPlugin.PLUGIN_ID, 0, Messages.errorLocation, null);
		else if (getVMInstall() == null)
			return new Status(IStatus.ERROR, JavaServerPlugin.PLUGIN_ID, 0, Messages.errorJRE, null);
		else
			return Status.OK_STATUS;
	}

	/**
	 * @see IGenericRuntimeWorkingCopy#setVMInstall(IVMInstall)
	 */
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

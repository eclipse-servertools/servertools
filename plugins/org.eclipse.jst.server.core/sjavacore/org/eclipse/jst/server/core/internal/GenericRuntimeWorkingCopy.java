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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jst.server.core.IGenericRuntimeWorkingCopy;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
/**
 * 
 */
public class GenericRuntimeWorkingCopy extends GenericRuntime implements IGenericRuntimeWorkingCopy {
	protected IRuntimeWorkingCopy wc;

	public void initialize(IRuntimeWorkingCopy runtime2) {
		wc = runtime2;
	}
	
	public void setDefaults() {
		IVMInstall vmInstall = JavaRuntime.getDefaultVMInstall();
		setVMInstall(vmInstall.getVMInstallType().getId(), vmInstall.getId());
	}

	public void setVMInstall(String typeId, String id) {
		if (typeId == null)
			wc.setAttribute(PROP_VM_INSTALL_TYPE_ID, (String)null);
		else
			wc.setAttribute(PROP_VM_INSTALL_TYPE_ID, typeId);
		
		if (id == null)
			wc.setAttribute(PROP_VM_INSTALL_ID, (String)null);
		else
			wc.setAttribute(PROP_VM_INSTALL_ID, id);
	}
	
	public void handleSave(byte id, IProgressMonitor monitor) { }
}
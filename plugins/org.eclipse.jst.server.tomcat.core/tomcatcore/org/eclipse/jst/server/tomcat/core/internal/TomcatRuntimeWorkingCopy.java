package org.eclipse.jst.server.tomcat.core.internal;
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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jst.server.tomcat.core.ITomcatRuntimeWorkingCopy;

import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
/**
 * 
 */
public class TomcatRuntimeWorkingCopy extends TomcatRuntime implements ITomcatRuntimeWorkingCopy {
	protected IRuntimeWorkingCopy wc;
	
	public void initialize(IRuntimeWorkingCopy runtime2) {
		wc = runtime2;
	}

	public void setDefaults() {
		IVMInstall vmInstall = JavaRuntime.getDefaultVMInstall();
		setVMInstall(vmInstall.getVMInstallType().getId(), vmInstall.getId());
		
		IRuntimeType type = wc.getRuntimeType();
		wc.setLocation(new Path(TomcatPlugin.getPreference("location" + type.getId())));
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
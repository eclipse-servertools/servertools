package org.eclipse.jst.server.tomcat.core.internal;
/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstallType;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jst.server.tomcat.core.ITomcatRuntime;
import org.eclipse.jst.server.tomcat.core.ITomcatRuntimeWorkingCopy;

import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.ServerUtil;
import org.eclipse.wst.server.core.model.RuntimeDelegate;
/**
 * 
 */
public class TomcatRuntime extends RuntimeDelegate implements ITomcatRuntime, ITomcatRuntimeWorkingCopy {
	protected static final String PROP_VM_INSTALL_TYPE_ID = "vm-install-type-id";
	protected static final String PROP_VM_INSTALL_ID = "vm-install-id";

	public TomcatRuntime() { }

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.IRuntime#getLocation()
	 */
	public ITomcatVersionHandler getVersionHandler() {
		IRuntimeType type = getRuntime().getRuntimeType();
		return TomcatPlugin.getTomcatVersionHandler(type.getId());
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
		} catch (Exception e) { }
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
	
	public IStatus validate() {
		IRuntime runtime = getRuntime();
		if (runtime.getName() == null || runtime.getName().length() == 0)
			return new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, TomcatPlugin.getResource("%errorRuntimeName"), null);

		if (runtime.isWorkingCopy() && ServerUtil.isNameInUse(runtime))
			return new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, TomcatPlugin.getResource("%errorDuplicateRuntimeName"), null);
	
		IPath path = runtime.getLocation();
		if (path == null || path.isEmpty())
			return new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, "", null);
		else if (!verifyLocation())
			return new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, TomcatPlugin.getResource("%errorInstallDir"), null);
		else if (getVMInstall() == null) {
			return new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, TomcatPlugin.getResource("%errorJRE"), null);
		} else
			return new Status(IStatus.OK, TomcatPlugin.PLUGIN_ID, 0, "", null);
	}

	public void setDefaults() {
		IVMInstall vmInstall = JavaRuntime.getDefaultVMInstall();
		setVMInstall(vmInstall.getVMInstallType().getId(), vmInstall.getId());
		
		IRuntimeType type = getRuntimeWC().getRuntimeType();
		getRuntimeWC().setLocation(new Path(TomcatPlugin.getPreference("location" + type.getId())));
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
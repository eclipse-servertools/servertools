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
package org.eclipse.jst.server.core;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.launching.IVMInstall;
/**
 * 
 */
public interface IGenericRuntime {
	public String getVMInstallTypeId();

	public String getVMInstallId();

	public IVMInstall getVMInstall();

	public IStatus validate();
}
/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.core.internal;

import org.eclipse.jdt.launching.IVMInstall;
/**
 * @since 1.0
 */
public interface IGenericRuntimeWorkingCopy extends IGenericRuntime {
	/**
	 * Set the VM install (installed JRE) that this runtime is using.
	 * Use <code>null</code> to use the Eclipse default JRE.
	 * 
	 * @param vmInstall the VM install to use
	 */
	public void setVMInstall(IVMInstall vmInstall);
}
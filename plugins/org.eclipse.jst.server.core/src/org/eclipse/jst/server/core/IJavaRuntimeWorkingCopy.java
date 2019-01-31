/*******************************************************************************
 * Copyright (c) 2005, 2011 IBM Corporation and others.
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
package org.eclipse.jst.server.core;

import org.eclipse.jdt.launching.IVMInstall;
/**
 * An interface for a server that contains a Java runtime.
 * <p>
 * <b>Note:</b> This class/interface is part of an interim API that is still under development and expected to
 * change significantly before reaching stability. It is being made available at this early stage to solicit feedback
 * from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
 * (repeatedly) as the API evolves.
 * </p>
 * @since 1.1
 */
public interface IJavaRuntimeWorkingCopy extends IJavaRuntime {
	/**
	 * Set the VM install (installed JRE) that this runtime is using.
	 * Use <code>null</code> to use the Eclipse default JRE.
	 * 
	 * @param vmInstall the VM install to use
	 */
	public void setVMInstall(IVMInstall vmInstall);
}
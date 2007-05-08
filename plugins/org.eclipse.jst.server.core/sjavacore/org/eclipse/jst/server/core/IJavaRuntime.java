/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
 * @since 3.0
 */
public interface IJavaRuntime {
	/**
	 * Return the VM install (installed JRE) that this runtime is using.
	 * 
	 * @return the current VM install
	 */
	public IVMInstall getVMInstall();

	/**
	 * Returns <code>true</code> if the runtime is using the default JRE.
	 * 
	 * @return <code>true</code> if the runtime is using the default JRE,
	 *    and <code>false</code> otherwise
	 * @since 2.0
	 */
	public boolean isUsingDefaultJRE();
}
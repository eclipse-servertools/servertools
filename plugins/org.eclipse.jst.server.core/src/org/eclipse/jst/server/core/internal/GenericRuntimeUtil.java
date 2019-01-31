/*******************************************************************************
 * Copyright (c) 2003, 2007 IBM Corporation and others.
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
package org.eclipse.jst.server.core.internal;

import org.eclipse.wst.server.core.IRuntime;
/**
 * Utility methods for the generic J2EE runtime.
 */
public class GenericRuntimeUtil {
	protected static final String RUNTIME_TYPE_ID = "org.eclipse.jst.server.core.runtimeType";

	/**
	 * Cannot create GenericRuntimeUtil - use static methods.
	 */
	private GenericRuntimeUtil() {
		// can't create
	}

	/**
	 * Returns <code>true</code> if the given runtime is a generic J2EE runtime, and
	 * <code>false</code> otherwise. The runtime may not be null.
	 * 
	 * @param runtime 
	 * @return <code>true</code> if 
	 */
	public static boolean isGenericJ2EERuntime(IRuntime runtime) {
		if (runtime == null)
			throw new IllegalArgumentException();

		return (runtime.getRuntimeType() != null &&
			runtime.getRuntimeType().getId().startsWith(RUNTIME_TYPE_ID));
	}
}

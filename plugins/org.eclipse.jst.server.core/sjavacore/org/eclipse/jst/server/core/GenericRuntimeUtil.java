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

import org.eclipse.wst.server.core.IRuntime;
/**
 * Utility methods for the generic J2EE runtime.
 */
public class GenericRuntimeUtil {
	protected static final String RUNTIME_TYPE_ID = "org.eclipse.jst.server.core.runtimeType";

	public static boolean isGenericJ2EERuntime(IRuntime runtime) {
		return (runtime != null && runtime.getRuntimeType() != null &&
			runtime.getRuntimeType().getId().startsWith(RUNTIME_TYPE_ID));
	}
}
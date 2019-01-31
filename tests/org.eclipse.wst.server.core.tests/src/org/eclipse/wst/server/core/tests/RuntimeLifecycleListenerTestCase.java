/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
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
package org.eclipse.wst.server.core.tests;

import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeLifecycleListener;

import junit.framework.TestCase;

public class RuntimeLifecycleListenerTestCase extends TestCase {
	public void testListener() {
		IRuntimeLifecycleListener listener = new IRuntimeLifecycleListener() {
			public void runtimeAdded(IRuntime runtime) {
				// ignore
			}

			public void runtimeChanged(IRuntime runtime) {
				// ignore
			}

			public void runtimeRemoved(IRuntime runtime) {
				// ignore
			}
		};
		
		listener.runtimeAdded(null);
		listener.runtimeChanged(null);
		listener.runtimeRemoved(null);
	}
}
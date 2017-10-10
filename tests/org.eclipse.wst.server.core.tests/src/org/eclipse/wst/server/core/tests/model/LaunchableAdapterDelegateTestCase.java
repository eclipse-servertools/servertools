/*******************************************************************************
 * Copyright (c) 2005, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.tests.model;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.server.core.IModuleArtifact;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.model.LaunchableAdapterDelegate;

public class LaunchableAdapterDelegateTestCase extends TestCase {
	protected static LaunchableAdapterDelegate delegate;

	protected LaunchableAdapterDelegate getLaunchableAdapterDelegate() {
		if (delegate == null) {
			delegate = new LaunchableAdapterDelegate() {
				public Object getLaunchable(IServer server, IModuleArtifact moduleArtifact) throws CoreException {
					return null;
				}
			};
		}
		return delegate;
	}

	public void testGetLaunchable() throws Exception {
		getLaunchableAdapterDelegate().getLaunchable(null, null);
	}
}
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

import org.eclipse.wst.server.core.IModuleArtifact;
import org.eclipse.wst.server.core.model.ModuleArtifactAdapterDelegate;

public class ModuleArtifactAdapterDelegateTestCase extends TestCase {
	protected static ModuleArtifactAdapterDelegate delegate;

	protected ModuleArtifactAdapterDelegate getModuleArtifactAdapterDelegate() {
		if (delegate == null) {
			delegate = new ModuleArtifactAdapterDelegate() {
				public IModuleArtifact getModuleArtifact(Object obj) {
					return null;
				}
			};
		}
		return delegate;
	}

	public void testGetModuleArtifact() {
		getModuleArtifactAdapterDelegate().getModuleArtifact(null);
	}
}
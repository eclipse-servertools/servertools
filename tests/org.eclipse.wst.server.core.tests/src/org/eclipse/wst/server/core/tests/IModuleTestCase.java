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
package org.eclipse.wst.server.core.tests;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IModuleType;

import junit.framework.TestCase;

public class IModuleTestCase extends TestCase {
	protected static IModule module;

	public void testCreate() {
		module = new IModule() {
			public String getId() {
				return null;
			}

			public String getName() {
				return null;
			}

			public IModuleType getModuleType() {
				return null;
			}

			public IProject getProject() {
				return null;
			}

			public Object getAdapter(Class adapter) {
				return null;
			}
			
			public Object loadAdapter(Class adapter, IProgressMonitor monitor) {
				return null;
			}

			public boolean isExternal() {
				return false;
			}
		};
		
		module.getId();
		module.getName();
		module.getModuleType();
		module.getProject();
		module.getAdapter(null);
		module.loadAdapter(null, null);
	}
}
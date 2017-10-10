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
package org.eclipse.wst.server.core.tests.impl;

import java.util.ArrayList;
import java.util.HashMap;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.model.ServerDelegate;

public class TestServerDelegate extends ServerDelegate {
	public IStatus canModifyModules(IModule[] add, IModule[] remove) {
		return null;
	}

	public IModule[] getChildModules(IModule[] module) {
		return null;
	}

	public IModule[] getRootModules(IModule module) throws CoreException {
		return null;
	}

	public void modifyModules(IModule[] add, IModule[] remove, IProgressMonitor monitor) throws CoreException {
		// do nothing
	}
	
	public void testProtected() {
		initialize();
		
		try {
			getAttribute("test", false);
		} catch (Exception e) {
			// ignore
		}
		
		try {
			getAttribute("test", 0);
		} catch (Exception e) {
			// ignore
		}
		
		try {
			getAttribute("test", new ArrayList<String>());
		} catch (Exception e) {
			// ignore
		}
		
		try {
			getAttribute("test", new HashMap());
		} catch (Exception e) {
			// ignore
		}
		
		try {
			getAttribute("test", "test");
		} catch (Exception e) {
			// ignore
		}
		
		try {
			setAttribute("test", false);
		} catch (Exception e) {
			// ignore
		}
		
		try {
			setAttribute("test", 0);
		} catch (Exception e) {
			// ignore
		}
		
		try {
			setAttribute("test", new ArrayList<String>());
		} catch (Exception e) {
			// ignore
		}
		
		try {
			setAttribute("test", new HashMap());
		} catch (Exception e) {
			// ignore
		}
		
		try {
			setAttribute("test", "test");
		} catch (Exception e) {
			// ignore
		}
	}
}

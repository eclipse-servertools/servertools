/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.tests.impl;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.model.RuntimeLocatorDelegate;

public class TestRuntimeLocatorDelegate extends RuntimeLocatorDelegate {
	public void searchForRuntimes(IPath path, IRuntimeSearchListener listener, IProgressMonitor monitor) {
		// do nothing
	}
}
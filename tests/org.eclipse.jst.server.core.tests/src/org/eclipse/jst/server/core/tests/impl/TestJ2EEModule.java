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
package org.eclipse.jst.server.core.tests.impl;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jst.server.core.IJ2EEModule;

public class TestJ2EEModule implements IJ2EEModule {
	public String getJ2EESpecificationVersion() {
		return null;
	}

	public IPath getLocation() {
		return null;
	}

	public boolean isBinary() {
		return false;
	}
}
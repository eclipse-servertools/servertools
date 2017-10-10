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

import org.eclipse.core.resources.IContainer;
import org.eclipse.jst.server.core.IJ2EEModule;

public class TestJ2EEModule implements IJ2EEModule {
	public boolean isBinary() {
		return false;
	}

	public IContainer[] getResourceFolders() {
		return null;
	}

	public IContainer[] getJavaOutputFolders() {
		return null;
	}
}
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
import org.eclipse.wst.server.core.model.IModuleResource;

public class TestModuleResource implements IModuleResource {
	public IPath getModuleRelativePath() {
		return null;
	}

	public String getName() {
		return null;
	}
}
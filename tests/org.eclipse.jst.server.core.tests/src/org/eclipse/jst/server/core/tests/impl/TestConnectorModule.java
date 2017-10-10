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
import org.eclipse.jst.server.core.IConnectorModule;

public class TestConnectorModule extends TestJ2EEModule implements IConnectorModule {
	public IPath[] getClasspath() {
		return null;
	}
}
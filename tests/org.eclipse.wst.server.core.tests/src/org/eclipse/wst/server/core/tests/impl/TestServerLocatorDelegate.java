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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.IServerLocator.Listener;
import org.eclipse.wst.server.core.model.ServerLocatorDelegate;

public class TestServerLocatorDelegate extends ServerLocatorDelegate {
	public void searchForServers(String host, Listener listener, IProgressMonitor monitor) {
		// ignore
	}
}
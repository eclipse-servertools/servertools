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

import org.eclipse.wst.server.core.model.ServerBehaviourDelegate;

public class TestServerBehaviourDelegate extends ServerBehaviourDelegate {
	public void stop(boolean force) {
		// ignore
	}
	
	public void testProtected() {
		try {
			initialize();
		} catch (Exception e) {
			// ignore
		}
		
		try {
			publishStart(null);
		} catch (Exception e) {
			// ignore
		}
		
		try {
			publishServer(0, null);
		} catch (Exception e) {
			// ignore
		}
		
		try {
			publishModule(0, null, 0, null);
		} catch (Exception e) {
			// ignore
		}
		
		try {
			publishFinish(null);
		} catch (Exception e) {
			// ignore
		}
	}
}
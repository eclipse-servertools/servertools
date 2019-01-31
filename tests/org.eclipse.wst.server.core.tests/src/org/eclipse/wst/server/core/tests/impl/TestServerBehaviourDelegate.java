/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
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
			setMode(null);
		} catch (Exception e) {
			// ignore
		}
		
		try {
			initialize(null);
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
		
		try {
			setServerState(0);
		} catch (Exception e) {
			// ignore
		}
		
		try {
			setServerPublishState(0);
		} catch (Exception e) {
			// ignore
		}
		
		try {
			setServerRestartState(false);
		} catch (Exception e) {
			// ignore
		}
		
		try {
			setModuleState(null, 0);
		} catch (Exception e) {
			// ignore
		}
		
		try {
			setModulePublishState(null, 0);
		} catch (Exception e) {
			// ignore
		}
		
		try {
			setModuleRestartState(null, false);
		} catch (Exception e) {
			// ignore
		}
		
		try {
			canControlModule(null);
		} catch (Exception e) {
			// ignore
		}
		
		try {
			getPublishedResourceDelta(null);
		} catch (Exception e) {
			// ignore
		}
		
		try {
			getPublishedResources(null);
		} catch (Exception e) {
			// ignore
		}
		
		try {
			getTempDirectory();
		} catch (Exception e) {
			// ignore
		}
		
		try {
			setServerStatus(null);
		} catch (Exception e) {
			// ignore
		}
		
		try {
			setModuleStatus(null, null);
		} catch (Exception e) {
			// ignore
		}
		
		try {
			startModule(null, null);
		} catch (Exception e) {
			// ignore
		}
		
		try {
			stopModule(null, null);
		} catch (Exception e) {
			// ignore
		}
	}
}
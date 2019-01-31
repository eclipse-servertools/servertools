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
package org.eclipse.wst.server.core.tests;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.server.core.IPublishListener;
import org.eclipse.wst.server.core.IServer;

import junit.framework.TestCase;

public class PublishListenerTestCase extends TestCase {
	public void testListener() {
		IPublishListener listener = new IPublishListener() {
			public void publishStarted(IServer server) {
				// ignore
			}

			public void publishFinished(IServer server, IStatus status) {
				// ignore
			}
		};
		
		listener.publishStarted(null);
		listener.publishFinished(null, null);
	}
}
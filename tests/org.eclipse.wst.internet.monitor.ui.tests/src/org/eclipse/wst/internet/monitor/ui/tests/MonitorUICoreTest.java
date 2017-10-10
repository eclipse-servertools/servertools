/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.internet.monitor.ui.tests;

import org.eclipse.wst.internet.monitor.ui.internal.provisional.MonitorUICore;

import junit.framework.TestCase;

public class MonitorUICoreTest extends TestCase {
	public void testRequests() {
		assertNotNull(MonitorUICore.getRequests());
	}
}
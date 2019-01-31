/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
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

import java.util.ArrayList;
import java.util.HashMap;
import org.eclipse.wst.server.core.model.RuntimeDelegate;

public class TestRuntimeDelegate extends RuntimeDelegate {
	public void testProtected() {
		initialize();
		
		try {
			getAttribute("test", false);
		} catch (Exception e) {
			// ignore
		}
		
		try {
			getAttribute("test", 0);
		} catch (Exception e) {
			// ignore
		}
		
		try {
		getAttribute("test", new ArrayList<String>());
		} catch (Exception e) {
			// ignore
		}

		try {
			getAttribute("test", new HashMap());
		} catch (Exception e) {
			// ignore
		}
	
		try {
			getAttribute("test", "test");
		} catch (Exception e) {
			// ignore
		}
		
		try {
			setAttribute("test", false);
		} catch (Exception e) {
			// ignore
		}
		
		try {
			setAttribute("test", 0);
		} catch (Exception e) {
			// ignore
		}
		
		try {
			setAttribute("test", new ArrayList<String>());
		} catch (Exception e) {
			// ignore
		}
		
		try {
			setAttribute("test", new HashMap());
		} catch (Exception e) {
			// ignore
		}
		
		try {
			setAttribute("test", "test");
		} catch (Exception e) {
			// ignore
		}
	}
}

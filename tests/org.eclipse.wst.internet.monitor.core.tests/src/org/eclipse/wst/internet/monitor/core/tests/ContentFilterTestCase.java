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
package org.eclipse.wst.internet.monitor.core.tests;

import org.eclipse.wst.internet.monitor.core.ContentFilterDelegate;

import junit.framework.Test;
import junit.framework.TestCase;

public class ContentFilterTestCase extends TestCase {
	protected static ContentFilterDelegate delegate;
	
	public static Test suite() {
		return new OrderedTestSuite(MonitorListenerTestCase.class, "MonitorTestCase");
	}
	
	public void test00Creation() {
		delegate = new TestContentFilterDelegate();
	}
	
	public void test01Filter() throws Exception {
		delegate.filter(null, false, null);
	}
}
/*******************************************************************************
 * Copyright (c) 2004, 2013 IBM Corporation and others.
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
package org.eclipse.wst.internet.monitor.ui.tests.extension;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.wst.internet.monitor.ui.internal.provisional.ContentViewer;

public class ContentViewersTestCase extends TestCase {
	protected static ContentViewer viewer;

	public static TestSuite getOrderedTests() {
		TestSuite mySuite = new TestSuite();
		mySuite.addTest(TestSuite.createTest(ContentViewersTestCase.class, "test00Create"));
		mySuite.addTest(TestSuite.createTest(ContentViewersTestCase.class, "test01GetContent"));
		mySuite.addTest(TestSuite.createTest(ContentViewersTestCase.class, "test02GetEditable"));
		mySuite.addTest(TestSuite.createTest(ContentViewersTestCase.class, "test03SetContent"));
		mySuite.addTest(TestSuite.createTest(ContentViewersTestCase.class, "test04SetEditable"));
		mySuite.addTest(TestSuite.createTest(ContentViewersTestCase.class, "test05Init"));
		mySuite.addTest(TestSuite.createTest(ContentViewersTestCase.class, "test06Dispose"));
		return mySuite;
	}
	
	public void test00Create() {
		viewer = new TestContentViewer();
	}
	
	public void test01GetContent() {
		viewer.getContent();
	}
	
	public void test02GetEditable() {
		viewer.getEditable();
	}
	
	public void test03SetContent() {
		viewer.setContent(null);
	}
	
	public void test04SetEditable() {
		viewer.setEditable(false);
	}
	
	public void test05Init() {
		viewer.init(null);
	}
	
	public void test06Dispose() {
		viewer.dispose();
	}
}

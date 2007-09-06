/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.internet.monitor.ui.tests.extension;

import junit.framework.TestCase;

import org.eclipse.wst.internet.monitor.ui.internal.provisional.ContentViewer;

public class ContentViewersTestCase extends TestCase {
	protected static ContentViewer viewer;

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

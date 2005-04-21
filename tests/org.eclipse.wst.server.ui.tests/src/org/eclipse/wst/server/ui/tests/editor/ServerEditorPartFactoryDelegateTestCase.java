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
package org.eclipse.wst.server.ui.tests.editor;

import junit.framework.Test;
import junit.framework.TestCase;

import org.eclipse.wst.server.ui.editor.ServerEditorPartFactoryDelegate;
import org.eclipse.wst.server.ui.tests.OrderedTestSuite;
import org.eclipse.wst.server.ui.tests.impl.TestServerEditorPartFactoryDelegate;

public class ServerEditorPartFactoryDelegateTestCase extends TestCase {
	protected static ServerEditorPartFactoryDelegate delegate;

	public static Test suite() {
		return new OrderedTestSuite(ServerEditorPartFactoryDelegateTestCase.class, "ServerEditorPartFactoryDelegateTestCase");
	}

	public void test00CreateEditor() {
		delegate = new TestServerEditorPartFactoryDelegate();
	}
	
	public void test01ShouldCreatePage() throws Exception {
		delegate.shouldCreatePage(null);
	}
	
	public void test02CreatePage() throws Exception {
		delegate.createPage();
	}
}
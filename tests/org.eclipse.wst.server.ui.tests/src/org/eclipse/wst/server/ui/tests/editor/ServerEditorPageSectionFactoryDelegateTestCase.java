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

import org.eclipse.wst.server.ui.editor.ServerEditorPageSectionFactoryDelegate;
import org.eclipse.wst.server.ui.tests.OrderedTestSuite;
import org.eclipse.wst.server.ui.tests.impl.TestServerEditorPageSectionFactoryDelegate;

public class ServerEditorPageSectionFactoryDelegateTestCase extends TestCase {
	protected static ServerEditorPageSectionFactoryDelegate delegate;

	public static Test suite() {
		return new OrderedTestSuite(ServerEditorPageSectionFactoryDelegateTestCase.class, "ServerEditorPageSectionFactoryDelegateTestCase");
	}

	public void test00CreateDelegate() throws Exception {
		delegate = new TestServerEditorPageSectionFactoryDelegate();
	}
	
	public void test01ShouldCreateSection() throws Exception {
		delegate.shouldCreateSection(null);
	}
	
	public void test02CreateSection() throws Exception {
		delegate.createSection();
	}
}

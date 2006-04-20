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

import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.wst.server.ui.internal.provisional.ServerEditorActionFactoryDelegate;
import org.eclipse.wst.server.ui.tests.OrderedTestSuite;
import org.eclipse.wst.server.ui.tests.impl.TestServerEditorActionFactoryDelegate;

public class ServerEditorActionFactoryDelegateTestCase extends TestCase {
	protected static ServerEditorActionFactoryDelegate delegate;

	public static Test suite() {
		return new OrderedTestSuite(ServerEditorActionFactoryDelegateTestCase.class, "ServerEditorActionFactoryDelegateTestCase");
	}
	protected void setUp() throws Exception {
		super.setUp();
		ErrorDialog.AUTOMATED_MODE=true;
	}
	public void test00CreateDelegate() throws Exception {
		delegate = new TestServerEditorActionFactoryDelegate();
	}
	
	public void test01ShouldDisplay() throws Exception {
		delegate.shouldDisplay(null);
	}
	
	public void test02CreateAction() throws Exception {
		delegate.createAction(null, null);
	}
}

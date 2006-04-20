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

import org.eclipse.wst.server.ui.editor.IServerEditorPartInput;
import org.eclipse.wst.server.ui.tests.OrderedTestSuite;
import org.eclipse.wst.server.ui.tests.impl.TestServerEditorPartInput;

public class IServerEditorPartInputTestCase extends TestCase {
	protected static IServerEditorPartInput input;

	public static Test suite() {
		return new OrderedTestSuite(IServerEditorPartInputTestCase.class, "IServerEditorPartInputTestCase");
	}



	public void test00Create() {
		input = new TestServerEditorPartInput();
	}

	public void test01GetServer() {
		input.getServer();
	}

	public void test02IsServerReadOnly() {
		input.isServerReadOnly();
	}

	public void test04Exists() {
		input.exists();
	}

	public void test05GetImageDescriptor() {
		input.getImageDescriptor();
	}

	public void test06GetName() {
		input.getName();
	}

	public void test07GetPersistable() {
		input.getPersistable();
	}

	public void test08GetToolTipText() {
		input.getToolTipText();
	}

	public void test09GetAdapter() {
		input.getAdapter(null);
	}
}
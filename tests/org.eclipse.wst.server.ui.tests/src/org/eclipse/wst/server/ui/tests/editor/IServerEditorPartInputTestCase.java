/*******************************************************************************
 * Copyright (c) 2005, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.tests.editor;

import junit.framework.TestCase;

import org.eclipse.wst.server.ui.editor.IServerEditorPartInput;
import org.eclipse.wst.server.ui.tests.impl.TestServerEditorPartInput;

public class IServerEditorPartInputTestCase extends TestCase {
	protected static IServerEditorPartInput input;

	protected IServerEditorPartInput getServerEditorPartInput() {
		if (input == null) {
			input = new TestServerEditorPartInput();
		}
		return input;
	}

	public void testGetServer() {
		getServerEditorPartInput().getServer();
	}

	public void testIsServerReadOnly() {
		getServerEditorPartInput().isServerReadOnly();
	}

	public void testExists() {
		getServerEditorPartInput().exists();
	}

	public void testGetImageDescriptor() {
		getServerEditorPartInput().getImageDescriptor();
	}

	public void testGetName() {
		getServerEditorPartInput().getName();
	}

	public void testGetPersistable() {
		getServerEditorPartInput().getPersistable();
	}

	public void testGetToolTipText() {
		getServerEditorPartInput().getToolTipText();
	}

	public void testGetAdapter() {
		getServerEditorPartInput().getAdapter(null);
	}
}
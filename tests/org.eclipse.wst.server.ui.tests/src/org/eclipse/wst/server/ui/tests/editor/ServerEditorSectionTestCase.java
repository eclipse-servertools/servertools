/*******************************************************************************
 * Copyright (c) 2005, 2010 IBM Corporation and others.
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
package org.eclipse.wst.server.ui.tests.editor;

import junit.framework.TestCase;

import org.eclipse.wst.server.ui.editor.ServerEditorSection;

/* Note: These tests may be executed in any order.  Because null is used as most
 * arguments, the order doesn't currently matter.  If non-null arguments are used,
 * it may be necessary to rewrite the tests to make them truly order independent.
 */

public class ServerEditorSectionTestCase extends TestCase {
	protected static ServerEditorSection section;

	protected ServerEditorSection getServerEditorSection() {
		if (section == null) {
			section = new ServerEditorSection() {
				// do nothing
			};
			// Ensure initialized called, though this is effectively a no-op
			section.init(null, null);
		}
		return section;
	}

	public void testCreateSection() {
		getServerEditorSection().createSection(null);
	}
	
	public void testDispose() {
		getServerEditorSection().dispose();
	}
	
	public void testGetErrorMessage() {
		getServerEditorSection().getErrorMessage();
	}
	
	public void testGetSaveStatus() {
		getServerEditorSection().getSaveStatus();
	}
	
	public void testGetShell() {
		try {
			getServerEditorSection().getShell();
		} catch (Exception e) {
			// ignore
		}
	}
	
	public void testSetServerEditorPart() {
		getServerEditorSection().setServerEditorPart(null);
	}
	
	public void testSetErrorMessage() {
		getServerEditorSection().setErrorMessage(null);
	}
}
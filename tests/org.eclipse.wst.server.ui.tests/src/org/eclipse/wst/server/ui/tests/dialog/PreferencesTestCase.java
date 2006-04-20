/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.tests.dialog;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ErrorDialog;

public class PreferencesTestCase extends TestCase {
	public static Test suite() {
		return new TestSuite(PreferencesTestCase.class);
	}

	public PreferencesTestCase(String name) {
		super(name);
	}
	protected void setUp() throws Exception {
		super.setUp();
		ErrorDialog.AUTOMATED_MODE=true;
	}
	public void testServerPreferencePage() {
		Dialog dialog = UITestHelper.getPreferenceDialog("org.eclipse.wst.server.ui.preferencePage");
		UITestHelper.assertDialog(dialog);
	}

	public void testRuntimePreferencePage() {
		Dialog dialog = UITestHelper.getPreferenceDialog("org.eclipse.wst.server.ui.runtime.preferencePage");
		UITestHelper.assertDialog(dialog);
	}

	public void testServerPropertyPage() throws Exception {
		// TODO temporarily disabled due to issues with internal build machine
		/*IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject("PropertyTest");
		project.create(null);
		project.open(null);

		Dialog dialog = UITestHelper.getPropertyDialog("org.eclipse.wst.server.ui.project.properties", project);
		UITestHelper.assertDialog(dialog);
		
		project.delete(true, true, null);*/
	}

	public void testInternetPreferencePage() {
		Dialog dialog = UITestHelper.getPreferenceDialog("org.eclipse.internet");
		UITestHelper.assertDialog(dialog);
	}

	public void testAudioPreferencePage() {
		Dialog dialog = UITestHelper.getPreferenceDialog("org.eclipse.wst.audio.preferencePage");
		UITestHelper.assertDialog(dialog);
	}
}
/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.tests.dialog;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;

import org.eclipse.jface.dialogs.Dialog;

public class PreferencesTestCase extends TestCase {
	public static Test suite() {
		return new TestSuite(PreferencesTestCase.class);
	}

	public PreferencesTestCase(String name) {
		super(name);
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
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject("PropertyTest");
		project.create(null);
		project.open(null);

		Dialog dialog = UITestHelper.getPropertyDialog("org.eclipse.wst.server.ui.project.properties", project);
		UITestHelper.assertDialog(dialog);
		
		project.delete(true, true, null);
	}
}
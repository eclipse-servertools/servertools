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
package org.eclipse.wst.internet.monitor.ui.tests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.jface.dialogs.Dialog;

public class PreferencesTestCase extends TestCase {
	public static Test suite() {
		return new TestSuite(PreferencesTestCase.class);
	}

	public PreferencesTestCase(String name) {
		super(name);
	}

	public void testPreferencePage() {
		Dialog dialog = UITestHelper.getPreferenceDialog("org.eclipse.wst.internet.monitor.preferencePage");
		UITestHelper.assertDialog(dialog);
	}
}
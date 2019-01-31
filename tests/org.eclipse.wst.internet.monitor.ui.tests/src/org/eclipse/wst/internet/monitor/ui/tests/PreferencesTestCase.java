/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.internet.monitor.ui.tests;

import junit.framework.TestCase;

import org.eclipse.jface.dialogs.Dialog;

public class PreferencesTestCase extends TestCase {
	public PreferencesTestCase(String name) {
		super(name);
	}

	public void testPreferencePage() {
		Dialog dialog = UITestHelper.getPreferenceDialog("org.eclipse.wst.internet.monitor.preferencePage");
		UITestHelper.assertDialog(dialog);
	}
}

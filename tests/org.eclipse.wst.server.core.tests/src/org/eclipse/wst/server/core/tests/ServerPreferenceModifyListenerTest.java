/*******************************************************************************
 * Copyright (c) 2026 Erik Brangs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Erik Brangs - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.tests;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.wst.server.core.internal.ServerPreferenceModifyListener;

import junit.framework.TestCase;

public class ServerPreferenceModifyListenerTest extends TestCase {

	public void testListener() throws Exception {
		IEclipsePreferences preferences = InstanceScope.INSTANCE.getNode("org.eclipse.wst.preferenceTest");
		ServerPreferenceModifyListener serverPreferenceModifyListener = new ServerPreferenceModifyListener();
		serverPreferenceModifyListener.preApply(preferences);
	}

}

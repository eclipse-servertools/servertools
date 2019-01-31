/*******************************************************************************
 * Copyright (c) 2016 IBM Corporation and others.
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
package org.eclipse.wst.server.ui.tests;

import java.util.List;

import org.eclipse.wst.server.ui.RemoveModuleMessageExtension;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;

import junit.framework.TestCase;

/**
 * This tests the extension point for RemoveModuleMessageExtension
 * 
 * @author kchong
 *
 */
public class RemoveModuleMessageExtensionTestCase extends TestCase {

	public void testRemoveModuleMessageExtension() {
	   List<RemoveModuleMessageExtension> extensions = ServerUIPlugin.getRemoveModuleMessageExtensions();
	   assertTrue("Remove Message Extensions", extensions.size() >= 1);  // Have two from the test plugin
	   
	   // Test the filter *
	   RemoveModuleMessageExtension removeModuleMessageExtensionA = ServerUIPlugin.getRemoveModuleMessageExtension("org.eclipse.wst.server.testA.v1");
	   assertNotNull(removeModuleMessageExtensionA);

	   String customMessageA = removeModuleMessageExtensionA.getConfirmationMessage(null, null);
	   assertTrue("Check Custom Message", RemoveModuleMessageTestExtension.customRemoveMessage.equals(customMessageA));
	   
	   // Test the filter .*
	   RemoveModuleMessageExtension removeModuleMessageExtensionB = ServerUIPlugin.getRemoveModuleMessageExtension("org.eclipse.wst.server.testB.v1");
	   assertNotNull(removeModuleMessageExtensionB);

	   String customMessageB = removeModuleMessageExtensionB.getConfirmationMessage(null, null);
	   assertTrue("Check Custom Message", RemoveModuleMessageTestExtension.customRemoveMessage.equals(customMessageB));

	   // Test exact match
	   RemoveModuleMessageExtension removeModuleMessageExtensionC = ServerUIPlugin.getRemoveModuleMessageExtension("org.eclipse.wst.server.testC");
	   assertNotNull(removeModuleMessageExtensionC);

	   String customMessageC = removeModuleMessageExtensionC.getConfirmationMessage(null, null);
	   assertTrue("Check Custom Message", RemoveModuleMessageTestExtension.customRemoveMessage.equals(customMessageC));

	}
}

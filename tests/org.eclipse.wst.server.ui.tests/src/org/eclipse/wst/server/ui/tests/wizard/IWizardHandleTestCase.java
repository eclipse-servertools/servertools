/*******************************************************************************
 * Copyright (c) 2005, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.tests.wizard;

import junit.framework.TestCase;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.wst.server.ui.tests.impl.TestWizardHandle;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;

public class IWizardHandleTestCase extends TestCase {
	protected static IWizardHandle wizardHandle;
	private final String sampleMessage = "Sample message";
	private final int sampleMessageType = IMessageProvider.ERROR;

	public void testAll() throws Exception {
		wizardHandle = new TestWizardHandle();

		wizardHandle.run(true, true, null); 

		wizardHandle.setDescription(null); 

		wizardHandle.setImageDescriptor(null); 

		wizardHandle.setMessage(sampleMessage, sampleMessageType); 

		wizardHandle.setTitle(null); 

		wizardHandle.update(); 

		assertEquals(sampleMessage, wizardHandle.getMessage());

		assertEquals(sampleMessageType, wizardHandle.getMessageType());
	}
}
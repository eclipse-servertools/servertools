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
package org.eclipse.wst.server.ui.tests.wizard;

import junit.framework.TestCase;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.wst.server.ui.tests.impl.TestWizardHandle;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;

public class IWizardHandleTestCase extends TestCase {
	protected static IWizardHandle wizardHandle;
	private final String sampleMessage = "Sample message";
	private final int sampleMessageType = IMessageProvider.ERROR;

	public void test00Create() {
		wizardHandle = new TestWizardHandle();
	}
	
	public void test01Run() throws Exception {
		wizardHandle.run(true, true, null); 
	}
	
	public void test02SetDescription() {
		wizardHandle.setDescription(null); 
	}
	
	public void test03SetImageDescriptor() {
		wizardHandle.setImageDescriptor(null); 
	}
	
	public void test04SetMessage() {
		wizardHandle.setMessage(sampleMessage, sampleMessageType); 
	}
	
	public void test05SetTitle() {
		wizardHandle.setTitle(null); 
	}
	
	public void test06Update() {
		wizardHandle.update(); 
	}
	
	public void test07GetMessage() {
		assertEquals(sampleMessage, wizardHandle.getMessage());
	}
    
	public void test08GetMessageType() {
		assertEquals(sampleMessageType, wizardHandle.getMessageType());
	}
}
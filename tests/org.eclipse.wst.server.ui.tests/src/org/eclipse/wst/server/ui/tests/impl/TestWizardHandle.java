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
package org.eclipse.wst.server.ui.tests.impl;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;

public class TestWizardHandle implements IWizardHandle {
	private String message = null;
	private int messageType = IMessageProvider.NONE;
	
	public void update() {
		// ignore
	}

	public void setTitle(String title) {
		// ignore
	}

	public void setDescription(String desc) {
		// ignore
	}

	public void setImageDescriptor(ImageDescriptor image) {
		// ignore
	}

	public void setMessage(String newMessage, int newType) {
		message = newMessage;
		messageType = newType;
	}

	public void run(boolean fork, boolean cancelable, IRunnableWithProgress runnable) throws InterruptedException, InvocationTargetException {
		// ignore
	}

	public String getMessage() {
		return message;
	}

	public int getMessageType() {
		return messageType;
	}
}

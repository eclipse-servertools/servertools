/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui.wizard;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
/**
 * 
 */
public interface IWizardHandle extends IMessageProvider {
	public void update();

	/**
	 * Sets the title of this wizard page.
	 *  
	 * @param title
	 */
	public void setTitle(String title);

	/**
	 * 
	 * @param desc
	 */
	public void setDescription(String desc);

	/**
	 * 
	 * @param image
	 */
	public void setImageDescriptor(ImageDescriptor image);

	/**
	 * 
	 * @param newMessage
	 * @param newType
	 */
	public void setMessage(String newMessage, int newType);

	/**
	 * Execute a runnable within the context of the wizard. This will typically
	 * disable the wizard while the runnable is running, and provide a progress
	 * monitor for the user.  
	 * 
	 * @param fork
	 * @param cancelable
	 * @param runnable
	 * @throws InterruptedException
	 * @throws InvocationTargetException
	 */
	public void run(boolean fork, boolean cancelable, IRunnableWithProgress runnable) throws InterruptedException, InvocationTargetException;
}
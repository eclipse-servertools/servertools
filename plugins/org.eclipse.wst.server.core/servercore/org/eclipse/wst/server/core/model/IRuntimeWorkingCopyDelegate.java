/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.model;

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
/**
 * An interface for defining server runtimes.
 */
public interface IRuntimeWorkingCopyDelegate extends IRuntimeDelegate {
	public static final byte PRE_SAVE = 0;
	public static final byte POST_SAVE = 1;

	public void initialize(IRuntimeWorkingCopy runtime);
	
	public void setDefaults();
	
	/**
	 * Handle a save of this runtime. This method is called when the runtime
	 * working copy save() method is invoked and can be used to resolve
	 * calculated fields or perform other operations related to the changes
	 * that are being made.
	 * 
	 * @param id
	 * @param monitor
	 */
	public void handleSave(byte id, IProgressMonitor monitor);
}
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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.server.core.IRuntime;
/**
 * An interface for defining server runtimes.
 */
public interface IRuntimeDelegate {
	public void initialize(IRuntime runtime);

	/**
	 * Return the validation status of the runtime.
	 * 
	 * @return
	 */
	public IStatus validate();

	public void dispose();
}
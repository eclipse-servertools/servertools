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
package org.eclipse.wst.server.core;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.server.core.model.IModule;
/**
 * An IStatus that also contains a time and module. This
 * status is returned from the publishing operations.
 *
 * <p>This interface is not intended to be implemented by clients.</p>
 */
public interface IPublishStatus extends IStatus {
	/**
	 * Returns the time taken (in ms) for this publish action.
	 * 
	 * @return long
	 */
	public long getTime();

	/**
	 * Returns the module that was published.
	 *
	 * @return org.eclipse.wst.server.core.model.IModule
	 */
	public IModule getModule();
}

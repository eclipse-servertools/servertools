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

import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.IPublishControl;
/**
 * Class that decides whether to publish or delete a given
 * resource on the remote system.
 */
public interface IPublishManagerDelegate {
	/**
	 * Sets the publish control, used to obtain information about
	 * the publishing.
	 *
	 * Sets the publish state, used to determine the timestamps
	 * of the last publishing action.
	 * 
	 * Resolve which resources to publish or delete.
	 *
	 * @param control org.eclipse.wst.server.model.IPublishControl[]
	 * @param modules org.eclipse.wst.server.core.model.IModule[]
	 * @param monitor org.eclipse.core.runtime.IProgressMonitor
	 */
	public void resolve(IPublishControl[] control, IModule[] modules, IProgressMonitor monitor);

	/**
	 * Returns the list of remote resources to delete from the
	 * remote system.
	 *
	 * @param module org.eclipse.wst.server.core.model.IModule
	 * @return java.util.List
	 */
	public List getResourcesToDelete(IModule module);

	/**
	 * Returns the list of resources to publish to the remote
	 * system.
	 *
	 * @param module org.eclipse.wst.server.core.model.IModule
	 * @return java.util.List
	 */
	public List getResourcesToPublish(IModule module);
}

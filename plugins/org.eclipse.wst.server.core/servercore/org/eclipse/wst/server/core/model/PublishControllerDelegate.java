/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
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
package org.eclipse.wst.server.core.model;

import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.wst.server.core.IServer;
/**
 * A controller allows to inteject a different points of the publish action  
 * <p>
 * <b>Provisional API:</b> This class/interface is part of an interim API that is still under development and expected to 
 * change significantly before reaching stability. It is being made available at this early stage to solicit feedback 
 * from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken 
 * (repeatedly) as the API evolves.
 * </p>
 * 
 * @since 1.1
 */
public abstract class PublishControllerDelegate {
	
	/**
	 * Create a new operation. The label and description must be supplied
	 * by overriding the getLabel() and getDescription() methods.
	 */
	public PublishControllerDelegate() {
		// do nothing
	}

	public abstract boolean isPublishRequired(IServer server, IResourceDelta delta);
}
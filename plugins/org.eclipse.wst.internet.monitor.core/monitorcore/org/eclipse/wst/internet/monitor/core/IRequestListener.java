/**********************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.internet.monitor.core;
/**
 * A listener for changes to the requests.
 * 
 * @since 1.0
 */
public interface IRequestListener {
	/**
	 * The given request has been added to the list.
	 * 
	 * @param request the request that has been added
	 */
	public void requestAdded(IRequest request);
	
	/**
	 * The given request has been changed.
	 * 
	 * @param request the request that has been changed
	 */
	public void requestChanged(IRequest request);
	
	/**
	 * The given request is been removed from the list.
	 * 
	 * @param request the request that has been removed
	 */
	public void requestRemoved(IRequest request);
}
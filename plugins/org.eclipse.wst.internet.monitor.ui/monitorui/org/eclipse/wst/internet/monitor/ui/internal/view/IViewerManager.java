/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.internet.monitor.ui.internal.view;

import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.wst.internet.monitor.core.Request;
import org.eclipse.wst.internet.monitor.core.internal.IContentFilter;
import org.eclipse.wst.internet.monitor.ui.ContentViewer;
import org.eclipse.wst.internet.monitor.ui.internal.viewers.HeaderViewer;
/**
 * Manager interface for TCP/IP request and response message viewers
 */
public interface IViewerManager {
	/**
	 * Set whether or not to show HTTP header details.
	 * 
	 * @param b boolean
	 */
	public void setDisplayHeaderInfo(boolean b);
	
	/**
	 * Returns whether or not HTTP header details is showing.
	 * 
	 * @return boolean
	 */
	public boolean getDisplayHeaderInfo();
	
	/**
	 * Show the TCP/IP request message in a parent Composite
	 * 
	 * @param request the request
	 */
	public void setRequest(Request request);
	
	/**
	 * Returns an array of the available TCP/IP request viewer ids
	 * 
	 * @return a list of request viewers
	 */
	public List getRequestViewers();
	
	/**
	 * Returns an array of the available TCP/IP response viewer ids
	 * 
	 * @return a list of response viewers
	 */
	public List getResponseViewers();
	
	/**
	 * Set the TCP/IP request message viewer
	 * 
	 * @param element
	 */
	public void setRequestViewer(IConfigurationElement element);
	
	/**
	 * Set the TCP/IP response message viewer
	 * 
	 * @param element
	 */
	public void setResponseViewer(IConfigurationElement element);
	
	/**
	 * 
	 * @param filter
	 */
	public void addFilter(IContentFilter filter);
	
	/**
	 * 
	 * @param filter
	 */
	public void removeFilter(IContentFilter filter);
	
	/**
	 * Return the current request viewer.
	 * 
	 * @return the current request viewer
	 */
	public ContentViewer getCurrentRequestViewer();
	
	/**
	 * Return the current request header viewer.
	 * 
	 * @return the current request header viewer
	 */
	public HeaderViewer getCurrentRequestHeaderViewer();
}
/*******************************************************************************
 * Copyright (c) 2003, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.internet.monitor.ui.internal.view;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.internet.monitor.core.internal.IContentFilter;
import org.eclipse.wst.internet.monitor.core.internal.http.ResendHTTPRequest;
import org.eclipse.wst.internet.monitor.core.internal.provisional.Request;
import org.eclipse.wst.internet.monitor.ui.internal.MonitorUIPlugin;
import org.eclipse.wst.internet.monitor.ui.internal.Trace;
import org.eclipse.wst.internet.monitor.ui.internal.custom.SashForm;
import org.eclipse.wst.internet.monitor.ui.internal.provisional.ContentViewer;
import org.eclipse.wst.internet.monitor.ui.internal.viewers.ByteViewer;
import org.eclipse.wst.internet.monitor.ui.internal.viewers.HeaderViewer;
/**
 * 
 */
public class ViewerManager {
	private boolean displayHeaderInf;

	protected ContentViewer reqViewer;
	protected ContentViewer respViewer;
	protected Viewer requestViewer;
	protected Viewer responseViewer;

	protected HeaderViewer reqHeader;
	protected HeaderViewer respHeader;

	protected Composite reqComp;
	protected Composite respComp;

	protected List<Viewer> viewers;

	protected Request request;

	protected SashForm reqSash;
	protected SashForm respSash;

	protected List<IContentFilter> filters = new ArrayList<IContentFilter>();

	public ViewerManager(Composite reqParent, Composite respParent) {
		reqSash = new SashForm(reqParent, SWT.VERTICAL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginWidth = 0;
		reqSash.setLayout(layout);
		reqSash.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		respSash = new SashForm(respParent, SWT.VERTICAL);
		layout = new GridLayout();
		layout.numColumns = 1;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginWidth = 0;
		respSash.setLayout(layout);
		respSash.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		reqComp = reqSash;
		respComp = respSash;
		
		reqHeader = new HeaderViewer(reqSash, HeaderViewer.REQUEST_HEADER);
		respHeader = new HeaderViewer(respSash, HeaderViewer.RESPONSE_HEADER);
		reqViewer = new ByteViewer();
		reqViewer.init(reqSash);
		respViewer = new ByteViewer();
		respViewer.init(respSash);
		
		reqSash.setWeights(new int[] { 10, 90 });
		respSash.setWeights(new int[] { 10, 90 });
		
		setDisplayHeaderInfo(MonitorUIPlugin.getShowHeaderPreference());
		loadAvailableViewers();
	}

	protected Viewer getDefaultViewer(String name) {
		if (name == null)
			return null;
		
		String name2 = name.toLowerCase(); 
		if (name2.endsWith(".xml"))
			return findViewer("org.eclipse.wst.internet.monitor.viewers.xml");
		else if (name2.endsWith(".html"))
			return findViewer("org.eclipse.wst.internet.monitor.viewers.browser");
		else if (name2.endsWith(".gif") || name2.endsWith(".jpg") ||
				name2.endsWith(".jpeg") || name2.endsWith(".png"))
			return findViewer("org.eclipse.wst.internet.monitor.viewers.image");
		else
			return findViewer("org.eclipse.wst.internet.monitor.viewers.byte");
	}

	protected Viewer findViewer(String id) {
		if (id == null)
			return null;
		
		Iterator iterator = viewers.iterator();
		while (iterator.hasNext()) {
			Viewer viewer = (Viewer) iterator.next();
			if (id.equals(viewer.getId()))
				return viewer;
		}
		return null;
	}

	private void loadAvailableViewers() {
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(MonitorUIPlugin.PLUGIN_ID, "viewers");
		int size = cf.length;
		viewers = new ArrayList<Viewer>(size);
		for (IConfigurationElement ce : cf)
			viewers.add(new Viewer(ce));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.internet.monitor.internal.view.IViewerManager#setDisplayHeaderInfo(boolean)
	 */
	public void setDisplayHeaderInfo(boolean b) {
		displayHeaderInf = b;
		reqHeader.setDisplayHeader(b);
		respHeader.setDisplayHeader(b);
		reqSash.setSimpleLayout(b);
		respSash.setSimpleLayout(b);
		reqSash.layout(true);
		respSash.layout(true);
		
		MonitorUIPlugin.setShowHeaderPreference(b);
		if (b) {
			reqHeader.setEditable(false);
			if (request instanceof ResendHTTPRequest && request.getResponse(Request.TRANSPORT) == null) {
				reqHeader.setEditable(true);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.internet.monitor.internal.view.IViewerManager#getDisplayHeaderInfo()
	 */
	public boolean getDisplayHeaderInfo() {
		return displayHeaderInf;
	}

	public void setRequest(Request rr) {
		// maintain the state of the request and request header if they've been modified.
		if (request instanceof ResendHTTPRequest && request.getResponse(Request.ALL) == null) {
			ResendHTTPRequest resRequest = (ResendHTTPRequest) request;
			//EditableContentViewer editViewer = (ContentViewer) reqViewer;
			byte[] content = reqViewer.getContent();
			byte[] b = resRequest.getRequest(Request.CONTENT);
			if (content != null && b != null && !MonitorUIPlugin.parse(b).equals(MonitorUIPlugin.parse(content))) {
				resRequest.setRequest(content, Request.CONTENT);
			}
			byte[] header = reqHeader.getContent();
			b = resRequest.getRequest(Request.TRANSPORT);
			if (header != null && b != null && !MonitorUIPlugin.parse(b).equals(MonitorUIPlugin.parse(header))) {
				resRequest.setRequest(header, Request.TRANSPORT);
			}
		}
		reqHeader.setRequestResponse(rr);
		respHeader.setRequestResponse(rr);
		byte[] b = null;
		if (rr != null)
			b = filter(rr.getRequest(Request.CONTENT));
		reqViewer.setContent(b);
		b = null;
		if (rr != null)
			b = filter(rr.getResponse(Request.CONTENT));
		respViewer.setContent(b);
		request = rr;
		
		// set the editor to editable if the request hasn't been sent and the
		// editor can be set as editable
		if (request instanceof ResendHTTPRequest && request.getResponse(Request.ALL) == null) {
			if (displayHeaderInf)
				reqHeader.setEditable(true);
			
			reqViewer.setEditable(true);
		} else {
			if (displayHeaderInf)
				reqHeader.setEditable(false);
			
			reqViewer.setEditable(false);
		}
	}

	public void addFilter(IContentFilter filter) {
		filters.add(filter);
		setRequest(request);
	}

	public void removeFilter(IContentFilter filter) {
		filters.remove(filter);
		setRequest(request);
	}

	protected byte[] filter(byte[] b) {
		if (b == null)
			return null;
		Iterator iterator = filters.iterator();
		while (iterator.hasNext()) {
			IContentFilter filter = (IContentFilter) iterator.next();
			try {
				b = filter.filter(request, false, b);
			} catch (Exception e) {
				if(Trace.SEVERE) {
					Trace.trace(Trace.STRING_SEVERE, "Error while filtering with " + filter.getId(), e);
				}
			}
		}
		return b;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.tcpip.monitor.internal.view.IViewerManager#getRequestViewers()
	 */
	public List getRequestViewers() {
		Iterator iterator = viewers.iterator();
		List<Viewer> temp = new ArrayList<Viewer>();
		while (iterator.hasNext()) {
			Viewer viewer = (Viewer) iterator.next();
			if (viewer.isRequestViewer())
				temp.add(viewer);
		}
		return temp;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.tcpip.monitor.internal.view.IViewerManager#getResponseViewers()
	 */
	public List getResponseViewers() {
		Iterator iterator = viewers.iterator();
		List<Viewer> temp = new ArrayList<Viewer>();
		while (iterator.hasNext()) {
			Viewer viewer = (Viewer) iterator.next();
			if (viewer.isResponseViewer())
				temp.add(viewer);
		}
		return temp;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.tcpip.monitor.internal.view.IViewerManager#setRequestViewer(java.lang.String)
	 */
	public void setRequestViewer(Viewer viewer) {
		if (viewer != null && viewer.equals(requestViewer))
			return;
		
		// call set request to save and reset the request
		setRequest(request);
		reqViewer.dispose();
		
		requestViewer = viewer;
		if (viewer != null)
			reqViewer = viewer.createViewer();
		else
			reqViewer = null;
		if (reqViewer == null)
			return;
		
		reqViewer.init(reqComp);
		//reqViewer.setRequestResponse(rr);
		byte[] b = null;
		if (request != null) {
			b = filter(request.getRequest(Request.CONTENT));
			// set the editor to editable if the request hasn't been sent and the
			// editor can be set as editable
			if (request instanceof ResendHTTPRequest && request.getResponse(Request.TRANSPORT) == null) {
				reqViewer.setEditable(true);
			} else {
				reqViewer.setEditable(false);
			}
		}
		reqViewer.setContent(b);
		reqComp.layout(true);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.tcpip.monitor.internal.view.IViewerManager#setResponseViewer(java.lang.String)
	 */
	public void setResponseViewer(Viewer viewer) {
		if (viewer != null && viewer.equals(responseViewer))
			return;
		respViewer.dispose();
		
		responseViewer = viewer;
		if (viewer != null)
			respViewer = viewer.createViewer();
		else
			respViewer = null;
		if (respViewer == null)
			return;
		
		respViewer.init(respComp);
		//respViewer.setRequestResponse(rr);
		byte[] b = null;
		if (request != null)
			b = filter(request.getResponse(Request.CONTENT));
		respViewer.setContent(b);
		respComp.layout(true);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.internet.monitor.ui.internal.view.IViewerManager#getCurrentRequestViewer()
	 */
	public ContentViewer getCurrentRequestViewer() {
		return reqViewer;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.internet.monitor.ui.internal.view.IViewerManager#getCurrentRequestHeaderViewer()
	 */
	public HeaderViewer getCurrentRequestHeaderViewer() {
		return reqHeader;
	}
}
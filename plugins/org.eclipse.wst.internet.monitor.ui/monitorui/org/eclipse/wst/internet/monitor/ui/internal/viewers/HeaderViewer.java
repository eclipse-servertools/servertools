/*******************************************************************************
 * Copyright (c) 2003, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.internet.monitor.ui.internal.viewers;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.internet.monitor.core.internal.provisional.Request;
import org.eclipse.wst.internet.monitor.ui.internal.ContextIds;
import org.eclipse.wst.internet.monitor.ui.internal.Messages;
import org.eclipse.wst.internet.monitor.ui.internal.MonitorUIPlugin;
import org.eclipse.wst.internet.monitor.ui.internal.custom.MonitorStackLayout;
/**
 * An transport (header) viewer.
 */
public class HeaderViewer {
	protected boolean displayHeader;

	protected Composite headerComp;
	protected MonitorStackLayout layout;

	protected Text headerLabel;
	protected Text headerText;
	protected Request rr;
	protected byte msg;

	protected boolean hidden;

	/**
	 * Request header constant.
	 */
	public static byte REQUEST_HEADER = 0;
	
	/**
	 * Response header constant.
	 */
	public static byte RESPONSE_HEADER = 1;

	/**
	 * Create a new header viewer.
	 * 
	 * @param parent
	 * @param message
	 */
	public HeaderViewer(Composite parent, byte message) {
		displayHeader = true;
		hidden = false;
		
		headerComp = new Composite(parent, SWT.NONE);
		layout = new MonitorStackLayout();
		headerComp.setLayout(layout);
		
		headerText = new Text(headerComp, SWT.BORDER | SWT.MULTI | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL);
		Display display = headerComp.getDisplay();
		headerText.setBackground(display.getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		headerText.setForeground(display.getSystemColor(SWT.COLOR_LIST_FOREGROUND));
		headerText.setFont(JFaceResources.getTextFont());
		PlatformUI.getWorkbench().getHelpSystem().setHelp(headerText, ContextIds.VIEW_RESPONSE);
		
		headerLabel = new Text(headerComp, SWT.READ_ONLY);
		
		layout.topControl = headerText;
		
		rr = null;
		msg = message;

		setDisplayHeader(false);
	}

	/**
	 * 
	 * @param request
	 */
	public void setRequestResponse(Request request) {
		rr = request;
		if (!hidden)
			getView();
	}

	/**
	 * 
	 * @param b
	 */
	public void setDisplayHeader(boolean b) {
		if (displayHeader != b) {
			displayHeader = b;
			
			if (displayHeader)
				layout.topControl = headerText;
			else
				layout.topControl = headerLabel;
			
			headerComp.layout(true);
			getView();
		}
	}

	private void getView() {
		String out = "";
		if (rr != null) {
			if (msg == REQUEST_HEADER) {
				byte[] b = rr.getRequest(Request.TRANSPORT);
				if (b != null)
					out = MonitorUIPlugin.parse(b);
			} else if (msg == RESPONSE_HEADER) {
				byte[] b = rr.getResponse(Request.TRANSPORT);
				if (b != null)
					out = MonitorUIPlugin.parse(b);
			}
		}
		
		if (displayHeader)
			headerText.setText(out);
		else {
			String lineSeparator = System.getProperty("line.separator");
			int index = out.indexOf(lineSeparator);
			if (index > 0)
				headerLabel.setText(NLS.bind(Messages.headerLabel, out.substring(0, index)));
			else 
				headerLabel.setText(NLS.bind(Messages.headerLabel, out));
		}
	}

	/**
	 * Dispose the header.
	 */
	public void dispose() {
		headerComp.dispose();
	}

	/**
	 * Set whether the header can be edited.
	 * 
	 * @param editable If true the header can be edited, otherwise the header cannot be edited.
	 */
	public void setEditable(boolean editable) {
		headerText.setEditable(editable);
	}

	/**
	 * Get the content from the header.
	 * 
	 * @return The content from the header.
	 */
	public byte[] getContent() {
		if (headerText == null || headerText.isDisposed())
			return null;
		
		String header = headerText.getText().trim();
		// Need to ensure that the following 4 bytes end the header. The getBytes()
		// method removes spaces at the end of the string.
		byte[] twoNewlines = new byte[] { '\015', '\012', '\015', '\012' };
		byte[] headerBytes = header.getBytes();
		byte[] retBytes = new byte[headerBytes.length + 4];
		System.arraycopy(headerBytes, 0, retBytes, 0, headerBytes.length);
		System.arraycopy(twoNewlines, 0, retBytes, headerBytes.length, 4);
		return retBytes;
	}
}
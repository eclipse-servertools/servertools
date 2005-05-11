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
package org.eclipse.wst.internet.monitor.ui.internal.viewers;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.internet.monitor.core.internal.provisional.Request;
import org.eclipse.wst.internet.monitor.ui.internal.ContextIds;
import org.eclipse.wst.internet.monitor.ui.internal.Messages;
import org.eclipse.wst.internet.monitor.ui.internal.MonitorUIPlugin;
/**
 * An transport (header) viewer.
 */
public class HeaderViewer {
	protected boolean displayHeader;

	protected Composite headerComp;
	protected Composite innerComp;
	protected Composite rootComp;

	protected Label headerLabel;
	protected Text headerText;
	protected Request rr;
	protected byte msg;
	protected GridLayout layout;
	protected GridData data;

	protected boolean hidden;

	protected static int HEADER_LABEL_SIZE = 15;
	protected static int HEADER_TEXT_SIZE = 110;
	
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
		rootComp = parent;
		displayHeader = true;
		hidden = false;
		
		headerComp = new Composite(parent, SWT.NONE);
		layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		headerComp.setLayout(layout);
		data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		headerComp.setLayoutData(data);

		innerComp = new Composite(headerComp, SWT.NONE);
		layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginHeight = 0;
		layout.marginWidth = 2;
		innerComp.setLayout(layout);
		data = new GridData(GridData.FILL_BOTH);
		data.heightHint = HEADER_LABEL_SIZE;
		innerComp.setLayoutData(data);

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
			if (displayHeader) {
				innerComp.dispose();

				data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
				data.heightHint = HEADER_TEXT_SIZE;
				headerComp.setLayoutData(data);
								
				innerComp = new Composite(headerComp, SWT.NONE);
				layout = new GridLayout();
				layout.numColumns = 1;
				layout.marginHeight = 0;
				layout.marginWidth = 0;
				innerComp.setLayout(layout);
				data = new GridData(GridData.FILL_BOTH);
				data.heightHint = HEADER_TEXT_SIZE;
				innerComp.setLayoutData(data);
								
				headerText = new Text(innerComp, SWT.BORDER | SWT.MULTI | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL);
				Display display = innerComp.getDisplay();
				headerText.setBackground(display.getSystemColor(SWT.COLOR_LIST_BACKGROUND));
				headerText.setForeground(display.getSystemColor(SWT.COLOR_LIST_FOREGROUND));
				headerText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));
				headerText.setFont(JFaceResources.getTextFont());
				PlatformUI.getWorkbench().getHelpSystem().setHelp(headerText, ContextIds.VIEW_RESPONSE);

				rootComp.layout(true);
			} else {
				innerComp.dispose();

				data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
				data.heightHint = HEADER_LABEL_SIZE;
				headerComp.setLayoutData(data);
				
				innerComp = new Composite(headerComp, SWT.NONE);
				layout = new GridLayout();
				layout.numColumns = 1;
				layout.marginHeight = 0;
				layout.marginWidth = 2;
				innerComp.setLayout(layout);
				data = new GridData(GridData.FILL_BOTH);
				data.heightHint = HEADER_LABEL_SIZE;
				innerComp.setLayoutData(data);

				headerLabel = new Label(innerComp, SWT.NONE);
				headerLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));
				
				rootComp.layout(true);
			}
		}
		getView();
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
		
		if (displayHeader) {
			headerText.setText(out);
		} else {
			String lineSeparator = System.getProperty("line.separator");
			int index = out.indexOf(lineSeparator);
			if(index > 0)
				headerLabel.setText(Messages.headerLabel + ": " + out.substring(0, index));
			else 
				headerLabel.setText(Messages.headerLabel + ":  " + out);
		}
	}

	/**
	 * Dispose the 
	 *
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
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

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.wst.internet.monitor.ui.internal.Messages;
import org.eclipse.wst.internet.monitor.ui.internal.MonitorUIPlugin;
import org.eclipse.wst.internet.monitor.ui.internal.provisional.ContentViewer;
/**
 * A browser viewer.
 */
public class BrowserViewer extends ContentViewer {
	protected static final byte CR = '\r';
	protected static final byte LF = '\n';

	protected Browser browser;

	protected byte[] content;

	/** (non-Javadoc)
	 * @see ContentViewer#init(Composite)
	 */
	public void init(Composite parent) {
		browser = new Browser(parent, SWT.NONE);
		browser.addListener(SWT.MenuDetect, new Listener() {
			public void handleEvent(Event event) {
				event.doit = false;
			}
		});
	}

	/** (non-Javadoc)
	 * @see ContentViewer#setContent(byte[])
	 */
	public void setContent(byte[] b) {
		content = b;
		if (b == null || b.length == 0) {
			browser.setText(Messages.htmlViewInvalid);
		} else {
			b = MonitorUIPlugin.unzip(b);
			
			int trimFront = 0;
			int trimBack = 0;
			int len = b.length - 1;
			while (trimFront < b.length && (b[trimFront] == CR || b[trimFront] == LF))
				trimFront++;
			while (trimBack < b.length && b.length > 0 && (b[len - trimBack] == CR || b[len - trimBack] == LF))
				trimBack++;
			
			if (trimFront + trimBack > 0) {
				if (trimFront + trimBack > b.length) {
					b = new byte[0];
				} else {
					byte[] temp = b;
					b = new byte[temp.length - trimBack - trimFront];
					for (int i = trimFront; i < temp.length - trimBack; i++) {
						b[i - trimFront] = temp[i];
					}
				}
			}
			browser.setText(new String(b));
		}
	}

	/**
	 * @see ContentViewer#getContent()
	 */
	public byte[] getContent() {
		return content;
	}

	/** (non-Javadoc)
	 * @see ContentViewer#dispose()
	 */
	public void dispose() {
		browser.dispose();
		browser = null;
		content = null;
	}
}
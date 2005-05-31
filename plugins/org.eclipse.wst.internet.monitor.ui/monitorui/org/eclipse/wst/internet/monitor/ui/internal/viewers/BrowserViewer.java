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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.internet.monitor.ui.internal.Messages;
import org.eclipse.wst.internet.monitor.ui.internal.provisional.ContentViewer;
/**
 * A browser viewer.
 */
public class BrowserViewer extends ContentViewer {
	protected Browser browser;

	protected byte[] content;

	/** (non-Javadoc)
	 * @see ContentViewer#init(Composite)
	 */
	public void init(Composite parent) {
		browser = new Browser(parent, SWT.BORDER);
		browser.setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	/** (non-Javadoc)
	 * @see ContentViewer#setContent(byte[])
	 */
	public void setContent(byte[] b) {
		content = b;
		if (b == null || b.length == 0) {
			browser.setText(Messages.htmlViewInvalid);
		} else {
			byte cr = '\r';
			byte lf = '\n';
			int trimFront = 0;
			int trimBack = 0;
			int len = b.length - 1;
			while (b[trimFront] == cr || b[trimFront] == lf)
				trimFront++;
			while (b[len - trimBack] == cr || b[len - trimBack] == lf)
				trimBack++;
				
			if (trimFront + trimBack > 0) {
				byte[] temp = b;
				b = new byte[temp.length - trimBack - trimFront];
				for (int i = trimFront; i < temp.length - trimBack; i++) {
					b[i - trimFront] = temp[i];
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
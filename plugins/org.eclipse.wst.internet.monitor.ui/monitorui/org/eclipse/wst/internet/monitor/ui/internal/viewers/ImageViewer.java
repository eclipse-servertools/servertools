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

import java.io.ByteArrayInputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wst.internet.monitor.ui.ContentViewer;
import org.eclipse.wst.internet.monitor.ui.internal.Messages;
/**
 * An image viewer.
 */
public class ImageViewer extends ContentViewer {
	protected Composite rootComp;
	protected Composite viewerComp;
	protected Label messageLabel;
	
	protected byte[] content;

	/** (non-Javadoc)
	 * @see ContentViewer#init(Composite)
	 */
	public void init(Composite parent) {
		rootComp = parent;
		
		viewerComp = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		viewerComp.setLayout(layout);
		GridData data = new GridData(GridData.FILL_BOTH);
		viewerComp.setLayoutData(data);

		messageLabel = new Label(viewerComp, SWT.NONE);
		messageLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_BEGINNING));
	}
	
	/** (non-Javadoc)
	 * @see ContentViewer#setContent(byte[])
	 */
	public void setContent(byte[] b) {
		content = b;
		if (b == null || b.length == 0) {
			messageLabel.setText(Messages.imageViewInvalid);
		} else {
			byte cr = '\r';
			byte lf = '\n';
			int trimFront = 0;
			int trimBack = 0;
			int len = b.length - 1;
			while(b[trimFront] == cr || b[trimFront] == lf)
				trimFront++;
			while(b[len - trimBack] == cr || b[len - trimBack] == lf)
				trimBack++;
				
			if (trimFront + trimBack > 0) {
				byte[] temp = b;
				b = new byte[temp.length - trimBack - trimFront];
				for(int i = trimFront; i < temp.length - trimBack; i++) {
					b[i - trimFront] = temp[i];
				}
			}
			try {
				ImageData imgD = new ImageData(new ByteArrayInputStream(b));
				Image img = new Image(null, imgD);
				messageLabel.setImage(img);
			} catch(Exception e) {
				messageLabel.setText(Messages.imageViewInvalid);
			}
		}
		
		viewerComp.layout(true);
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
		viewerComp.dispose();
	}
}
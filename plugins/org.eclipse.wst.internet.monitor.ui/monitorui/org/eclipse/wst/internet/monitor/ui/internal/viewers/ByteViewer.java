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
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.internet.monitor.ui.internal.ContextIds;
import org.eclipse.wst.internet.monitor.ui.internal.MonitorUIPlugin;
import org.eclipse.wst.internet.monitor.ui.internal.provisional.ContentViewer;
/**
 * A basic byte viewer.
 */
public class ByteViewer extends ContentViewer {
	protected Text text;
	protected Composite comp;

	/** (non-Javadoc)
	 * @see ContentViewer#dispose()
	 */
	public void dispose() {
		comp.dispose();
	}

	/** (non-Javadoc)
	 * @see ContentViewer#setContent(byte[])
	 */
	public void setContent(byte[] b) {
		String out = "";
		if (b != null)
			out = MonitorUIPlugin.parse(b);

		String lineSeparator = System.getProperty("line.separator");
		int ls = lineSeparator.length();
		if (out.length() > ls) {
			while (out.substring(0, ls).indexOf(lineSeparator) >= 0)
				out = out.substring(ls, out.length()); 
		}
		
		text.setText(out);
	}

	/** (non-Javadoc)
	 * @see ContentViewer#init(Composite)
	 */
	public void init(Composite parent) {
		comp = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		comp.setLayout(layout);
		GridData data = new GridData(GridData.FILL_BOTH);
		comp.setLayoutData(data);

		text = new Text(comp, SWT.BORDER | SWT.MULTI | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL);
		Display display = comp.getDisplay();
		text.setBackground(display.getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		text.setForeground(display.getSystemColor(SWT.COLOR_LIST_FOREGROUND));
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));
		text.setFont(JFaceResources.getTextFont());
		PlatformUI.getWorkbench().getHelpSystem().setHelp(text, ContextIds.VIEW_RESPONSE);
	}
	
	/** (non-Javadoc)
	 * @see ContentViewer#setEditable(boolean)
	 */
	public void setEditable(boolean editable) {
		text.setEditable(editable);
	}
	
	/** (non-Javadoc)
	 * @see ContentViewer#getContent()
	 */
	public byte[] getContent() {
		if (text == null) {
			return new byte[0];
		}
		String content = text.getText().trim();
		if (content.equals("")) {
			return new byte[0];
		}
		// Need to ensure that there is a newline at the end of the content.
		// getBytes() removes the newline.
		byte[] twoNewlines = new byte[] { '\015', '\012' };
		byte[] contentBytes = content.getBytes();
		byte[] retBytes = new byte[contentBytes.length + 2];
		System.arraycopy(contentBytes, 0, retBytes, 0, contentBytes.length);
		System.arraycopy(twoNewlines, 0, retBytes, contentBytes.length, 2);
		return retBytes;
	}
}
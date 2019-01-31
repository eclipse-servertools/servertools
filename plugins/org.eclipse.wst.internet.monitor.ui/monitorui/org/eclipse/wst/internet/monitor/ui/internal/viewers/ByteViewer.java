/*******************************************************************************
 * Copyright (c) 2003, 2013 IBM Corporation and others.
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
package org.eclipse.wst.internet.monitor.ui.internal.viewers;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.internet.monitor.ui.internal.ContextIds;
import org.eclipse.wst.internet.monitor.ui.internal.Messages;
import org.eclipse.wst.internet.monitor.ui.internal.MonitorUIPlugin;
import org.eclipse.wst.internet.monitor.ui.internal.Trace;
import org.eclipse.wst.internet.monitor.ui.internal.provisional.ContentViewer;
/**
 * A basic byte viewer.
 */
public class ByteViewer extends ContentViewer {
	protected Text text;
	protected Label encodingLabel;
	protected Combo encodingCombo;
	protected Composite byteViewerBodyComposite;
	protected String encodingType = null;
	
	// Cache the content in order to encode the content when the user changes
	// the content
	protected byte[] contentArray;
	
	// copied from MonitorUI
	private static final String lineSeparator = System.getProperty("line.separator");
	
	protected String[] encodings = null;
	
	/** (non-Javadoc)
	 * @see ContentViewer#init(Composite)
	 */
	public void init(Composite parent) {		
		byteViewerBodyComposite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		
		GridData data = new GridData(GridData.FILL_HORIZONTAL 
				| GridData.FILL_VERTICAL 
				| GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan = 2;
		
		byteViewerBodyComposite.setLayoutData(data);		
		byteViewerBodyComposite.setLayout(layout);
		
		// This additional layout helps with aligning the text boxes in
		// org.eclipse.wst.internet.monitor.ui.internal.view.MonitorView
		Composite request = new Composite(byteViewerBodyComposite, SWT.NONE);
		GridLayout layout2 = new GridLayout();
		layout2.numColumns = 2;
		request.setLayout(layout2);
		
		encodingLabel = new Label(request, SWT.NONE);
		encodingLabel.setText(NLS.bind(Messages.viewEncoding,""));		
		encodingCombo = new Combo(request, SWT.RIGHT);
		
		// Add the default option
		encodingCombo.add(NLS.bind(Messages.defaultEncodingOption,""));
		// Select the default option
		encodingCombo.select(0);
		
		if (encodings != null){
			int size = encodings.length;
			for (int i=0;i<size;i++){
				encodingCombo.add(encodings[i]);
			}
		}
		
		encodingCombo.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent me) {
				encodingType = encodingCombo.getText();				
				if (contentArray != null){
					setContent(contentArray);
				}
			}
			
		});
		
		text = new Text(byteViewerBodyComposite, SWT.BORDER | SWT.MULTI | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL);
		Display display = byteViewerBodyComposite.getDisplay();
		text.setLayoutData(data);
		text.setBackground(display.getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		text.setForeground(display.getSystemColor(SWT.COLOR_LIST_FOREGROUND));
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
		if (text == null)
			return new byte[0];
		
		String content = text.getText().trim();
		if (content.equals(""))
			return new byte[0];
		
		// Need to ensure that there is a newline at the end of the content
		// getBytes() removes the newline
		byte[] twoNewlines = new byte[] { '\015', '\012' };
		byte[] contentBytes = content.getBytes();
		byte[] retBytes = new byte[contentBytes.length + 2];
		System.arraycopy(contentBytes, 0, retBytes, 0, contentBytes.length);
		System.arraycopy(twoNewlines, 0, retBytes, contentBytes.length, 2);
		return retBytes;
	}

	/** (non-Javadoc)
	 * @see ContentViewer#setContent(byte[])
	 */
	public void setContent(byte[] b) {
		String out = "";
		if (b != null) {
			contentArray = b;
			b = MonitorUIPlugin.unzip(b);
			out = parseEncoded(b);
		}
		
		int ls = lineSeparator.length();
		if (out.length() > ls) {
			while (out.substring(0, ls).indexOf(lineSeparator) >= 0)
				out = out.substring(ls, out.length()); 
		}
		
		text.setText(out);
	}
	
	/**
	 * Convenience method to parse the given bytes into String form. The bytes
	 * are parsed into a line delimited string. The byte array must not be null.
	 * 
	 * @param b a byte array
	 * @return the string after the conversion
	 */
	public String parseEncoded(byte[] b) {
		if (b == null)
			throw new IllegalArgumentException();
		
		ByteArrayInputStream bin = new ByteArrayInputStream(b);
		BufferedReader br = null;
		if (encodingType != null){
			try {
				br = new BufferedReader(new InputStreamReader(bin,encodingType));
			} catch (UnsupportedEncodingException e) {
				if (Trace.FINEST) {
					Trace.trace(Trace.STRING_FINEST, 
							"An unsupported encoding exception when using encodingType="
							+ encodingType, e);
				}
				
				// If it is unsupported, use default. No prompt is given to the user
				br = new BufferedReader(new InputStreamReader(bin));
			}
		}
		else {
			br = new BufferedReader(new InputStreamReader(bin));
		}
		
		StringBuffer sb = new StringBuffer();
		try {
			String s = br.readLine();
			
			while (s != null) {
				sb.append(s);
				s = br.readLine();
				if (s != null)
					sb.append(lineSeparator);
			}
		} catch (Exception e) {
			if (Trace.SEVERE) {
				Trace.trace(Trace.STRING_SEVERE, "Error parsing input", e);
			}
		}
		
		return sb.toString();
	}	
	

	/** (non-Javadoc)
	 * @see ContentViewer#dispose()
	 */
	public void dispose() {
		text.dispose();
		text = null;
		
		encodingCombo.dispose();
		encodingCombo = null;
		
		encodingLabel.dispose();
		encodingLabel = null;
		
		byteViewerBodyComposite.dispose();
		byteViewerBodyComposite = null;
	}

	/** (non-Javadoc)
	 * 
	 * Sets the encodings to show in the encodings combo box
	 * 
	 */
	public void setEncodings(String[] str){
		this.encodings = str;
	}	
	
}

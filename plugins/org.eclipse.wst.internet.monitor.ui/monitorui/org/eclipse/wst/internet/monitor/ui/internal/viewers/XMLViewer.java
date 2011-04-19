/*******************************************************************************
 * Copyright (c) 2003, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *     
 *     2005-11-25 Arthur Ryman, ryman@ca.ibm.com
 *     - fixed bug 118102: set xmlTagMissing correctly each time setContent() is called
 *******************************************************************************/
package org.eclipse.wst.internet.monitor.ui.internal.viewers;
 
import java.io.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.jface.resource.JFaceResources;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.internet.monitor.ui.internal.ContextIds;
import org.eclipse.wst.internet.monitor.ui.internal.Messages;
import org.eclipse.wst.internet.monitor.ui.internal.MonitorUIPlugin;
import org.eclipse.wst.internet.monitor.ui.internal.provisional.ContentViewer;

import org.w3c.dom.*;
import org.xml.sax.*;
/**
 * XML Viewer.
 */
public class XMLViewer extends ContentViewer {
	protected Composite viewerComp;
	protected StackLayout layout;
	protected Text messageText;
	protected Label messageLabel;

	protected boolean xmlTagMissing = false;
	protected boolean setEncoding = false;
	protected boolean missingEncoding = false;
	protected String originalEncoding;

	protected byte[] content;

	/** (non-Javadoc)
	 * @see ContentViewer#setContent(byte[])
	 */
	public void setContent(byte[] b) {
		content = b;
		String out = "";	
		if (b != null) {
			b = MonitorUIPlugin.unzip(b);
			out = MonitorUIPlugin.parse(b);
		}
		
		String lineSeparator = System.getProperty("line.separator");
		int ls = lineSeparator.length();
		if (out.length() > ls) {
			while (out.substring(0, ls).indexOf(lineSeparator) >= 0)
			out = out.substring(ls, out.length());
		}
		
		String out_temp = out.toLowerCase();
		xmlTagMissing = !out_temp.startsWith("<?xml");
		
		if (out.length() > 0) {
			String finalMsg = null;
			try {
				byte[] b1 = createDocument(out);
				finalMsg = new String(b1).trim();
			} catch (Exception e) {
				// case: error parsing
				messageText.setVisible(false);
				layout.topControl = messageLabel;
				messageLabel.setVisible(true);
				messageLabel.setText(Messages.xmlViewInvalid);
				return;
			}
			if (xmlTagMissing && (finalMsg.toLowerCase().startsWith("<?xml version=\"1.0\" encoding=\"utf-8\"?>")
							|| finalMsg.toLowerCase().startsWith("<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"no\"?>") 
							|| finalMsg.toLowerCase().startsWith("<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"yes\"?>"))) {
				int x = finalMsg.indexOf(">") + 1;
				//remove <?xml version="1.0" encoding="UTF-8"?>
				String Msg = finalMsg.substring(x);
				//remove starting newlines
				while (Msg.substring(0, ls).indexOf(lineSeparator) >= 0){
					Msg = Msg.substring(ls, Msg.length());
				}
				finalMsg = Msg;
				
				messageText.setText(finalMsg);
			} else if (setEncoding) {
				// change back to original encoding
				int begin = finalMsg.toLowerCase().indexOf("utf-8"); // location of opening "
				if (begin >= 0) {
					int last = begin + 5;  // location of closing "
					String first_half = finalMsg.substring(0,begin);
					String second_half = finalMsg.substring(last);
					finalMsg = first_half + originalEncoding + second_half;
				}
				
				messageText.setText(finalMsg);
			} else if (missingEncoding) {
				// remove encoding completely
				int begin = finalMsg.toLowerCase().indexOf("encoding=\"utf-8\""); //location of opening "
				int last = begin + 16;  //location of closing "
				String first_half = finalMsg.substring(0,begin);
				String second_half = finalMsg.substring(last);
				finalMsg = first_half + second_half;
				
				messageText.setText(finalMsg);	
			} else
				messageText.setText(finalMsg);
		} else
			messageText.setText(out);
		
		messageLabel.setVisible(false);
		layout.topControl = messageText;
		messageText.setVisible(true);
	}

	/**
	 * @see ContentViewer#getContent()
	 */
	public byte[] getContent() {
		return content;
	}

	/** (non-Javadoc)
	 * @see ContentViewer#init(Composite)
	 */
	public void init(Composite parent) {
		viewerComp = new Composite(parent, SWT.NONE);
		layout = new StackLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		viewerComp.setLayout(layout);
		
		messageText = new Text(viewerComp, SWT.BORDER | SWT.MULTI | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL);
		Display display = viewerComp.getDisplay();
		messageText.setBackground(display.getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		messageText.setForeground(display.getSystemColor(SWT.COLOR_LIST_FOREGROUND));
		messageText.setFont(JFaceResources.getTextFont());
		messageText.setVisible(true);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(messageText, ContextIds.VIEW_RESPONSE);
		
		messageLabel = new Label(viewerComp, SWT.NONE);
		messageLabel.setVisible(false);
		
		layout.topControl = messageText;
	}

	/* (non-Javadoc)
	 * @#createDocument(String)
	 */
	protected byte[] createDocument(String str) throws IOException {
		try {	
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			try {
				factory.setAttribute("http://apache.org/xml/features/allow-java-encodings", new Boolean(true));
				factory.setAttribute("http://apache.org/xml/features/continue-after-fatal-error", new Boolean(true));
			} catch (Exception e) {
				// ignore
			}
			DocumentBuilder parser = factory.newDocumentBuilder();
			
			if (xmlTagMissing) {
				str = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + str;
			} else {
				String str_temp = str.toLowerCase();
				
				// if encoding present, then save original encoding and change to UTF-8
				int ind = str_temp.indexOf("encoding=");
				if (ind >= 0) {
					String temp1 = str.substring(ind);
					int beginIndex = temp1.indexOf("\"") + 1;
					String temp2 = temp1.substring(beginIndex);
					int endIndex = temp2.indexOf("\"");
					originalEncoding = temp2.substring(0, endIndex);
					if (!"utf-8".equals(originalEncoding))
						setEncoding = true;
				} else if (ind < 0) { //if no encoding at all,then no changes to be made
					setEncoding = false;
					missingEncoding = true;
				}
			}
			byte[] parseArray = str.getBytes();
			Document document = parser.parse(new InputSource(new ByteArrayInputStream(parseArray)));
			return getContents(document);
		} catch (Exception e) {
			throw new IOException("Invalid XML");
		}
	}

	protected byte[] getContents(Document document) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Result result = new StreamResult(out);
		Source source = new DOMSource(document);
		try {
			TransformerFactory tf = TransformerFactory.newInstance();
			try {
				tf.setAttribute("indent-number", new Integer(2));
			} catch (Exception e) {
				e.printStackTrace();
				// ignore - fails on JDK 1.4
			}
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$
			transformer.setOutputProperty(OutputKeys.METHOD, "xml"); //$NON-NLS-1$
			transformer.setOutputProperty(OutputKeys.STANDALONE, "no"); //$NON-NLS-1$
			transformer.transform(source, result);
		} catch (TransformerConfigurationException e) {
			throw (IOException) (new IOException().initCause(e));
		} catch (TransformerException e) {
			throw (IOException) (new IOException().initCause(e));
		}
		return out.toByteArray();
	}

	/** (non-Javadoc)
	 * @see ContentViewer#dispose()
	 */
	public void dispose() {
		viewerComp.dispose();
		viewerComp = null;
		content = null;
	}
}
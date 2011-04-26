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
package org.eclipse.jst.server.tomcat.core.internal;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jst.server.tomcat.core.internal.xml.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
/**
 * Helper class to access a web.xml file.
 */
public class WebAppDocument {
	protected boolean isWebAppDirty;
	protected Document webAppDocument;

	/**
	 * Loads a web.xml from the given URL.
	 *
	 * @param path a path
	 * @throws Exception if anything goes wrong
	 */
	public WebAppDocument(IPath path) throws Exception {
		webAppDocument = XMLUtil.getDocumentBuilder().parse(new InputSource(new FileInputStream(path.toFile())));
	}

	/**
	 * Loads a web.xml from the given resource.
	 *
	 * @param file a file
	 * @throws Exception if anything goes wrong
	 */
	public WebAppDocument(IFile file) throws Exception {
		webAppDocument = XMLUtil.getDocumentBuilder().parse(new InputSource(file.getContents()));
	}

	/**
	 * Adds a MimeMapping.
	 *
	 * @param index int
	 * @param map org.eclipse.jst.server.tomcat.IMimeMapping
	 */
	public void addMimeMapping(int index, IMimeMapping map) {
		Trace.trace(Trace.FINER, "Adding mime mapping " + index + " " + map.getMimeType() + " " + map.getExtension());
		Element element = webAppDocument.getDocumentElement();
		Element mapping = XMLUtil.createChildElement(webAppDocument, element, index, "mime-mapping");
		XMLUtil.insertText(webAppDocument, mapping, "\n\t");
		XMLUtil.createTextChildElement(webAppDocument, mapping, "extension", map.getExtension());
		XMLUtil.insertText(webAppDocument, mapping, "\n\t");
		XMLUtil.createTextChildElement(webAppDocument, mapping, "mime-type", map.getMimeType());
		XMLUtil.insertText(webAppDocument, mapping, "\n");
		
		isWebAppDirty = true;
	}

	/**
	 * Returns a list of MimeMappings.
	 *
	 * @return java.util.List
	 */
	public List getMimeMappings() {
		List<MimeMapping> map = new ArrayList<MimeMapping>();
		
		Element root = webAppDocument.getDocumentElement();
		Iterator iterator = XMLUtil.getNodeIterator(root, "mime-mapping");
		while (iterator.hasNext()) {
			Element element = (Element) iterator.next();
			String mimeType = XMLUtil.getSubNodeValue(element, "mime-type");
			String extension = XMLUtil.getSubNodeValue(element, "extension");
			MimeMapping mm = new MimeMapping(extension, mimeType);
			map.add(mm);
		}
		
		return map;
	}

	/**
	 * Modifies a mime mapping.
	 *
	 * @param index
	 * @param map
	 */
	public void modifyMimeMapping(int index, IMimeMapping map) {
		Element element = webAppDocument.getDocumentElement();
		NodeList list = element.getElementsByTagName("mime-mapping");
		Element element2 = (Element) list.item(index);
		XMLUtil.setNodeValue(element2.getElementsByTagName("extension").item(0), "extension", map.getExtension());
		XMLUtil.setNodeValue(element2.getElementsByTagName("mime-type").item(0), "mime-type", map.getMimeType());
		
		isWebAppDirty = true;
	}

	/**
	 * Removes the mime mapping at the specified index.
	 *
	 * @param index int
	 */
	public void removeMimeMapping(int index) {
		Element element = webAppDocument.getDocumentElement();
		NodeList list = element.getElementsByTagName("mime-mapping");
		Node node = list.item(index);
		element.removeChild(node);
		isWebAppDirty = true;
	}

	/**
	 * Saves the Web app document.
	 *
	 * @param path a path
	 * @param forceDirty true to force a save
	 * @throws IOException if anything goes wrong
	 */
	public void save(String path, boolean forceDirty) throws IOException {
		if (forceDirty || isWebAppDirty)
			XMLUtil.save(path, webAppDocument);
	}

	/**
	 * Saves the Web app document.
	 *
	 * @param file a file
	 * @param monitor a progress monitor
	 * @throws Exception if anything goes wrong 
	 */
	public void save(IFile file, IProgressMonitor monitor) throws Exception {
		if (file.exists() && !isWebAppDirty)
			return;
		
		byte[] data = XMLUtil.getContents(webAppDocument);
		InputStream in = null;
		try {
			in = new ByteArrayInputStream(data);
			if (file.exists())
				file.setContents(in, true, true, ProgressUtil.getSubMonitorFor(monitor, 200));
			else
				file.create(in, true, ProgressUtil.getSubMonitorFor(monitor, 200));
		} catch (Exception e) {
			// ignore
		} finally {
			try {
				in.close();
			} catch (Exception e) {
				// ignore
			}
		}
		isWebAppDirty = false;
	}
}

/*******************************************************************************
 * Copyright (c) 2003, 2007 IBM Corporation and others.
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
package org.eclipse.wst.server.preview.adapter.internal.core;

import java.io.*;

import org.w3c.dom.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
/**
 * A Memento is a class independent container for persistence
 * info.  It is a reflection of 3 storage requirements.
 *
 * 1)   We need the ability to persist an object and restore it.  
 * 2)   The class for an object may be absent.  If so we would 
 *      like to skip the object and keep reading. 
 * 3)   The class for an object may change.  If so the new class 
 *      should be able to read the old persistence info.
 *
 * We could ask the objects to serialize themselves into an 
 * ObjectOutputStream, DataOutputStream, or Hashtable.  However 
 * all of these approaches fail to meet the second requirement.
 *
 * Memento supports binary persistance with a version ID.
 */
public final class XMLMemento implements IMemento {
	private Document factory;
	private Element element;

	/**
	 * Answer a memento for the document and element.  For simplicity
	 * you should use createReadRoot and createWriteRoot to create the initial
	 * mementos on a document.
	 */
	private XMLMemento(Document doc, Element el) {
		factory = doc;
		element = el;
	}

	/**
	 * @see IMemento#createChild(String)
	 */
	public IMemento createChild(String type) {
		Element child = factory.createElement(type);
		element.appendChild(child);
		return new XMLMemento(factory, child);
	}

	/**
	 * Answer a root memento for writing a document.
	 * 
	 * @param type a type
	 * @return a memento
	 */
	public static XMLMemento createWriteRoot(String type) {
		Document document;
		try {
			document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			Element element = document.createElement(type);
			document.appendChild(element);
			return new XMLMemento(document, element);            
		} catch (ParserConfigurationException e) {
			throw new Error(e);
		}
	}

	/*
	 * @see IMemento
	 */
	public void putInteger(String key, int n) {
		element.setAttribute(key, String.valueOf(n));
	}

	/*
	 * @see IMemento
	 */
	public void putString(String key, String value) {
		if (value == null)
			return;
		element.setAttribute(key, value);
	}
	
	/**
	 * Save this Memento to a Writer.
	 * 
	 * @param os an output stream
	 * @throws IOException if anything goes wrong
	 */
	private void save(OutputStream os) throws IOException {
		Result result = new StreamResult(os);
		Source source = new DOMSource(factory);
		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty("{http://xml.apache.org/xalan}indent-amount", "2");
			transformer.transform(source, result);
		} catch (Exception e) {
			throw (IOException) (new IOException().initCause(e));
		}
	}

	/**
	 * Saves the memento to the given file.
	 *
	 * @param filename java.lang.String
	 * @exception java.io.IOException
	 */
	public void saveToFile(String filename) throws IOException {
		FileOutputStream w = null;
		try {
			w = new FileOutputStream(filename);
			save(w);
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new IOException(e.getLocalizedMessage());
		} finally {
			if (w != null) {
				try {
					w.close();
				} catch (Exception e) {
					// ignore
				}
			}
		}
	}
}
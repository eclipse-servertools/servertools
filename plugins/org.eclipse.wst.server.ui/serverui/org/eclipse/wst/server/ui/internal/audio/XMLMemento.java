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
package org.eclipse.wst.server.ui.internal.audio;

import java.io.*;
import java.util.*;
import org.w3c.dom.*;
import org.xml.sax.*;
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
 * Memento supports binary persistence with a version ID.
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
	
	/*
	 * @see IMemento
	 */
	public IMemento createChild(String type) {
		Element child = factory.createElement(type);
		element.appendChild(child);
		return new XMLMemento(factory, child);
	}

	/**
	 * Create a Document from a Reader and answer a root memento for reading 
	 * a document.
	 */
	protected static XMLMemento createReadRoot(Reader reader) {
		Document document = null;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder parser = factory.newDocumentBuilder();
			document = parser.parse(new InputSource(reader));
			Node node = document.getFirstChild();
			if (node instanceof Element)
				return new XMLMemento(document, (Element) node);
		} catch (Exception e) {
			// ignore
		}
		return null;
	}

	/**
	 * Answer a root memento for writing a document.
	 * 
	 * @param type a type
	 * @return a memento
	 */
	protected static XMLMemento createWriteRoot(String type) {
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
	public IMemento getChild(String type) {
		
		// Get the nodes.
		NodeList nodes = element.getChildNodes();
		int size = nodes.getLength();
		if (size == 0)
			return null;
	
		// Find the first node which is a child of this node.
		for (int nX = 0; nX < size; nX ++) {
			Node node = nodes.item(nX);
			if (node instanceof Element) {
				Element element2 = (Element)node;
				if (element2.getNodeName().equals(type))
					return new XMLMemento(factory, element2);
			}
		}
	
		// A child was not found.
		return null;
	}
	
	/*
	 * @see IMemento
	 */
	public IMemento [] getChildren(String type) {
	
		// Get the nodes.
		NodeList nodes = element.getChildNodes();
		int size = nodes.getLength();
		if (size == 0)
			return new IMemento[0];
	
		// Extract each node with given type.
		List<Element> list = new ArrayList<Element>(size);
		for (int nX = 0; nX < size; nX ++) {
			Node node = nodes.item(nX);
			if (node instanceof Element) {
				Element element2 = (Element)node;
				if (element2.getNodeName().equals(type))
					list.add(element2);
			}
		}
	
		// Create a memento for each node.
		size = list.size();
		IMemento [] results = new IMemento[size];
		for (int x = 0; x < size; x ++) {
			results[x] = new XMLMemento(factory, list.get(x));
		}
		return results;
	}

	/*
	 * @see IMemento
	 */
	public String getString(String key) {
		Attr attr = element.getAttributeNode(key);
		if (attr == null)
			return null; 
		return attr.getValue();
	}
	
	/**
	 * Loads a memento from the given filename.
	 *
	 * @param in java.io.InputStream
	 * @return org.eclipse.ui.IMemento
	 */
	public static IMemento loadMemento(InputStream in) {
		Document document = null;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder parser = factory.newDocumentBuilder();
			document = parser.parse(in);
			Node node = document.getFirstChild();
			if (node instanceof Element)
				return new XMLMemento(document, (Element) node);
		} catch (Exception e) {
			// ignore
		} finally {
			try {
				in.close();
			} catch (Exception e) {
				// ignore
			}
		}
		return null;
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
	 */
	protected void save(OutputStream os) throws IOException {
		Result result = new StreamResult(os);
		Source source = new DOMSource(factory);
		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$
			transformer.setOutputProperty(OutputKeys.METHOD, "xml"); //$NON-NLS-1$
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8"); //$NON-NLS-1$
			transformer.transform(source, result);            
		}
		catch (TransformerConfigurationException e) {
			throw (IOException) (new IOException().initCause(e));
		}
		catch (TransformerException e) {
			throw (IOException) (new IOException().initCause(e));
		}
	}
}
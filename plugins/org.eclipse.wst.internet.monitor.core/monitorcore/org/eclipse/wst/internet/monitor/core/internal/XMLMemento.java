/*******************************************************************************
 * Copyright (c) 2003, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.internet.monitor.core.internal;

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

	/**
	 * @see IMemento#createChild(String)
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
	private static XMLMemento createReadRoot(InputStream in) {
		Document document = null;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder parser = factory.newDocumentBuilder();
			document = parser.parse(new InputSource(in));
			Node node = document.getFirstChild();
			if (node instanceof Element)
				return new XMLMemento(document, (Element) node);
		} catch (ParserConfigurationException e) {
			// ignore
		} catch (IOException e) {
			// ignore
		} catch (SAXException e) {
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
	
	/**
	 * Answer a root memento for writing a document.
	 * 
	 * @param type
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
	
	/**
	 * @see IMemento#getChild(String)
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

	/**
	 * @see IMemento#getChildren(String)
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

	/**
	 * @see IMemento#getInteger(String)
	 */
	public Integer getInteger(String key) {
		Attr attr = element.getAttributeNode(key);
		if (attr == null)
			return null; 
		String strValue = attr.getValue();
		try {
			return new Integer(strValue);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/**
	 * @see IMemento#getString(String)
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
	 * @param in the input stream
	 * @return a memento
	 */
	public static IMemento loadMemento(InputStream in) {
		return createReadRoot(in);
	}

	/**
	 * @see IMemento#putInteger(String, int)
	 */
	public void putInteger(String key, int n) {
		element.setAttribute(key, String.valueOf(n));
	}

	/**
	 * @see IMemento#putString(String, String)
	 */
	public void putString(String key, String value) {
		if (value == null)
			return;
		element.setAttribute(key, value);
	}
	
	/**
	 * Save this Memento to an output stream.
	 * 
	 * @param os
	 * @throws IOException
	 */
	private void save(OutputStream os) throws IOException {
		Result result = new StreamResult(os);
		Source source = new DOMSource(factory);
		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$
			transformer.setOutputProperty(OutputKeys.METHOD, "xml"); //$NON-NLS-1$
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8"); //$NON-NLS-1$
			transformer.transform(source, result);            
		} catch (Exception e) {
			throw (IOException) (new IOException().initCause(e));
		}
	}

	/**
	 * Save to string.
	 * 
	 * @return the string
	 * @throws IOException
	 */
	public String saveToString() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		save(out);
		return out.toString("UTF-8");
	}

	/**
	 * @see IMemento#getBoolean(String)
	 */
	public Boolean getBoolean(String key) {
		Attr attr = element.getAttributeNode(key);
		if (attr == null)
			return null;
		String strValue = attr.getValue();
		if ("true".equalsIgnoreCase(strValue))
			return new Boolean(true);
		
		return new Boolean(false);
	}

	/**
	 * @see IMemento#putBoolean(String, boolean)
	 */
	public void putBoolean(String key, boolean value) {
		element.setAttribute(key, value ? "true" : "false");
	}
}
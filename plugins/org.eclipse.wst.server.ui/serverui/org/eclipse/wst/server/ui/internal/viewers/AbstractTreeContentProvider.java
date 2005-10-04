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
package org.eclipse.wst.server.ui.internal.viewers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
/**
 * Runtime type content provider.
 */
public abstract class AbstractTreeContentProvider implements ITreeContentProvider {
	public static final byte STYLE_FLAT = 0;

	public static final String ROOT = "root";

	protected byte style;

	protected Object initialSelection;

	public class TreeElement {
		String text;
		List contents;
	}

	protected Object[] elements;
	protected Map elementToParentMap = new HashMap(2);
	protected Map textMap = new HashMap(2);

	/**
	 * AbstractTreeContentProvider constructor comment.
	 * 
	 * @param style a style parameter
	 */
	public AbstractTreeContentProvider(byte style) {
		super();
		this.style = style;
		
		fillTree();
	}

	public AbstractTreeContentProvider(byte style, boolean init) {
		super();
		this.style = style;
	}

	protected abstract void fillTree();

	protected void clean() {
		elements = null;
		elementToParentMap = new HashMap(2);
		textMap = new HashMap(2);
		
		initialSelection = null;
	}

	protected TreeElement getOrCreate(List list, String text) {
		try {
			Object obj = textMap.get(text);
			if (obj != null)
				return (TreeElement) obj;
		} catch (Exception e) {
			return null;
		}
		
		TreeElement element = new TreeElement();
		element.text = text;
		element.contents = new ArrayList();
		textMap.put(text, element);
		list.add(element);
		return element;
	}
	
	protected TreeElement getOrCreate(List list, String id, String text) {
		try {
			Object obj = textMap.get(id);
			if (obj != null)
				return (TreeElement) obj;
		} catch (Exception e) {
			return null;
		}
		
		TreeElement element = new TreeElement();
		element.text = text;
		element.contents = new ArrayList();
		textMap.put(id, element);
		list.add(element);
		return element;
	}

	protected TreeElement getByText(String text) {
		try {
			return (TreeElement) textMap.get(text);
		} catch (Exception e) {
			return null;
		}
	}
	
	protected TreeElement getParentImpl(Object obj) {
		try {
			return (TreeElement) elementToParentMap.get(obj);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Disposes of this content provider.  
	 * This is called by the viewer when it is disposed.
	 */
	public void dispose() {
		// do nothing
	}

	/**
	 * Returns the elements to display in the viewer 
	 * when its input is set to the given element. 
	 * These elements can be presented as rows in a table, items in a list, etc.
	 * The result is not modified by the viewer.
	 *
	 * @param element the input element
	 * @return the array of elements to display in the viewer
	 */
	public Object[] getElements(Object element) {
		return elements;
	}

	public Object[] getChildren(Object element) {
		if (style == STYLE_FLAT)
			return null;
		else if (!(element instanceof TreeElement))
			return null;
		
		TreeElement rte = (TreeElement) element;
		return rte.contents.toArray();
	}

	public Object getParent(Object element) {
		if (style == STYLE_FLAT)
			return null;
		//else if (element instanceof TreeElement)
		//	return null;

		return getParentImpl(element);
	}

	public boolean hasChildren(Object element) {
		if (style == STYLE_FLAT)
			return false;
		//else if (!(element instanceof TreeElement))
		//	return false;
		Object[] children = getChildren(element);
		return children != null && children.length > 0;

		//return true;
	}

	/**
	 * Notifies this content provider that the given viewer's input
	 * has been switched to a different element.
	 * <p>
	 * A typical use for this method is registering the content provider as a listener
	 * to changes on the new input (using model-specific means), and deregistering the viewer 
	 * from the old input. In response to these change notifications, the content provider
	 * propagates the changes to the viewer.
	 * </p>
	 *
	 * @param viewer the viewer
	 * @param oldInput the old input element, or <code>null</code> if the viewer
	 *   did not previously have an input
	 * @param newInput the new input element, or <code>null</code> if the viewer
	 *   does not have an input
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// do nothing
	}

	private Object[] getAllObjects() {
		List list = new ArrayList();
		Object[] obj = getElements(null);
		if (obj != null) {
			int size = obj.length;
			for (int i = 0; i < size; i++) {
				if (!(obj[i] instanceof AbstractTreeContentProvider.TreeElement))
					list.add(obj[i]);
				getAllChildren(list, obj[i]);
			}
		}
		return list.toArray();
	}

	private void getAllChildren(List list, Object element) {
		Object[] obj = getChildren(element);
		if (obj != null) {
			int size = obj.length;
			for (int i = 0; i < size; i++) {
				if (!(obj[i] instanceof AbstractTreeContentProvider.TreeElement))
					list.add(obj[i]);
				getAllChildren(list, obj[i]);
			}
		}
	}

	public Object getInitialSelection() {
		if (initialSelection == null) {
			InitialSelectionProvider isp = ServerUIPlugin.getInitialSelectionProvider();
			initialSelection = isp.getInitialSelection(getAllObjects());
		}
		return initialSelection;
	}
}
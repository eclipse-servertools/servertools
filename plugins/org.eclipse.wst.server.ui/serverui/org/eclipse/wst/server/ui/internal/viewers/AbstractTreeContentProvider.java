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
	public static final String ROOT = "root";

	protected Object initialSelection;

	public class TreeElement {
		String text;
		List<Object> contents;
	}

	protected Object[] elements;
	protected Map<Object, TreeElement> elementToParentMap = new HashMap<Object, TreeElement>(2);
	protected Map<String, TreeElement> textMap = new HashMap<String, TreeElement>(2);

	/**
	 * AbstractTreeContentProvider constructor comment.
	 */
	public AbstractTreeContentProvider() {
		super();
		
		fillTree();
	}

	public AbstractTreeContentProvider(boolean init) {
		super();
	}

	protected abstract void fillTree();

	protected void clean() {
		elements = null;
		elementToParentMap = new HashMap<Object, TreeElement>(2);
		textMap = new HashMap<String, TreeElement>(2);
		
		initialSelection = null;
	}

	protected TreeElement getOrCreate(List<TreeElement> list, String text) {
		try {
			Object obj = textMap.get(text);
			if (obj != null)
				return (TreeElement) obj;
		} catch (Exception e) {
			return null;
		}
		
		TreeElement element = new TreeElement();
		element.text = text;
		element.contents = new ArrayList<Object>();
		textMap.put(text, element);
		list.add(element);
		return element;
	}
	
	protected TreeElement getOrCreate(List<TreeElement> list, String id, String text) {
		try {
			Object obj = textMap.get(id);
			if (obj != null)
				return (TreeElement) obj;
		} catch (Exception e) {
			return null;
		}
		
		TreeElement element = new TreeElement();
		element.text = text;
		element.contents = new ArrayList<Object>();
		textMap.put(id, element);
		list.add(element);
		return element;
	}

	protected TreeElement getByText(String text) {
		try {
			return textMap.get(text);
		} catch (Exception e) {
			return null;
		}
	}
	
	protected TreeElement getParentImpl(Object obj) {
		try {
			return elementToParentMap.get(obj);
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
		if (!(element instanceof TreeElement))
			return null;
		
		TreeElement rte = (TreeElement) element;
		return rte.contents.toArray();
	}

	public Object getParent(Object element) {
		return getParentImpl(element);
	}

	public boolean hasChildren(Object element) {
		Object[] children = getChildren(element);
		return children != null && children.length > 0;
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
		List<Object> list = new ArrayList<Object>();
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

	private void getAllChildren(List<Object> list, Object element) {
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
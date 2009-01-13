/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.discovery.internal.wizard;

import java.util.List;

import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.server.discovery.internal.model.Extension;
/**
 * Extension item table label provider.
 */
public class ExtensionTableLabelProvider extends BaseLabelProvider implements ITableLabelProvider {
	/**
	 * ExtensionTableLabelProvider constructor comment.
	 */
	public ExtensionTableLabelProvider() {
		super();
	}

	/**
	 * ExtensionTableLabelProvider constructor comment.
	 * 
	 * @param decorator a label decorator, or null if no decorator is required
	 */
	public ExtensionTableLabelProvider(ILabelDecorator decorator) {
		super(decorator);
	}

	/**
	 * @see ITableLabelProvider#getColumnImage(Object, int)
	 */
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	/**
	 * @see ITableLabelProvider#getColumnText(Object, int)
	 */
	public String getColumnText(Object element, int columnIndex) {
		if (element instanceof List)
			return "Some update sites failed";
		
		if (element instanceof String)
			return ((String) element) + Math.random();
		
		Extension item = (Extension) element;
		return item.getName() + "\n" + item.getProvider();
	}
}
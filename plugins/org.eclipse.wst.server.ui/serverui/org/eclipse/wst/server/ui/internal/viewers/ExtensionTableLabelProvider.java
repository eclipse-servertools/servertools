/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.viewers;

import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.update.core.IFeature;
import org.eclipse.swt.graphics.Image;
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
		//if (columnIndex == 0)
		//	return ImageResource.getImage(ImageResource.IMG_WIZBAN_NEW_SERVER);
		
		return null;
	}

	/**
	 * @see ITableLabelProvider#getColumnText(Object, int)
	 */
	public String getColumnText(Object element, int columnIndex) {
		if (element instanceof String) {
			return ((String) element) + Math.random();
		}
		IFeature item = (IFeature) element;
		if (columnIndex == 0) {
		//	return "";
		//} else if (columnIndex == 1) {
			return item.getLabel() + "\n" + item.getProvider();
		}
		return "";
	}
}
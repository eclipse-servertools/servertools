/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui.internal.viewers;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
/**
 * Abstract tree label provider.
 */
public abstract class AbstractTreeLabelProvider extends BaseLabelProvider {
	/**
	 * AbstractTreeLabelProvider constructor comment.
	 */
	public AbstractTreeLabelProvider() {
		super();
	}

	/**
	 * Returns the label image for the given column of the given element.
	 *
	 * @param element the object representing the entire row, or 
	 *    <code>null</code> indicating that no input object is set
	 *    in the viewer
	 * @param columnIndex the zero-based index of the column in which
	 *   the label appears
	 */
	public Image getImage(Object element) {
		if (element instanceof ServerTreeContentProvider.TreeElement) {
			ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
			return sharedImages.getImage(ISharedImages.IMG_OBJ_FOLDER);
		}
		return getImageImpl(element);
	}
	
	protected abstract Image getImageImpl(Object element);

	/**
	 * Returns the label text for the given column of the given element.
	 *
	 * @param element the object representing the entire row, or
	 *   <code>null</code> indicating that no input object is set
	 *   in the viewer
	 * @param columnIndex the zero-based index of the column in which the label appears
	 */
	public String getText(Object element) {
		if (element instanceof ServerTreeContentProvider.TreeElement) {
			return ((ServerTreeContentProvider.TreeElement) element).text;
		}
		return getTextImpl(element);
	}
	
	protected abstract String getTextImpl(Object element);
}
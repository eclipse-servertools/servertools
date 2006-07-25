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

import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
/**
 * Abstract tree label provider.
 */
public abstract class AbstractTreeLabelProvider extends BaseLabelProvider {
	/**
	 * A standard tree label provider.
	 */
	public AbstractTreeLabelProvider() {
		super();
	}

	/**
	 * A standard tree label provider.
	 * 
	 * @param decorator a label decorator, or null if no decorator is required
	 */
	public AbstractTreeLabelProvider(ILabelDecorator decorator) {
		super(decorator);
	}

	/**
	 * @see BaseLabelProvider#getImage(Object)
	 */
	public Image getImage(Object element) {
		if (element instanceof ServerTreeContentProvider.TreeElement) {
			ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
			return sharedImages.getImage(ISharedImages.IMG_OBJ_FOLDER);
		}
		Image image = getImageImpl(element);
		if (decorator != null) {
			Image dec = decorator.decorateImage(image, element);
			if (dec != null)
				return dec;
		}
		return image;
	}

	/**
	 * Return an image for the given element.
	 *  
	 * @param element an element
	 * @return an image
	 */
	protected abstract Image getImageImpl(Object element);

	/**
	 * @see BaseLabelProvider#getText(Object)
	 */
	public String getText(Object element) {
		if (element instanceof ServerTreeContentProvider.TreeElement) {
			return ((ServerTreeContentProvider.TreeElement) element).text;
		}
		String text = getTextImpl(element);
		if (decorator != null) {
			String dec = decorator.decorateText(text, element);
			if (dec != null && !dec.equals(""))
				return dec;
		}
		return text;
	}

	/**
	 * Return a label for the given element.
	 *  
	 * @param element an element
	 * @return a label
	 */
	protected abstract String getTextImpl(Object element);
}
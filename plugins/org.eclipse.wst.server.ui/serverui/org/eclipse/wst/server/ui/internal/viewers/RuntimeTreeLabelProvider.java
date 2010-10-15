/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
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
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.ui.internal.ImageResource;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
/**
 * Runtime label provider.
 */
public class RuntimeTreeLabelProvider extends AbstractTreeLabelProvider implements ITableLabelProvider {
	/**
	 * RuntimeTreeLabelProvider constructor comment.
	 */
	public RuntimeTreeLabelProvider() {
		super();
	}

	/**
	 * RuntimeTreeLabelProvider constructor comment.
	 * 
	 * @param decorator a label decorator, or null if no decorator is required
	 */
	public RuntimeTreeLabelProvider(ILabelDecorator decorator) {
		super(decorator);
	}

	/**
	 * 
	 */
	protected Image getImageImpl(Object element) {
		IRuntime runtime = (IRuntime) element;
		IRuntimeType runtimeType = runtime.getRuntimeType();
		if (runtimeType != null) {
			Image image = ImageResource.getImage(runtimeType.getId());
			if (decorator != null) {
				Image dec = decorator.decorateImage(image, element);
				if (dec != null)
					return dec;
			}
			return image;
		}
		return null;
	}

	/**
	 * 
	 */
	protected String getTextImpl(Object element) {
		IRuntime runtime = (IRuntime) element;
		return notNull(runtime.getName());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
	 */
	public Image getColumnImage(Object element, int columnIndex) {
		if (columnIndex == 0) {
			if (element instanceof ServerTreeContentProvider.TreeElement) {
				ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
				return sharedImages.getImage(ISharedImages.IMG_OBJ_FOLDER);
			}
			return getImageImpl(element);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
	 */
	public String getColumnText(Object element, int columnIndex) {
		if (element instanceof ServerTreeContentProvider.TreeElement) {
			if (columnIndex == 0)
				return ((ServerTreeContentProvider.TreeElement) element).text;
			return "";
		}
		IRuntime runtime = (IRuntime) element;
		if (columnIndex == 0) {
			String text = notNull(runtime.getName());
			if (decorator != null) {
				String dec = decorator.decorateText(text, runtime);
				if (dec != null)
					return dec;
			}
			return text;
		} else if (columnIndex == 1) {
			if (runtime.getRuntimeType() != null)
				return notNull(runtime.getRuntimeType().getName());
			return "";
		}
		return "";
	}
}
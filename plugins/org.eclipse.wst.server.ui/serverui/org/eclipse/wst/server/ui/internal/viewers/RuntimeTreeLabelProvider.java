/**
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 */
package org.eclipse.wst.server.ui.internal.viewers;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.wst.server.core.IRuntime;
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
	 * 
	 */
	protected Image getImageImpl(Object element) {
		IRuntime runtime = (IRuntime) element;
		if (runtime.getRuntimeType() != null)
			return ImageResource.getImage(runtime.getRuntimeType().getId());
		else
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
			} else
				return getImageImpl(element);
		} else
			return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
	 */
	public String getColumnText(Object element, int columnIndex) {
		if (element instanceof ServerTreeContentProvider.TreeElement) {
			if (columnIndex == 0)
				return ((ServerTreeContentProvider.TreeElement) element).text;
			else
				return "";
		} else {
			IRuntime runtime = (IRuntime) element;
			if (columnIndex == 0)
				return runtime.getName();
			else if (columnIndex == 1) {
				if (runtime.getRuntimeType() != null)
					return notNull(runtime.getRuntimeType().getName());
				else
					return "";
			} else
				return "X";
		}
	}
}

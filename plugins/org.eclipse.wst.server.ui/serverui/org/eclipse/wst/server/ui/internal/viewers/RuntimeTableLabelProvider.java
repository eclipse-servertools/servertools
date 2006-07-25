/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
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
/**
 * Runtime table label provider.
 */
public class RuntimeTableLabelProvider extends BaseLabelProvider implements ITableLabelProvider, ILockedLabelProvider {
	/**
	 * RuntimeTableLabelProvider constructor comment.
	 */
	public RuntimeTableLabelProvider() {
		super();
	}

	/**
	 * RuntimeTableLabelProvider constructor comment.
	 * 
	 * @param decorator a label decorator, or null if no decorator is required
	 */
	public RuntimeTableLabelProvider(ILabelDecorator decorator) {
		super(decorator);
	}

	/**
	 * @see ITableLabelProvider#getColumnImage(Object, int)
	 */
	public Image getColumnImage(Object element, int columnIndex) {
		if (columnIndex == 0) {
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
		}
		return null;
	}

	/**
	 * @see ITableLabelProvider#getColumnText(Object, int)
	 */
	public String getColumnText(Object element, int columnIndex) {
		IRuntime runtime = (IRuntime) element;
		if (columnIndex == 0) {
			String text = notNull(runtime.getName());
			if (decorator != null) {
				String dec = decorator.decorateText(text, element);
				if (dec != null)
					return dec;
			}
			return text;
		} else if (columnIndex == 1) {
			IRuntimeType runtimeType = runtime.getRuntimeType();
			if (runtimeType != null)
				return notNull(runtimeType.getName());
			return "";
		} else
			return "";
	}

	public boolean isLocked(Object element) {
		IRuntime runtime = (IRuntime) element;
		return runtime.isReadOnly();
	}
}
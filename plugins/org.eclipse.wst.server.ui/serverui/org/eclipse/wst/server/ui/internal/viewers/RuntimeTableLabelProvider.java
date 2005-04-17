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
	 * @see ITableLabelProvider#getColumnImage(Object, int)
	 */
	public Image getColumnImage(Object element, int columnIndex) {
		if (columnIndex == 0) {
			IRuntime runtime = (IRuntime) element;
			IRuntimeType runtimeType = runtime.getRuntimeType();
			if (runtimeType != null)
				return ImageResource.getImage(runtimeType.getId());
		}
		return null;
	}

	/**
	 * @see ITableLabelProvider#getColumnText(Object, int)
	 */
	public String getColumnText(Object element, int columnIndex) {
		IRuntime runtime = (IRuntime) element;
		if (columnIndex == 0)
			return notNull(runtime.getName());
		/*else if (columnIndex == 1) {
			IPath location = runtime.getLocation();
			if (location == null)
				return "";
			else
				return notNull(location.toOSString());
		}*/
		else if (columnIndex == 1) {
			IRuntimeType runtimeType = runtime.getRuntimeType();
			if (runtimeType != null)
				return notNull(runtimeType.getName());
			return "";
		} else
			return "X";
	}
	
	public boolean isLocked(Object element) {
		IRuntime runtime = (IRuntime) element;
		return runtime.isReadOnly();
	}
}
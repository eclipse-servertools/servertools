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

import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.ui.internal.ImageResource;
import org.eclipse.swt.graphics.Image;
/**
 * Runtime type label provider.
 */
public class RuntimeTypeTreeLabelProvider extends AbstractTreeLabelProvider {
	/**
	 * RuntimeTypeTreeLabelProvider constructor comment.
	 */
	public RuntimeTypeTreeLabelProvider() {
		super();
	}

	/**
	 * 
	 */
	protected Image getImageImpl(Object element) {
		IRuntimeType runtimeType = (IRuntimeType) element;
		return ImageResource.getImage(runtimeType.getId());
	}

	/**
	 * 
	 */
	protected String getTextImpl(Object element) {
		IRuntimeType runtimeType = (IRuntimeType) element;
		return notNull(runtimeType.getName());
	}
}
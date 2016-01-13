/*******************************************************************************
 * Copyright (c) 2003, 2016 IBM Corporation and others.
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
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.internal.RuntimeTypeWithServerProxy;
import org.eclipse.wst.server.ui.internal.DefaultServerImageDescriptor;
import org.eclipse.wst.server.ui.internal.ImageResource;
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
	 * RuntimeTypeTreeLabelProvider constructor comment.
	 * 
	 * @param decorator a label decorator, or null if no decorator is required
	 */
	public RuntimeTypeTreeLabelProvider(ILabelDecorator decorator) {
		super(decorator);
	}

	/**
	 * 
	 */
	protected Image getImageImpl(Object element) {
		IRuntimeType runtimeType = (IRuntimeType) element;
		Image image = ImageResource.getImage(runtimeType.getId());
		DefaultServerImageDescriptor dsid = null;
		if (element instanceof RuntimeTypeWithServerProxy) {
			Image image1 = ImageResource.getImage(ImageResource.IMG_DOWN_ARROW);
			dsid = new DefaultServerImageDescriptor(image, image1);
			dsid.setFlags(DefaultServerImageDescriptor.BOTTOM_RIGHT);
			image = dsid.createImage();
		}

		return image;
	}

	/**
	 * 
	 */
	protected String getTextImpl(Object element) {
		IRuntimeType runtimeType = (IRuntimeType) element;
		return notNull(runtimeType.getName());
	}
}
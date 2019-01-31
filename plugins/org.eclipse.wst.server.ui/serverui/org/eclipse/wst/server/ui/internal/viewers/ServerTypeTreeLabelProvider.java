/*******************************************************************************
 * Copyright (c) 2003, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.viewers;

import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.server.core.IServerType;
import org.eclipse.wst.server.core.internal.ServerTypeProxy;
import org.eclipse.wst.server.ui.internal.DefaultServerImageDescriptor;
import org.eclipse.wst.server.ui.internal.ImageResource;
/**
 * Server type label provider.
 */
public class ServerTypeTreeLabelProvider extends AbstractTreeLabelProvider {
	/**
	 * ServerTypeTreeLabelProvider constructor comment.
	 */
	public ServerTypeTreeLabelProvider() {
		super();
	}

	/**
	 * ServerTypeTreeLabelProvider constructor comment.
	 * 
	 * @param decorator a label decorator, or null if no decorator is required
	 */
	public ServerTypeTreeLabelProvider(ILabelDecorator decorator) {
		super(decorator);
	}

	/**
	 * 
	 */
	protected Image getImageImpl(Object element) {
		IServerType type = (IServerType) element;
		Image image = ImageResource.getImage(type.getId());
		DefaultServerImageDescriptor dsid = null;
		if (element instanceof ServerTypeProxy) {
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
		IServerType type = (IServerType) element;
		return notNull(type.getName());
	}
}
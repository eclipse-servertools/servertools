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

import org.eclipse.wst.server.core.IServerConfigurationType;
import org.eclipse.wst.server.ui.internal.ImageResource;
import org.eclipse.swt.graphics.Image;

/**
 * Server configuration type label provider.
 */
public class ServerConfigurationTypeTreeLabelProvider extends AbstractTreeLabelProvider {
	/**
	 * ServerConfigurationTypeTreeLabelProvider constructor comment.
	 */
	public ServerConfigurationTypeTreeLabelProvider() {
		super();
	}

	/**
	 * 
	 */
	protected Image getImageImpl(Object element) {
		IServerConfigurationType type = (IServerConfigurationType) element;
		return ImageResource.getImage(type.getId());
	}

	/**
	 * 
	 */
	protected String getTextImpl(Object element) {
		IServerConfigurationType type = (IServerConfigurationType) element;
		return notNull(type.getName());
	}
}

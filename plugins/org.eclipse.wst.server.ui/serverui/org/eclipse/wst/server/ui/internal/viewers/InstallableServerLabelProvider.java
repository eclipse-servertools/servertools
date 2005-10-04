/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.viewers;

import org.eclipse.wst.server.core.internal.IInstallableServer;
import org.eclipse.wst.server.ui.internal.ImageResource;
import org.eclipse.swt.graphics.Image;
/**
 * Installable server label provider.
 */
public class InstallableServerLabelProvider extends AbstractTreeLabelProvider {
	/**
	 * InstallableServerLabelProvider constructor comment.
	 */
	public InstallableServerLabelProvider() {
		super();
	}

	/**
	 * 
	 */
	protected Image getImageImpl(Object element) {
		return ImageResource.getImage(ImageResource.IMG_SERVER);
	}

	/**
	 * 
	 */
	protected String getTextImpl(Object element) {
		IInstallableServer is = (IInstallableServer) element;
		return notNull(is.getName());
	}
}
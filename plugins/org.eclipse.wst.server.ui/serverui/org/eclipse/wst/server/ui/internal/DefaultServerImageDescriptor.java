/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui.internal;

import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
/**
 * A OverlayImageDescriptor consists of a main icon and one or more overlays.
 * The overlays are computed according to flags set on creation of the descriptor.
 */
public class DefaultServerImageDescriptor extends CompositeImageDescriptor {
	private Image fBaseImage;
	private Point fSize;
	
	/**
	 * Create a new OverlayImageDescriptor.
	 * 
	 * @param baseImage an image descriptor used as the base image
	 * @param flags flags indicating which adornments are to be rendered
	 */
	public DefaultServerImageDescriptor(Image baseImage) {
		setBaseImage(baseImage);
	}

	/**
	 * @see CompositeImageDescriptor#getSize()
	 */
	protected Point getSize() {
		if (fSize == null) {
			ImageData data = getBaseImage().getImageData();
			setSize(new Point(data.width, data.height));
		}
		return fSize;
	}
	
	/**
	 * @see Object#equals(java.lang.Object)
	 */
	public boolean equals(Object object) {
		if (!(object instanceof DefaultServerImageDescriptor))
			return false;
			
		DefaultServerImageDescriptor other = (DefaultServerImageDescriptor) object;
		return (getBaseImage().equals(other.getBaseImage()));
	}
	
	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() {
		return getBaseImage().hashCode();
	}
	
	/**
	 * @see CompositeImageDescriptor#drawCompositeImage(int, int)
	 */
	protected void drawCompositeImage(int width, int height) {
		ImageData bg = getBaseImage().getImageData();
		if (bg == null)
			bg = DEFAULT_IMAGE_DATA;

		drawImage(bg, 0, 0);
		drawOverlays();
	}

	/**
	 * Add any overlays to the image as specified in the flags.
	 */
	protected void drawOverlays() {
		ImageData data = ImageResource.getImage(ImageResource.IMG_DEFAULT_SERVER_OVERLAY).getImageData();
		int x = getSize().x - data.width;
		drawImage(data, x, 0);
	}
	
	protected Image getBaseImage() {
		return fBaseImage;
	}

	protected void setBaseImage(Image baseImage) {
		fBaseImage = baseImage;
	}

	protected void setSize(Point size) {
		fSize = size;
	}
}
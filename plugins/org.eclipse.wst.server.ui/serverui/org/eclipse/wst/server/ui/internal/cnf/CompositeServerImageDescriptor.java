/*******************************************************************************
 * Copyright (c) 2010, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.cnf;

import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.ui.internal.ImageResource;
import org.eclipse.wst.server.ui.internal.Trace;

/**
 * A CompositeServerImageDescriptor consists of a main icon and one overlay. The overlay will be
 *  created at the bottom right of the base image
 * 
 */

public class CompositeServerImageDescriptor extends CompositeImageDescriptor {
	private Image fBaseImage;
	private Point fSize;
	private Image overlay;
	
	/**
	 * Create a new CompositeServerImageDescriptor with the base icon being the ServerType image 
	 * provided by the adopter
	 * 
	 * @param baseImage
	 * @param overlay
	 */
	public CompositeServerImageDescriptor(final IServer server, Image overlay) {
		setBaseImage(ImageResource.getImage(server.getServerType().getId()));
		if (overlay == null){
			if (Trace.FINEST) {
				Trace.trace(Trace.STRING_FINEST, "Invalid overlay icon");
			}
		}
		this.overlay = overlay;
	}
	
	/**
	 * Create a new CompositeServerImageDescriptor with the base icon being the ServerType image 
	 * provided by the adopter
	 * 
	 * @param baseImage
	 * @param overlay
	 */
	public CompositeServerImageDescriptor(final Image baseImage, Image overlay) {
		setBaseImage(baseImage);
		if (overlay == null){
			if (Trace.FINEST) {
				Trace.trace(Trace.STRING_FINEST, "Invalid overlay icon");
			}
		}
		this.overlay = overlay;
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
		if (!(object instanceof CompositeServerImageDescriptor))
			return false;
			
		CompositeServerImageDescriptor other = (CompositeServerImageDescriptor) object;
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

	protected void drawOverlays() {
		if (overlay == null){
			return;
		}
		
		ImageData data = overlay.getImageData();
		int x = getSize().x - data.width;
		int y = getSize().y - data.height;
		drawImage(data, x, y);
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
/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.wst.server.core.internal.Server;
/**
 * 
 */
public class DefaultServerLabelDecorator implements ILabelDecorator {
	protected Map<Object, Image> map = new HashMap<Object, Image>();

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILabelDecorator#decorateImage(org.eclipse.swt.graphics.Image, java.lang.Object)
	 */
	public Image decorateImage(Image image, Object element) {
		try {
			Image img = map.get(element);
			if (img != null)
				return img;
		} catch (Exception e) {
			// ignore
		}
		
		DefaultServerImageDescriptor dsid = null;
		if (element instanceof Server) {
			IStatus status = ((Server) element).getServerStatus();
			if (status != null) {
				ISharedImages sharedImages = ServerUIPlugin.getInstance().getWorkbench().getSharedImages();
				if (status.getSeverity() == IStatus.ERROR)
					dsid = new DefaultServerImageDescriptor(image, sharedImages.getImage(ISharedImages.IMG_OBJS_ERROR_TSK));
				else if (status.getSeverity() == IStatus.WARNING)
					dsid = new DefaultServerImageDescriptor(image, sharedImages.getImage(ISharedImages.IMG_OBJS_WARN_TSK));
				else if (status.getSeverity() == IStatus.INFO)
					dsid = new DefaultServerImageDescriptor(image, sharedImages.getImage(ISharedImages.IMG_OBJS_INFO_TSK));
			}
		}
		
		if (dsid == null)
			dsid = new DefaultServerImageDescriptor(image);
		Image image2 = dsid.createImage();
		map.put(element, image2);
		return image2;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILabelDecorator#decorateText(java.lang.String, java.lang.Object)
	 */
	public String decorateText(String text, Object element) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void addListener(ILabelProviderListener listener) {
		// do nothing
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	public void dispose() {
		try {
			Iterator iterator = map.values().iterator();
			while (iterator.hasNext()) {
				Image image = (Image) iterator.next();
				image.dispose();
			}
		} catch (Exception e) {
			Trace.trace(Trace.WARNING, "Could not dispose images", e);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
	 */
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void removeListener(ILabelProviderListener listener) {
		// do nothing
	}
}
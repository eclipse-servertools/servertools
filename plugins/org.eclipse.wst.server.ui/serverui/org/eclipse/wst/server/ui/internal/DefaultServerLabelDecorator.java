package org.eclipse.wst.server.ui.internal;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
/**
 * 
 */
public class DefaultServerLabelDecorator implements ILabelDecorator {
	protected Map map = new HashMap();

	//protected Image image2;

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILabelDecorator#decorateImage(org.eclipse.swt.graphics.Image, java.lang.Object)
	 */
	public Image decorateImage(Image image, Object element) {
		try {
			Image img = (Image) map.get(element);
			if (img != null)
				return img;
		} catch (Exception e) {
			// ignore
		}
		
		DefaultServerImageDescriptor dsid = new DefaultServerImageDescriptor(image);
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
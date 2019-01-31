/*******************************************************************************
 * Copyright (c) 2003, 2006 IBM Corporation and others.
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
package org.eclipse.wst.server.ui.internal.audio;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.core.runtime.*;
import java.net.URL;
import java.util.Map;

import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.server.ui.internal.ImageResource;
import org.eclipse.wst.server.ui.internal.Messages;
import org.eclipse.jface.viewers.ITableLabelProvider;
/**
 * Audio table label provider.
 */
class AudioTableLabelProvider implements ITableLabelProvider {
	protected AudioPreferencePage page;

	/**
	 * AudioTableLabelProvider constructor comment.
	 * 
	 * @param page the preference page
	 */
	public AudioTableLabelProvider(AudioPreferencePage page) {
		super();
		this.page = page;
	}

	/**
	 * Adds a listener to this label provider. 
	 * Has no effect if an identical listener is already registered.
	 * <p>
	 * Label provider listeners are informed about state changes 
	 * that affect the rendering of the viewer that uses this label provider.
	 * </p>
	 *
	 * @param listener a label provider listener
	 */
	public void addListener(ILabelProviderListener listener) {
		// do nothing
	}

	/**
	 * Disposes of this label provider.  When a label provider is
	 * attached to a viewer, the viewer will automatically call
	 * this method when the viewer is being closed.  When label providers
	 * are used outside of the context of a viewer, it is the client's
	 * responsibility to ensure that this method is called when the
	 * provider is no longer needed.
	 */
	public void dispose() {
		// do nothing
	}

	/**
	 * @see ITableLabelProvider#getColumnImage(java.lang.Object, int)
	 */
	public Image getColumnImage(Object element, int columnIndex) {
		/*AudioCore core = AudioCore.getInstance();
	
		if (columnIndex == 0) {
			if (element instanceof String) {
				if (element != AudioTableContentProvider.MISC_CATEGORY) {
					if (core.isCategoryEnabled((String) element))
						return ImageResource.getImage(ImageResource.IMG_AUDIO_ENABLED);
					return ImageResource.getImage(ImageResource.IMG_AUDIO_DISABLED);
				}
				return null;
			}
			Sound sound = (Sound) element;
			if (!core.isCategoryEnabled(sound.getCategory()))
				return ImageResource.getImage(ImageResource.IMG_AUDIO_UNAVAILABLE);
			if (core.isSoundEnabled(sound.getId()))
				return ImageResource.getImage(ImageResource.IMG_AUDIO_ENABLED);
			return ImageResource.getImage(ImageResource.IMG_AUDIO_DISABLED);
		} else*/
		if (columnIndex == 1) {
			if (element instanceof String)
				return ImageResource.getImage(ImageResource.IMG_AUDIO_CATEGORY);
			return ImageResource.getImage(ImageResource.IMG_AUDIO_SOUND);
		}
		return null;
	}

	/**
	 * @see ITableLabelProvider#getColumnText(java.lang.Object, int)
	 */
	public String getColumnText(Object element, int columnIndex) {
		if (columnIndex == 0)
			return "";
		
		if (element instanceof String) {
			String categoryId = (String) element;
			if (columnIndex == 1) {
				if (categoryId.equals(AudioTableContentProvider.MISC_CATEGORY))
					return ""; //(Miscellaneous)";
				Map categories = AudioCore.getInstance().getCategories();
				return (String) categories.get(categoryId);
			}
			return "";
		}
		Sound sound = (Sound) element;
		
		if (columnIndex == 1) {
			String s = sound.getName();
			if (s != null)
				return s;
			return Messages.audioUnknown;
		} else if (columnIndex == 2) {
			IPath path = page.getUserSoundPath(sound.getId());
			if (path != null)
				return path.lastSegment();
			
			URL url = page.getSoundURL(sound.getId());
			if (url == null)
				return Messages.audioNone;
			return Messages.audioDefault;
		}
		return "";
	}

	/**
	 * Returns whether the label would be affected 
	 * by a change to the given property of the given element.
	 * This can be used to optimize a non-structural viewer update.
	 * If the property mentioned in the update does not affect the label,
	 * then the viewer need not update the label.
	 *
	 * @param element the element
	 * @param property the property
	 * @return <code>true</code> if the label would be affected,
	 *    and <code>false</code> if it would be unaffected
	 */
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	/**
	 * Removes a listener to this label provider.
	 * Has no affect if an identical listener is not registered.
	 *
	 * @param listener a label provider listener
	 */
	public void removeListener(ILabelProviderListener listener) {
		// do nothing
	}
}
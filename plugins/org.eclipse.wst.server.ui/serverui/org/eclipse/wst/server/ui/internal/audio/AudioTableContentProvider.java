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
package org.eclipse.wst.server.ui.internal.audio;

import java.util.*;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
/**
 * Audio table content provider.
 */
class AudioTableContentProvider implements IStructuredContentProvider {
	protected static final String MISC_CATEGORY = "miscCategory";

	/**
	 * AudioTableContentProvider constructor comment.
	 */
	public AudioTableContentProvider() {
		super();
	}

	/**
	 * Disposes of this content provider.  
	 * This is called by the viewer when it is disposed.
	 */
	public void dispose() {
		// do nothing
	}

	/**
	 * Returns the elements to display in the viewer 
	 * when its input is set to the given element. 
	 * These elements can be presented as rows in a table, items in a list, etc.
	 * The result is not modified by the viewer.
	 *
	 * @param inputElement the input element
	 * @return the array of elements to display in the viewer
	 */
	public Object[] getElements(Object inputElement) {
		AudioCore core = AudioCore.getInstance();
	
		Map categories = core.getCategories();
		Map sounds = core.getSounds();
	
		List list = new ArrayList(sounds.size());
	
		// first, find all the categories and sort
		List cats = new ArrayList();
		Iterator iterator = categories.keySet().iterator();
		while (iterator.hasNext())
			cats.add(iterator.next());
		sortCategories(cats);
	
		// list them, ignoring empty ones
		iterator = categories.keySet().iterator();
		while (iterator.hasNext()) {
			String id = (String) iterator.next();
			List l = getSoundsByCategory(id);
			if (!l.isEmpty()) {
				list.add(id);
				sortSounds(l);
	
				int size = l.size();
				for (int i = 0; i < size; i++)
					list.add(l.get(i));
			}
		}
	
		// finally, list the "misc" sounds
		List l = getSoundsByCategory(null);
		if (!l.isEmpty()) {
			list.add(MISC_CATEGORY);
			sortSounds(l);
	
			int size = l.size();
			for (int i = 0; i < size; i++)
				list.add(l.get(i));
		}
	
		return list.toArray();
	}

	/**
	 * Returns the sounds from the given category. Use null
	 * to return all miscelleneous sounds with no category or
	 * an invalid category.
	 *
	 * @return java.util.List
	 * @param category java.lang.String
	 */
	protected List getSoundsByCategory(String category) {
		AudioCore core = AudioCore.getInstance();
	
		Map sounds = core.getSounds();
		Map categories = core.getCategories();
		List list = new ArrayList();
	
		Iterator iterator = sounds.keySet().iterator();
		while (iterator.hasNext()) {
			String id = (String) iterator.next();
			Sound sound = (Sound) sounds.get(id);
			if (category != null && category.equals(sound.getCategory()))
				list.add(sound);
			else if (category == null && (sound.getCategory() == null || !categories.containsKey(sound.getCategory())))
				list.add(sound);
		}
		return list;
	}

	/**
	 * Notifies this content provider that the given viewer's input
	 * has been switched to a different element.
	 * <p>
	 * A typical use for this method is registering the content provider as a listener
	 * to changes on the new input (using model-specific means), and deregistering the viewer 
	 * from the old input. In response to these change notifications, the content provider
	 * propagates the changes to the viewer.
	 * </p>
	 *
	 * @param viewer the viewer
	 * @param oldInput the old input element, or <code>null</code> if the viewer
	 *   did not previously have an input
	 * @param newInput the new input element, or <code>null</code> if the viewer
	 *   does not have an input
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// do nothing
	}

	/**
	 * Sorts a list of categories, in place.
	 *
	 * @param list java.util.List
	 */
	protected void sortCategories(List list) {
		int size = list.size();
		if (size < 2)
			return;
	
		Map categories = AudioCore.getInstance().getCategories();
	
		for (int i = 0; i < size - 1; i++) {
			for (int j = i+1; j < size; j++) {
				String a = (String) list.get(i);
				String aa = (String) categories.get(a);
				String b = (String) list.get(j);
				String bb = (String) categories.get(b);
				if (aa.compareTo(bb) > 0) {
					list.set(i, b);
					list.set(j, a);
				}
			}
		}
	}

	/**
	 * Sorts a list of sounds, in place.
	 *
	 * @param sounds java.util.List
	 */
	protected void sortSounds(List sounds) {
		int size = sounds.size();
		if (size < 2)
			return;
	
		for (int i = 0; i < size - 1; i++) {
			for (int j = i+1; j < size; j++) {
				Sound a = (Sound) sounds.get(i);
				Sound b = (Sound) sounds.get(j);
				if (a.getName().compareTo(b.getName()) > 0) {
					sounds.set(i, b);
					sounds.set(j, a);
				}
			}
		}
	}
}
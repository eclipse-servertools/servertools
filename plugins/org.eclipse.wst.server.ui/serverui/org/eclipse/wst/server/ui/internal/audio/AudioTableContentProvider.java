/*******************************************************************************
 * Copyright (c) 2003, 2008 IBM Corporation and others.
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

import java.util.*;
import org.eclipse.wst.server.ui.internal.viewers.BaseContentProvider;
/**
 * Audio table content provider.
 */
class AudioTableContentProvider extends BaseContentProvider {
	protected static final String MISC_CATEGORY = "miscCategory";

	/**
	 * AudioTableContentProvider constructor comment.
	 */
	public AudioTableContentProvider() {
		super();
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
	
		Map<String, String> categories = core.getCategories();
		Map<String, Sound> sounds = core.getSounds();
	
		List<Object> list = new ArrayList<Object>(sounds.size());
	
		// first, find all the categories and sort
		List<String> cats = new ArrayList<String>();
		Iterator iterator = categories.keySet().iterator();
		while (iterator.hasNext())
			cats.add((String) iterator.next());
		sortCategories(cats);
		
		// list them, ignoring empty ones
		iterator = categories.keySet().iterator();
		while (iterator.hasNext()) {
			String id = (String) iterator.next();
			List<Sound> l = getSoundsByCategory(id);
			if (!l.isEmpty()) {
				list.add(id);
				sortSounds(l);
	
				int size = l.size();
				for (int i = 0; i < size; i++)
					list.add(l.get(i));
			}
		}
	
		// finally, list the "misc" sounds
		List<Sound> l = getSoundsByCategory(null);
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
	protected static List<Sound> getSoundsByCategory(String category) {
		AudioCore core = AudioCore.getInstance();
	
		Map<String, Sound> sounds = core.getSounds();
		Map<String, String> categories = core.getCategories();
		List<Sound> list = new ArrayList<Sound>();
	
		Iterator iterator = sounds.keySet().iterator();
		while (iterator.hasNext()) {
			String id = (String) iterator.next();
			Sound sound = sounds.get(id);
			if (category != null && category.equals(sound.getCategory()))
				list.add(sound);
			else if (category == null && (sound.getCategory() == null || !categories.containsKey(sound.getCategory())))
				list.add(sound);
		}
		return list;
	}

	/**
	 * Sorts a list of categories, in place.
	 *
	 * @param list java.util.List
	 */
	protected void sortCategories(List<String> list) {
		int size = list.size();
		if (size < 2)
			return;
		
		Map<String, String> categories = AudioCore.getInstance().getCategories();
		
		for (int i = 0; i < size - 1; i++) {
			for (int j = i+1; j < size; j++) {
				String a = list.get(i);
				String aa = categories.get(a);
				String b = list.get(j);
				String bb = categories.get(b);
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
	protected void sortSounds(List<Sound> sounds) {
		int size = sounds.size();
		if (size < 2)
			return;
	
		for (int i = 0; i < size - 1; i++) {
			for (int j = i+1; j < size; j++) {
				Sound a = sounds.get(i);
				Sound b = sounds.get(j);
				if (a.getName().compareTo(b.getName()) > 0) {
					sounds.set(i, b);
					sounds.set(j, a);
				}
			}
		}
	}
}

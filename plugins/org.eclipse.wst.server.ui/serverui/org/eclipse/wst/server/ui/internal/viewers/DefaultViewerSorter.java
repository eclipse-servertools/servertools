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

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.IServerType;
/**
 * Class used to sort categories, runtime types, and server types in the
 * New wizards.
 */
public class DefaultViewerSorter extends ViewerSorter {
	/**
	 * @see ViewerSorter#compare(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public int compare(Viewer viewer, Object o1, Object o2) {
		if (o1 instanceof AbstractTreeContentProvider.TreeElement)
			o1 = ((AbstractTreeContentProvider.TreeElement) o1).text;
		
		if (o2 instanceof AbstractTreeContentProvider.TreeElement)
			o2 = ((AbstractTreeContentProvider.TreeElement) o2).text;
		
		// filter out strings
		if (o1 instanceof String && o2 instanceof String)
			return compareCategories((String) o1, (String) o2);
		if (o1 instanceof String)
			return -1;
		if (o2 instanceof String)
			return 1;
		
		if (o1 instanceof IRuntimeType && o2 instanceof IRuntimeType)
			return compareRuntimeTypes((IRuntimeType) o1, (IRuntimeType) o2);
		
		if (o1 instanceof IServerType && o2 instanceof IServerType)
			return compareServerTypes((IServerType) o1, (IServerType) o2);
		
		return 0;
	}

	/**
	 * Sort two category names.
	 * 
	 * @param s1 the first category
	 * @param s2 the second category
	 * @return a negative number if the first element is less  than the 
	 *    second element; the value <code>0</code> if the first element is
	 *    equal to the second element; and a positive number if the first
	 *    element is greater than the second element
	 */
	protected int compareCategories(String s1, String s2) {
		try {
			Version v1 = Version.parseVersion(s1);
			Version v2 = Version.parseVersion(s2);
			
			return Version.compare(v1, v2);
		} catch (NumberFormatException nfe) {
			// ignore
		}
		
		return s1.compareTo(s2);
	}

	/**
	 * Sort two runtime types.
	 * 
	 * @param r1 the first runtime type
	 * @param r2 the second runtime type
	 * @return a negative number if the first element is less  than the 
	 *    second element; the value <code>0</code> if the first element is
	 *    equal to the second element; and a positive number if the first
	 *    element is greater than the second element
	 */
	protected int compareRuntimeTypes(IRuntimeType r1, IRuntimeType r2) {
		return r1.getName().compareToIgnoreCase(r2.getName());
	}

	/**
	 * Sort two server types.
	 * 
	 * @param s1 the first server type
	 * @param s2 the second server type
	 * @return a negative number if the first element is less  than the 
	 *    second element; the value <code>0</code> if the first element is
	 *    equal to the second element; and a positive number if the first
	 *    element is greater than the second element
	 */
	protected int compareServerTypes(IServerType s1, IServerType s2) {
		return s1.getName().compareToIgnoreCase(s2.getName());
	}
}
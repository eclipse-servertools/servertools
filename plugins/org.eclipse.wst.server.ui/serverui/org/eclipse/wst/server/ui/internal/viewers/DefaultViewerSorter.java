/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.viewers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.IServerType;
/**
 * Class used to sort categories, runtime types, and server types in the
 * New wizards.
 */
public class DefaultViewerSorter extends ViewerSorter {
	private static AlphanumComparator alphanum = new AlphanumComparator();
	
	public static class Version implements Comparable {
		private static final String SEPARATORS = ".,";

		private final String[] segments;

		private Version(String[] segments) {
			this.segments = segments;
		}

		public static Version parseVersion(String version) {
			List<String> list = new ArrayList<String>();
			StringTokenizer st = new StringTokenizer(version, SEPARATORS, false);
			while (st.hasMoreTokens())
				list.add(st.nextToken());
			
			String[] s = new String[list.size()];
			list.toArray(s);
			return new Version(s);
		}

		private int compareTo(String s1, String s2) {
			try {
				int i1 = Integer.parseInt(s1);
				int i2 = Integer.parseInt(s2);
				if (i1 == i2)
					return 0;
				if (i1 > i2)
					return 1;
				return -1;
			} catch (Exception e) {
				// ignore
			}
			return s1.compareTo(s2);
		}

		public int compareTo(Object object) {
			if (object == this)
				return 0;
			
			Version other = (Version) object;
			int i = 0;
			while (i < segments.length && i < other.segments.length) {
				String s1 = segments[i];
				String s2 = other.segments[i];
				int c = compareTo(s1, s2);
				if (c != 0)
					return c;
				i++;
			}
			
			if (i == segments.length && i == other.segments.length)
				return 0;
			
			if (i == segments.length)
				return -1;
			return 1;
		}
	}

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
	protected static int compareCategories(String s1, String s2) {
		try {
			Version v1 = Version.parseVersion(s1);
			Version v2 = Version.parseVersion(s2);
			
			return v1.compareTo(v2);
		} catch (NumberFormatException nfe) {
			// ignore
		}
		
		return s1.compareTo(s2);
	}

	/**
	 * Returns <code>true</code> if the two items are in the same 'family', and
	 * <code>false</code> otherwise.
	 * 
	 * @param s1 - first name
	 * @param v1 - first version
	 * @param s2 - second name
	 * @param v2 - second version
	 * @return <code>true</code> if the two items are in the same 'family', and
	 *    <code>false</code> otherwise
	 */
	protected static boolean isSameFamily(String s1, String v1, String s2, String v2) {
		if (s1 == null || s2 == null)
			return false;
		
		if (v1 != null) {
			int ind = s1.indexOf(v1);
			if (ind >= 0)
				s1 = s1.substring(0, ind) + s1.substring(ind+v1.length());
		}
		
		if (v2 != null) {
			int ind = s2.indexOf(v2);
			if (ind >= 0)
				s2 = s2.substring(0, ind) + s2.substring(ind+v2.length());
		}
		return (s1.equals(s2));
	}

	protected static int compareVersions(String s1, String s2) {
		Version v1 = Version.parseVersion(s1);
		Version v2 = Version.parseVersion(s2);
		
		return v1.compareTo(v2);
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
	protected static int compareRuntimeTypes(IRuntimeType r1, IRuntimeType r2) {
		if (isSameFamily(r1.getName(), r1.getVersion(), r2.getName(), r2.getVersion()))
			return compareVersions(r1.getVersion(), r2.getVersion());

		return alphanum.compare(r1.getName(),r2.getName());
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
	protected static int compareServerTypes(IServerType s1, IServerType s2) {
		IRuntimeType r1 = s1.getRuntimeType();
		IRuntimeType r2 = s2.getRuntimeType();
		if (r1 != null && r2 != null) {
			if (isSameFamily(s1.getName(), r1.getVersion(), s2.getName(), r2.getVersion()))
				return compareVersions(r1.getVersion(), r2.getVersion());
		}

		return alphanum.compare(s1.getName(), s2.getName());
	}

	/**
	 * Split strings into blocks of numerics and non-numerics, and compare those blocks. 
	 * This will allow a list containing "SomeServer 9.0" and "SomeServer 10.0" to have 
	 * proper numeric sorting for the numeric portions.  
	 */
	private static class AlphanumComparator implements Comparator<String> {
		public AlphanumComparator() {
		}

		private final boolean isDigit(char ch) {
			return ((ch >= 48) && (ch <= 57));
		}

		/**
		 * Length of string is passed in for improved efficiency (only need to calculate
		 * it once)
		 **/
		private final String getChunk(String s, int slength, int marker) {
			StringBuilder chunk = new StringBuilder();
			char c = s.charAt(marker);
			chunk.append(c);
			marker++;
			if (isDigit(c)) {
				while (marker < slength) {
					c = s.charAt(marker);
					if (!isDigit(c))
						break;
					chunk.append(c);
					marker++;
				}
			} else {
				while (marker < slength) {
					c = s.charAt(marker);
					if (isDigit(c))
						break;
					chunk.append(c);
					marker++;
				}
			}
			return chunk.toString();
		}

		public int compare(String s1, String s2) {
			if ((s1 == null) || (s2 == null)) {
				return 0;
			}

			int thisMarker = 0;
			int thatMarker = 0;
			int s1Length = s1.length();
			int s2Length = s2.length();

			while (thisMarker < s1Length && thatMarker < s2Length) {
				String thisChunk = getChunk(s1, s1Length, thisMarker);
				thisMarker += thisChunk.length();

				String thatChunk = getChunk(s2, s2Length, thatMarker);
				thatMarker += thatChunk.length();

				// If both chunks contain numeric characters, sort them numerically
				int result = 0;
				if (isDigit(thisChunk.charAt(0)) && isDigit(thatChunk.charAt(0))) {
					// Simple chunk comparison by length.
					int thisChunkLength = thisChunk.length();
					result = thisChunkLength - thatChunk.length();
					// If equal, the first different number counts
					if (result == 0) {
						for (int i = 0; i < thisChunkLength; i++) {
							result = thisChunk.charAt(i) - thatChunk.charAt(i);
							if (result != 0) {
								return result;
							}
						}
					}
				} else {
					result = thisChunk.compareTo(thatChunk);
				}

				if (result != 0)
					return result;
			}

			return s1Length - s2Length;
		}
	}

}